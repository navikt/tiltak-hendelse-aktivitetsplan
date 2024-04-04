package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

fun testProducerConfig(serverUrl: String? = "localhost:9092") = Properties().apply {
    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl)
    put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT")
}
