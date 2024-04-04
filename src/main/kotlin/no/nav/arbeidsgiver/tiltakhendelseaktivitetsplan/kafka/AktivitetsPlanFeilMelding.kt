package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.time.LocalDateTime

data class AktivitetsPlanFeilMelding(
    val timestamp: LocalDateTime,
    val failingMessage: String,
    val errorMessage: String,
    val errorType: String
)
