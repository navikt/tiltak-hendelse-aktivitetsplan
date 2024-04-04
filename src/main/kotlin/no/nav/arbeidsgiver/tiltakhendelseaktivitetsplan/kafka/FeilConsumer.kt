package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration
import java.util.*

class FeilConsumer(
    private val consumer: Consumer<String, String>,
    private val database: Database
) {
    val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    fun start() = runBlocking  {
        log.info("Starter konsumering på topic: ${Topics.AKTIVITETSPLAN_FEIL}")

        consumer.subscribe(listOf(Topics.AKTIVITETSPLAN_FEIL))
        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            println("FeilConsumer: records empty? : ${records.isEmpty}")
            records.isEmpty && continue
            records.forEach {
                val melding: AktivitetsPlanFeilMelding = mapper.readValue(it.value())

                val avtaleId = it.key(); // Kafka key er funksjonell id som altså skal være avtaleId.
                val hendelseMelding: List<AktivitetsplanMeldingEntitet>? =  database.hentEntitetMedAvtaleId(UUID.fromString(avtaleId));
                println("FeilConsumer MELDING ID ${avtaleId} type: ${melding.errorType} og finnes den i databasen?  ${hendelseMelding?.size}") //TODO Fjern meg

                if (!hendelseMelding.isNullOrEmpty()) {
                   println("EN FEIL MELDING SOM SKAL IGNORERES") //TODO Fjern meg
                   log.error("Feil fra aktivitetsplan for avtale ${avtaleId}. Feilmelding: ${melding.errorMessage}");
               }

                //TODO: SJekk om feilmelding er til oss og send en ny melding tilbake til aktivitetsplan

            }
            consumer.commitAsync()
        }
    }
}
