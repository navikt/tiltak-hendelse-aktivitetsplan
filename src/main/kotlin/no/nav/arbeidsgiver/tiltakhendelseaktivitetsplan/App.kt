package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.dataSource
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.io.Closeable

class App(private val avtaleHendelseConsumer: AvtaleHendelseConsumer, private val aktivitetsplanFeilConsumer: FeilConsumer) : Closeable {
    private val logger = KotlinLogging.logger {}
    private val server = embeddedServer(Netty, port = 8080) {

        routing {
            get("/tiltak-hendelse-aktivitetsplan/internal/isAlive") { call.respond(HttpStatusCode.OK) }
            get("/tiltak-hendelse-aktivitetsplan/internal/isReady") { call.respond(HttpStatusCode.OK) }
        }
    }

    suspend fun start() {
        logger.info("Starter applikasjon :)")
        server.start()
        coroutineScope {
            launch { avtaleHendelseConsumer.start() }
            launch { aktivitetsplanFeilConsumer.start() }
        }
    }

    override fun close() {
        logger.info("Stopper app")
        server.stop(0, 0)
    }
}
suspend fun main() {
    val schema = JSONSchema.parseFile("schema.yml")
    val kasseringSchema = JSONSchema.parseFile("schema-kassering.yml")
    // Setup kafka and database
    val consumer: Consumer<String, String> = KafkaConsumer(consumerConfig())
    val feilConsumer: Consumer<String, String> = KafkaConsumer(feilConsumerConfig())
    val producer: Producer<String, String> = KafkaProducer(producerConfig())
    val database = Database(dataSource)
    val aktivitetsplanProducer = AktivitetsplanProducer(producer, database, schema, kasseringSchema)
    val avtaleHendelseConsumer = AvtaleHendelseConsumer(consumer, aktivitetsplanProducer, database)
    val aktivitetsplanFeilConsumer = FeilConsumer(feilConsumer, database)

    App(avtaleHendelseConsumer, aktivitetsplanFeilConsumer).start()
}
