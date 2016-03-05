package com.github.chengpohi.connector

import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import com.typesafe.config.ConfigFactory

/**
 * Created by chengpohi on 3/19/15.
 */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("application.conf").getConfig("elastic")
  val host: String = indexConfig.getString("host")
  val port: Int = indexConfig.getInt("port")
  val uri = ElasticsearchClientUri(s"elasticsearch://$host:$port")
  val client = ElasticClient.transport(uri)
}
