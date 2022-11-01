package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*

const val aktivitsplanMeldingTopic = "aktivitetsplan-topic"

fun consumerConfig() = Properties().apply {
    /*put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1)
    put(ConsumerConfig.GROUP_ID_CONFIG, "tiltak-hendelse-aktivitetsplan-1")
    put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
    put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)

    put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, environmentVariables.kafkaBrokers)

    put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL")
    put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "")
    put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, environmentVariables.kafkaTruststorePath)
    put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, environmentVariables.kafkaCredstorePassword)
    put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12")
    put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, environmentVariables.kafkaKeystorePath)
    put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, environmentVariables.kafkaCredstorePassword)*/
}
