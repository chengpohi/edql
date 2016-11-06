package com.github.chengpohi.connector

import java.net.InetSocketAddress
import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import org.elasticsearch.client.Client
import org.elasticsearch.common.network.NetworkModule
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import org.elasticsearch.transport.Netty3Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient

/**
  * elasticshell
  * Created by chengpohi on 3/19/15.
  */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("elastic.conf").getConfig("elastic")
  val clusterName: String = indexConfig.getString("cluster.name")
  val isStandalone: Boolean = indexConfig.getBoolean("standalone")

  val client = isStandalone match {
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
      .put("node.name", "elasticshell")
      .put("cluster.name", clusterName)
      .put(NetworkModule.TRANSPORT_TYPE_KEY, Netty3Plugin.NETTY_TRANSPORT_NAME)
      .build()

    val host: String = indexConfig.getString("host")
    val port: Int = indexConfig.getInt("port")

    val client = new PreBuiltTransportClient(settings)
      .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
    client
  }
}
