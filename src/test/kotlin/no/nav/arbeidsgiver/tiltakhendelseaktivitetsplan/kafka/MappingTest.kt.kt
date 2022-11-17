package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.pwall.json.schema.JSONSchema
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.Oppgave
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan.OppgaveLenke
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.util.*

class MappingTest {

    val meldingJson = "{\n" +
            "        \"hendelseType\": \"STILLINGSBESKRIVELSE_ENDRET\",\n" +
            "        \"avtaleStatus\": \"GJENNOMFØRES\",\n" +
            "        \"deltakerFnr\": \"00000000000\",\n" +
            "        \"mentorFnr\": null,\n" +
            "        \"bedriftNr\": \"999999999\",\n" +
            "        \"veilederNavIdent\": \"Z123456\",\n" +
            "        \"tiltakstype\": \"MIDLERTIDIG_LONNSTILSKUDD\",\n" +
            "        \"opprettetTidspunkt\": \"2022-11-01T15:24:31.761508\",\n" +
            "        \"avtaleId\": \"9f32e775-51d4-47af-87ac-5374cd250614\",\n" +
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

    @Test
    fun json_melding_skall_kunne_deserialiseres() {
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.registerModule(JavaTimeModule())
        val melding: AvtaleHendelseMelding = mapper.readValue(meldingJson)
    }

    @Test
    fun melding_til_aktivitetsplan_skal_serialiseres() {
        val aktivitetsKort = AktivitetsKort(
            id = UUID.randomUUID(),
            personIdent = "12345678901",
            startDato = LocalDate.now(),
            sluttDato = LocalDate.now(),
            tittel = "En tittel",
           // "En beskrivelse",
            aktivitetStatus = AktivitetStatus.FULLFORT,
            endretAv = Ident("Z123456", IdentType.NAVIDENT),
            endretTidspunkt = Instant.now(),
            avtaltMedNav = true,
            oppgave = Oppgave(ekstern = OppgaveLenke("", "", ""), intern = OppgaveLenke("", "", ""))
            //"Hepp"
        )

        val aktivitetsplanMelding = AktivitetsplanMelding.fromAktivitetskort(UUID.randomUUID(), "TEAM_TILTAK", "UPSERT_AKTIVITETSKORT_V1", "MIDL_LONNSTILSK", aktivitetsKort)

        val schema = JSONSchema.parseFile("src/test/resources/schema.json")


        val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val json = mapper.writeValueAsString(aktivitetsplanMelding)
        val output = schema.validateBasic(json)
        output.errors?.forEach {
            println("${it.error} - ${it.instanceLocation}")
        }
        require(schema.validate(json))
        println(json)

    }
}