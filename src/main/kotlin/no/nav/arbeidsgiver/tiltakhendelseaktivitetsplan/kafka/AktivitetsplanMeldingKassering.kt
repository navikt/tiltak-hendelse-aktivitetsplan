package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*

data class AktivitetsplanMeldingKassering(
    val source: String,
    val actionType: String,
    @JsonSerialize(using = AktivitetsplanId::class)
    val aktivitetsId: AktivitetsplanId,
    val personIdent: String,
    val navIdent: String,
    val messageId: String
) {
    companion object {
        fun fromHendelseMelding(aktivitetsplanId: AktivitetsplanId, melding: AvtaleHendelseMelding): AktivitetsplanMeldingKassering {
            return AktivitetsplanMeldingKassering(
                source = "TEAM_TILTAK",
                actionType = "KASSER_AKTIVITET",
                aktivitetsId = aktivitetsplanId,
                personIdent = melding.deltakerFnr,
                navIdent = melding.veilederNavIdent.toString(),
                messageId = UUID.randomUUID().toString()
            )
        }
    }
}
