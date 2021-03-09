import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._


object Settings {
  lazy val commonSettings = Seq(
    version := Versions.eqlVersion,
    organization := "com.github.chengpohi",
    scalaVersion := Versions.scalaVersion,
    scalacOptions ++= Seq("-Ywarn-unused",
      "-feature",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:postfixOps"),
    resolvers += Resolver.mavenLocal,
    //resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases",
    test in assembly := {}
  )

  val coreDependencies = Seq(
    "com.typesafe" % "config" % "1.3.0",
    "org.elasticsearch.client" % "transport" % "7.3.0",
    "org.codelibs.elasticsearch.module" % "analysis-common" % "7.3.0",
    "org.apache.commons" % "commons-lang3" % "3.5",
    "org.scalactic" %% "scalactic" % "3.2.3",
    "com.lihaoyi" %% "fastparse" % "2.3.0",
    "org.json4s" %% "json4s-jackson" % "3.7.0-M1",
    "org.apache.logging.log4j" % "log4j-core" % "2.11.1",
    "org.typelevel" %% "cats-core" % "2.0.0",
    "org.typelevel" %% "cats-effect" % "2.0.0",
    "org.scalatest" %% "scalatest" % "3.2.3" % "test"
  )

  val replDependencies = Seq(
    "jline" % "jline" % "2.12",
    "org.scalatest" %% "scalatest" % "3.2.3" % "test"
  )

  def defaultShellScript(javaOpts: Seq[String] = Seq.empty): Seq[String] = {
    val javaOptsString = javaOpts.map(_ + " ").mkString
    Seq("#!/usr/bin/env sh", s"""exec java -jar $javaOptsString$$JAVA_OPTS "$$@" "$$0"""", "")
  }
}
