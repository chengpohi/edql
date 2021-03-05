package com.github.chengpohi

import com.github.chengpohi.repl.EQLReplRunner
import com.github.chengpohi.script.EQLScriptRunner

import java.io.File

object EQLCommand {
  def main(args: Array[String]): Unit = {
    try {
      val scriptRunner = new EQLScriptRunner()
      val file = scriptRunner.getScriptFilePathFromEnv
      file match {
        case Some(f) =>
          val res = scriptRunner.run(new File(f))
          println(res.get)
          System.exit(0)
        case None =>
          EQLReplRunner().runRepl()
      }
    } catch {
      case ex: Exception => {
        ex.printStackTrace()
        System.exit(-1)
      }
    }
  }
}
