package com.github.chengpohi

import com.github.chengpohi.repl.ReplRunner
import com.github.chengpohi.script.EDQLScriptRunner

import java.io.File
import scala.util.{Failure, Success}

object EDQLCommand {
  def main(args: Array[String]): Unit = {
    try {
      val scriptRunner = new EDQLScriptRunner(Seq("/lib.edql"))
      val file = scriptRunner.getScriptFilePathFromEnv
      file match {
        case Some(f) =>
          val result =
            scriptRunner.readFile(new File(f))
              .flatMap(s => scriptRunner.run(s).response)

          result match {
            case Success(value) =>
              println(value.mkString(System.lineSeparator()))
            case Failure(exception) =>
              println(exception.getMessage)
          }
          System.exit(0)
        case None =>
          ReplRunner().runRepl()
      }
    } catch {
      case ex: Exception => {
        ex.printStackTrace()
        System.exit(-1)
      }
    }
  }
}
