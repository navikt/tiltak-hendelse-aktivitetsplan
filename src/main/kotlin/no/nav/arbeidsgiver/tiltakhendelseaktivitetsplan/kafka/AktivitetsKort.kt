package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class AktivitetsKort(
    // Obligatoriske
    val id: UUID,
    val personIdent: String,
    val startDato: LocalDate?,
    val sluttDato: LocalDate?,
    val tittel: String,
    val beskrivelse: String,
    val aktivitetStatus: AktivitetStatus,
    val endretAv: Ident,
    val endretDato: Instant,
    val avtaltMedNav: Boolean,
    val avsluttetBegrunnelse: String,

    // Attributter, lenker og lignende

) {
    companion object {
        fun fromHendelseMelding(melding: AvtaleHendelseMelding) : AktivitetsKort {
            return AktivitetsKort(
                id = melding.avtaleId,
                personIdent = melding.deltakerFnr,
                startDato = melding.startDato,
                sluttDato = melding.sluttDato,
                tittel = "Avtale om ${melding.tiltakstype.beskrivelse}",
                beskrivelse = "Dette er en beskrivelse",
                aktivitetStatus = aktivitetStatusFraAvtaleStatus(melding.avtaleStatus),
                endretAv = endretAvAktivitetsplanformat(melding.utførtAv, melding.utførtAvRolle),
                endretDato = melding.sistEndret,
                avtaltMedNav = true,
                avsluttetBegrunnelse = ""
            )
        }

        private fun endretAvAktivitetsplanformat(utførtAv: String, utførtAvRolle: AvtaleHendelseUtførtAvRolle): Ident {
            val identType = if (utførtAvRolle == AvtaleHendelseUtførtAvRolle.VEILEDER) IdentType.NAVIDENT else IdentType.ARBEIDSGIVER
            return Ident(utførtAv, identType)
        }

        private fun aktivitetStatusFraAvtaleStatus(avtaleStatus: AvtaleStatus): AktivitetStatus {
            return when (avtaleStatus) {
                AvtaleStatus.ANNULLERT -> AktivitetStatus.FULLFORT
                AvtaleStatus.AVBRUTT -> AktivitetStatus.FULLFORT
                AvtaleStatus.PÅBEGYNT -> AktivitetStatus.PLANLAGT
                AvtaleStatus.MANGLER_GODKJENNING -> AktivitetStatus.PLANLAGT
                AvtaleStatus.KLAR_FOR_OPPSTART -> AktivitetStatus.PLANLAGT
                AvtaleStatus.GJENNOMFØRES -> AktivitetStatus.GJENNOMFORES
                AvtaleStatus.AVSLUTTET -> AktivitetStatus.FULLFORT
            }
        }

    }
}
