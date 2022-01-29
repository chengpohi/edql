import Settings._

lazy val eql = project
  .in(file("."))
  .settings(commonSettings: _*)
  .dependsOn(
    eqlRepl,
    eqlScript,
    eqlCore
  )

lazy val eqlRepl = project
  .in(file("modules/repl"))
  .settings(commonSettings: _*)
  .enablePlugins(NativeImagePlugin)
  .settings(
    name := "eql-repl",
    version := Versions.eqlVersion,
    mainClass in Compile := Some("com.github.chengpohi.EQLCommand"),
    libraryDependencies ++= replDependencies,
    assemblyJarName in assembly := "eql-repl",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript())),
    nativeImageOptions ++= List("--initialize-at-build-time", "--no-fallback"),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs@_*) => MergeStrategy.first
      case PathList("module-info.class") => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .dependsOn(eqlScript)

lazy val eqlScript = project
  .in(file("modules/script"))
  .settings(commonSettings: _*)
  .settings(
    name := "eql-script",
    version := Versions.eqlVersion,
    libraryDependencies ++= scriptDependencies
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


addCommandAlias("pbCore", "; eqlCore/publishLocal")
addCommandAlias("pbm2", "; eqlCore/publishM2;eqlScript/publishM2")
addCommandAlias("binary", "; eqlRepl/assembly")
addCommandAlias("ni", "; eqlRepl/nativeImage")
