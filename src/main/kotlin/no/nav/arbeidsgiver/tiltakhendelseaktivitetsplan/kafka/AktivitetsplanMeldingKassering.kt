package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.util.*

data class AktivitetsplanMeldingKassering(
    val source: String,
    val actionType: String,
    val aktivitetsId: UUID,
    val personIdent: String,
    val navIdent: String,
    val messageId: String
) {
    companion object {
        fun fromHendelseMelding(aktivitetsplanId: AktivitetsplanId, melding: AvtaleHendelseMelding): AktivitetsplanMeldingKassering {
            return AktivitetsplanMeldingKassering(
                source = "TEAM_TILTAK",
                actionType = "KASSER_AKTIVITET",
                aktivitetsId = aktivitetsplanId.value,
                personIdent = melding.deltakerFnr,
                navIdent = melding.veilederNavIdent.toString(),
                messageId = UUID.randomUUID().toString()
            )
        }
    }
}
