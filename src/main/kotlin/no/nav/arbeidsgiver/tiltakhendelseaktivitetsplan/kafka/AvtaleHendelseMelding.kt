package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka


import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class AvtaleHendelseMelding(
    var hendelseType: HendelseType,
    var tiltakstype: Tiltakstype,
    var avtaleStatus: AvtaleStatus,
    var startDato: LocalDate?,
    var sluttDato: LocalDate?,
    var bedriftNavn: String?,
    var bedriftNr: String,
    var stillingstittel: String?,
    var stillingprosent: Int?,
    var avtaleInngått: LocalDateTime?,
    var utførtAv: String,
    var deltakerFnr: String,
    var avtaleId: UUID,
    var avtaleNr: Int,
    var sistEndret: Instant,
    var veilederNavIdent: String?
)
