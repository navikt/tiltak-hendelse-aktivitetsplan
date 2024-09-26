package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleId
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
        avtaleId = AvtaleId("251c5828-59dc-11ed-9b6a-0242ac120002"),
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
        avtaleId = AvtaleId("66276156-9bc6-11ed-a8fc-0242ac120002"),
        avtaleStatus = AvtaleStatus.GJENNOMFØRES,
        opprettetTidspunkt = LocalDateTime.now(),
        hendelseType = HendelseType.AVTALE_FORLENGET,
        mottattJson = "",
        sendingJson = "",
        sendt = false,
        topicOffset = 1235346L,
        producerTopicOffset = 54321L
    )

    val entitet3medSammeAvtaleId = AktivitetsplanMeldingEntitet(
        id = UUID.fromString("71c33696-442d-49a0-ac04-18c77373a1fe"),
        avtaleId = AvtaleId("64e80700-c9b9-4e03-9741-7566eb0542e7"),
        avtaleStatus = AvtaleStatus.GJENNOMFØRES,
        opprettetTidspunkt = LocalDateTime.now(),
        hendelseType = HendelseType.AVTALE_FORLENGET,
        mottattJson = "",
        sendingJson = "",
        sendt = false,
        topicOffset = 1235346L,
        producerTopicOffset = 54321L
    )

    val entitet4medSammeAvtaleId = AktivitetsplanMeldingEntitet(
        id = UUID.fromString("7433d909-01f6-496c-afc1-56b76c9f3795"),
        avtaleId = AvtaleId("64e80700-c9b9-4e03-9741-7566eb0542e7"),
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
        val aktivitetsplanMeldingEntitetHentetMedAvtaleId = database.hentEntitet(AvtaleId("251c5828-59dc-11ed-9b6a-0242ac120002"))
        assertNotNull(aktivitetsplanMeldingEntitet)
        assertNotNull(aktivitetsplanMeldingEntitetHentetMedAvtaleId)
        assertEquals(aktivitetsplanMeldingEntitetHentetMedAvtaleId.size, 1)
    }

    @Test
    fun skal_lunne_lagre_flere_meldinger_med_samme_avtale_id() {
        val database = Database(testDataSource)
        database.lagreNyAktivitetsplanMeldingEntitet(entitet3medSammeAvtaleId)
        database.lagreNyAktivitetsplanMeldingEntitet(entitet4medSammeAvtaleId)
        val aktivitetsplanMeldingEntitetHentetMedAvtaleId = database.hentEntitet(AvtaleId("64e80700-c9b9-4e03-9741-7566eb0542e7"))
        assertEquals(aktivitetsplanMeldingEntitetHentetMedAvtaleId!!.size, 2)
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
