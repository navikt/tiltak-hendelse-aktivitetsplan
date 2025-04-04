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
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

private const val AKTIVITETSPLAN_TOPIC = "dab.aktivitetskort-v1.1"

class AktivitetsplanProducer(
    private val producer: Producer<String, String>,
    private val database: Database,
    private val schema: JSONSchema,
    private val kasseringSchema: JSONSchema
) {
    private val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun sendMelding(entitet: AktivitetsplanMeldingEntitet) {
        val aktivitetsplanId = database.hentAktivitetsplanId(entitet.avtaleId)
            ?: throw IllegalStateException("Fant ikke aktivitetsplanId for avtaleId ${entitet.avtaleId}")
        val melding: AvtaleHendelseMelding = mapper.readValue(entitet.mottattJson)
        val aktivitetsKort = AktivitetsKort.fromHendelseMelding(aktivitetsplanId, melding)
        val aktivitetsplanMelding = AktivitetsplanMelding.fromAktivitetskort(
            UUID.randomUUID(),
            "TEAM_TILTAK",
            "UPSERT_AKTIVITETSKORT_V1",
            AktivitetTiltakstype.parse(melding.tiltakstype),
            aktivitetsKort
        )
        val meldingJson = mapper.writeValueAsString(aktivitetsplanMelding)

        log.info(
            "Sender melding for avtaleId {} og aktivitetsplanId {} med melding id {} til aktivitetsplan",
            entitet.avtaleId,
            aktivitetsplanId,
            aktivitetsplanMelding.messageId
        )

        if (!schema.validate(meldingJson)) {
            val output = schema.validateBasic(meldingJson)
            output.errors?.forEach {
                log.error("${it.error} - ${it.instanceLocation}")
            }
            return
        }

        val record = ProducerRecord(AKTIVITETSPLAN_TOPIC, aktivitetsplanId.toString(), meldingJson)
        database.settEntitetSendingJson(entitet.id, meldingJson)
        producer.send(record) { recordMetadata, exception ->
            when (exception) {
                null -> {
                    log.info("Sendt melding til aktivitetsplan (topic=${recordMetadata.topic()}, partition=${recordMetadata.partition()}, offset= ${recordMetadata.offset()})")
                    // Oppdatere sendt til true
                    database.settEntitetTilSendt(entitet.id, recordMetadata.offset())
                }

                else -> {
                    log.error("Kunne ikke sende melding til aktivitetsplan", exception)
                    database.settFeilmeldingPåEntitet(entitet.id, exception.message ?: "")
                }
            }
        }
    }

    fun sendKasserMelding(entitet: AktivitetsplanMeldingEntitet) {
        val aktivitetsplanId = database.hentAktivitetsplanId(entitet.avtaleId)
            ?: throw IllegalStateException("Fant ikke aktivitetsplanId for avtaleId ${entitet.avtaleId}")
        val melding: AvtaleHendelseMelding = mapper.readValue(entitet.mottattJson)
        val kasseringsMelding = AktivitetsplanMeldingKassering.fromHendelseMelding(aktivitetsplanId, melding)
        val meldingJson = mapper.writeValueAsString(kasseringsMelding)

        log.info(
            "Sender kasseringsmelding for avtaleId {} og aktivitetsplanId {} med melding id {} til aktivitetsplan",
            entitet.avtaleId,
            aktivitetsplanId,
            kasseringsMelding.messageId
        )

        if (!kasseringSchema.validate(meldingJson)) {
            val output = kasseringSchema.validateBasic(meldingJson)
            output.errors?.forEach {
                log.error("${it.error} - ${it.instanceLocation}")
            }
            return
        }

        val record = ProducerRecord(AKTIVITETSPLAN_TOPIC, aktivitetsplanId.toString(), meldingJson)
        database.settEntitetSendingJson(entitet.id, meldingJson)
        producer.send(record) { recordMetadata, exception ->
            when (exception) {
                null -> {
                    log.info("Sendt melding til aktivitetsplan (topic=${recordMetadata.topic()}, partition=${recordMetadata.partition()}, offset= ${recordMetadata.offset()})")
                    // Oppdatere sendt til true
                    database.settEntitetTilSendt(entitet.id, recordMetadata.offset())
                }

                else -> {
                    log.error("Kunne ikke sende melding til aktivitetsplan", exception)
                    database.settFeilmeldingPåEntitet(entitet.id, exception.message ?: "")
                }
            }
        }
    }
}
