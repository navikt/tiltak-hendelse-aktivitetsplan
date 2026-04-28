package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan

import net.pwall.json.schema.JSONSchema
import java.io.File

class SchemaResources

fun loadAktivitetsplanSchema(): JSONSchema = loadSchemaFromClasspath("schema.yml")

fun loadKasseringSchema(): JSONSchema = loadSchemaFromClasspath("schema-kassering.yml")

fun loadSchemaFromClasspath(resourceName: String): JSONSchema = JSONSchema.parse(loadFromClasspath(resourceName))

fun loadFromClasspath(resourceName: String): File {
    val schemaUrl = SchemaResources::class.java.getResource("/$resourceName")
        ?: throw IllegalArgumentException("Schema resource '$resourceName' was not found on the classpath")

    return File(schemaUrl.toURI())
}
