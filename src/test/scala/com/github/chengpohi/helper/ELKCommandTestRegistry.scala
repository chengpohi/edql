package com.github.chengpohi.helper

import java.nio.file.{Files, Paths}

import com.github.chengpohi.api.ElasticCommand
import com.github.chengpohi.parser.{ELKCommand, ELKParser, ParserUtils}
import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.common.settings.Settings

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
  private[this] val client = ElasticClient.local(settings)
  private[this] val elasticCommand = new ElasticCommand(client)
  val responseGenerator = new ResponseGenerator
  private[this] val elkCommand = new ELKCommand(elasticCommand, responseGenerator)
  private[this] val parserUtils = new ParserUtils
  val elkParser = new ELKParser(elkCommand, parserUtils)
}

