name := "elasticshell"

version := "1.0"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.jsoup" % "jsoup" % "1.7.2",
  "jline" % "jline" % "2.12",
  "com.typesafe.akka" %% "akka-remote" % "2.3.8",
  "com.sksamuel.elastic4s" % "elastic4s-core_2.11" % "1.7.4",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.4.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.2",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "com.lihaoyi" % "fastparse_sjs0.6_2.11" % "0.3.3"
)

