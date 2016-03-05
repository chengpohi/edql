name := "elasticshell"

organization := "com.github.chengpohi"

version := "1.1"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

mainClass in Compile := Some("com.github.chengpohi.repl.ELKRepl")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-remote" % "2.3.8",
  "com.lihaoyi" %% "fastparse" % "0.3.4",
  "jline" % "jline" % "2.12",
  "com.sksamuel.elastic4s" % "elastic4s-core_2.11" % "2.2.0",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10"
)


assemblyMergeStrategy in assembly := {
   case PathList("org", "joda", "time", "base", "BaseDateTime.class") => MergeStrategy.first
   case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
