package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

private const val AKTIVITETSPLAN_TOPIC = "topic"

class AktivitetsplanProducer(
    private val producer: Producer<String, String>,
    private val database: Database
) {
    private val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    // Burde det egentlig AktivitetsplanMeldingEntitet som parameter? Med tanke på resend
    fun sendMelding(melding: AvtaleHendelseMelding) {
        log.info("Sender melding for avtaleId ${melding.avtaleId} til aktivitetsplan")
        val aktivitetsplanMelding = AktivitetsplanMelding.fromHendelseMelding(melding)
        val meldingJson = mapper.writeValueAsString(aktivitetsplanMelding)
        val record = ProducerRecord(AKTIVITETSPLAN_TOPIC, melding.avtaleId.toString(), meldingJson)
        producer.send(record) { recordMetadata, exception ->
            when (exception) {
                null -> {
                    log.info("Sendt melding til aktivitetsplan (topic=${recordMetadata.topic()}, partition=${recordMetadata.partition()}, offset= ${recordMetadata.offset()})")
                    // Oppdatere sendt til true
                    database.settEntitetTilSendt(melding.avtaleId)
                }
                else -> {
                    log.error("Kunne ikke sende melding til aktivitetsplan ${exception.stackTrace}")
                    database.settFeilmeldingPåEntitet(melding.avtaleId, "feilmeldingen her")
                }
            }
        }
    }
}