package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer

fun main() {
    // Testoppsett
    val consumer: Consumer<String, String> = KafkaConsumer(testConsumerConfig())
    val producer: Producer<String, String> = KafkaProducer(testProducerConfig())
    val database = Database()
    val aktivitetsplanProducer = AktivitetsplanProducer(producer, database)
    val avtaleHendelseConsumer = AvtaleHendelseConsumer(consumer, aktivitetsplanProducer, database)

    val app = App(avtaleHendelseConsumer)
    app.start()
}