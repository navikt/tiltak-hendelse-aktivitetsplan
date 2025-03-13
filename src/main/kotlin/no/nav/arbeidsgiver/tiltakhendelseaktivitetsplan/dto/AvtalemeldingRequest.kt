package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AktivitetsplanId
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AvtaleId

data class AvtalemeldingRequest(
    @JsonDeserialize(using = AvtaleId.Deserializer::class)
    val avtaleId: AvtaleId,
    @JsonDeserialize(using = AktivitetsplanId.Deserializer::class)
    val aktivitetsplanId: AktivitetsplanId,
    val resendSisteMelding: Boolean = false,
)
