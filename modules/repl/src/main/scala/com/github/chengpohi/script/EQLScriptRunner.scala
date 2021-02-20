package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.serializer.JSONOps
import com.github.chengpohi.repl.EQLInterpreter
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang3.StringUtils

import scala.io.Source

class EQLScriptRunner(eqlInterpreter: EQLInterpreter) extends EQLConfig with EQLContext with JSONOps {
  def run: Option[String] = {
    val scriptFile= getScriptFilePath
    if (scriptFile.isEmpty) {
      return None
    }

    val script = Resource
      .fromAutoCloseable(IO {
        Source.fromFile(scriptFile.get)
      })
      .use(i => IO(i.getLines().mkString(System.lineSeparator())))
      .unsafeRunSync()
    val result = eqlInterpreter.run(script)
    Some(result)
  }

  def getScriptFilePath: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}
