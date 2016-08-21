package com.github.chengpohi.helper

import com.github.chengpohi.api.ElasticCommand
import com.github.chengpohi.parser.{ELKCommand, ELKParser, ParserUtils}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

/**
  * elasticshell
  * Created by chengpohi on 4/4/16.
  */

object ELKCommandTestRegistry {
  private[this] val settings: Settings = Settings.builder()
    .put("http.enabled", "false")
    .put("cluster.name", "distribution_run")
    .put("path.repo", "./target/elkrepo")
    .put("action.destructive_requires_name", "false")
    .put("path.home", "./target/elkdata")
    .put("transport.type", "local")
    .build()
  val node = new Node(settings).start()
  private[this] val client = node.client()
  private[this] val elasticCommand = new ElasticCommand(client)
  val responseGenerator = new ResponseGenerator
  private[this] val elkCommand = new ELKCommand(elasticCommand, responseGenerator)
  private[this] val parserUtils = new ParserUtils
  val elkParser = new ELKParser(elkCommand, parserUtils)
}

