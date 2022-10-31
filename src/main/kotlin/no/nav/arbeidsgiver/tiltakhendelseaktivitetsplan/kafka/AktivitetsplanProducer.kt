package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class AktivitetsplanProducer(
    private val producer: Producer<String, String>
) {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun sendMelding(melding: AvtaleHendelseMelding) {
        val aktivitetsplanMelding = AktivitetsplanMelding.fromHendelseMelding(melding)
        val meldingJson = mapper.writeValueAsString(aktivitetsplanMelding)
        val record = ProducerRecord("aktivitetsPlanTopic", meldingJson)

        producer.send(record, Callback { recordMetadata, exception ->  })
        // Mappe melding til aktivitetplan-kontrakten
        // Sende den
        // onSuccess oppdaterer database sendt = true


    }
}