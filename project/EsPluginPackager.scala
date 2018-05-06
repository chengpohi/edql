import sbt.Def.Initialize
import sbt.Keys._
import sbt._
import sbtassembly.Assembly

object EsPluginPackager {

  import EsPluginPackagerPlugin.autoImport.{ESPluginPackager => _, _}

  def packageToEsPlugin(key: TaskKey[File]): Initialize[Task[File]] = Def.task {
    val s = (streams in key).value
    val pn = (pluginName in key).value
    val pd = (pluginDescription in key).value
    val pc = (pluginClassName in key).value

    Assembly(
      new File(s"${crossTarget.value}/$pn"),
      (assemblyOption in key).value,
      (packageOptions in(Compile, packageBin)).value,
      Assembly.assembledMappingsTask(key).value,
      s.cacheDirectory,
      s.log
    )
  }

}
