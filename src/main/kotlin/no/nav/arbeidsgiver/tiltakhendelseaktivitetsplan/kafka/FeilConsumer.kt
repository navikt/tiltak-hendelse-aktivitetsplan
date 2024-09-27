package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration

class FeilConsumer(
    private val consumer: Consumer<String, String>,
    private val database: Database
) {
    fun start() = runBlocking {
        log.info("Starter konsumering på topic: ${Topics.AKTIVITETSPLAN_FEIL}")
        val mapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JavaTimeModule())
        consumer.subscribe(listOf(Topics.AKTIVITETSPLAN_FEIL))
        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            records.isEmpty && continue
            records.forEach {
                val melding: AktivitetsPlanFeilMelding = mapper.readValue(it.value())
                val avtaleId = database.hentAvtaleId(AktivitetsplanId(it.key()));
                val hendelseMelding = if (avtaleId != null) database.hentEntitet(avtaleId) else null;
                // Log error om det er en melding vi har sendt
                if (!hendelseMelding.isNullOrEmpty()) {
                    log.error("Feil fra aktivitetsplan for avtale ${avtaleId}. Feilmelding: ${melding.errorMessage}");
                }
            }
            consumer.commitAsync()
        }
    }
}
