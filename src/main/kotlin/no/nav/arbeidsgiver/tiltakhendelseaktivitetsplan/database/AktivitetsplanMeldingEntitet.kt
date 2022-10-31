package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleStatus
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.HendelseType
import java.time.LocalDateTime
import java.util.*

data class AktivitetsplanMeldingEntitet (
    val meldingId: String,
    val avtaleId: UUID,
    val avtaleStatus: AvtaleStatus,
    val tidspunkt: LocalDateTime,
    val hendelseType: HendelseType,
    val mottattJson: String,
    val sendingJson: String?,
    val sendt: Boolean,
)

