package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.*

data class AktivitetsplanId(val value: UUID? = null): JsonSerializer<AktivitetsplanId>() {
    constructor(value: String) : this(UUID.fromString(value))
    override fun toString(): String {
        return value.toString()
    }

    override fun serialize(p0: AktivitetsplanId, p1: JsonGenerator, p2: SerializerProvider?) {
        p1.writeString(p0.value.toString())
    }

    companion object {
        fun fromAvtaleId(avtaleId: AvtaleId): AktivitetsplanId {
            return if (avtaleId.value != null) AktivitetsplanId(avtaleId.value)
            else throw IllegalArgumentException("AvtaleId kan ikke være null")
        }
    }
}
