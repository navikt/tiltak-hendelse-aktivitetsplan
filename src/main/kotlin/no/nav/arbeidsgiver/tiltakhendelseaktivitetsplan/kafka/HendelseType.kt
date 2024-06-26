package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class HendelseType(val tekst: String, val skalTilAktivitetsplan: Boolean) {

    // Skal med
    OPPRETTET("Avtale er opprettet av veileder", true),
    ENDRET("Avtale endret", true),
    GODKJENT_AV_VEILEDER("Avtale er godkjent av veileder", true),
    OPPRETTET_AV_ARBEIDSGIVER("Avtale er opprettet av arbeidsgiver", true),
    AVTALE_INNGÅTT("Avtale godkjent av NAV", true),
    AVTALE_FORKORTET("Avtale forkortet", true),
    AVTALE_FORLENGET("Avtale forlenget av veileder", true),
    STATUSENDRING("Statusendring", true),

    // Skal ikke med
    GODKJENT_AV_ARBEIDSGIVER("Avtale er godkjent av arbeidsgiver", false),
    GODKJENT_AV_DELTAKER("Avtale er godkjent av deltaker", false),
    SIGNERT_AV_MENTOR("Mentor har signert taushetserklæring", false),
    GODKJENT_PAA_VEGNE_AV("Veileder godkjente avtalen på vegne av seg selv og deltaker", false),
    GODKJENT_PAA_VEGNE_AV_DELTAKER_OG_ARBEIDSGIVER("Veileder godkjente avtalen på vegne av seg selv, deltaker og arbeidsgiver", false),
    GODKJENT_PAA_VEGNE_AV_ARBEIDSGIVER("Veileder godkjente avtalen på vegne av seg selv og arbeidsgiver", false),
    GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER("Avtalens godkjenninger er opphevet av arbeidsgiver", false),
    GODKJENNINGER_OPPHEVET_AV_VEILEDER("Avtalens godkjenninger er opphevet av veileder", false),
    DELT_MED_DELTAKER("Avtale delt med deltaker", false),
    DELT_MED_ARBEIDSGIVER("Avtale delt med arbeidsgiver", false),
    DELT_MED_MENTOR("Avtale delt med mentor", false),
    AVBRUTT("Avtale avbrutt av veileder", false),
    ANNULLERT("Avtale annullert av veileder", true),
    LÅST_OPP("Avtale låst opp av veileder", false),
    GJENOPPRETTET("Avtale gjenopprettet", false),
    NY_VEILEDER("Avtale tildelt ny veileder", false),
    AVTALE_FORDELT("Avtale tildelt veileder", false),
    TILSKUDDSPERIODE_AVSLATT("Tilskuddsperiode har blitt sendt i retur av ", false),
    TILSKUDDSPERIODE_GODKJENT("Tilskuddsperiode har blitt godkjent av beslutter", false),
    MÅL_ENDRET("Mål endret av veileder", false),
    INKLUDERINGSTILSKUDD_ENDRET("Inkluderingstilskudd endret av veileder", false),
    OM_MENTOR_ENDRET("Om mentor endret av veileder", false),
    TILSKUDDSBEREGNING_ENDRET("Tilskuddsberegning endret av veileder", false),
    KONTAKTINFORMASJON_ENDRET("Kontaktinformasjon endret av veileder", false),
    STILLINGSBESKRIVELSE_ENDRET("Stillingsbeskrivelse endret av veileder" , false),
    OPPFØLGING_OG_TILRETTELEGGING_ENDRET("Oppfølging og tilrettelegging endret av veileder", false),
    REFUSJON_KLAR("Refusjon klar", false),
    REFUSJON_KLAR_REVARSEL("Refusjon klar, revarsel", false),
    REFUSJON_FRIST_FORLENGET("Frist for refusjon forlenget", false),
    REFUSJON_KORRIGERT("Refusjon korrigert", false),
    VARSLER_SETT("Varsler lest", false),
    AVTALE_SLETTET("Avtale slettet av veileder", false),
    GODKJENT_FOR_ETTERREGISTRERING("Avtale er godkjent for etterregistrering", false),
    FJERNET_ETTERREGISTRERING("Fjernet etterregistrering på avtale", false),
    DELTAKERS_GODKJENNING_OPPHEVET_AV_VEILEDER("Deltakers godkjenning opphevet av veileder", false),
    DELTAKERS_GODKJENNING_OPPHEVET_AV_ARBEIDSGIVER("Deltakers godkjenning opphevet av arbeidsgiver", false),
    ARBEIDSGIVERS_GODKJENNING_OPPHEVET_AV_VEILEDER("Arbeidsgivers godkjenning opphevet av veileder", false)
}