FROM maven:3.9.9-amazoncorretto-21 as build
LABEL authors="bravos"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests

FROM amazoncorretto:21.0.7 as runtime

WORKDIR /app
COPY --from=build /app/target/*.jar steak.jar
COPY private.pem .
COPY public.pem .
COPY prod.env .env

ENTRYPOINT ["java","-jar","steak.jar"]


