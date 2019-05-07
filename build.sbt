import Settings._

scalaVersion := "2.12.1"

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .settings(rootProjectSettings: _*)
