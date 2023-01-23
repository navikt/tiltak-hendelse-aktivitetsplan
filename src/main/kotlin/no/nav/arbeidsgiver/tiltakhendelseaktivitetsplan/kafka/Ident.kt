package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

data class Ident(val ident: String, val identType: IdentType)

enum class IdentType {
    NAVIDENT, ARENAIDENT, ARBEIDSGIVER, PERSONBRUKER, SYSTEM
}
