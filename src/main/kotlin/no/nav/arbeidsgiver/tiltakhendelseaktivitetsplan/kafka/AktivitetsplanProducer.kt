package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

private const val AKTIVITETSPLAN_TOPIC = "topic"

class AktivitetsplanProducer(
    private val producer: Producer<String, String>
) {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun sendMelding(melding: AvtaleHendelseMelding) {
        val aktivitetsplanMelding = AktivitetsplanMelding.fromHendelseMelding(melding)
        val meldingJson = mapper.writeValueAsString(aktivitetsplanMelding)
        val record = ProducerRecord<String, String>(AKTIVITETSPLAN_TOPIC, melding.id.toString(),meldingJson)
        producer.send(record) { recordMetadata, exception ->
            when (exception) {
                null -> {
                    log.info("Record was successfully sent (topic=${recordMetadata.topic()}, partition=${recordMetadata.partition()}, offset= ${recordMetadata.offset()})")
                    // Oppdatere sendt til true
                }
                else -> log.error("Kunne ikke sende melding til aktivitetsplan ${exception.stackTrace}")
            }
        }
        // Mappe melding til aktivitetplan-kontrakten
        // Sende den
        // onSuccess oppdaterer database sendt = true
    }
}