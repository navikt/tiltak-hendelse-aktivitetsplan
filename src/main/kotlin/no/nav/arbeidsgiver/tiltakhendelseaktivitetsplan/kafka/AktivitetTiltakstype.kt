package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class AktivitetTiltakstype {
    ARBEIDSTRENING,
    FIREARIG_LONNSTILSKUDD,
    MENTOR,
    MIDLERTIDIG_LONNSTILSKUDD,
    SOMMERJOBB,
    VARIG_LONNSTILSKUDD,
    VARIG_TILRETTELAGT_ARBEID_I_ORDINAER_VIRKSOMHET;

    companion object {
        fun parse(tiltakstype: Tiltakstype): AktivitetTiltakstype {
            return when (tiltakstype) {
                Tiltakstype.ARBEIDSTRENING -> ARBEIDSTRENING
                Tiltakstype.FIREARIG_LONNSTILSKUDD -> FIREARIG_LONNSTILSKUDD
                Tiltakstype.MENTOR -> MENTOR
                Tiltakstype.MIDLERTIDIG_LONNSTILSKUDD -> MIDLERTIDIG_LONNSTILSKUDD
                Tiltakstype.SOMMERJOBB -> SOMMERJOBB
                Tiltakstype.VARIG_LONNSTILSKUDD -> VARIG_LONNSTILSKUDD
                Tiltakstype.VTAO -> VARIG_TILRETTELAGT_ARBEID_I_ORDINAER_VIRKSOMHET
                Tiltakstype.INKLUDERINGSTILSKUDD -> throw IllegalArgumentException("Tiltakstype har ingen tilhørende aktivitetskort-type: $tiltakstype")
            }
        }

    }
}
