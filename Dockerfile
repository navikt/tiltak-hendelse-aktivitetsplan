FROM ghcr.io/navikt/baseimages/temurin:17
COPY /target/tiltak-hendelse-aktivitetsplan-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY /src/main/resources/schema.yml schema.yml
COPY /src/main/resources/schema-kassering.yml schema-kassering.yml
EXPOSE 8080
