import sbt._

trait EsPluginPackagerKeys {
  lazy val esplugin = taskKey[Unit]("builds a es plugin.")
  lazy val pluginName = taskKey[String]("the plugin name")
  lazy val pluginDescription = taskKey[String]("plugin's version!")
  lazy val pluginClassname = taskKey[String]("the name of the class to load, fully-qualified.")
}

object EsPluginPackagerKeys extends EsPluginPackagerKeys
