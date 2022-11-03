package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.HikariCP
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.flywaydb.core.Flyway
import java.util.*

class Database {
    //private val dataSource: HikariDataSource = hikari()
    val DB_HOST = System.getenv("DB_HOST")
    val DB_PORT = System.getenv("DB_PORT")
    val DB_DATABASE = System.getenv("DB_DATABASE")
    val DB_USERNAME = System.getenv("DB_USERNAME")
    val DB_PASSWORD = System.getenv("DB_PASSWORD")

    val dataSource = HikariCP.init(
        url = "jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}?user=${DB_USERNAME}&password=${DB_PASSWORD}",
        username = DB_USERNAME,
        password = DB_PASSWORD
    ) {
        maximumPoolSize = 4
    }

    init {
        val flyway = Flyway.configure()
            .locations("db.migration")
            .dataSource(dataSource)
            .load()
        flyway.migrate()
    }

    fun lagreNyAvtaleMeldingEntitet(entitet: AktivitetsplanMeldingEntitet) {
        val query: String = """
            insert into aktivitetsplan_melding (id, avtale_id, avtale_status, opprettet_tidspunkt, hendelse_type, mottatt_json, sending_json, sendt) values
            (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent();
        using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    query,
                    entitet.id,
                    entitet.avtaleId,
                    entitet.avtaleStatus.name,
                    entitet.opprettetTidspunkt,
                    entitet.hendelseType.name,
                    entitet.mottattJson,
                    entitet.sendingJson,
                    entitet.sendt
                ).asUpdate
            )
        }
        log.info("Lagret avtalemeldingentitet i database")
    }

    fun hentEntitet(id: UUID): AktivitetsplanMeldingEntitet? {
        val query: String = "select * from aktivitetsplan_melding where id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).map(tilAvtaleMeldingEntitet).asSingle)
        }
    }

    fun settEntitetTilSendt(id: UUID) {
        val query: String = """
            update aktivitetsplan_melding set sendt = true, feilmelding = null where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).asUpdate)
        }
    }

    fun settFeilmeldingPåEntitet(id: UUID, feilmelding: String) {
        val query: String = """
            update aktivitetsplan_melding set feilmelding = ? where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, feilmelding, id).asUpdate)
        }
    }

    fun settEntitetSendingJson(id: UUID, meldingJson: String) {
        val query: String = """
            update aktivitetsplan_melding set sending_json = ? where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, meldingJson, id).asUpdate)
        }
    }

}
