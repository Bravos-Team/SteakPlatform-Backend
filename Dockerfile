FROM maven:3.9.9-amazoncorretto-21-alpine
LABEL authors="bravos"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM azul/zulu-openjdk-alpine:21-jre-headless-latest

WORKDIR /app
COPY --from=build /app/target/*.jar steak.jar
COPY prod.env .env

ENTRYPOINT ["java", "-Xms1g", "-Xmx1g", "-XX:+UseG1GC", "-jar", "steak.jar", "--spring.profiles.active=prod"]

