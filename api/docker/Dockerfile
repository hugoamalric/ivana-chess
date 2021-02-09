FROM openjdk:11-jre-slim

ARG version

ENV VERSION $version

WORKDIR /app
COPY ivana-chess-api-$VERSION-fatjar.jar ivana-chess-api.jar
ENTRYPOINT ["java", "-jar", "ivana-chess-api.jar"]
