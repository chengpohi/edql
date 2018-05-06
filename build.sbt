import Settings._

scalaVersion := "2.12.1"

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .settings(rootProjectSettings: _*)
  .settings(
    pluginName in espackage := "dslplugin.jar",
    pluginClassName in espackage := "com.github.chengpohi.DSLPlugin",
    pluginDescription in espackage := "dsl explore for elasticsearch"
  )

