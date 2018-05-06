import EsPluginPackager.packageToEsPlugin
import sbt.Keys._
import sbt._
import sbtassembly.{Assembly, AssemblyOption, MergeStrategy, PathList}


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
    logLevel in espackage := Level.Info,
    assemblyOption in espackage := {
      val s = streams.value
      AssemblyOption(
        assemblyDirectory = s.cacheDirectory / "espackage",
        includeBin = true,
        includeScala = true,
        includeDependency = true,
        mergeStrategy = {
          case PathList("META-INF", xs @ _*) => MergeStrategy.discard
          case _ => MergeStrategy.first
        },
        excludedJars = Seq(),
        excludedFiles = Assembly.defaultExcludedFiles,
        cacheOutput = true,
        cacheUnzip = true,
        appendContentHash = false,
        prependShellScript = None,
        maxHashLength = None,
        shadeRules = Seq(),
        level = (logLevel in espackage).value
      )
    },
    espackage := packageToEsPlugin(espackage).value
  )

  lazy val esPluginSettings: Seq[sbt.Def.Setting[_]] = baseEsPluginPackagerSettings
}
