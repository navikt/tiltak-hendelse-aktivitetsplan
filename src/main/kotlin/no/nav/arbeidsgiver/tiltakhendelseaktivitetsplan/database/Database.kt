package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import org.flywaydb.core.Flyway

class Database {
    private val dataSource: HikariDataSource = hikari()

    init {
        val flyway = Flyway.configure()
            .locations("db.migration")
            .dataSource(dataSource)
            .load()
        flyway.migrate()
    }

    companion object {
        private fun hikari(): HikariDataSource {
            val config = HikariConfig()
            config.driverClassName = "org.h2.Driver"
            config.jdbcUrl = "jdbc:h2:mem:test"
            config.maximumPoolSize = 3
            config.isAutoCommit = false
            config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            config.validate()
            return HikariDataSource(config)
        }
    }

    fun lagreEntitet() {
        val query: String = ""
        val query2: String = "select 1 from avtale_hendelse";
        runQuery(query)
    }


    fun runQuery(query: String) {
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf("${query}".trimIndent()).asExecute)
        }
    }
}