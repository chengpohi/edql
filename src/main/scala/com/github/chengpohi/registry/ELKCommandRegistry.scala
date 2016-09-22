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
  private[this] val client = ElasticClientConnector.client
  private[this] val elasticCommand = new ElasticDSL(client)
  val responseGenerator = new ResponseGenerator
  private[this] val elkCommand = new ELKCommand(elasticCommand, responseGenerator)
  private[this] val parserUtils = new ParserUtils
  val elkParser = new ELKParser(elkCommand, parserUtils)
}
