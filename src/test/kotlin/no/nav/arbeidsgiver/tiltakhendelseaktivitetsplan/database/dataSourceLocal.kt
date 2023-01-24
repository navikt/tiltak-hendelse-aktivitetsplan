package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.HikariCP

val testDataSource = HikariCP.init(
    url = "jdbc:h2:mem:default",
    username = "sa",
    password = "sa"
) {
    maximumPoolSize = 2
}
