package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.util.*

data class AktivitetsplanMelding(
    val messageId: UUID,
    val source: String,
    val actionType: String,
    val aktivitetskortType: String,
    val aktivitetskort: AktivitetsKort
) {
    companion object {
        fun fromAktivitetskort(messageId: UUID, source: String, actionType: String, aktivitetskortType: String, aktivitetskort: AktivitetsKort): AktivitetsplanMelding {
            return AktivitetsplanMelding(messageId, source, actionType, aktivitetskortType, aktivitetskort)
        }
    }
}
