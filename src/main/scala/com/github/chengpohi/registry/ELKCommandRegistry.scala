package com.github.chengpohi.registry

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.connector.ElasticClientConnector
import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.{ELKCommand, ELKParser, ParserUtils}

/**
  * elasticshell
  * Created by chengpohi on 4/4/16.
  */

object ELKCommandRegistry {
  val client = ElasticClientConnector.client
  val responseGenerator = new ResponseGenerator
  val elasticCommand = new ElasticDSL(client)
  val elkCommand = new ELKCommand(elasticCommand, responseGenerator)
  val parserUtils = new ParserUtils
  val elkParser = new ELKParser(elkCommand, parserUtils)
}
