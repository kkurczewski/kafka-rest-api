FROM openjdk:11-jre-slim

ARG VERSION

LABEL version=${VERSION}

WORKDIR /app

COPY lib/ /app/lib

COPY *.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]