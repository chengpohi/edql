import sbt.Keys._
import sbt._
import sbt.internal.DslEntry
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

object Settings {
  lazy val commonSettings = Seq(
    version := "6.x-SNAPSHOT",
    organization := "com.github.chengpohi",
    scalaVersion := "2.12.1",
    resolvers += Resolver.mavenLocal,
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    test in assembly := {}
  )

  lazy val rootProjectSettings = Seq(
    name := "elasticdsl",
    unmanagedBase := baseDirectory.value / "lib",
    scalacOptions in compile ++= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    testOptions in Test += Tests.Cleanup(() => println("Cleanup")),
    mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl"),
    libraryDependencies ++= dependencies
  )

  val dependencies = Seq(
    "com.typesafe" % "config" % "1.3.0",
    "jline" % "jline" % "2.12",
    "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7",
    "org.apache.logging.log4j" % "log4j-api" % "2.7",
    "org.apache.logging.log4j" % "log4j-core" % "2.7",
    "org.elasticsearch.client" % "transport" % "6.1.2",
    "org.apache.commons" % "commons-lang3" % "3.5",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalactic" %% "scalactic" % "3.0.1",
    "com.lihaoyi" %% "fastparse" % "0.4.2",
    "org.json4s" %% "json4s-native" % "3.5.0",
    "org.json4s" %% "json4s-jackson" % "3.5.0",
    "org.scalaz" %% "scalaz-core" % "7.3.0-M9"
  )

  implicit class ElasticPlugin(pro: Project) {
    def toElasticPlugin(pluginName: String): Project = {
      val plugin = project
        .in(file("modules/dsl-plugin"))
        .settings(commonSettings: _*)
        .settings(
          assemblyJarName in assembly := pluginName,
          libraryDependencies ++= Seq(
            "org.elasticsearch.client" % "transport" % "6.1.2" % "provided",
            "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7" % "provided",
            "org.apache.logging.log4j" % "log4j-api" % "2.7" % "provided",
            "org.apache.logging.log4j" % "log4j-core" % "2.7" % "provided"
          )
        )
        .settings(projectDependencies := {
          Seq(
            (projectID in pro).value.exclude("org.elasticsearch.client",
              "transport"),
            (projectID in pro).value.exclude("org.apache.logging.log4j",
              "log4j-1.2-api"),
            (projectID in pro).value.exclude("org.apache.logging.log4j",
              "log4j-api"),
            (projectID in pro).value.exclude("org.apache.logging.log4j",
              "log4j-core")
          )
        })
        .dependsOn(pro)
      plugin
    }

  }

}
