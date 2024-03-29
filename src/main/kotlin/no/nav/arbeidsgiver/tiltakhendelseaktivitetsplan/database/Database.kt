package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import com.zaxxer.hikari.HikariDataSource
import kotliquery.HikariCP
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.flywaydb.core.Flyway
import java.util.*

class Database(val dataSource: HikariDataSource) {
    //private val dataSource: HikariDataSource = hikari()


    init {
        val flyway = Flyway.configure()
            .locations("db.migration")
            .dataSource(dataSource)
            .load()
        flyway.migrate()
    }

    fun lagreNyAktivitetsplanMeldingEntitet(entitet: AktivitetsplanMeldingEntitet) {
        val query: String = """
            insert into aktivitetsplan_melding (id, avtale_id, avtale_status, opprettet_tidspunkt, hendelse_type, mottatt_json, sending_json, sendt, topic_offset, producer_topic_offset) values
            (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                    entitet.sendt,
                    entitet.topicOffset,
                    entitet.producerTopicOffset
                ).asUpdate
            )
        }
        log.info("Lagret avtalemeldingentitet i database")
    }

    fun hentEntitet(id: UUID): AktivitetsplanMeldingEntitet? {
        val query: String = "select * from aktivitetsplan_melding where id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).map(tilAktivitetsplanMeldingEntitet).asSingle)
        }
    }

    fun hentEntitetMedAvtaleId(avtaleId: UUID): List<AktivitetsplanMeldingEntitet>? {
        val query = "select * from aktivitetsplan_melding where avtale_id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, avtaleId).map(tilAktivitetsplanMeldingEntitet).asList)
        }
    }

    fun settEntitetTilSendt(id: UUID, offset: Long) {
        val query: String = """
            update aktivitetsplan_melding set sendt = true, producer_topic_offset = ?, feilmelding = null where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, offset, id).asUpdate)
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

    fun lagreNyHendelseMeldingFeiletEntitet(hendelseMeldingFeiletEntitet: HendelseMeldingFeiletEntitet) {
        val query: String = """
            insert into hendelse_melding_feilet (id, avtale_id, mottatt_tidspunkt, mottatt_json, topic_offset, feilmelding) values
            (?, ?, ?, ?, ?, ?)
        """.trimIndent();
        using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    query,
                    hendelseMeldingFeiletEntitet.id,
                    hendelseMeldingFeiletEntitet.avtaleId,
                    hendelseMeldingFeiletEntitet.mottattTidspunkt,
                    hendelseMeldingFeiletEntitet.mottattJson,
                    hendelseMeldingFeiletEntitet.topicOffset,
                    hendelseMeldingFeiletEntitet.feilmelding
                ).asUpdate
            )
        }
        log.info("Lagret feilet hendelse i database")
    }

}
