package com.github.chengpohi.qa

import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.registry.EQLContext

object QuestionsImport extends EQLConfig with EQLContext {

  import eql._

  EQL {
    index into "qa" / "question" doc Map("question" -> "question",
                                         "answers" -> List("answer1",
                                                           "answer2"))
  }.await
}
