package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class Tiltakstype(val beskrivelse: String, val skalTilAktivitetsplan: Boolean) {
    ARBEIDSTRENING("Arbeidstrening", true),
    FIREARIG_LONNSTILSKUDD("Fireårig lønnstilskudd for unge", true),
    INKLUDERINGSTILSKUDD("Inkluderingstilskudd", false),
    MENTOR("Mentor", true),
    MIDLERTIDIG_LONNSTILSKUDD("Midlertidig lønnstilskudd", true),
    SOMMERJOBB("Sommerjobb", true),
    VARIG_LONNSTILSKUDD("Varig lønnstilskudd", true),
    VTAO("Varig tilrettelagt arbeid i ordinær virksomhet", true)
}
