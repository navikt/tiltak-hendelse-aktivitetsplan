package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.HendelseMeldingFeiletEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class AvtaleHendelseConsumer(
    private val consumer: Consumer<String, String>,
    private val aktivitetsplanProducer: AktivitetsplanProducer,
    private val database: Database
    ) {
    val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    fun start() = runBlocking {
        log.info("Starter konsumering på topic: ${Topics.AVTALE_HENDELSE}")
        consumer.subscribe(listOf(Topics.AVTALE_HENDELSE))
        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            records.isEmpty && continue
            records.forEach {
                val melding: AvtaleHendelseMelding = try {
                     mapper.readValue(it.value())
                } catch (e: Exception) {
                    // lagre i database med feilstatus
                    log.error("Meldingen med avtaleId (record key) ${it.key()} kunne ikke parses")
                    val hendelseMeldingFeiletEntitet = HendelseMeldingFeiletEntitet(
                        id = UUID.randomUUID(),
                        avtaleId = it.key(),
                        mottattJson = it.value(),
                        topicOffset = it.offset(),
                        mottattTidspunkt = LocalDateTime.now(),
                        feilmelding = e.toString()
                    )
                    database.lagreNyHendelseMeldingFeiletEntitet(hendelseMeldingFeiletEntitet)
                    consumer.commitAsync()
                    return@forEach
                }

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
                    topicOffset = it.offset(),
                    producerTopicOffset = null
                )
                database.lagreNyAktivitetsplanMeldingEntitet(aktivitetsplanMeldingEntitet)
                database.lagreAktivitetsplanId(melding.avtaleId, AktivitetsplanId.fromAvtaleId(melding.avtaleId))

                consumer.commitAsync()
                // kjør en asynkron co-routine
                if (melding.annullertGrunn.equals("Feilregistrering")) {
                    val job = kallProducerForKassering(aktivitetsplanMeldingEntitet)
                    log.info("Startet en coroutine for å sende kasseringsmelding til aktivitetsplan med job ${job.key}")
                } else {
                    val job = kallProducer(aktivitetsplanMeldingEntitet)
                    log.info("Startet en coroutine for å sende melding til aktivitetsplan med job ${job.key}")
                }
            }
        }
    }

    suspend fun kallProducer(melding: AktivitetsplanMeldingEntitet) = coroutineScope {
        launch {
            log.info("Launcher produsent for å sende melding til aktivitsplan")
            aktivitetsplanProducer.sendMelding(melding)
        }
    }

    suspend fun kallProducerForKassering(melding: AktivitetsplanMeldingEntitet) = coroutineScope {
        launch {
            log.info("Launcher produsent for å sende kasseringsmelding til aktivitsplan")
            aktivitetsplanProducer.sendKasserMelding(melding)
        }
    }
}
