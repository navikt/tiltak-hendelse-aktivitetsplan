package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import java.util.*

data class AktivitetsplanId(val value: UUID) {
    constructor(value: String) : this(UUID.fromString(value))
    override fun toString(): String {
        return value.toString()
    }

    companion object {
        fun fromAvtaleId(avtaleId: AvtaleId): AktivitetsplanId {
            return if (avtaleId.value != null) AktivitetsplanId(avtaleId.value)
            else throw IllegalArgumentException("AvtaleId kan ikke være null")
        }
    }
}
