package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotliquery.HikariCP
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.flywaydb.core.Flyway
import java.util.UUID

class Database {
    //private val dataSource: HikariDataSource = hikari()

    val dataSource = HikariCP.init(
        url = "jdbc:h2:mem:default",
        username = "sa",
        password = "sa"
    ) {
        maximumPoolSize = 2
    }

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
            insert into aktivitetsplan_melding (id, avtale_id, avtale_status, opprettet_tidspunkt, hendelse_type, mottatt_json, sending_json, sendt) values
            (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent();
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(
                query,
                entitet.id,
                entitet.avtaleId,
                entitet.avtaleStatus.name,
                entitet.opprettetTidspunkt,
                entitet.hendelseType.name,
                entitet.mottattJson,
                entitet.sendingJson,
                entitet.sendt
            ).asUpdate)
        }
        log.info("Lagret avtalemeldingentitet i database")
    }

    fun hentEntitet(id: UUID): AktivitetsplanMeldingEntitet? {
        val query: String = "select * from aktivitetsplan_melding where id = ?"
        return using( sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).map(tilAvtaleMeldingEntitet).asSingle)
        }
    }

    fun settEntitetTilSendt(id: UUID) {
        val query: String = """
            update aktivitetsplan_melding set sendt = true, feilmelding = null where id = ?"
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).asUpdate)
        }
    }

    fun settFeilmeldingPåEntitet(id: UUID, feilmelding: String) {
        val query: String = """
            update aktivitetsplan_melding set feilmelding = ? where id = ?"
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, feilmelding, id).asUpdate)
        }
    }

}
