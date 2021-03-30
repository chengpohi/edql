package com.github.chengpohi.script

import com.github.chengpohi.helper.EQLTestTrait

import java.io.File

class EQLScriptRunnerTest extends EQLTestTrait {
  val runner = new EQLScriptRunner()

  "ELKParser" should "get health of elasticsearch" in {
    val file = new File(this.getClass.getResource("/test.eql").toURI)

    val result = runner.run(runner.readFile(file).get)

    println(result.get.mkString(System.lineSeparator()))
  }
}
