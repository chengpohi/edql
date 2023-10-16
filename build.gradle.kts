plugins {
    idea
    scala
    `java-library`
    `maven-publish`
    id("org.jetbrains.grammarkit") version ("2021.2.2")
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.github.chengpohi"
version = "7.X-SNAPSHOT"

allprojects {
    apply {
        plugin("scala")
        plugin("maven-publish")
        plugin("java-library")
        plugin("idea")
        plugin("org.jetbrains.grammarkit")
        plugin("org.jetbrains.intellij")
    }

    scala {
        zincVersion.set("1.8.0")
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
            resources.srcDirs("src/main/resources")
        }
        test {
            resources.srcDirs("src/test/resources")
        }
    }


    tasks.withType<ScalaCompile>().configureEach {
        scalaCompileOptions.forkOptions.apply {
            memoryMaximumSize = "8g"
            jvmArgs = listOf(
                    "-XX:MaxMetaspaceSize=4g",
                    "-Xss8M"
            )
        }
        scalaCompileOptions.additionalParameters = listOf(
                "-Ywarn-unused",
                "-feature",
                "-language:implicitConversions",
                "-language:higherKinds",
                "-language:postfixOps",
        )
    }

    tasks {
        withType<Copy> {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    intellij {
        version.set("2022.1.1")
    }
}

project(":") {
    dependencies {
        api("com.typesafe:config:1.3.0")
        api("org.apache.commons:commons-lang3:3.5")
        api("org.scala-lang:scala-reflect:2.13.10")
        api("org.json4s:json4s-jackson_2.13:3.7.0-M1")
        api("org.apache.logging.log4j:log4j-core:2.11.1")
        api("org.elasticsearch.client:elasticsearch-rest-client:8.7.1")
        api("com.jayway.jsonpath:json-path:2.6.0") {
            exclude("org.slf4j", "slf4j-api")
        }
        api("com.amazonaws:aws-java-sdk-core:1.12.141")
        testImplementation("org.scalatest:scalatest_2.13:3.2.4")
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
}

publishing {
    publications {
        create<MavenPublication>("edql") {
            groupId = "com.github.chengpohi"
            artifactId = "edql"
            version = rootProject.version.toString()
            from(components["java"])

            pom {
                name.set("edql")
                description.set("edql")
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
