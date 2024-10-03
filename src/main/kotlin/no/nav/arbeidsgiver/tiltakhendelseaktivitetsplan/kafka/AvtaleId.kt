package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import java.util.*

data class AvtaleId(val value: UUID? = null) {
    constructor(value: String) : this(UUID.fromString(value))

    override fun toString(): String {
        return value.toString()
    }

    object Serializer: JsonSerializer<AvtaleId>() {
        override fun serialize(p0: AvtaleId, p1: JsonGenerator, p2: SerializerProvider?) {
            p1.writeString(p0.value.toString())
        }
    }

    object Deserializer: JsonDeserializer<AvtaleId>() {
        override fun deserialize(p0: JsonParser, p1: DeserializationContext?): AvtaleId {
            val node = p0.readValueAsTree<JsonNode>()
            return AvtaleId(node.asText())
        }
    }
}
