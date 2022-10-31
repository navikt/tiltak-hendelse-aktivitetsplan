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
class AvtaleHendelseConsumer(
    private val consumer: Consumer<String, String>,
    private val aktivitetsplanProducer: AktivitetsplanProducer
    ) {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


    suspend fun start() {
        log.info("Starter konsumering på topic: ${Topics.AVTALE_HENDELSE}")
        consumer.subscribe(listOf(Topics.AVTALE_HENDELSE))

        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            records.isEmpty && continue
            consumer.commitAsync()
            records.forEach {
                val melding: AvtaleHendelseMelding = mapper.readValue(it.value())
                val aktivitetsplanMeldingEntitet = AktivitetsplanMeldingEntitet(
                    meldingId = melding.id.toString(),
                    avtaleId = melding.id,
                    avtaleStatus = melding.avtaleStatus,
                    tidspunkt = LocalDateTime.now(),
                    hendelseType = melding.hendelseType,
                    mottattJson = melding.toString(),
                    sendingJson = null,
                    sendt = false
                )

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