package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class AvtaleId(val value: UUID? = null) : JsonDeserializer<AvtaleId>() {
    constructor(value: String) : this(UUID.fromString(value))

    override fun toString(): String {
        return value.toString()
    }

    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): AvtaleId {
        val node = p0.readValueAsTree<JsonNode>()
        return AvtaleId(UUID.fromString(node.asText()))
    }
}
