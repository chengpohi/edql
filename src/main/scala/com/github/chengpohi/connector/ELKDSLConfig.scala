package com.github.chengpohi.connector

import java.net.InetSocketAddress
import java.util
import java.util.Collections

import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}
import org.apache.lucene.util.IOUtils
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.reindex.ReindexPlugin
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty4Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient

/**
  * elasticdsl
  * Created by chengpohi on 16/06/16.
  */
trait ELKDSLConfig {
  val config: Config =
    ConfigFactory.load("elasticdsl.conf").getConfig("elasticdsl")

  def buildClient(config: Config): Client =
    config.getBoolean("standalone") match {
      case true  => buildLocalClient(config)
      case false => buildRemoteClient(config)
    }

  private def buildLocalClient(config: Config): Client = {
    val settings: Settings = Settings
      .builder()
      .loadFromSource(
        config.getConfig("local").root().render(ConfigRenderOptions.concise()),
        XContentType.JSON
      )
      .put("cluster.name", config.getString("cluster.name"))
      .build()

    val plugins =
      Collections.unmodifiableList(
        util.Arrays.asList(classOf[Netty4Plugin],
                           classOf[ReindexPlugin],
                           classOf[PercolatorPlugin],
                           classOf[MustachePlugin]))
    val clientNode: ClientNode = new ClientNode(
      settings,
      plugins.asInstanceOf[util.List[Class[_ <: Plugin]]])
    clientNode.start()
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      IOUtils.close(clientNode)
    }))
    clientNode.client()
  }

  private def buildRemoteClient(config: Config): Client = {
    val settings = Settings
      .builder()
      .put("node.name", "elasticdsl")
      .put("cluster.name", config.getString("cluster.name"))
      .build()

    val host: String = config.getString("host")
    val port: Int = config.getInt("port")

    val client = new PreBuiltTransportClient(settings)
      .addTransportAddress(
        new TransportAddress(new InetSocketAddress(host, port))
      )
    client
  }
}
