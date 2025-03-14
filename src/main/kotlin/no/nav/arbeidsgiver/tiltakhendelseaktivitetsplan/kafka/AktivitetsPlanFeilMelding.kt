package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.time.ZonedDateTime

data class AktivitetsPlanFeilMelding(
    val timestamp: ZonedDateTime,
    val failingMessage: String,
    val errorMessage: String
)
