import sbt._
import sbtassembly.AssemblyOption

trait EsPluginPackagerKeys {
  lazy val espackage = taskKey[File]("builds a es plugin.")
  lazy val pluginName = taskKey[String]("the plugin name")
  lazy val pluginDescription = taskKey[String]("plugin's version!")
  lazy val pluginClassName =
    taskKey[String]("the name of the class to load, fully-qualified.")
  lazy val assemblyOption =
    taskKey[AssemblyOption]("Configuration for making a deployable fat jar.")
}

object EsPluginPackagerKeys extends EsPluginPackagerKeys
