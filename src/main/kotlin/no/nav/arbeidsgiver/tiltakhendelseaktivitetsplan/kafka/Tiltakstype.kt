package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class Tiltakstype(val beskrivelse: String) {
    ARBEIDSTRENING("Arbeidstrening"),
    MIDLERTIDIG_LONNSTILSKUDD("Midlertidig lønnstilskudd"),
    VARIG_LONNSTILSKUDD("Varig lønnstilskudd"),
    MENTOR("Mentor"),
    INKLUDERINGSTILSKUDD("Inkluderingstilskudd"),
    SOMMERJOBB("Sommerjobb");
}