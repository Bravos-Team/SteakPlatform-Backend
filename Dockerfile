FROM maven:3.9.9-amazoncorretto-21-alpine AS build
LABEL authors="bravos"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests && rm -rf /root/.m2

FROM azul/zulu-openjdk-alpine:21-jre-headless-latest AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar steak.jar
COPY prod.env .env

ENTRYPOINT ["java", "-Xms5g", "-Xmx5g", "-XX:+UseZGC", "-XX:+ZGenerational", "-XX:+UseContainerSupport", "-Duser.timezone=GMT+7", "-jar", "steak.jar", "--spring.profiles.active=prod"]
