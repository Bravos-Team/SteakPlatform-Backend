plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bravos"
version = "1.0.0"
description = "steak"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    implementation(libs.org.springframework.boot.spring.boot.starter.data.mongodb)
    implementation(libs.org.springframework.boot.spring.boot.starter.data.redis)
    implementation(libs.org.springframework.boot.spring.boot.starter.validation)
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.configuration.processor)
    implementation(libs.com.google.guava.guava)
    implementation(libs.io.github.cdimascio.dotenv.java)
    implementation(libs.org.springframework.boot.spring.boot.starter.security)
    implementation(libs.org.springframework.boot.spring.boot.starter.webflux)
    implementation(libs.software.amazon.awssdk.s3)
    implementation(libs.software.amazon.awssdk.cloudfront)
    implementation(libs.io.hypersistence.hypersistence.utils.hibernate.v63)
    implementation(libs.com.azure.azure.identity)
    implementation(libs.com.azure.azure.security.keyvault.secrets)
    implementation(libs.io.projectreactor.netty.reactor.netty.http)
    implementation(libs.io.netty.netty.codec.http2)
    implementation(libs.org.springframework.boot.spring.boot.starter.websocket)
    implementation(libs.com.github.scribejava.scribejava.apis)
    runtimeOnly(libs.org.postgresql.postgresql)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("ch.qos.logback:logback-core:1.5.21")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
