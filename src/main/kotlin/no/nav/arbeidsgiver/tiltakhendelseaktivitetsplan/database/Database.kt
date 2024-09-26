package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import com.zaxxer.hikari.HikariDataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AktivitetsplanId
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleId
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.flywaydb.core.Flyway
import java.util.*

class Database(val dataSource: HikariDataSource) {

    init {
        val flyway = Flyway.configure()
            .locations("db.migration")
            .dataSource(dataSource)
            .load()
        flyway.migrate()
    }

    fun lagreNyAktivitetsplanMeldingEntitet(entitet: AktivitetsplanMeldingEntitet) {
        //language=postgresql
        val query: String = """
            insert into aktivitetsplan_melding (id, avtale_id, avtale_status, opprettet_tidspunkt, hendelse_type, mottatt_json, sending_json, sendt, topic_offset, producer_topic_offset) values
            (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    query,
                    entitet.id,
                    entitet.avtaleId.value,
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
        //language=postgresql
        val query = "select * from aktivitetsplan_melding where id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, id).map(tilAktivitetsplanMeldingEntitet).asSingle)
        }
    }

    fun hentEntitet(avtaleId: AvtaleId): List<AktivitetsplanMeldingEntitet> {
        //language=postgresql
        val query = "select * from aktivitetsplan_melding where avtale_id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, avtaleId.value).map(tilAktivitetsplanMeldingEntitet).asList)
        }
    }

    fun settEntitetTilSendt(id: UUID, offset: Long) {
        //language=postgresql
        val query = """
            update aktivitetsplan_melding set sendt = true, producer_topic_offset = ?, feilmelding = null where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, offset, id).asUpdate)
        }
    }

    fun settFeilmeldingPåEntitet(id: UUID, feilmelding: String) {
        //language=postgresql
        val query = """
            update aktivitetsplan_melding set feilmelding = ? where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, feilmelding, id).asUpdate)
        }
    }

    fun settEntitetSendingJson(id: UUID, meldingJson: String) {
        //language=postgresql
        val query = """
            update aktivitetsplan_melding set sending_json = ? where id = ?
        """.trimIndent()
        using(sessionOf(dataSource)) { session ->
            session.run(queryOf(query, meldingJson, id).asUpdate)
        }
    }

    fun lagreNyHendelseMeldingFeiletEntitet(hendelseMeldingFeiletEntitet: HendelseMeldingFeiletEntitet) {
        //language=postgresql
        val query = """
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

    fun lagreAktivitetsplanId(avtaleId: AvtaleId, aktivitetsplanId: AktivitetsplanId) {
        //language=postgresql
        val query = "insert into aktivitetsplan_id (avtale_id, aktivitetsplan_id) values (?, ?) on conflict do nothing"
        using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(query, avtaleId.value, aktivitetsplanId.value).asUpdate
            )
        }
        log.info("Lagret aktivitetsplanId i database")
    }

    fun hentAktivitetsplanId(avtaleId: AvtaleId): AktivitetsplanId? {
        //language=postgresql
        val query = "select aktivitetsplan_id from aktivitetsplan_id where avtale_id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(query, avtaleId.value)
                    .map{ AktivitetsplanId(it.string("aktivitetsplan_id")) }
                    .asSingle
            )
        }
    }

    fun hentAvtaleId(aktivitetsplanId: AktivitetsplanId): AvtaleId? {
        //language=postgresql
        val query = "select avtale_id from aktivitetsplan_id where avtale_id = ?"
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(query, aktivitetsplanId.value)
                    .map{ AvtaleId(it.string("aktivitetsplan_id")) }
                    .asSingle
            )
        }
    }

}
