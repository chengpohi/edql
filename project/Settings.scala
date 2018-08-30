import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.{MergeStrategy, PathList}


object Settings {
  lazy val commonSettings = Seq(
    version := "6.x-SNAPSHOT",
    organization := "com.github.chengpohi",
    scalaVersion := "2.12.1",
    scalacOptions ++= Seq("-Ywarn-unused",
      "-Ywarn-unused-import",
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"),
    resolvers += Resolver.mavenLocal,
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    test in assembly := {}
  )

  lazy val rootProjectSettings = Seq(
    name := "eql",
    unmanagedBase := baseDirectory.value / "lib",
    scalacOptions ++= Seq(
      "-Ywarn-unused",
      "-Ywarn-unused-import",
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"
    ),
    testOptions in Test += Tests.Cleanup(() => println("Cleanup")),
    mainClass in Compile := Some("com.github.chengpohi.repl.EQLRepl"),
    libraryDependencies ++= dependencies,
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript())),
    assemblyMergeStrategy in assembly := {
      case "META-INF/io.netty.versions.properties" => MergeStrategy.discard
      case PathList("META-INF/MANIFEST.MF") => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

  val dependencies = Seq(
    "com.typesafe" % "config" % "1.3.0",
    "jline" % "jline" % "2.12",
    "org.elasticsearch.client" % "transport" % "6.4.0",
    "org.apache.commons" % "commons-lang3" % "3.5",
    "org.scalactic" %% "scalactic" % "3.0.1",
    "com.lihaoyi" %% "fastparse" % "0.4.2",
    "org.json4s" %% "json4s-jackson" % "3.5.0",
    "org.scalaz" %% "scalaz-core" % "7.3.0-M9",
    "org.apache.logging.log4j" % "log4j-core" % "2.11.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  )

  def defaultShellScript(javaOpts: Seq[String] = Seq.empty): Seq[String] = {
    val javaOptsString = javaOpts.map(_ + " ").mkString
    Seq("#!/usr/bin/env sh", s"""exec java -jar $javaOptsString$$JAVA_OPTS "$$@" "$$0"""", "")
  }
}
