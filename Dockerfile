FROM ghcr.io/navikt/baseimages/temurin:21
COPY /target/tiltak-hendelse-aktivitetsplan-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY /src/main/resources/schema.yml schema.yml
COPY /src/main/resources/schema-kassering.yml schema-kassering.yml
COPY /src/main/resources/application.conf application.conf
