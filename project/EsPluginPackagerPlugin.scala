import sbt._

object EsPluginPackagerPlugin extends AutoPlugin {
  override def requires: AutoPlugin = plugins.JvmPlugin
  override def trigger: PluginTrigger = allRequirements
  override lazy val projectSettings: Seq[Def.Setting[_]] = esPluginSettings


  object autoImport extends EsPluginPackagerKeys {
    val ESPluginPackager = EsPluginPackager
    val baseEsPluginSettings = EsPluginPackagerPlugin.baseEsPluginPackagerSettings
  }

  import autoImport.{ESPluginPackager => _, baseEsPluginSettings => _, _}

  lazy val baseEsPluginPackagerSettings: Seq[sbt.Def.Setting[_]] = Seq(
    esplugin := EsPluginPackager.assemblyTask(esplugin).value
  )

  lazy val esPluginSettings: Seq[sbt.Def.Setting[_]] = baseEsPluginPackagerSettings
}
