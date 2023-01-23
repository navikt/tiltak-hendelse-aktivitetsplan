FROM navikt/java:17
COPY /target/tiltak-hendelse-aktivitetsplan-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY /src/main/resources/schema.yml schema.yml
EXPOSE 8080
