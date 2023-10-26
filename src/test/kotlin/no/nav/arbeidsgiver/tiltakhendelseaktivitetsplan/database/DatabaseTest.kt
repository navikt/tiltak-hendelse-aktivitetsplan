package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleStatus
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.HendelseType
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DatabaseTest {

    val entitet = AktivitetsplanMeldingEntitet(
        id = UUID.fromString("6cb7a6ce-59d7-11ed-9b6a-0242ac120002"),
        avtaleId = UUID.fromString("251c5828-59dc-11ed-9b6a-0242ac120002"),
        avtaleStatus = AvtaleStatus.GJENNOMFØRES,
        opprettetTidspunkt = LocalDateTime.now(),
        hendelseType = HendelseType.AVTALE_FORLENGET,
        mottattJson = "",
        sendingJson = "",
        sendt = false,
        topicOffset = 1235346L,
        producerTopicOffset = 54321L
    )

    val entitet2 = AktivitetsplanMeldingEntitet(
        id = UUID.fromString("66276156-9bc6-11ed-a8fc-0242ac120002"),
        avtaleId = UUID.fromString("66276156-9bc6-11ed-a8fc-0242ac120002"),
        avtaleStatus = AvtaleStatus.GJENNOMFØRES,
        opprettetTidspunkt = LocalDateTime.now(),
        hendelseType = HendelseType.AVTALE_FORLENGET,
        mottattJson = "",
        sendingJson = "",
        sendt = false,
        topicOffset = 1235346L,
        producerTopicOffset = 54321L
    )

    val feiletEntitet = HendelseMeldingFeiletEntitet(
        id = UUID.fromString("66276156-9bc6-11ed-a8fc-0242ac120002"),
        avtaleId = "66276156-9bc6-11ed-a8fc-0242ac120002",
        mottattTidspunkt = LocalDateTime.now(),
        mottattJson = "",
        topicOffset = 123456L,
        feilmelding = "Oh noes"
    )

    @Test
    fun skal_kunne_lagre_og_hente_entiteter() {
        val database = Database(testDataSource)
        database.lagreNyAktivitetsplanMeldingEntitet(entitet)
        val aktivitetsplanMeldingEntitet = database.hentEntitet(UUID.fromString("6cb7a6ce-59d7-11ed-9b6a-0242ac120002"))
        assertNotNull(aktivitetsplanMeldingEntitet)
    }

    @Test
    fun skal_kunne_oppdatere_entitet_til_sendt() {
        val database = Database(testDataSource)
        database.lagreNyAktivitetsplanMeldingEntitet(entitet2)
        database.settEntitetTilSendt(UUID.fromString("66276156-9bc6-11ed-a8fc-0242ac120002"), 1337L)
        val aktivitetsplanMeldingEntitet = database.hentEntitet(UUID.fromString("66276156-9bc6-11ed-a8fc-0242ac120002"))
        assertNotNull(aktivitetsplanMeldingEntitet)
        if (aktivitetsplanMeldingEntitet != null) {
            assertTrue(aktivitetsplanMeldingEntitet.sendt)
            assertEquals(1337L, aktivitetsplanMeldingEntitet.producerTopicOffset)
        }
    }

    @Test
    fun skal_kunne_lagre_feilede_hendelse_i_database() {
        val database = Database(testDataSource)
        database.lagreNyHendelseMeldingFeiletEntitet(feiletEntitet)
    }

}
