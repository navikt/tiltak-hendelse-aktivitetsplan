package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.*
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.Cluster
import java.net.URL
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
    // val beskrivelse: String,
    val aktivitetStatus: AktivitetStatus,
    val endretAv: Ident,
    val endretTidspunkt: Instant,
    val avtaltMedNav: Boolean,
    val oppgaver: Oppgave?,
    val handlinger: List<LenkeSeksjon>,
    val detaljer: List<Attributt>
    //val avsluttetBegrunnelse: String?,

    // Attributter, lenker og lignende

) {
    companion object {
        fun fromHendelseMelding(melding: AvtaleHendelseMelding): AktivitetsKort {
            return AktivitetsKort(
                id = melding.avtaleId,
                personIdent = melding.deltakerFnr,
                startDato = melding.startDato,
                sluttDato = melding.sluttDato,
                tittel = "Avtale om ${melding.tiltakstype.beskrivelse}",
                //  beskrivelse = "Dette er en beskrivelse",
                aktivitetStatus = aktivitetStatusFraAvtaleStatus(melding.avtaleStatus),
                endretAv = endretAvAktivitetsplanformat(melding.utførtAv, melding.utførtAvRolle),
                endretTidspunkt = melding.sistEndret,
                avtaltMedNav = melding.veilederNavIdent != null,
                oppgaver = null,
                handlinger = listOf(
                    LenkeSeksjon("Gå til avtalen", "", lenke("INTERN"), LenkeType.INTERN),
                    LenkeSeksjon("Gå til avtalen", "", lenke("EKSTERN"), LenkeType.EKSTERN)
                ),
                detaljer = listOf(
                    lagAttributt(label = "Arbeidsgiver", verdi = melding.bedriftNavn),
                    lagAttributt(label = "Stilling", verdi = melding.stillingstittel),
                    lagAttributt(label = "Stillingsprosent", verdi = melding.stillingprosent?.toString())
                )
                //avsluttetBegrunnelse = null
            )
        }

        private fun lagAttributt(label: String, verdi: String?): Attributt {
            val feltVerdi = if (verdi !== null) verdi.toString() else "Ikke fylt ut";
            return Attributt(label = label, verdi = feltVerdi)
        }

        private fun lenke(side: String): URL {
            val internDev = "https://tiltaksgjennomforing.dev.intern.nav.no/tiltaksgjennomforing/"
            val internProd = "https://tiltaksgjennomforing.intern.nav.no/tiltaksgjennomforing/"
            val eksternDev = "https://tiltaksgjennomforing.dev.nav.no/tiltaksgjennomforing/"
            val eksternProd = "https://arbeidsgiver.nav.no/tiltaksgjennomforing/"
            if (Cluster.current == Cluster.PROD_GCP) {
                return if (side == "INTERN") URL(internProd) else URL(eksternProd)
            } else {
                return if (side == "INTERN") URL(internDev) else URL(eksternDev)
            }
        }

        private fun lagOppgave(avtaleId: UUID): Oppgave {
            val internAvtalePath =
                if (Cluster.current == Cluster.PROD_GCP) "https://tiltaksgjennomforing.intern.nav.no/tiltaksgjennomforing/avtale/${avtaleId}" else "https://tiltaksgjennomforing.dev.intern.nav.no/tiltaksgjennomforing/avtale/${avtaleId}"
            val eksternAvtalePath =
                if (Cluster.current == Cluster.PROD_GCP) "https://arbeidsgiver.nav.no/tiltaksgjennomforing/avtale/${avtaleId}" else "https://tiltaksgjennomforing.dev.nav.no/tiltaksgjennomforing/${avtaleId}"
            val internOppgaveLenke = OppgaveLenke(
                tekst = "Gå til avtalen",
                subtekst = "Trykk her",
                url = URL(internAvtalePath),
                knapptekst = "Gå til avtalen"
            )
            val eksternOppgaveLenke = OppgaveLenke(
                tekst = "Gå til avtalen",
                subtekst = "Trykk her",
                url = URL(eksternAvtalePath),
                knapptekst = "Gå til avtalen"
            )
            return Oppgave(ekstern = eksternOppgaveLenke, intern = internOppgaveLenke)
        }

        private fun endretAvAktivitetsplanformat(utførtAv: String, utførtAvRolle: AvtaleHendelseUtførtAvRolle): Ident {
            val identType =
                if (utførtAvRolle == AvtaleHendelseUtførtAvRolle.VEILEDER) IdentType.NAVIDENT else IdentType.ARBEIDSGIVER
            return Ident(utførtAv, identType)
        }

        private fun aktivitetStatusFraAvtaleStatus(avtaleStatus: AvtaleStatus): AktivitetStatus {
            return when (avtaleStatus) {
                AvtaleStatus.PÅBEGYNT -> AktivitetStatus.PLANLAGT
                AvtaleStatus.MANGLER_GODKJENNING -> AktivitetStatus.PLANLAGT
                AvtaleStatus.KLAR_FOR_OPPSTART -> AktivitetStatus.PLANLAGT
                AvtaleStatus.GJENNOMFØRES -> AktivitetStatus.GJENNOMFORES
                AvtaleStatus.AVSLUTTET -> AktivitetStatus.FULLFORT
                AvtaleStatus.ANNULLERT -> AktivitetStatus.AVBRUTT
                AvtaleStatus.AVBRUTT -> AktivitetStatus.AVBRUTT
            }
        }

    }
}
