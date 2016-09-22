package com.github.chengpohi.connector

import java.net.InetSocketAddress
import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import org.elasticsearch.transport.client.PreBuiltTransportClient

/**
  * elasticshell
  * Created by chengpohi on 3/19/15.
  */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("application.conf").getConfig("elastic")
  val clusterName: String = indexConfig.getString("cluster.name")
  val isLocal: Boolean = indexConfig.getBoolean("local")

  val client = isLocal match {
    case false => buildRemoteClient()
    case true => buildLocalClient()
  }

  def buildLocalClient(): Client = {
    val settings: Settings = Settings.builder()
      .loadFromPath(Paths.get(getClass.getResource("/local.yml").toURI))
      .build()
    val node = new Node(settings).start()
    node.client()
  }

  def buildRemoteClient(): Client = {
    val settings = Settings.builder()
      .put("cluster.name", clusterName)
      .build()
    val host: String = indexConfig.getString("host")
    val port: Int = indexConfig.getInt("port")

    val client = new PreBuiltTransportClient(settings)
    client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
  }
}
