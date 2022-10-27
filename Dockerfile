FROM navikt/java:17
COPY /target/tiltak-hendelse-aktivitetsplan-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
