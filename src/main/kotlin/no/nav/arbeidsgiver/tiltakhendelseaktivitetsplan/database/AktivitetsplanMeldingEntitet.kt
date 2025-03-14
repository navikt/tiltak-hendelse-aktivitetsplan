package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.Row
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleId
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleStatus
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.HendelseType
import java.time.LocalDateTime
import java.util.*

data class AktivitetsplanMeldingEntitet(
    val id: UUID,
    val avtaleId: AvtaleId,
    val avtaleStatus: AvtaleStatus,
    val opprettetTidspunkt: LocalDateTime,
    val hendelseType: HendelseType,
    val mottattJson: String,
    val sendingJson: String?,
    val sendt: Boolean,
    val topicOffset: Long,
    val producerTopicOffset: Long?
) {
    companion object {
        fun fra(id: UUID, aktivitetsplanMeldingEntitet: AktivitetsplanMeldingEntitet): AktivitetsplanMeldingEntitet {
            return AktivitetsplanMeldingEntitet(
                id,
                aktivitetsplanMeldingEntitet.avtaleId,
                aktivitetsplanMeldingEntitet.avtaleStatus,
                aktivitetsplanMeldingEntitet.opprettetTidspunkt,
                aktivitetsplanMeldingEntitet.hendelseType,
                aktivitetsplanMeldingEntitet.mottattJson,
                aktivitetsplanMeldingEntitet.sendingJson,
                aktivitetsplanMeldingEntitet.sendt,
                aktivitetsplanMeldingEntitet.topicOffset,
                aktivitetsplanMeldingEntitet.producerTopicOffset
            )
        }
    }
}

val tilAktivitetsplanMeldingEntitet: (Row) -> AktivitetsplanMeldingEntitet = { row ->
    AktivitetsplanMeldingEntitet(
        id = UUID.fromString(row.string("id")),
        avtaleId = AvtaleId(row.string("avtale_id")),
        avtaleStatus = AvtaleStatus.valueOf(row.string("avtale_status")),
        opprettetTidspunkt = row.localDateTime("opprettet_tidspunkt"),
        hendelseType = HendelseType.valueOf(row.string("hendelse_type")),
        mottattJson = row.string("mottatt_json"),
        sendingJson = row.stringOrNull("sending_json"),
        sendt = row.boolean("sendt"),
        topicOffset = row.long("topic_offset"),
        producerTopicOffset = row.longOrNull("producer_topic_offset")
    )
}
