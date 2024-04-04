package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*

fun testConsumerConfig(serverUrl:String = "localhost:9092") = Properties().apply {
    put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1)
    put(ConsumerConfig.GROUP_ID_CONFIG, "tiltak-hendelse-aktivitetsplan-1")
    put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
    put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
    put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, serverUrl)
    put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT")
}
