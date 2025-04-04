package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import com.nimbusds.jose.util.DefaultResourceRetriever
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.dataSource
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.dto.AvtalemeldingRequest
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.*
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import no.nav.security.token.support.v2.tokenValidationSupport
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.io.Closeable

class App(
    private val avtaleHendelseConsumer: AvtaleHendelseConsumer,
    private val aktivitetsplanFeilConsumer: FeilConsumer,
    private val database: Database
) : Closeable {
    private val logger = KotlinLogging.logger {}
    private val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {}
        }
        install(Authentication) {
            tokenValidationSupport(config = ApplicationConfig("application.conf"), resourceRetriever = DefaultResourceRetriever())
        }
        routing {
            get("/tiltak-hendelse-aktivitetsplan/internal/isAlive") { call.respond(HttpStatusCode.OK) }
            get("/tiltak-hendelse-aktivitetsplan/internal/isReady") { call.respond(HttpStatusCode.OK) }
            authenticate {
                put("/tiltak-hendelse-aktivitetsplan/api/avtale/{avtaleId}/aktivitetsplan-id") {
                    try {
                        val avtaleId = AvtaleId(
                            call.parameters["avtaleId"]
                                ?: throw IllegalArgumentException("Avtale-id mangler i forespørselen")
                        )
                        val avtalemeldingRequest = call.receive<AvtalemeldingRequest>()

                        log.info(
                            "Oppdaterer avtale {} med aktivitetsplan id {}",
                            avtaleId,
                            avtalemeldingRequest.aktivitetsplanId
                        )

                        database.upsertAktivitetsplanId(
                            avtaleId,
                            avtalemeldingRequest.aktivitetsplanId,
                        )

                        if (avtalemeldingRequest.resendSisteMelding) {
                            database.hentEntitet(avtaleId)
                                .filter { it.sendt }
                                .maxByOrNull { it.opprettetTidspunkt }
                                ?.let {
                                    logger.info(
                                        "Sender melding ${it.id} på ny for avtale ${avtaleId}"
                                    )
                                    avtaleHendelseConsumer.kallProducer(it)
                                }
                        }

                        call.respond(HttpStatusCode.NoContent)
                    } catch (ex: Exception) {
                        log.error("Feil ved oppdatering av avtale", ex)
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
            authenticate {
                post("/tiltak-hendelse-aktivitetsplan/api/avtale/{avtaleId}/send-siste-melding") {
                    try {
                        val avtaleId = AvtaleId(
                            call.parameters["avtaleId"]
                                ?: throw IllegalArgumentException("Avtale-id mangler i forespørselen")
                        )
                        database.hentEntitet(avtaleId)
                            .filter { it.sendt }
                            .maxByOrNull { it.opprettetTidspunkt }
                            ?.let {
                                logger.info(
                                    "Sender melding ${it.id} på ny for avtale ${avtaleId}"
                                )
                                avtaleHendelseConsumer.kallProducer(it)
                            }
                        call.respond(HttpStatusCode.NoContent)
                    } catch (ex: Exception) {
                        log.error("Feil ved oppdatering av avtale", ex)
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
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
    val logger = KotlinLogging.logger {}
    try {
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

        App(avtaleHendelseConsumer, aktivitetsplanFeilConsumer, database).start()
    } catch (e: Exception) {
        logger.error(e.message, e)
        throw e
    }
}
