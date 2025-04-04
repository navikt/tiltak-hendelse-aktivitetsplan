package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka.AktivitetsplanId

data class AvtalemeldingRequest(
    @JsonDeserialize(using = AktivitetsplanId.Deserializer::class)
    val aktivitetsplanId: AktivitetsplanId,
    val resendSisteMelding: Boolean = false,
)
