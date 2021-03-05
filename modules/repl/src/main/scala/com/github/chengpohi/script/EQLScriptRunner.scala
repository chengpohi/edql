package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.EQLParser
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import java.net.URL
import scala.io.Source

class EQLScriptRunner {
  val eqlParser: EQLParser = new EQLParser

  import eqlParser._

  def parse: String => PSI = (s: String) => instruction(s)

  def generateInstructions(source: String): Seq[Instruction2] = {
    (parse andThen gi).apply(source)
  }

  def run(file: File): Option[String] = {
    if (!file.exists()) {
      return None
    }

    val script: String = this.readFile(file)
    val instructions = generateInstructions(script)
    val result = instructions.find(i => i.isInstanceOf[EndpointBindInstruction]) match {
      case Some(hi) =>
        val hostInstruction2 = hi.asInstanceOf[EndpointBindInstruction]
        val context = ScriptEQLContext(hostInstruction2.endpoint)
        val res = instructions
          .filter(i => !i.isInstanceOf[EndpointBindInstruction])
          .map(i => i.execute(context).json)
          .mkString(System.lineSeparator())
        res
      case None => "Please set host bind"
    }
    Some(result)
  }

  private def readFile(file: File) = {
    val script = Resource
      .fromAutoCloseable(IO {
        Source.fromFile(file)
      })
      .use(i => IO(i.getLines().mkString(System.lineSeparator())))
      .unsafeRunSync()
    script
  }

  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}

class ScriptEQLContext(host: String, port: Int) extends EQLConfig with EQLContext {
  override implicit lazy val eqlClient: EQLClient =
    buildRemoteClient(host, port, null)
}

object ScriptEQLContext {
  def apply(endpoint: String): ScriptEQLContext = {
    val url = new URL(endpoint)
    new ScriptEQLContext(url.getHost, url.getPort)
  }
}
