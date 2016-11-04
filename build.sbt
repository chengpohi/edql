name := "elasticshell"

organization := "com.github.chengpohi"

version := "0.2.1-SNAPSHOT"

scalaVersion := "2.11.8"

unmanagedBase := baseDirectory.value / "lib"

resolvers += Resolver.mavenLocal

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl")

val elkDependencies = Seq(
  "org.apache.lucene" % "lucene-analyzers-common" % "6.2.0",
  "org.apache.lucene" % "lucene-backward-codecs" % "6.2.0",
  "org.apache.lucene" % "lucene-grouping" % "6.2.0",
  "org.apache.lucene" % "lucene-highlighter" % "6.2.0",
  "org.apache.lucene" % "lucene-join" % "6.2.0",
  "org.apache.lucene" % "lucene-memory" % "6.2.0",
  "org.apache.lucene" % "lucene-misc" % "6.2.0",
  "org.apache.lucene" % "lucene-queries" % "6.2.0",
  "org.apache.lucene" % "lucene-queryparser" % "6.2.0",
  "org.apache.lucene" % "lucene-sandbox" % "6.2.0",
  "org.apache.lucene" % "lucene-spatial" % "6.2.0",
  "org.apache.lucene" % "lucene-spatial-extras" % "6.2.0",
  "org.apache.lucene" % "lucene-spatial3d" % "6.2.0",
  "org.apache.lucene" % "lucene-suggest" % "6.2.0",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "io.netty" % "netty-transport" % "4.1.5.Final",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-smile" % "2.8.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.8.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.8.1"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.typesafe" % "config" % "1.3.0",
  "com.lihaoyi" %% "fastparse" % "0.4.1",
  "jline" % "jline" % "2.12",
  "org.scalaz" %% "scalaz-core" % "7.2.1",
  "org.scalaz" %% "scalaz-effect" % "7.2.1",
  "org.scalanlp" %% "breeze" % "0.12",
  "org.scalanlp" %% "breeze-viz" % "0.12",
  "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.elasticsearch.client" % "transport" % "5.0.0-rc1",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10"
)

libraryDependencies ++= elkDependencies


assemblyMergeStrategy in assembly := {
  case PathList("org", "joda", "time", "base", "BaseDateTime.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val compileScalaStyle = taskKey[Unit]("compileScalastyle")

compileScalaStyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(scalastyleConfig in Compile) := file("project/scalastyle-config.xml")
(scalastyleConfig in Test) := file("project/scalastyle-test-config.xml")

(compile in Compile) <<= (compile in Compile) dependsOn compileScalaStyle
(test in Test) <<= (test in Test) dependsOn compileScalaStyle
