package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.HikariCP
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import org.testcontainers.containers.PostgreSQLContainer

private val postgres14Container = PostgreSQLContainer<Nothing>("postgres:14")
    .apply {
        withDatabaseName("tiltak_hendelse_aktivitetsplan_test")
        withUsername("test")
        withPassword("test")
        start()
    }

val testDataSource = HikariCP.init(
    url = postgres14Container.jdbcUrl,
    username = postgres14Container.username,
    password = postgres14Container.password
) {
    maximumPoolSize = 2
}

fun resetTestDatabase() {
    val query = "truncate table aktivitetsplan_melding, hendelse_melding_feilet, aktivitetsplan_id"
    using(sessionOf(testDataSource)) { session ->
        session.run(queryOf(query).asUpdate)
    }
}

