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

  val client = buildClient(host, port)

  def buildClient(host: String, port: Int) = {
    val uri = ElasticsearchClientUri(s"elasticsearch://$host:$port")
    ElasticClient.transport(uri)
  }
}
