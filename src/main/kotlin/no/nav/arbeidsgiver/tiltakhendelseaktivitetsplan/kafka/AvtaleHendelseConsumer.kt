package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration

class AvtaleHendelseConsumer(private val consumer: Consumer<String, String>) {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


    fun start() {
        log.info("Starter konsumering på topic: ${Topics.AVTALE_HENDELSE}")
        consumer.subscribe(listOf(Topics.AVTALE_HENDELSE))

        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
            records.isEmpty && continue
            consumer.commitAsync()
            records.forEach {
                val melding: AvtaleHendelseMelding = mapper.readValue(it.value())
            }
        }

    }

}