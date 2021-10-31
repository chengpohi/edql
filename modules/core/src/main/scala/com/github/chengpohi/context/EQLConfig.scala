package com.github.chengpohi.context

import com.github.chengpohi.connector.ClientNode
import com.github.chengpohi.dsl.EQLClient
import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}
import org.apache.commons.lang3.StringUtils
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.message.BasicHeader
import org.apache.http.{Header, HttpHost}
import org.apache.logging.log4j.LogManager
import org.apache.lucene.util.IOUtils
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest
import org.elasticsearch.analysis.common.CommonAnalysisPlugin
import org.elasticsearch.client.{Client, RestClient, RestClientBuilder}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.reindex.ReindexPlugin
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty4Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient

import java.net.{InetSocketAddress, URI}
import java.nio.charset.StandardCharsets
import java.util
import java.util.{Base64, Collections}
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
      case false => buildRemoteClient(
        config.getString("host"),
        config.getInt("port"),
        config.getString("cluster.name"))
    }

  def buildLocalClient(config: Config): EQLClient = {
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

    val restClient = buildRestClientByTransportClient(clientNode.client())
    EQLClient(Some(clientNode.client()), restClient)
  }

  def buildRemoteClient(host: String, port: Int, clusterName: String): EQLClient = {
    val address = new TransportAddress(new InetSocketAddress(host, port))

    val client = new PreBuiltTransportClient(
      Settings
        .builder()
        .put("cluster.name", clusterName)
        .build()
    ).addTransportAddress(address)

    val restClient: RestClient = buildRestClientByTransportClient(client)
    EQLClient(Some(client), restClient)
  }

  def buildRestClientByTransportClient(client: Client) = {
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

  def buildRestClient(uri: URI,
                      auth: Option[String],
                      username: Option[String],
                      password: Option[String],
                      apiKeyId: Option[String],
                      apiKeySecret: Option[String],
                      timeout: Option[Int]) = {
    val restClientBuilder =
      RestClient.builder(new HttpHost(uri.getHost, uri.getPort, uri.getScheme))
        .setRequestConfigCallback(
          new RestClientBuilder.RequestConfigCallback() {
            override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = return requestConfigBuilder
              .setConnectTimeout(timeout.getOrElse(5000))
              .setSocketTimeout(timeout.getOrElse(5000));
          })

    auth.map(a => {
      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", a)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    })

    if (apiKeyId.isDefined && apiKeySecret.isDefined) {
      val apiKeyAuth = Base64.getEncoder.encodeToString((apiKeyId.get + ":" + apiKeySecret.get).getBytes(StandardCharsets.UTF_8))

      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", "ApiKey " + apiKeyAuth)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    }

    if (StringUtils.isNotBlank(uri.getUserInfo)) {
      val credentialsProvider = new BasicCredentialsProvider
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(uri.getUserInfo))
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
          }
        })
    }
    if (username.isDefined && password.isDefined) {
      val credentialsProvider = new BasicCredentialsProvider
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username.get, password.get))
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.disableAuthCaching
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
          }
        })
    }


    EQLClient(None, restClientBuilder.build())
  }
}
