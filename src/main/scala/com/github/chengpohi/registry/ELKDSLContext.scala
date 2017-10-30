package com.github.chengpohi.registry

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.connector.ELKDSLConfig
import com.github.chengpohi.parser.{ELKParser, InterceptFunction}

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */
trait ELKDSLContext {
  this: ELKDSLConfig =>
  private val client = buildClient(config)

  implicit lazy val dsl: ElasticDSL = new ElasticDSL(client)
  implicit lazy val elkParser = new ELKParser(dsl)
}
