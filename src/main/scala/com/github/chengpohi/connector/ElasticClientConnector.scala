package com.github.chengpohi.connector

import java.net.InetSocketAddress

import com.typesafe.config.ConfigFactory
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress

/**
  * elasticshell
  * Created by chengpohi on 3/19/15.
  */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("application.conf").getConfig("elastic")
  val clusterName: String = indexConfig.getString("cluster.name")
  val settings = Settings.builder()
    .put("cluster.name", clusterName)
    .build()
  val host: String = indexConfig.getString("host")
  val port: Int = indexConfig.getInt("port")

  val client = buildClient(settings, host, port)

  def buildClient(settings: Settings, host: String, port: Int) = {
    val client = new PreBuiltTransportClient(settings)
    client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
  }
}
