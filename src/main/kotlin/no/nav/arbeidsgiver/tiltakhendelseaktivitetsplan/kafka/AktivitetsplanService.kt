package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log

class AktivitetsplanService(
    private val database: Database,
    private val aktivitetsplanProducer: AktivitetsplanProducer
) {

    fun rekjørFeilede() = runBlocking {
        // Hent alle feilede
        val entiteterSomIkkeErSendt = database.hentEntiteterSomIkkeErSendt()
        log.info("Hentet ${entiteterSomIkkeErSendt.size} meldinger som har sendt=false for rekjøring")
        entiteterSomIkkeErSendt.forEach {
            kallProducer(it)
        }
    }

    suspend fun kallProducer(melding: AktivitetsplanMeldingEntitet) = coroutineScope {
        launch {
            log.info("Launcher produsent rekjøring av entiet som ikke har blitt sendt")
            aktivitetsplanProducer.sendMelding(melding)
        }
    }
}