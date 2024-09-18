package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.util.*

data class AktivitetsplanId(val value: UUID) {
    constructor(value: String) : this(UUID.fromString(value))
    override fun toString(): String {
        return value.toString()
    }
}
