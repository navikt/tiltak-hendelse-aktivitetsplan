package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class AktivitetsplanMelding(
    // Obligatoriske
    val id: UUID,
    val personIdent: String,
    val startDato: LocalDate,
    val sluttDato: LocalDate,
    val tittel: String,
    val beskrivelse: String,
    val aktivitetStatus: AktivitetStatus,
    val endretAv: String,
    val endretDato: Instant,
    val avtaltMedNav: Boolean,
    val avsluttetBegrunnelse: String,

    // Attributter, lenker og lignende

) {
    companion object {
        fun fromHendelseMelding(melding: AvtaleHendelseMelding) : AktivitetsplanMelding {
            return AktivitetsplanMelding(
                id = melding.avtaleId,
                personIdent = melding.deltakerFnr,
                startDato = melding.startDato,
                sluttDato = melding.sluttDato,
                tittel = "Dette er en tittel",
                beskrivelse = "Dette er en beskrivelse",
                aktivitetStatus = AktivitetStatus.FULLFØRT,
                endretAv = melding.utførtAv,
                endretDato = melding.sistEndret,
                avtaltMedNav = true,
                avsluttetBegrunnelse = ""
            )
        }
    }
}
