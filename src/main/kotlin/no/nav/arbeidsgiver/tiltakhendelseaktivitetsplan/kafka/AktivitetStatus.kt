package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

enum class AktivitetStatus {
    PLANLAGT,
    GJENNOMFORES,
    FULLFORT,
    AVBRUTT;

    companion object {
        fun parse(avtaleStatus: AvtaleStatus): AktivitetStatus {
            return when (avtaleStatus) {
                AvtaleStatus.PÅBEGYNT -> PLANLAGT
                AvtaleStatus.MANGLER_GODKJENNING -> PLANLAGT
                AvtaleStatus.KLAR_FOR_OPPSTART -> PLANLAGT
                AvtaleStatus.GJENNOMFØRES -> GJENNOMFORES
                AvtaleStatus.AVSLUTTET -> FULLFORT
                AvtaleStatus.ANNULLERT -> AVBRUTT
                AvtaleStatus.AVBRUTT -> AVBRUTT
            }
        }
    }
}
