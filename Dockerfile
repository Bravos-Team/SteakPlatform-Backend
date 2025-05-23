FROM maven:3.9.9-amazoncorretto-21-alpine as build
LABEL authors="bravos"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21.0.7-al2023-headless as runtime

WORKDIR /app
COPY --from=build /app/target/*.jar steak.jar
COPY prod.env .env

ENTRYPOINT ["java", "-Xms1g", "-Xmx1g", "-XX:+UseG1GC", "-jar", "steak.jar", "--spring.profiles.active=prod"]

