package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import java.util.*

data class AktivitetsplanId(val value: UUID? = null) {
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

    object Serializer: JsonSerializer<AktivitetsplanId>() {
        override fun serialize(p0: AktivitetsplanId, p1: JsonGenerator, p2: SerializerProvider?) {
            p1.writeString(p0.value.toString())
        }
    }

    object Deserializer: JsonDeserializer<AktivitetsplanId>() {
        override fun deserialize(p0: JsonParser, p1: DeserializationContext?): AktivitetsplanId {
            val node = p0.readValueAsTree<JsonNode>()
            return AktivitetsplanId(node.asText())
        }
    }
}
