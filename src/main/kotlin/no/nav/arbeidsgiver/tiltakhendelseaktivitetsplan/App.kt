package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AktivitetsplanProducer
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleHendelseConsumer
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.consumerConfig
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.producerConfig
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.Cluster
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.io.Closeable
import java.io.File

class App(private val avtaleHendelseConsumer: AvtaleHendelseConsumer) : Closeable {
    private val logger = KotlinLogging.logger {}
    private val server = embeddedServer(Netty, port = 8092) {

        routing {
            get("/tiltak-hendelse-aktivitetsplan/internal/isAlive") { call.respond(HttpStatusCode.OK) }
            get("/tiltak-hendelse-aktivitetsplan/internal/isReady") { call.respond(HttpStatusCode.OK) }
        }
    }

    fun start() {
        logger.info("Starter applikasjon :)")

        if(Cluster.current != Cluster.PROD_GCP) {
            server.start()
            avtaleHendelseConsumer.start()
        } else {
            server.start(wait = true)
        }
    }

    override fun close() {
        logger.info("Stopper app")
        server.stop(0, 0)
    }
}
fun main() {
    val schema = JSONSchema.parseFile("schema.json")
    // Setup kafka and database
    val consumer: Consumer<String, String> = KafkaConsumer(consumerConfig())
    val producer: Producer<String, String> = KafkaProducer(producerConfig())
    val database = Database()
    val aktivitetsplanProducer = AktivitetsplanProducer(producer, database, schema)
    val avtaleHendelseConsumer = AvtaleHendelseConsumer(consumer, aktivitetsplanProducer, database)

    App(avtaleHendelseConsumer).start()
}
