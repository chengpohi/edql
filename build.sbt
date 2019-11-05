import Settings._

lazy val eql = project
  .in(file("."))
  .settings(commonSettings: _*)
  .dependsOn(
    eqlRepl,
    eqlCore
  )

lazy val eqlRepl = project
  .in(file("modules/repl"))
  .settings(commonSettings: _*)
  .settings(
    name := "eql-repl",
    version := Versions.eqlVersion,
    mainClass in Compile := Some("com.github.chengpohi.repl.EQLRepl"),
    libraryDependencies ++= replDependencies,
    assemblyJarName in assembly := "eql-repl",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript())),
    assemblyMergeStrategy in assembly := {
      case "META-INF/io.netty.versions.properties" => MergeStrategy.discard
      case PathList("META-INF/MANIFEST.MF") => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .dependsOn(eqlCore)

lazy val eqlCore = project
  .in(file("modules/core"))
  .settings(commonSettings: _*)
  .settings(
    name := "eql-core",
    version := Versions.eqlVersion,
    libraryDependencies ++= coreDependencies
  )


addCommandAlias("pbCore", "; eqlCore/publishLocal ")
addCommandAlias("binary", "; eqlRepl/assembly")
