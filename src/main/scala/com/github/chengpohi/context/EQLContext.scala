package com.github.chengpohi.context

import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.EQLParser


trait EQLContext {
  this: EQLConfig =>
  implicit lazy val eql: EQLClient = buildClient(config)
  implicit lazy val elkParser = new EQLParser(eql)
}
