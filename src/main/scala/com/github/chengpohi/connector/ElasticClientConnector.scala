package com.github.chengpohi.connector

import java.net.InetSocketAddress
import java.nio.file.Paths
import java.util
import java.util.Collections

import com.typesafe.config.ConfigFactory
import org.elasticsearch.client.Client
import org.elasticsearch.common.network.NetworkModule
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.reindex.ReindexPlugin
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty4Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient

/**
  * elasticdsl
  * Created by chengpohi on 3/19/15.
  */
object ElasticClientConnector {
  lazy val indexConfig = ConfigFactory.load("elastic.conf").getConfig("elastic")
  val clusterName: String = indexConfig.getString("cluster.name")
  val isStandalone: Boolean = indexConfig.getBoolean("standalone")

  val client: Client = isStandalone match {
    case false => buildRemoteClient()
    case true => buildLocalClient()
  }

  def buildLocalClient(): Client = {
    val settings: Settings = Settings.builder()
      .loadFromPath(Paths.get(getClass.getResource("/local.yml").toURI))
      .put("cluster.name", clusterName)
      .build()

    val plugins =
      Collections.unmodifiableList(
        util.Arrays.asList(
          classOf[Netty4Plugin],
          classOf[ReindexPlugin],
          classOf[PercolatorPlugin],
          classOf[MustachePlugin]))
    val clientNode: ClientNode = new ClientNode(settings, plugins.asInstanceOf[util.List[Class[_ <: Plugin]]])
    clientNode.start()
    clientNode.client()
  }

  def buildRemoteClient(): Client = {
    val settings = Settings.builder()
      .put("node.name", "elasticdsl")
      .put("cluster.name", clusterName)
      .build()

    val host: String = indexConfig.getString("host")
    val port: Int = indexConfig.getInt("port")

    val client = new PreBuiltTransportClient(settings)
      .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
    client
  }
}
