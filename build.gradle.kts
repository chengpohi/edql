import org.gradle.internal.os.OperatingSystem

plugins {
    idea
    scala
    `java-library`
    `maven-publish`
    id("org.jetbrains.grammarkit") version ("2021.2.2")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.github.chengpohi"
version = "7.X-SNAPSHOT"


allprojects {
    apply {
        plugin("idea")
        plugin("scala")
        plugin("maven-publish")
        plugin("java-library")
        plugin("org.jetbrains.grammarkit")
        plugin("org.jetbrains.intellij")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://cache-redirector.jetbrains.com/repo.maven.apache.org/maven2")
        maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/main/gen"))
        }
    }

    if (OperatingSystem.current().isWindows()) {
        intellij {
            localPath.set("C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2022.1.1\\")
        }
    } else {
        intellij {
            //version '2020.3.2'
            localPath.set("/Users/xiang/Library/Application Support/JetBrains/Toolbox/apps/IDEA-C/ch-1/221.6008.13/IntelliJ IDEA CE.app/")
        }
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
            resources.srcDirs("src/main/resources")
        }
        test {
            resources.srcDirs("src/test/resources")
        }
    }

    tasks.withType<ScalaCompile>().configureEach {
        scalaCompileOptions.forkOptions.apply {
            memoryMaximumSize = "1g"
            jvmArgs = listOf(
                    "-XX:MaxMetaspaceSize=512m"
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
}

project(":") {
    dependencies {
        api("com.typesafe:config:1.3.0")
        api("org.apache.commons:commons-lang3:3.5")
        api("org.scalactic:scalactic_2.13:3.2.3")
        api("org.json4s:json4s-jackson_2.13:3.7.0-M1")
        api("org.apache.logging.log4j:log4j-core:2.11.1")
        api("org.elasticsearch.client:elasticsearch-rest-client:7.13.0")
        api("com.jayway.jsonpath:json-path:2.6.0") {
            exclude("org.slf4j", "slf4j-api")
        }
        api("com.amazonaws:aws-java-sdk-core:1.12.141")
        testImplementation("org.scalatest:scalatest_2.13:3.2.4")
    }
}

project(":modules:core") {
    version = rootProject.version.toString()
    dependencies {
        implementation(project(":"))
    }

    publishing {
        publications {
            create<MavenPublication>("edqlCore") {
                groupId = "com.github.chengpohi"
                artifactId = "edql-core"
                version = rootProject.version.toString()
                from(components["java"])

                pom {
                    name.set("edql core")
                    description.set("edql core")
                }
            }
        }
        repositories {
            mavenLocal()
        }
    }

}


project(":modules:repl") {
    version = rootProject.version.toString()
    dependencies {
        implementation(project(":"))
        implementation(project(":modules:core"))
        implementation(project(":modules:script"))
        implementation("jline:jline:2.12")
        implementation("org.typelevel:cats-core_2.13:2.0.0")
        implementation("org.typelevel:cats-effect_2.13:2.0.0")
        implementation("org.scala-lang:scala-library:2.13.10")
    }

}

project(":modules:script") {
    version = rootProject.version.toString()
    dependencies {
        api(project(":"))
        api(project(":modules:core"))
        implementation("org.scala-lang:scala-library:2.13.10")
    }


    publishing {
        publications {
            create<MavenPublication>("edqlScript") {
                groupId = "com.github.chengpohi"
                artifactId = "edql-script"
                version = rootProject.version.toString()
                from(components["java"])

                pom {
                    name.set("edql script")
                    description.set("edql script")
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
    }
    repositories {
        mavenLocal()
    }
}
