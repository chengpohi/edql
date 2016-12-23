import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import sbt.Package.ManifestAttributes

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

assemblyMergeStrategy in assembly := {
  case PathList("org", "joda", "time", "base", "BaseDateTime.class") => MergeStrategy.first
  case "META-INF/io.netty.versions.properties" => MergeStrategy.first
  case PathList("org", "apache", "logging", "log4j", "core", "impl", "ThrowableProxy$CacheEntry.class") => MergeStrategy.first
  case PathList("org", "apache", "logging", "log4j", "core", "impl", "ThrowableProxy.class") => MergeStrategy.first
  case PathList("org", "apache", "logging", "log4j", "core", "jmx", "Server.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val compileScalaStyle = taskKey[Unit]("compileScalastyle")

compileScalaStyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(scalastyleConfig in Compile) := file("project/scalastyle-config.xml")
(scalastyleConfig in Test) := file("project/scalastyle-test-config.xml")

(compile in Compile) <<= (compile in Compile) dependsOn compileScalaStyle
(test in Test) := {
  (test in Test) dependsOn compileScalaStyle
}
packageOptions := Seq(ManifestAttributes(
  ("Change", "253032b"),
  ("X-Compile-Target-JDK", "1.8"),
  ("X-Compile-Source-JDK", "1.8"),
  ("Branch", "5395e21a627bfe3ab8f037f49e68a9060bc967dd"),
  ("X-Compile-Elasticsearch-Version", "5.1.1"),
  ("X-Compile-Lucene-Version", "6.3.0"),
  ("X-Compile-Elasticsearch-Snapshot", "false"),
  ("Build-Date", System.currentTimeMillis().toString)
))

parallelExecution in ThisBuild := false
parallelExecution in Test := false

//publish to sonatype
publishArtifact in Test := false

sonatypeProfileName := "org.xerial"

pomIncludeRepository := { _ => false }

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  isSnapshot.value match {
    case true =>
      Some("snapshots" at nexus + "content/repositories/snapshots")
    case false =>
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
}

pomExtra := (
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
    <url>https://github.com/chengpohi/elasticdsl</url>
    <scm>
      <url>git@github.com:chengpohi/elasticdsl.git</url>
      <connection>scm:git:git@github.com:chengpohi/elasticdsl.git</connection>
    </scm>
    <developers>
      <developer>
        <id>chengpohi</id>
        <name>chengpohi</name>
        <url>https://github.com/chengpohi/elasticdsl</url>
      </developer>
    </developers>
  )

lazy val distribution = project.in(file("distribution")).settings(
  name := "elasticdsl_distribution",
  organization := "com.github.chengpohi",
  scalaVersion := "2.12.1"
)


lazy val prepareDistribution = taskKey[Unit]("release elasticdsl distribution")

val distributionPath = "./distribution/src/main/resources"

prepareDistribution := {
  println("assembly elasticdsl")
  val f = assembly.value
  copyJar(f)
  copyConf()
}

def copyJar(f: File): Unit = {
  val dist = Paths.get(distributionPath + "/lib/elasticdsl.jar")
  println(s"copy $f to $dist")
  Files.copy(f.toPath, dist, REPLACE_EXISTING)
}

def copyConf(): Unit = {
  println(s"copy resources to conf/")
  val f = Paths.get("./src/main/resources/")
  f.toFile.listFiles().foreach(i => {
    val d = Paths.get(distributionPath + "/conf/" + i.getName)
    Files.copy(i.toPath, d, REPLACE_EXISTING)
  })
}


lazy val cleanDistribution = taskKey[Unit]("clean elasticdsl distribution")

cleanDistribution := {
  Paths.get(distributionPath + "/conf/").toFile.listFiles().foreach(_.delete())
  Paths.get(distributionPath + "/lib/").toFile.listFiles().foreach(_.delete())
}


import ReleaseTransformations._

//release release-version 0.2.2 next-version 0.2.2-SNAPSHOT
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("prepareDistribution", _)),
  ReleaseStep(action = Command.process("publishSigned", _)),
  ReleaseStep(action = Command.process("cleanDistribution", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
