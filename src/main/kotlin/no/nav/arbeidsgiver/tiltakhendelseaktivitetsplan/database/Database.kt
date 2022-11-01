package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import org.flywaydb.core.Flyway
import java.util.UUID

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

    fun lagreNyAvtaleMeldingEntitet(entitet: AktivitetsplanMeldingEntitet) {
        val query: String = """
            insert into avtale_hendelse () values
            (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent();
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(
                query,
                entitet.id,
                entitet.avtaleId,
                entitet.avtaleStatus,
                entitet.opprettetTidspunkt,
                entitet.hendelseType,
                entitet.mottattJson,
                entitet.sendingJson,
                entitet.sendt
            ).asUpdate)
        }
    }

    fun hentEntitet(id: UUID): AktivitetsplanMeldingEntitet? {
        val query: String = "select * from avtale_hendelse where id = ?"
        return using( sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).map(tilAvtaleMeldingEntitet).asSingle)
        }
    }

    fun settEntitetTilSendt(id: UUID) {
        val query: String = """
            update avtale_hendelse set sendt = true, feilmelding = null where id = ?"
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).asUpdate)
        }
    }

    fun settFeilmeldingPåEntitet(id: UUID, feilmelding: String) {
        val query: String = """
            update avtale_hendelse set feilmelding = ? where id = ?"
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, feilmelding, id).asUpdate)
        }
    }

}
