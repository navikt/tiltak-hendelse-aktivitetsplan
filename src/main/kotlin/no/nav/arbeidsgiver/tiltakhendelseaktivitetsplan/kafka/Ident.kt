package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

data class Ident(val ident: String, val identType: IdentType) {
    companion object {
        fun fra(melding: AvtaleHendelseMelding): Ident {
            val type = IdentType.parse(melding.utførtAvRolle)
            when (type) {
                IdentType.ARBEIDSGIVER -> return Ident(melding.bedriftNr, type)
                else -> return Ident(melding.utførtAv, type)
            }
        }
    }
}

enum class IdentType {
    NAVIDENT, ARENAIDENT, ARBEIDSGIVER, PERSONBRUKER, SYSTEM;

    companion object {
        fun parse(avtaleHendelseUtførtAvRolle: AvtaleHendelseUtførtAvRolle): IdentType {
            return when (avtaleHendelseUtførtAvRolle) {
                AvtaleHendelseUtførtAvRolle.VEILEDER -> NAVIDENT
                AvtaleHendelseUtførtAvRolle.BESLUTTER -> NAVIDENT
                AvtaleHendelseUtførtAvRolle.ARBEIDSGIVER -> ARBEIDSGIVER
                AvtaleHendelseUtførtAvRolle.MENTOR -> ARBEIDSGIVER
                AvtaleHendelseUtførtAvRolle.SYSTEM -> SYSTEM
                AvtaleHendelseUtførtAvRolle.DELTAKER -> PERSONBRUKER
            }
        }
    }
}
