package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import java.io.Closeable

class App() : Closeable {
    private val server = embeddedServer(Netty, port = 8080) {

        routing {
            get("/tiltak-hendelse-aktivitetsplan/internal/isAlive") { call.respond(HttpStatusCode.OK) }
            get("/tiltak-hendelse-aktivitetsplan/internal/isReady") { call.respond(HttpStatusCode.OK) }
        }
    }

    fun start() {
        log.info("Starter applikasjon :)")
        server.start()
        runBlocking { while(true) { } }
    }

    override fun close() {
        log.info("Stopper app")
        server.stop(0, 0)
    }
}
fun main() {
    App().start()
}
