package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration
import java.util.*

class FeilConsumer(
    private val consumer: Consumer<String, String>,
    private val database: Database
)  {
    fun start() = runBlocking {
        log.info("Starter konsumering på topic: ${Topics.AKTIVITETSPLAN_FEIL}")
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            records.isEmpty && continue
            records.forEach {
                val melding: AktivitetsPlanFeilMelding = mapper.readValue(it.value())
                val avtaleId = it.key(); // Kafka key er funksjonell id som altså skal være avtaleId.
                val hendelseMelding = database.hentEntitetMedAvtaleId(UUID.fromString(avtaleId));
                // Log error om det er en melding vi har sendt
                if(hendelseMelding != null) {
                    log.error("Feil fra aktivitetsplan for avtale ${avtaleId}. Feilmelding: ${melding.errorMessage}");
                }
            }
        }
    }
}