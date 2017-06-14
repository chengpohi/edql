package com.github.chengpohi.registry

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.connector.ElasticClientConnector
import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.{ELKParser, InterceptFunction}

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */
object ELKDSLContext {
  implicit lazy val dsl: ElasticDSL = new ElasticDSL(client)
  implicit lazy val elkParser = new ELKParser(interceptFunction)

  val responseGenerator = new ResponseGenerator
  private val client = ElasticClientConnector.client
  private val interceptFunction = new InterceptFunction(dsl)
}
