package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class AktivitetTiltakstype {
    MIDLERTIDIG_LONNSTILSKUDD,
    VARIG_LONNSTILSKUDD,
    ARBEIDSTRENING,
    VARIG_TILRETTELAGT_ARBEID_I_ORDINÆR_VIRKSOMHET;

    companion object {
        fun parse(tiltakstype: Tiltakstype): AktivitetTiltakstype {
            return when (tiltakstype) {
                Tiltakstype.MIDLERTIDIG_LONNSTILSKUDD -> MIDLERTIDIG_LONNSTILSKUDD
                Tiltakstype.VARIG_LONNSTILSKUDD -> VARIG_LONNSTILSKUDD
                Tiltakstype.ARBEIDSTRENING -> ARBEIDSTRENING
                Tiltakstype.VTAO -> VARIG_TILRETTELAGT_ARBEID_I_ORDINÆR_VIRKSOMHET
                else -> throw IllegalArgumentException("Ukjent tiltakstype: $tiltakstype")
            }
        }

    }
}
