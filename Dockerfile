FROM navikt/java:17
COPY /target/tiltak-hendelse-aktivitetsplan-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY /src/main/resources/schema.json schema.json
EXPOSE 8080
