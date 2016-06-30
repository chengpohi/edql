name := "elasticshell"

organization := "com.github.chengpohi"

version := "0.1"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.typesafe" % "config" % "1.3.0",
  "com.lihaoyi" %% "fastparse" % "0.3.4",
  "jline" % "jline" % "2.12",
  "org.elasticsearch" % "elasticsearch" % "2.3.3",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10"
)


assemblyMergeStrategy in assembly := {
   case PathList("org", "joda", "time", "base", "BaseDateTime.class") => MergeStrategy.first
   case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
