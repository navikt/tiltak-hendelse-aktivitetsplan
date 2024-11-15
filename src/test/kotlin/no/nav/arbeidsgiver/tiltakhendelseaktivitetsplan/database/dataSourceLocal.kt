package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.HikariCP

val testDataSource = HikariCP.init(
    url = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    username = "sa",
    password = "sa"
) {
    maximumPoolSize = 2
}
