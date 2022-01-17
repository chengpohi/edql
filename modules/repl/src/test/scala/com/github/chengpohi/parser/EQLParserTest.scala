package com.github.chengpohi.parser

import com.github.chengpohi.helper.EQLTestTrait
import com.github.chengpohi.repl.EQLReplInterpreter

import java.io.ByteArrayOutputStream


class EQLParserTest extends EQLTestTrait {
  val runEngine: EQLReplInterpreter = new EQLReplInterpreter(com.github.chengpohi.helper.EQLTestContext)

  val outContent = new ByteArrayOutputStream()
  val errContent = new ByteArrayOutputStream()
}
