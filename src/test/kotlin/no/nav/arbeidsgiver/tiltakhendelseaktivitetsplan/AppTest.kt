package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.AktivitetsplanMeldingEntitet
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.Database
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.DatabaseTest
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database.testDataSource
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.h2.tools.Server
import org.junit.jupiter.api.Assertions.assertTrue
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ApplicationTest {
    @Test
    fun testApp() = runTest {
        testApplication {
            val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.0"))
            kafkaContainer.start()
            val producerProps = Properties().apply {
                put("bootstrap.servers", kafkaContainer.bootstrapServers)
                put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            }

            val testProducer = KafkaProducer<String, String>(producerProps)
            testProducer.send(ProducerRecord(Topics.AVTALE_HENDELSE, "1", enMelding().trimMargin()))

            val schema = JSONSchema.parseFile("src/test/resources/schema.yml")
            val kasseringSchema = JSONSchema.parseFile("src/test/resources/schema-kassering.yml")
            // Testoppsett
            val consumer: Consumer<String, String> = KafkaConsumer(testConsumerConfig(kafkaContainer.bootstrapServers))
            val feilConsumer: Consumer<String, String> = KafkaConsumer(testConsumerConfig(kafkaContainer.bootstrapServers))
            //val producer: Producer<String, String> = KafkaProducer(testProducerConfig())
            val database = Database(testDataSource)
            val aktivitetsplanProducer = AktivitetsplanProducer(testProducer, database, schema, kasseringSchema)
            val avtaleHendelseConsumer = AvtaleHendelseConsumer(consumer, aktivitetsplanProducer, database)
            val aktivitetsplanFeilConsumer = FeilConsumer(feilConsumer, database)

            val scope = CoroutineScope(Dispatchers.Default)
            val app = App(avtaleHendelseConsumer, aktivitetsplanFeilConsumer,scope)
            Server.createWebServer().start()

            val result = withTimeoutOrNull(5000) { // Timeout after 8000 milliseconds (8 seconds)
                scope.launch {
                    app.start()
                }
            }

            if (result == null) {
                println("App did not start within the specified time")
                scope.cancel()
            }

            delay(1000)
            testProducer.close()
            val dataBehandletOgLagret: List<AktivitetsplanMeldingEntitet>? = database.hentEntitetMedAvtaleId(UUID.fromString("66276156-9bc6-11ed-a8fc-0242ac120002"))
            kafkaContainer.close()
            assertNotEquals(0,dataBehandletOgLagret?.size)
        }
    }
    fun enMelding():String{
       return "{\n" +
                "        \"hendelseType\": \"OPPRETTET\",\n" +
                "        \"avtaleStatus\": \"GJENNOMFØRES\",\n" +
                "        \"deltakerFnr\": \"00000000000\",\n" +
                "        \"mentorFnr\": null,\n" +
                "        \"bedriftNr\": \"999999999\",\n" +
                "        \"veilederNavIdent\": \"Z123456\",\n" +
                "        \"tiltakstype\": \"MIDLERTIDIG_LONNSTILSKUDD\",\n" +
                "        \"opprettetTidspunkt\": \"2022-11-01T15:24:31.761508\",\n" +
                "        \"avtaleId\": \"66276156-9bc6-11ed-a8fc-0242ac120002\",\n" +
                "        \"avtaleNr\": 12,\n" +
                "        \"sistEndret\": \"2022-11-02T08:36:56.866170256Z\",\n" +
                "        \"annullertTidspunkt\": null,\n" +
                "        \"annullertGrunn\": null,\n" +
                "        \"slettemerket\": false,\n" +
                "        \"opprettetAvArbeidsgiver\": false,\n" +
                "        \"enhetGeografisk\": null,\n" +
                "        \"enhetsnavnGeografisk\": null,\n" +
                "        \"enhetOppfolging\": \"0906\",\n" +
                "        \"enhetsnavnOppfolging\": \"Oslo gamlebyen\",\n" +
                "        \"godkjentForEtterregistrering\": false,\n" +
                "        \"kvalifiseringsgruppe\": \"BFORM\",\n" +
                "        \"formidlingsgruppe\": null,\n" +
                "        \"feilregistrert\": false,\n" +
                "        \"versjon\": 4,\n" +
                "        \"deltakerFornavn\": \"Geir\",\n" +
                "        \"deltakerEtternavn\": \"Geirsen\",\n" +
                "        \"deltakerTlf\": \"40000000\",\n" +
                "        \"bedriftNavn\": \"Pers butikk\",\n" +
                "        \"arbeidsgiverFornavn\": \"Per\",\n" +
                "        \"arbeidsgiverEtternavn\": \"Kremmer\",\n" +
                "        \"arbeidsgiverTlf\": \"99999999\",\n" +
                "        \"veilederFornavn\": \"Vera\",\n" +
                "        \"veilederEtternavn\": \"Veileder\",\n" +
                "        \"veilederTlf\": \"44444444\",\n" +
                "        \"oppfolging\": \"Telefon hver uke\",\n" +
                "        \"tilrettelegging\": \"Ingen\",\n" +
                "        \"startDato\": \"2022-08-01\",\n" +
                "        \"sluttDato\": \"2022-12-01\",\n" +
                "        \"stillingprosent\": 50,\n" +
                "        \"journalpostId\": null,\n" +
                "        \"arbeidsoppgaver\": \"Woop\",\n" +
                "        \"stillingstittel\": \"Butikkbetjent\",\n" +
                "        \"stillingStyrk08\": 5223,\n" +
                "        \"stillingKonseptId\": 112968,\n" +
                "        \"antallDagerPerUke\": 5,\n" +
                "        \"refusjonKontaktperson\": {\n" +
                "            \"refusjonKontaktpersonFornavn\": \"Ola\",\n" +
                "            \"refusjonKontaktpersonEtternavn\": \"Olsen\",\n" +
                "            \"refusjonKontaktpersonTlf\": \"12345678\",\n" +
                "            \"ønskerVarslingOmRefusjon\": true\n" +
                "        },\n" +
                "        \"mentorFornavn\": null,\n" +
                "        \"mentorEtternavn\": null,\n" +
                "        \"mentorOppgaver\": null,\n" +
                "        \"mentorAntallTimer\": null,\n" +
                "        \"mentorTimelonn\": null,\n" +
                "        \"mentorTlf\": null,\n" +
                "        \"arbeidsgiverKontonummer\": \"22222222222\",\n" +
                "        \"lonnstilskuddProsent\": 60,\n" +
                "        \"manedslonn\": 20000,\n" +
                "        \"feriepengesats\": 0.12,\n" +
                "        \"arbeidsgiveravgift\": 0.141,\n" +
                "        \"harFamilietilknytning\": true,\n" +
                "        \"familietilknytningForklaring\": \"En middels god forklaring\",\n" +
                "        \"feriepengerBelop\": 10000,\n" +
                "        \"otpSats\": 0.02,\n" +
                "        \"otpBelop\": 400,\n" +
                "        \"arbeidsgiveravgiftBelop\": 20400,\n" +
                "        \"sumLonnsutgifter\": 40800,\n" +
                "        \"sumLonnstilskudd\": 16320,\n" +
                "        \"manedslonn100pst\": 81600,\n" +
                "        \"sumLønnstilskuddRedusert\": 12240,\n" +
                "        \"datoForRedusertProsent\": \"2023-05-01\",\n" +
                "        \"stillingstype\": \"FAST\",\n" +
                "        \"maal\": [],\n" +
                "        \"inkluderingstilskuddsutgift\": [],\n" +
                "        \"inkluderingstilskuddBegrunnelse\": null,\n" +
                "        \"inkluderingstilskuddTotalBeløp\": 0,\n" +
                "        \"godkjentAvDeltaker\": \"2022-11-01T15:24:31.762177\",\n" +
                "        \"godkjentTaushetserklæringAvMentor\": null,\n" +
                "        \"godkjentAvArbeidsgiver\": \"2022-11-01T15:24:31.762167\",\n" +
                "        \"godkjentAvVeileder\": \"2022-11-01T15:24:31.762207\",\n" +
                "        \"godkjentAvBeslutter\": null,\n" +
                "        \"avtaleInngått\": \"2022-11-01T15:24:31.762214\",\n" +
                "        \"ikrafttredelsestidspunkt\": \"2022-11-02T09:36:56.866113999\",\n" +
                "        \"godkjentAvNavIdent\": \"Q987654\",\n" +
                "        \"godkjentAvBeslutterNavIdent\": null,\n" +
                "        \"enhetKostnadssted\": null,\n" +
                "        \"enhetsnavnKostnadssted\": null,\n" +
                "        \"godkjentPaVegneGrunn\": null,\n" +
                "        \"godkjentPaVegneAv\": false,\n" +
                "        \"godkjentPaVegneAvArbeidsgiverGrunn\": null,\n" +
                "        \"godkjentPaVegneAvArbeidsgiver\": false,\n" +
                "        \"innholdType\": \"ENDRE_STILLING\",\n" +
                "        \"utførtAv\": \"Z123456\",\n" +
                "        \"utførtAvRolle\": \"VEILEDER\"\n" +
                "    }"
    }
}