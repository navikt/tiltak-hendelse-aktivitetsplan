package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
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

    fun start() {
        log.info("Starter konsumering på topic: ${Topics.AVTALE_HENDELSE}")
        consumer.subscribe(listOf(Topics.AVTALE_HENDELSE))

        runBlocking {
            while (true) {
                val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
                records.isEmpty && continue
                consumer.commitAsync()
                records.forEach {
                    val melding: AvtaleHendelseMelding = mapper.readValue(it.value())
                    val aktivitetsplanMeldingEntitet = AktivitetsplanMeldingEntitet(
                        id = UUID.randomUUID(),
                        avtaleId = melding.avtaleId,
                        avtaleStatus = melding.avtaleStatus,
                        opprettetTidspunkt = LocalDateTime.now(),
                        hendelseType = melding.hendelseType,
                        mottattJson = it.value(),
                        sendingJson = null,
                        sendt = false
                    )
                    database.lagreNyAvtaleMeldingEntitet(aktivitetsplanMeldingEntitet)
                    ;
                    // Opprette DB-entitet med sendt=false som registrerer event
                    // Save entitet i DB med sendt = false
                    // Lytte på entitet oppretet event og sende på kafka til aktivitetsplnanen

                    // kjør en asynkron co-routine
                    val job = launch {
                        aktivitetsplanProducer.sendMelding(melding)
                        // denne oppdaterer sendt til true
                    }
                }
            }
        }
    }

}