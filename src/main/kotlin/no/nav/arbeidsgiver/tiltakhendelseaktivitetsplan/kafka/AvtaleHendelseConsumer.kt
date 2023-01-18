package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration
import java.time.LocalDateTime
import kotlinx.coroutines.*
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import java.util.UUID

class AvtaleHendelseConsumer(
    private val consumer: Consumer<String, String>,
    private val aktivitetsplanProducer: AktivitetsplanProducer,
    private val database: Database
    ) {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun start() = runBlocking {
        log.info("Starter konsumering på topic: ${Topics.AVTALE_HENDELSE}")
        consumer.subscribe(listOf(Topics.AVTALE_HENDELSE))
        mapper.registerModule(JavaTimeModule())
        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            records.isEmpty && continue
            records.forEach {
                val melding: AvtaleHendelseMelding = mapper.readValue(it.value())
                log.info("Lest melding med avtale-id ${melding.avtaleId}")
                // Filtrere vekk de som ikke skal til aktivitetplan
                if (!melding.hendelseType.skalTilAktivitetsplan) {
                    log.info("melding med hendelsetype ${melding.hendelseType} skal ikke til aktivitetsplan")
                    consumer.commitAsync()
                    return@forEach
                }
                if (!melding.tiltakstype.skalTilAktivitetsplan) {
                    log.info("melding med tiltakstype ${melding.tiltakstype} skal ikke til aktivitetsplan")
                    consumer.commitAsync()
                    return@forEach
                }

                val aktivitetsplanMeldingEntitet = AktivitetsplanMeldingEntitet(
                    id = UUID.randomUUID(),
                    avtaleId = melding.avtaleId,
                    avtaleStatus = melding.avtaleStatus,
                    opprettetTidspunkt = LocalDateTime.now(),
                    hendelseType = melding.hendelseType,
                    mottattJson = it.value(),
                    sendingJson = null,
                    sendt = false,
                    offset = it.offset()
                )
                database.lagreNyAktivitetsplanMeldingEntitet(aktivitetsplanMeldingEntitet)
                consumer.commitAsync()
                // kjør en asynkron co-routine
                val job = kallProducer(aktivitetsplanMeldingEntitet)
                log.info("Startet en coroutine for å sende melding til aktivitetsplan med job ${job.key}")
            }
        }
    }

    suspend fun kallProducer(melding: AktivitetsplanMeldingEntitet) = coroutineScope {
        launch {
            log.info("Launcher produsent for å sende melding til aktivitsplan")
            aktivitetsplanProducer.sendMelding(melding)
        }
    }
}