package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class Tiltakstype(val beskrivelse: String, val skalTilAktivitetsplan: Boolean) {
    ARBEIDSTRENING("Arbeidstrening", true),
    MIDLERTIDIG_LONNSTILSKUDD("Midlertidig lønnstilskudd", true),
    VARIG_LONNSTILSKUDD("Varig lønnstilskudd", true),
    MENTOR("Mentor", false),
    INKLUDERINGSTILSKUDD("Inkluderingstilskudd", false),
    SOMMERJOBB("Sommerjobb", false),
    VTAO("Varig tilrettelagt arbeid i ordinær virksomhet", false);
}
