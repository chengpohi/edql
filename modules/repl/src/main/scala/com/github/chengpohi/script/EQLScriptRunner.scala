package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.EQLParser
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import java.net.URL
import scala.io.Source
import scala.util.{Failure, Try}

class EQLScriptRunner {
  val eqlParser: EQLParser = new EQLParser

  import eqlParser._

  def parse: String => PSI = (s: String) => instruction(s)

  def generateInstructions(source: String): Try[Seq[Instruction2]] = {
    (parse andThen gi).apply(source)
  }

  def readFile(file: File): Try[String] = Try {
    val script = Resource
      .fromAutoCloseable(IO {
        Source.fromFile(file)
      })
      .use(i => IO(i.getLines().mkString(System.lineSeparator())))
      .unsafeRunSync()
    script
  }

  def run(script: String): Try[Seq[String]] = {
    val instructions = this.generateInstructions(script)
    instructions.map(ins => {
      val rIns = ins.filter(!_.isInstanceOf[ScriptContextInstruction2])
      val cIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])

      cIns.find(_.isInstanceOf[EndpointBindInstruction]) match {
        case Some(h) =>
          val hostInstruction2 = h.asInstanceOf[EndpointBindInstruction]
          val aInstruction2 = cIns.find(_.isInstanceOf[AuthorizationBindInstruction])
            .map(i => i.asInstanceOf[AuthorizationBindInstruction]).map(i => i.auth)

          val context = ScriptEQLContext(hostInstruction2.endpoint, aInstruction2)
          rIns.map(i => i.execute(context).json)
        case None =>
          return Failure(new RuntimeException("Please set host bind"))
      }
    })
  }

  private def scriptContextInstruction(i: eqlParser.Instruction2) = {
    i.isInstanceOf[ScriptContextInstruction2]
  }

  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}

class ScriptEQLContext(host: String, port: Int, auth: Option[String]) extends EQLConfig with EQLContext {
  override implicit lazy val eqlClient: EQLClient =
    buildRestClient(host, port, auth)
}

object ScriptEQLContext {
  def apply(endpoint: String, auth: Option[String] = None): ScriptEQLContext = {
    val url = new URL(endpoint)
    new ScriptEQLContext(url.getHost, url.getPort, auth)
  }
}
