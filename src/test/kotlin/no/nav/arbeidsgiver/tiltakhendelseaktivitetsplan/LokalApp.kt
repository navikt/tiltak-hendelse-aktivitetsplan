package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import net.pwall.json.JSON
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.testDataSource
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.h2.tools.Server
import java.io.File

fun main() {
    val schema = JSONSchema.parseFile("src/test/resources/schema.yml")
    // Testoppsett
    val consumer: Consumer<String, String> = KafkaConsumer(testConsumerConfig())
    val producer: Producer<String, String> = KafkaProducer(testProducerConfig())
    val database = Database(testDataSource)
    val aktivitetsplanProducer = AktivitetsplanProducer(producer, database, schema)
    val avtaleHendelseConsumer = AvtaleHendelseConsumer(consumer, aktivitetsplanProducer, database)

    val app = App(avtaleHendelseConsumer)
    Server.createWebServer().start()
    app.start()
}
