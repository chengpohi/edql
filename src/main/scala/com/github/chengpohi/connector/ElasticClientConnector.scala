package com.github.chengpohi.connector

import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.ConfigFactory
import org.elasticsearch.common.settings.ImmutableSettings

/**
 * Created by chengpohi on 3/19/15.
 */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("application.conf").getConfig("elastic")
  lazy val settings = ImmutableSettings.settingsBuilder().put("cluster.name", indexConfig.getString("cluster.name")).build()
  lazy val client = ElasticClient.remote(settings, (indexConfig.getString("host"), indexConfig.getInt("port")))
}
