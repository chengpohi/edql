package com.github.chengpohi.registry

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.connector.ElasticClientConnector
import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.{InterceptFunction, ELKParser, ParserUtils}

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */

object ELKCommandRegistry {
  val client = ElasticClientConnector.client
  val responseGenerator = new ResponseGenerator
  val elasticDSL = new ElasticDSL(client)
  val interceptFunction = new InterceptFunction(elasticDSL)
  val elkParser = new ELKParser(interceptFunction)
}
