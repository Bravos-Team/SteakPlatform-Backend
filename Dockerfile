FROM maven:3.9.9-amazoncorretto-21-alpine AS build
LABEL authors="bravos"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM azul/zulu-openjdk-alpine:21-jre-headless-latest AS runtime

WORKDIR /app
COPY --from=build /app/target/*.jar steak.jar
COPY prod.env .env

ENTRYPOINT ["java", "-Xms1g", "-Xmx1g", "-jar", "steak.jar", "--spring.profiles.active=prod"]

