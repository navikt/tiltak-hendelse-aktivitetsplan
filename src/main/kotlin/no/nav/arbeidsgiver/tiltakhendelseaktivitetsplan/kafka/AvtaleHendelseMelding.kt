package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

data class AvtaleHendelseMelding(
    val hendelseType: HendelseType,
    val tiltakstype: Tiltakstype,
    val avtaleStatus: AvtaleStatus,
    val startDato: LocalDate?,
    val sluttDato: LocalDate?,
    val bedriftNavn: String?,
    val bedriftNr: String,
    val stillingstittel: String?,
    val stillingprosent: Int?,
    val avtaleInngått: LocalDateTime?,
    val utførtAv: String,
    val utførtAvRolle: AvtaleHendelseUtførtAvRolle,
    val deltakerFnr: String,
    @JsonDeserialize(using = AvtaleId.Deserializer::class)
    val avtaleId: AvtaleId,
    val avtaleNr: Int,
    val sistEndret: Instant,
    val veilederNavIdent: String?,
    val annullertGrunn: String?,
    val antallDagerPerUke: Double?
)
