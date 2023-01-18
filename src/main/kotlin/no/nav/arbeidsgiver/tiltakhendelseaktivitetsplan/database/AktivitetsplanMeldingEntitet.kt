package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.Row
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleStatus
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.HendelseType
import java.time.LocalDateTime
import java.util.*

data class AktivitetsplanMeldingEntitet (
    val id: UUID,
    val avtaleId: UUID,
    val avtaleStatus: AvtaleStatus,
    val opprettetTidspunkt: LocalDateTime,
    val hendelseType: HendelseType,
    val mottattJson: String,
    val sendingJson: String?,
    val sendt: Boolean,
    val offset: Long
)

val tilAktivitetsplanMeldingEntitet: (Row) -> AktivitetsplanMeldingEntitet = { row ->
    AktivitetsplanMeldingEntitet(
        id = UUID.fromString(row.string("id")),
        avtaleId = UUID.fromString(row.string("avtale_id")),
        avtaleStatus = AvtaleStatus.valueOf(row.string("avtale_status")),
        opprettetTidspunkt = row.localDateTime("opprettet_tidspunkt"),
        hendelseType = HendelseType.valueOf(row.string("hendelse_type")),
        mottattJson = row.string("mottatt_json"),
        sendingJson = row.string("sending_json"),
        sendt = row.boolean("sendt"),
        offset = row.long("offset")
    )
}

