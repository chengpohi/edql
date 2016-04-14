package com.github.chengpohi.connector

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.typesafe.config.ConfigFactory
import org.elasticsearch.common.settings.Settings

/**
  * elasticshell
  * Created by chengpohi on 3/19/15.
  */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("application.conf").getConfig("elastic")
  val clusterName: String = indexConfig.getString("cluster.name")
  val settings = Settings.settingsBuilder()
    .put("cluster.name", clusterName)
    .build()
  val host: String = indexConfig.getString("host")
  val port: Int = indexConfig.getInt("port")

  val client = buildClient(settings, host, port)

  def buildClient(settings: Settings, host: String, port: Int) = {
    val uri = ElasticsearchClientUri(s"elasticsearch://$host:$port")
    ElasticClient.transport(settings, uri)
  }
}
