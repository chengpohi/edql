package com.github.chengpohi.context

import java.net.InetSocketAddress
import java.util
import java.util.Collections

import com.github.chengpohi.connector.ClientNode
import com.github.chengpohi.dsl.EQLClient
import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}
import org.apache.http.HttpHost
import org.apache.logging.log4j.LogManager
import org.apache.lucene.util.IOUtils
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest
import org.elasticsearch.analysis.common.CommonAnalysisPlugin
import org.elasticsearch.client.{Client, RestClient}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.reindex.ReindexPlugin
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty4Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient

import scala.collection.JavaConverters._

/**
 * eql
 * Created by chengpohi on 16/06/16.
 */
trait EQLConfig {
  private val log = LogManager.getLogger(this.getClass)

  lazy val config: Config =
    ConfigFactory.load("eql.conf").getConfig("eql")

  def buildClient(config: Config): EQLClient =
    config.getBoolean("standalone") match {
      case true => buildLocalClient(config)
      case false => buildRemoteClient(config)
    }

  private def buildLocalClient(config: Config): EQLClient = {
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
          classOf[CommonAnalysisPlugin],
          classOf[PercolatorPlugin],
          classOf[MustachePlugin]))
    val clientNode: ClientNode = new ClientNode(
      settings,
      plugins.asInstanceOf[util.List[Class[_ <: Plugin]]])
    clientNode.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      IOUtils.close(clientNode)
    }))

    val restClient = buildRestClient(clientNode.client())
    EQLClient(clientNode.client(), restClient)
  }

  private def buildRemoteClient(config: Config): EQLClient = {
    val host: String = config.getString("host")
    val port: Int = config.getInt("port")

    val address = new TransportAddress(new InetSocketAddress(host, port))

    val client = new PreBuiltTransportClient(
      Settings
        .builder()
        .put("cluster.name", config.getString("cluster.name"))
        .build()).addTransportAddress(
      address
    )

    val restClient: RestClient = buildRestClient(client)
    EQLClient(client, restClient)
  }

  private def buildRestClient(client: Client) = {
    val request = new NodesInfoRequest()
    request.http(true)
    val resp = client.admin().cluster().nodesInfo(request).get()
    resp.getNodes.asScala.toStream
      .map(i => {
        Some(i.getHttp.getAddress).map(_.publishAddress())
      })
      .head
      .map(i =>
        RestClient.builder(new HttpHost(i.getAddress, i.getPort)).build())
      .getOrElse({
        log.error("rest client not enabled")
        null
      })
  }
}
