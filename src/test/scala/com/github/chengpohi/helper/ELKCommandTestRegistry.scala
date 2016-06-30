package com.github.chengpohi.helper

import com.github.chengpohi.api.ElasticCommand
import com.github.chengpohi.parser.{ELKCommand, ELKParser, ParserUtils}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.NodeBuilder

/**
  * elasticshell
  * Created by chengpohi on 4/4/16.
  */

object ELKCommandTestRegistry {
  private[this] val settings: Settings = Settings.settingsBuilder()
    .put("http.enable", "false")
    .put("cluster.name", "test")
    .put("path.repo", "./target/elkrepo")
    .put("action.destructive_requires_name", "false")
    .put("update_all_types", "true")
    .put("action.operate_all_indices", "true")
    .put("path.home", "./target/elkdata")
    .build()
  val node = NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node()
  private[this] val client = node.client()
  private[this] val elasticCommand = new ElasticCommand(client)
  val responseGenerator = new ResponseGenerator
  private[this] val elkCommand = new ELKCommand(elasticCommand, responseGenerator)
  private[this] val parserUtils = new ParserUtils
  val elkParser = new ELKParser(elkCommand, parserUtils)
}

