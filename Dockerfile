FROM maven:3.9.9-amazoncorretto-21-alpine AS build
LABEL authors="bravos"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests && rm -rf /root/.m2

FROM azul/prime-ubuntu:21 AS runtime

WORKDIR /app
COPY --from=build /app/target/*.jar steak.jar
COPY prod.env .env

ENTRYPOINT ["java", "-Xms1g", "-Xmx1g", "-XX:+UseContainerSupport", "-jar", "steak.jar", "--spring.profiles.active=prod"]

