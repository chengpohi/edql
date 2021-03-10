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

  def run(script: String): Try[String] = {
    val instructions = this.generateInstructions(script)
    val result = instructions.flatMap(_.find(i => i.isInstanceOf[EndpointBindInstruction]) match {
      case Some(hi) =>
        val hostInstruction2 = hi.asInstanceOf[EndpointBindInstruction]
        val context = ScriptEQLContext(hostInstruction2.endpoint)
        val res = instructions
          .map(_.filter(i => !i.isInstanceOf[EndpointBindInstruction] && !i.isInstanceOf[CommentInstruction])
            .map(i => i.execute(context).json)
            .mkString(System.lineSeparator()))
        res
      case None =>
        return Failure(new RuntimeException("Please set host bind"))
    })
    result
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
    buildRestClient(host, port)
}

object ScriptEQLContext {
  def apply(endpoint: String): ScriptEQLContext = {
    val url = new URL(endpoint)
    new ScriptEQLContext(url.getHost, url.getPort)
  }
}
