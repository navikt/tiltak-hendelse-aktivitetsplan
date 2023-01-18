package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleStatus
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.HendelseType
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertNotNull

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
        topicOffset = 1235346L
    )

    @Test
    fun skal_kunne_lagre_og_hente_entiteter() {
        val database = DatabaseLokal()
        database.lagreNyAvtaleMeldingEntitet(entitet)
        val aktivitetsplanMeldingEntitet = database.hentEntitet(UUID.fromString("6cb7a6ce-59d7-11ed-9b6a-0242ac120002"))
        assertNotNull(aktivitetsplanMeldingEntitet)
    }

}