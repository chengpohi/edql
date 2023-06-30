package com.github.chengpohi.context

import com.amazonaws.auth.AWS4Signer
import com.github.chengpohi.aws.{AWSRequestSigningApacheInterceptor, EDQLAWSCredentialsProviderChain, UnsafeX509ExtendedTrustManager}
import com.github.chengpohi.http.KibanaProxyApacheInterceptor
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang3.StringUtils
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.impl.client.SystemDefaultCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.impl.nio.reactor.IOReactorConfig
import org.apache.http.message.BasicHeader
import org.apache.http.protocol.HttpContext
import org.apache.http.{Header, HttpHost, HttpResponse}
import org.elasticsearch.client.{RestClient, RestClientBuilder}

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.net.ssl.{SSLContext, TrustManager}
import scala.concurrent.duration.DurationInt

/**
 * eql
 * Created by chengpohi on 16/06/16.
 */

case class AuthInfo(auth: String,
                    username: String,
                    password: String,
                    apiKeyId: String,
                    apiKeySecret: String,
                    apiSessionToken: String,
                    awsRegion: String) {

  def cacheKey: String = {
    s"""
       |${Option.apply(auth).getOrElse("")} - ${Option.apply(username).getOrElse("")}
       |-${Option.apply(apiKeyId).getOrElse("")} -${Option.apply(apiKeySecret).getOrElse("")}
       |-${Option.apply(apiSessionToken).getOrElse("")} -${Option.apply(awsRegion).getOrElse("")}
       |""".stripMargin
  }
}

case class ProxyInfo(httpHost: String, httpPort: Int, username: Option[String], password: Option[String])

case class HostInfo(host: String,
                    uri: URI,
                    timeout: Int = 5000,
                    kibanaProxy: Boolean = false,
                    authInfo: Option[AuthInfo] = None,
                    proxyInfo: Option[ProxyInfo] = None)

trait EDQLConfig {
  lazy val config: Config =
    ConfigFactory.load("eql.conf").getConfig("eql")

  def buildClient(config: Config): EDQLClient = {
    val endPoint = config.getString("host")
    buildRestClient(HostInfo(endPoint, URI.create(endPoint)))
  }

  def buildRestClient(hostInfo: HostInfo): EDQLClient = {
    val restClientBuilder =
      RestClient.builder(new HttpHost(hostInfo.uri.getHost, hostInfo.uri.getPort, hostInfo.uri.getScheme))
        .setRequestConfigCallback(
          new RestClientBuilder.RequestConfigCallback() {
            override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = {
              return requestConfigBuilder
                .setConnectTimeout(hostInfo.timeout)
                .setConnectionRequestTimeout(hostInfo.timeout)
                .setSocketTimeout(hostInfo.timeout)
            }
          })

    val sslContext: SSLContext = initSSLContext(restClientBuilder)
    val credentialsProvider = initAuthInfo(hostInfo.uri, hostInfo.authInfo, sslContext, restClientBuilder)
    if (StringUtils.isNotBlank(hostInfo.uri.getPath) && !StringUtils.equals(hostInfo.uri.getPath, "/") && !hostInfo.kibanaProxy) {
      restClientBuilder.setPathPrefix(hostInfo.uri.getPath)
    }
    this.initKibanaProxy(hostInfo.kibanaProxy, sslContext, restClientBuilder, credentialsProvider)
    val client = restClientBuilder.build()
    EDQLClient(client, hostInfo.kibanaProxy, hostInfo.uri.getPath)
  }

  private def initSSLContext(restClientBuilder: RestClientBuilder) = {
    val sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, Array[TrustManager](UnsafeX509ExtendedTrustManager.INSTANCE), null);
    restClientBuilder.setHttpClientConfigCallback(
      new RestClientBuilder.HttpClientConfigCallback() {
        override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
          httpClientBuilder.setSSLContext(sslContext)
            .useSystemProperties()
            .setDefaultIOReactorConfig(IOReactorConfig.custom()
              .setSoKeepAlive(true)
              .build())
            .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
              override def getKeepAliveDuration(response: HttpResponse, context: HttpContext): Long = (30 minutes).toMillis
            })
            .setConnectionTimeToLive(120, TimeUnit.SECONDS)
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        }
      })
    sslContext
  }

  private def initKibanaProxy(kibanaProxy: Boolean,
                              sslContext: SSLContext,
                              restClientBuilder: RestClientBuilder,
                              credentialsProvider: CredentialsProvider): Unit = {
    if (!kibanaProxy) {
      return
    }

    restClientBuilder.setHttpClientConfigCallback(
      new RestClientBuilder.HttpClientConfigCallback() {
        override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
          if (credentialsProvider != null) {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
          }
          httpClientBuilder.addInterceptorLast(new KibanaProxyApacheInterceptor)
            .useSystemProperties()
            .setDefaultIOReactorConfig(IOReactorConfig.custom()
              .setSoKeepAlive(true)
              .build())
            .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
              override def getKeepAliveDuration(response: HttpResponse, context: HttpContext): Long = (30 minutes).toMillis
            })
            .setConnectionTimeToLive(120, TimeUnit.SECONDS)
            .setSSLContext(sslContext)
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        }
      })
  }

  private def initAuthInfo(uri: URI, authInfo: Option[AuthInfo],
                           sslContext: SSLContext,
                           restClientBuilder: RestClientBuilder): CredentialsProvider = {
    if (authInfo.isEmpty) {
      return null
    }

    val a = authInfo.get
    if (a.auth != null) {
      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", a.auth)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    }

    if (a.apiKeyId != null && a.apiKeySecret != null) {
      val apiKeyAuth = Base64.getEncoder.encodeToString((a.apiKeyId + ":" + a.apiKeySecret).getBytes(StandardCharsets.UTF_8))

      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", "ApiKey " + apiKeyAuth)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    }

    var credentialsProvider: CredentialsProvider = null
    if (StringUtils.isNotBlank(uri.getUserInfo)) {
      credentialsProvider = new SystemDefaultCredentialsProvider
      credentialsProvider.setCredentials(new AuthScope(uri.getHost, uri.getPort, null, null), new UsernamePasswordCredentials(uri.getUserInfo))
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.disableAuthCaching
              .useSystemProperties()
              .setDefaultIOReactorConfig(IOReactorConfig.custom()
                .setSoKeepAlive(true)
                .build())
              .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                override def getKeepAliveDuration(response: HttpResponse, context: HttpContext): Long = (30 minutes).toMillis
              })
              .setConnectionTimeToLive(120, TimeUnit.SECONDS)
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          }
        })
    }

    if (a.username != null && a.password != null) {
      credentialsProvider = new SystemDefaultCredentialsProvider
      credentialsProvider.setCredentials(new AuthScope(uri.getHost, uri.getPort, null, null), new UsernamePasswordCredentials(a.username, a.password))
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.disableAuthCaching
              .useSystemProperties()
              .setDefaultIOReactorConfig(IOReactorConfig.custom()
                .setSoKeepAlive(true)
                .build())
              .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                override def getKeepAliveDuration(response: HttpResponse, context: HttpContext): Long = (30 minutes).toMillis
              })
              .setConnectionTimeToLive(120, TimeUnit.SECONDS)
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
              .useSystemProperties()
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          }
        })
    }

    if (a.awsRegion != null) {
      val signer = new AWS4Signer
      val serviceName = "es"
      signer.setServiceName(serviceName)
      signer.setRegionName(a.awsRegion)

      val credentialsProvider = Option.apply(a.apiKeyId).map(i => {
        new EDQLAWSCredentialsProviderChain(i, a.apiKeySecret, a.apiSessionToken)
      }).getOrElse(new EDQLAWSCredentialsProviderChain())

      val interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider)
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.addInterceptorLast(interceptor)
              .useSystemProperties()
              .setDefaultIOReactorConfig(IOReactorConfig.custom()
                .setSoKeepAlive(true)
                .build())
              .setSSLContext(sslContext)
              .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                override def getKeepAliveDuration(response: HttpResponse, context: HttpContext): Long = (30 minutes).toMillis
              })
              .setConnectionTimeToLive(120, TimeUnit.SECONDS)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          }
        })
    }

    credentialsProvider
  }
}
