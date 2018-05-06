import sbt.Def.Initialize
import sbt._

object EsPluginPackager {

  import EsPluginPackagerPlugin.autoImport.{ESPluginPackager => _, _}

  def assemblyTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = Def.task {
    val pn = (pluginName in key).value
    println(pn)
  }
}
