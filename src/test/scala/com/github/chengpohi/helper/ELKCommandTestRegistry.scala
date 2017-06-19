package com.github.chengpohi.helper

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.parser.{InterceptFunction, ELKParser, ParserUtils}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */

trait ELKCommandTestRegistry {
  val responseGenerator = new ResponseGenerator

  private[this] val settings: Settings = Settings.builder()
    .put("http.enabled", "false")
    .put("cluster.name", "testelk")
    .put("path.repo", "./target/elkrepo")
    .put("action.destructive_requires_name", "false")
    .put("path.home", "./target/elkdata")
    .put("transport.type", "local")
    .build()
  private[this] val node = new Node(settings).start()
  private[this] val client = node.client()
  val elasticdsl = new ElasticDSL(client)
  private[this] val elkCommand = new InterceptFunction(elasticdsl)
  private[this] val parserUtils = new ParserUtils
  implicit lazy val elkParser = new ELKParser(elkCommand)

  import elasticdsl._

  DSL {
    delete index "*"
  }.await
}

