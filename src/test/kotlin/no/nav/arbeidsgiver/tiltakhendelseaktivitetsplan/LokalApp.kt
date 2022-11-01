import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.App
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database

fun startTestApp(): App {
    val app = App()
    app.start()
    return app
}