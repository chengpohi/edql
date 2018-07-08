import Settings._

scalaVersion := "2.12.1"

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .settings(rootProjectSettings: _*)
  .settings(
    pluginName in espackage := "eqlplugin.jar",
    pluginClassName in espackage := "com.github.chengpohi.EQLPlugin",
    pluginDescription in espackage := "eql explore for elasticsearch"
  )

