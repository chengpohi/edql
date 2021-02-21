package com.github.chengpohi.script

import com.github.chengpohi.helper.EQLTestTrait
import com.github.chengpohi.repl.EQLInterpreter

import java.io.File

class EQLScriptRunnerTest extends EQLTestTrait{
  import com.github.chengpohi.helper.EQLTestContext._
  val interpreter: EQLInterpreter = new EQLInterpreter()
  val runner = new EQLScriptRunner(interpreter)


  "ELKParser" should "get health of elasticsearch" in {
    val file = new File(this.getClass.getResource("/test.eql").toURI)
    val result = runner.run(file)

    println(result.get)
  }

}
