import java.nio.file.{Files, Paths, StandardCopyOption}

import com.typesafe.sbt.packager.SettingsHelper._
import sbt.Package.ManifestAttributes
import sbtassembly.{MergeStrategy, PathList}

import scala.sys.process._


packageOptions := Seq(
  ManifestAttributes(
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

//------------------------------------------------//

//------------------------------------------------//
//------------------Release-----------------------//

//publish to sonatype
publishArtifact in Test := false

sonatypeProfileName := "com.github.chengpohi"

pomIncludeRepository := { _ =>
  false
}

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

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

//release release-version 0.2.2 next-version 0.2.2-SNAPSHOT
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  //ReleaseStep(action = Command.process("universal:packageBin", _)), //publish zip artificat
  // ReleaseStep(action = Command.process("publishSigned", _)), //publishLibarary
  setNextVersion,
  commitNextVersion,
  //ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)

scriptClasspath in bashScriptDefines ~= (cp => "../conf" +: cp)

enablePlugins(JavaAppPackaging, UniversalDeployPlugin)

makeDeploymentSettings(Universal, packageBin in Universal, "zip")

val pb = taskKey[Unit]("packageBin")

pb := {
  val elasticConf = Paths.get("./src/universal/conf/eql.conf")
  val log4j2Properties = Paths.get("./src/universal/conf/log4j2.properties")
  Files.copy(Paths.get("./src/main/resources/eql.conf"),
    elasticConf,
    StandardCopyOption.REPLACE_EXISTING)
  Files.copy(Paths.get("./src/main/resources/log4j2.properties"),
    log4j2Properties,
    StandardCopyOption.REPLACE_EXISTING)
  //Command.process("universal:packageBin", state.value)
  "sbt universal:packageBin".!!
  Files.delete(elasticConf)
  Files.delete(log4j2Properties)
}

//------------------------------------------------//
//------------------------------------------------//
