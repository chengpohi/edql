name := "elasticdsl"

organization := "com.github.chengpohi"

scalaVersion := "2.12.1"

unmanagedBase := baseDirectory.value / "lib"

resolvers += Resolver.mavenLocal

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "com.typesafe" % "config" % "1.3.0",
  "com.lihaoyi" %% "fastparse" % "0.4.2",
  "jline" % "jline" % "2.12",
  "org.apache.logging.log4j" % "log4j-1.2-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.elasticsearch.client" % "transport" % "5.1.1",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.json4s" %% "json4s-jackson" % "3.5.0",
  "org.apache.commons" % "commons-lang3" % "3.5"
)

lazy val compileScalaStyle = taskKey[Unit]("compileScalastyle")

compileScalaStyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(scalastyleConfig in Compile) := file("project/scalastyle-config.xml")
(scalastyleConfig in Test) := file("project/scalastyle-test-config.xml")

(compile in Compile) <<= (compile in Compile) dependsOn compileScalaStyle
(test in Test) := {
  (test in Test) dependsOn compileScalaStyle
}
