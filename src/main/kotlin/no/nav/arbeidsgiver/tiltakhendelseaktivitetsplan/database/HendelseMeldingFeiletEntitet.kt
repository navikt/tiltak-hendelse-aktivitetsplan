package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import java.time.LocalDateTime
import java.util.*

data class HendelseMeldingFeiletEntitet(
    val id: UUID,
    val avtaleId: String,
    val mottattJson: String,
    val topicOffset: Long,
    val mottattTidspunkt: LocalDateTime,
    val feilmelding: String
)
