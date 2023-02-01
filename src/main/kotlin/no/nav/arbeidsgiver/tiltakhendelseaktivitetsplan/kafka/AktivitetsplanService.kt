package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log

class AktivitetsplanService(
    private val database: Database,
    private val aktivitetsplanProducer: AktivitetsplanProducer
) {

    fun rekjørFeilede() {
        // Hent alle feilede
        val entiteterSomIkkeErSendt = database.hentEntiteterSomIkkeErSendt()
        log.info("Hentet ${entiteterSomIkkeErSendt.size} meldinger som har sendt=false for rekjøring")
        entiteterSomIkkeErSendt.forEach {
            aktivitetsplanProducer.sendMelding(it)
        }
    }
}