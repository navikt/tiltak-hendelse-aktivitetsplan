package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
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
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    // Burde det egentlig AktivitetsplanMeldingEntitet som parameter? Med tanke på resend
    fun sendMelding(entitet: AktivitetsplanMeldingEntitet) {
        log.info("Sender melding for avtaleId ${entitet.avtaleId} til aktivitetsplan")
        val melding: AvtaleHendelseMelding = mapper.readValue(entitet.mottattJson)
        val aktivitetsKort = AktivitetsKort.fromHendelseMelding(melding)
        val aktivitetsplanMelding = AktivitetsplanMelding.fromAktivitetskort(
            melding.avtaleId,
            "TEAM_TILTAK",
            "UPSERT_AKTIVITETSKORT_V1",
            "MIDL_LONNSTILSK", // må sjekke
            aktivitetsKort)
        val meldingJson = mapper.writeValueAsString(aktivitetsplanMelding)
        val record = ProducerRecord(AKTIVITETSPLAN_TOPIC, melding.avtaleId.toString(), meldingJson)
        database.settEntitetSendingJson(entitet.id, meldingJson)
        log.info("Skulle egentlig sendt melding for avtaleId ${entitet.avtaleId} til aktivitetsplan")
//        producer.send(record) { recordMetadata, exception ->
//            when (exception) {
//                null -> {
//                    log.info("Sendt melding til aktivitetsplan (topic=${recordMetadata.topic()}, partition=${recordMetadata.partition()}, offset= ${recordMetadata.offset()})")
//                    // Oppdatere sendt til true
//                    database.settEntitetTilSendt(entitet.id)
//                }
//                else -> {
//                    log.error("Kunne ikke sende melding til aktivitetsplan ${exception.stackTrace}")
//                    database.settFeilmeldingPåEntitet(melding.avtaleId, "feilmeldingen her")
//                }
//            }
//        }
    }
}