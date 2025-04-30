FROM debian:latest
LABEL authors="bravos"

RUN apt-get update && apt-get install -y wget dpkg maven

RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb && \
    dpkg -i jdk-21_linux-x64_bin.deb && \
    rm jdk-21_linux-x64_bin.deb

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests

COPY target/steak-0.0.1-SNAPSHOT.jar /app/steak.jar
COPY .env /app/.env

CMD ["java","-jar","app/steak.jar"]

