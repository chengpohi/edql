scalaVersion := "2.12.1"

lazy val commonSettings = Seq(
  version := "6.x-SNAPSHOT",
  organization := "com.github.chengpohi",
  scalaVersion := "2.12.1",
  resolvers += Resolver.mavenLocal,
  resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
  test in assembly := {}
)

val ds = Seq(
  "com.typesafe" % "config" % "1.3.0",
  "jline" % "jline" % "2.12",
  "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.elasticsearch.client" % "transport" % "6.1.2",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "com.lihaoyi" %% "fastparse" % "0.4.2",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.json4s" %% "json4s-jackson" % "3.5.0",
  "org.scalaz" %% "scalaz-core" % "7.3.0-M9"
)
lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "elasticdsl",
    unmanagedBase := baseDirectory.value / "lib",
    scalacOptions in compile ++= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    testOptions in Test += Tests.Cleanup(() => println("Cleanup")),
    mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl"),
    libraryDependencies ++= ds
  )
  .dependsOn(root)

lazy val dslplugin = project
  .in(file("modules/dsl-plugin"))
  .settings(commonSettings: _*)
  .settings(
    assemblyJarName in assembly := "dsl-plugin.jar",
    libraryDependencies ++= Seq(
      "org.elasticsearch.client" % "transport" % "6.1.2" % "provided",
      "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7" % "provided",
      "org.apache.logging.log4j" % "log4j-api" % "2.7" % "provided",
      "org.apache.logging.log4j" % "log4j-core" % "2.7" % "provided"
    )
  )
  .settings(
    projectDependencies := {
      Seq(
        (projectID in root).value.exclude("org.elasticsearch.client", "transport"),
        (projectID in root).value.exclude("org.apache.logging.log4j", "log4j-1.2-api"),
        (projectID in root).value.exclude("org.apache.logging.log4j", "log4j-api"),
        (projectID in root).value.exclude("org.apache.logging.log4j", "log4j-core")
      )
    })
  .dependsOn(root)
