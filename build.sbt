name := "elasticshell"

organization := "com.github.chengpohi"

version := "1.0"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

mainClass in Compile := Some("com.github.chengpohi.ELKRepl")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-remote" % "2.3.8",
  "com.lihaoyi" %% "fastparse" % "0.3.4",
  "com.sksamuel.elastic4s" % "elastic4s-core_2.11" % "1.7.4",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10"
)

