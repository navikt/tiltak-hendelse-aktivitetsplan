FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21

COPY target/tiltak-hendelse-aktivitetsplan-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY src/main/resources/schema.yml schema.yml
COPY src/main/resources/schema-kassering.yml schema-kassering.yml
COPY src/main/resources/application.conf application.conf

ENV TZ="Europe/Oslo"
EXPOSE 8080

CMD ["-jar", "app.jar"]
