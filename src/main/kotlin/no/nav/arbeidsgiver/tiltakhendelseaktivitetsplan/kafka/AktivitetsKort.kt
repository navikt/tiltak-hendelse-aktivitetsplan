package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.Attributt
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.LenkeSeksjon
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.LenkeType
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.Oppgave
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.Cluster
import java.net.URL
import java.time.Instant
import java.time.LocalDate

data class AktivitetsKort(
    // Obligatoriske
    @JsonSerialize(using = AktivitetsplanId.Serializer::class)
    val id: AktivitetsplanId,
    val personIdent: String,
    val startDato: LocalDate?,
    val sluttDato: LocalDate?,
    val tittel: String,
    val aktivitetStatus: AktivitetStatus,
    val endretAv: Ident,
    val endretTidspunkt: Instant,
    val avtaltMedNav: Boolean,
    val oppgave: Oppgave?,
    val handlinger: List<LenkeSeksjon>?,
    val detaljer: List<Attributt>

    // Attributter, lenker og lignende

) {
    companion object {
        fun fromHendelseMelding(aktivitetsplanId: AktivitetsplanId, melding: AvtaleHendelseMelding): AktivitetsKort {
            return AktivitetsKort(
                id = aktivitetsplanId,
                personIdent = melding.deltakerFnr,
                startDato = melding.startDato,
                sluttDato = melding.sluttDato,
                tittel = formaterTittel(melding.tiltakstype, melding.stillingstittel, melding.bedriftNavn),
                //  beskrivelse = "Dette er en beskrivelse",
                aktivitetStatus = AktivitetStatus.parse(melding.avtaleStatus),
                endretAv = Ident.fra(melding),
                endretTidspunkt = melding.sistEndret,
                avtaltMedNav = if (melding.opphav == AvtaleOpphav.ARENA) true else melding.veilederNavIdent != null,
                oppgave = null,
                handlinger = listOf(
                    LenkeSeksjon("Gå til avtalen", "", lenke("INTERN", melding.avtaleId), LenkeType.INTERN),
                    LenkeSeksjon("Gå til avtalen", "", lenke("EKSTERN", melding.avtaleId), LenkeType.EKSTERN)
                ),
                detaljer = lagDetaljer(melding)
            )
        }

        private fun lagDetaljer(melding: AvtaleHendelseMelding): List<Attributt> {
            if (melding.tiltakstype == Tiltakstype.MENTOR) {
                val mentorBeregninger = listOf(melding.arbeidsgiverKontonummer, melding.otpSats, melding.arbeidsgiveravgift, melding.feriepengesats)
                val mentorTimeEnhet = if (mentorBeregninger.any { it != null }) "måned" else "uke"

                return listOf(
                    lagAttributt(label = "Arbeidsgiver", verdi = melding.bedriftNavn),
                    lagAttributt(label = "Antall timer mentor per $mentorTimeEnhet", verdi = melding.mentorAntallTimer?.toString()),
                )
            }

            return listOf(
                lagAttributt(label = "Arbeidsgiver", verdi = melding.bedriftNavn),
                lagAttributt(label = "Stilling", verdi = melding.stillingstittel),
                lagAttributt(label = "Stillingsprosent", verdi = melding.stillingprosent?.toString()),
                lagAttributt(label = "Antall dager per uke", verdi = melding.antallDagerPerUke?.toString())
            )
        }

        private fun formaterTittel(tiltakstype: Tiltakstype, stillingstittel: String?, bedriftNavn: String?): String {
            if (tiltakstype == Tiltakstype.MENTOR) {
                return "Avtale om tilskudd til mentor"
            }
            if (!stillingstittel.isNullOrBlank() && !bedriftNavn.isNullOrBlank()) {
                return "$stillingstittel hos $bedriftNavn"
            }
            if (!bedriftNavn.isNullOrBlank()) {
                return "${tiltakstype.beskrivelse} hos $bedriftNavn"
            }
            return "Avtale om ${tiltakstype.beskrivelse}"
        }

        private fun lagAttributt(label: String, verdi: String?): Attributt {
            val feltVerdi = if (verdi !== null) verdi else "Ikke fylt ut";
            return Attributt(label = label, verdi = feltVerdi)
        }

        private fun lenke(side: String, avtaleId: AvtaleId): URL {
            val internDev = "https://tiltaksgjennomforing.intern.dev.nav.no/tiltaksgjennomforing/avtale/${avtaleId}?part=VEILEDER"
            val internProd = "https://tiltaksgjennomforing.intern.nav.no/tiltaksgjennomforing/avtale/${avtaleId}?part=VEILEDER"
            val eksternDev = "https://tiltaksgjennomforing.ekstern.dev.nav.no/tiltaksgjennomforing/avtale/${avtaleId}?part=DELTAKER"
            val eksternProd = "https://arbeidsgiver.nav.no/tiltaksgjennomforing/avtale/${avtaleId}?part=DELTAKER"
            if (Cluster.current == Cluster.PROD_GCP) {
                return if (side == "INTERN") URL(internProd) else URL(eksternProd)
            } else {
                return if (side == "INTERN") URL(internDev) else URL(eksternDev)
            }
        }
    }
}
