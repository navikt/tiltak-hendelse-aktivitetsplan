package no.nav.arbeidsgiver.tiltakhendelseaktivitetsplan.database

import kotliquery.HikariCP

val DB_HOST = System.getenv("DB_HOST")
val DB_PORT = System.getenv("DB_PORT")
val DB_DATABASE = System.getenv("DB_DATABASE")
val DB_USERNAME = System.getenv("DB_USERNAME")
val DB_PASSWORD = System.getenv("DB_PASSWORD")

val dataSource = HikariCP.init(
    url = "jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}?user=${DB_USERNAME}&password=${DB_PASSWORD}",
    username = DB_USERNAME,
    password = DB_PASSWORD
) {
    maximumPoolSize = 4
}