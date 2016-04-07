package com.github.chengpohi

import com.github.chengpohi.registry.ELKCommandRegistry

/**
  * elasticshell
  * Created by chengpohi on 4/6/16.
  */
class ELKExecutor {
  val eLKRunEngine = new ELKRunEngine(ELKCommandRegistry)

  def execute(query: String): String = {
    ""
  }
}
