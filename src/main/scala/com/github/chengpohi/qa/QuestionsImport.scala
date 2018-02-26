package com.github.chengpohi.qa

import com.github.chengpohi.connector.ELKDSLConfig
import com.github.chengpohi.registry.ELKDSLContext

object QuestionsImport extends ELKDSLConfig with ELKDSLContext {

  import dsl._

  DSL {
    index into "qa" / "question" doc Map("question" -> "question",
                                         "answers" -> List("answer1",
                                                           "answer2"))
  }.await
}
