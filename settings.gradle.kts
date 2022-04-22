rootProject.name = "habit"

pluginManagement {
    plugins {
        id("pl.allegro.tech.build.axion-release") version "1.13.6"
        id("org.springframework.boot") version "2.6.6"
        id("io.freefair.lombok") version "6.4.2"
        id("com.github.node-gradle.node") version "3.2.1"
        id("com.google.cloud.tools.jib") version "3.2.1"
        id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
        id("com.gradle.plugin-publish") version "1.0.0-rc-1"
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val nitriteVersion = version("nitrite", "3.4.4")
            val dockerClientVersion = version("docker-client", "8.15.1")
            val dockerJavaClientVersion = version("docker-java-client", "3.2.7")
            val reactorVersion = version("reactor-core", "3.4.17")
            val jacksonVersion = version("jackson", "2.13.1")
            val logbackVersion = version("logback", "1.2.11")
            val slf4jApiVersion = version("slf4j", "1.7.36")
            val guavaVersion = version("guava", "31.1-jre")
            val okHttpVersion = version("ok-http", "4.9.3")
            val commonsIoVersion = version("commons-io", "2.11.0")
            val commonsCompressVersion = version("commons-compress", "1.21")
            val junitVersion = version("junit", "5.8.2")
            val assertjVersion = version("assertj", "3.22.0")
            val springdocVersion = version("springdoc", "1.6.7")
            val dependencyInjectionVersion = version("dependency-injection", "1")
            val mockitoVersion = version("mockito", "4.5.0")
            val nattyVersion = version("natty", "0.13")
            val jerseyVersion = version("jersey", "2.27")
            library(
                "spring-boot-configuration-processor",
                "org.springframework.boot",
                "spring-boot-configuration-processor"
            ).withoutVersion()
            library("spring-boot-webflux", "org.springframework.boot", "spring-boot-starter-webflux").withoutVersion()
            library("spring-boot-actuator", "org.springframework.boot", "spring-boot-starter-actuator").withoutVersion()
            library("springdoc", "org.springdoc", "springdoc-openapi-webflux-ui").versionRef(springdocVersion)
            library("ok-http-sse", "com.squareup.okhttp3", "okhttp-sse").versionRef(okHttpVersion)
            library("ok-http", "com.squareup.okhttp3", "okhttp").versionRef(okHttpVersion)
            library("guava", "com.google.guava", "guava").versionRef(guavaVersion)
            library("reactor-core", "io.projectreactor", "reactor-core").versionRef(reactorVersion)
            library("reactor-test", "io.projectreactor", "reactor-test").versionRef(reactorVersion)
            library("javax-inject", "javax.inject", "javax.inject").versionRef(dependencyInjectionVersion)
            library("mockito", "org.mockito", "mockito-junit-jupiter").versionRef(mockitoVersion)
            library("natty", "com.joestelmach", "natty").versionRef(nattyVersion)
            library("docker-client", "com.spotify", "docker-client").versionRef(dockerClientVersion)
            library("docker-java-client", "com.github.docker-java", "docker-java").versionRef(dockerJavaClientVersion)
            library("nitrite", "org.dizitart", "nitrite").versionRef(nitriteVersion)
            library("jersey", "org.glassfish.jersey.inject", "jersey-hk2").versionRef(jerseyVersion)
            library("commons-compress", "org.apache.commons", "commons-compress").versionRef(commonsCompressVersion)
            library("commons-io", "commons-io", "commons-io").versionRef(commonsIoVersion)
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef(jacksonVersion)
            library(
                "jackson-jdk8",
                "com.fasterxml.jackson.datatype",
                "jackson-datatype-jdk8"
            ).versionRef(jacksonVersion)
            library("jackson-jsr310", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310").versionRef(
                jacksonVersion
            )
            library("logback-classic", "ch.qos.logback", "logback-classic").versionRef(logbackVersion)
            library("logback-core", "ch.qos.logback", "logback-core").versionRef(logbackVersion)
            library("logback-access", "ch.qos.logback", "logback-access").versionRef(logbackVersion)
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef(slf4jApiVersion)
            library("slf4j-jcl", "org.slf4j", "jcl-over-slf4j").versionRef(slf4jApiVersion)
            library("slf4j-log4j", "org.slf4j", "log4j-over-slf4j").versionRef(slf4jApiVersion)
            library("assertj", "org.assertj", "assertj-core").versionRef(assertjVersion)
            library("reactor-test", "io.projectreactor", "reactor-test").versionRef(reactorVersion)
            library("junit-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(junitVersion)
            library("junit-params", "org.junit.jupiter", "junit-jupiter-params").versionRef(junitVersion)
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(junitVersion)
            library("spring-boot-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            bundle("jackson", listOf("jackson-databind", "jackson-jdk8", "jackson-jsr310"))
            bundle(
                "logging",
                listOf("slf4j-api", "slf4j-jcl", "slf4j-log4j", "logback-classic", "logback-access", "logback-core")
            )
            bundle("testing", listOf("junit-api", "junit-params", "junit-engine", "assertj"))
        }
    }
}

include(
    "docs-site",
    "server",
    "server-ui",
    "client",
    "dev",
    "front-proxy",
    "environment-front-proxy",
    "request-graph-server",
    "junit-plugin",
    "gradle-plugin",
    "request-persister",
    "mock-server"
)
