import sbt.Package.ManifestAttributes

name := "elasticshell"

organization := "com.github.chengpohi"

version := "0.2.2-SNAPSHOT"

scalaVersion := "2.11.8"

unmanagedBase := baseDirectory.value / "lib"

resolvers += Resolver.mavenLocal

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "com.typesafe" % "config" % "1.3.0",
  "com.lihaoyi" %% "fastparse" % "0.4.1",
  "jline" % "jline" % "2.12",
  "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.elasticsearch.client" % "transport" % "5.0.0",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "org.json4s" %% "json4s-jackson" % "3.2.10"
)

assemblyMergeStrategy in assembly := {
  case PathList("org", "joda", "time", "base", "BaseDateTime.class") => MergeStrategy.first
  case "META-INF/io.netty.versions.properties" => MergeStrategy.first
  case PathList("org", "apache", "logging", "log4j", "core", "impl", "ThrowableProxy$CacheEntry.class") => MergeStrategy.first
  case PathList("org", "apache", "logging", "log4j", "core", "impl", "ThrowableProxy.class") => MergeStrategy.first
  case PathList("org", "apache", "logging", "log4j", "core", "jmx", "Server.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val compileScalaStyle = taskKey[Unit]("compileScalastyle")

compileScalaStyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(scalastyleConfig in Compile) := file("project/scalastyle-config.xml")
(scalastyleConfig in Test) := file("project/scalastyle-test-config.xml")

(compile in Compile) <<= (compile in Compile) dependsOn compileScalaStyle
(test in Test) := {
  (test in Test) dependsOn compileScalaStyle
}
packageOptions := Seq(ManifestAttributes(
  ("Change", "253032b"),
  ("Branch", "253032b4a7818992af360097e3ddc1475fa7b044"),
  ("X-Compile-Target-JDK", "1.8"),
  ("X-Compile-Source-JDK", "1.8"),
  ("Branch", "253032b4a7818992af360097e3ddc1475fa7b044"),
  ("X-Compile-Elasticsearch-Version", "5.0.0"),
  ("X-Compile-Lucene-Version", "6.2.0"),
  ("X-Compile-Elasticsearch-Snapshot", "false"),
  ("Build-Date", System.currentTimeMillis().toString)
))

parallelExecution in ThisBuild := false
parallelExecution in Test := false
