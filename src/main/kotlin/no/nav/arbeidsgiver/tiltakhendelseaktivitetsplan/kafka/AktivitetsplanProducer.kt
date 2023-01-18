package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.Cluster
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.UUID

private const val AKTIVITETSPLAN_TOPIC = "dab.aktivitetskort-v1"

class AktivitetsplanProducer(
    private val producer: Producer<String, String>,
    private val database: Database,
    private val schema: JSONSchema
) {
    private val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    // Burde det egentlig AktivitetsplanMeldingEntitet som parameter? Med tanke på resend
    fun sendMelding(entitet: AktivitetsplanMeldingEntitet) {
        log.info("Sender melding for avtaleId ${entitet.avtaleId} til aktivitetsplan")
        val melding: AvtaleHendelseMelding = mapper.readValue(entitet.mottattJson)
        val kafkaMeldingId = UUID.randomUUID()
        val aktivitetsKort = AktivitetsKort.fromHendelseMelding(melding)
        val aktivitetsplanMelding = AktivitetsplanMelding.fromAktivitetskort(
            kafkaMeldingId,
            "TEAM_TILTAK",
            "UPSERT_AKTIVITETSKORT_V1",
            melding.tiltakstype, // må sjekke
            aktivitetsKort)
        val meldingJson = mapper.writeValueAsString(aktivitetsplanMelding)

        if(!schema.validate(meldingJson))  {
            val output = schema.validateBasic(meldingJson)
            output.errors?.forEach {
                log.error("${it.error} - ${it.instanceLocation}")
            }
            log.error("")
            return
        }
        val record = ProducerRecord(AKTIVITETSPLAN_TOPIC, kafkaMeldingId.toString(), meldingJson)
        database.settEntitetSendingJson(entitet.id, meldingJson)
        if(Cluster.current != Cluster.PROD_GCP) {
            producer.send(record) { recordMetadata, exception ->
                when (exception) {
                    null -> {
                        log.info("Sendt melding til aktivitetsplan (topic=${recordMetadata.topic()}, partition=${recordMetadata.partition()}, offset= ${recordMetadata.offset()})")
                        // Oppdatere sendt til true
                        database.settEntitetTilSendt(entitet.id)
                    }
                    else -> {
                        log.error("Kunne ikke sende melding til aktivitetsplan ${exception.stackTrace}")
                        database.settFeilmeldingPåEntitet(melding.avtaleId, "feilmeldingen her")
                    }
                }
            }
        }
    }
}
