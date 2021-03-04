package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.serializer.JSONOps
import com.github.chengpohi.repl.EQLInterpreter
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import scala.io.Source

class EQLScriptRunner(eqlInterpreter: EQLInterpreter) extends EQLConfig with EQLContext with JSONOps {
  def run(file: File): Option[String] = {
    if (!file.exists()) {
      return None
    }

    val script = Resource
      .fromAutoCloseable(IO {
        Source.fromFile(file)
      })
      .use(i => IO(i.getLines().mkString(System.lineSeparator())))
      .unsafeRunSync()
    val result = eqlInterpreter.parse(script)
    Some(result)
  }

  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}
