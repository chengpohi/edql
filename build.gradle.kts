import org.gradle.internal.os.OperatingSystem

plugins {
    idea
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.grammarkit") version ("2021.2.2")
}

group = "com.github.chengpohi"
version = "7.X-SNAPSHOT"

allprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("maven-publish")
        plugin("java-library")
        plugin("idea")
        plugin("org.jetbrains.grammarkit")
    }

    java {
        withSourcesJar()
    }

    tasks.withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen", "src/main/java")
            kotlin.srcDirs("src/main/java")
            resources.srcDirs("src/main/resources")
        }
        test {
            java.srcDirs("src/test/java")
            kotlin.srcDirs("src/test/java")
            resources.srcDirs("src/test/resources")
        }
    }

    tasks {
        withType<Copy> {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}

dependencies {
    compileOnly("org.apache.commons:commons-lang3:3.5")
    compileOnly("org.apache.commons:commons-collections4:4.4")
    compileOnly("org.apache.logging.log4j:log4j-core:2.11.1")
    api("org.elasticsearch.client:elasticsearch-rest-client:8.7.1")
    api("com.jayway.jsonpath:json-path:2.6.0") {
        exclude("org.slf4j", "slf4j-api")
    }
    api("software.amazon.awssdk:auth:2.31.12")

    // Kotlin dependencies
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")
    // IntelliJ dependencies
    compileOnly("com.jetbrains.intellij.platform:lang-impl:243.26574.98")
    compileOnly("com.jetbrains.intellij.java:java-psi-impl:243.26574.91")
    compileOnly("com.jetbrains.intellij.platform:icons:243.26574.98")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.15.0")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.10.0")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            groupId = "com.github.chengpohi"
            artifactId = "edql-lib"
            version = rootProject.version.toString()
            from(components["java"])
            pom {
                name.set("edql lib")
                description.set("edql lib")
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
