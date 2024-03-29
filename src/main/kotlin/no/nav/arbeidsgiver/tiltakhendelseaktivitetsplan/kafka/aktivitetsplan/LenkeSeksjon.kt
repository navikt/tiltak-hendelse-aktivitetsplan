package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.aktivitetsplan

import java.net.URL

data class LenkeSeksjon(
    val tekst: String,
    val subtekst: String,
    val url: URL,
    val lenkeType: LenkeType
)

enum class LenkeType {
    EKSTERN,
    INTERN,
    FELLES
}
