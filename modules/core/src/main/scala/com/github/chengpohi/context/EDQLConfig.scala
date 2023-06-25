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
import org.apache.http.impl.client.BasicCredentialsProvider
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

case class AuthInfo(auth: Option[String] = None,
                    username: Option[String] = None,
                    password: Option[String] = None,
                    apiKeyId: Option[String] = None,
                    apiKeySecret: Option[String] = None,
                    apiSessionToken: Option[String] = None,
                    awsRegion: Option[String] = None) {

  def cacheKey: String = {
    s"""
       |${auth.getOrElse("")} - ${username.getOrElse("")}
       |-${apiKeyId.getOrElse("")} -${apiKeySecret.getOrElse("")}
       |-${apiSessionToken.getOrElse("")} -${awsRegion.getOrElse("")}
       |""".stripMargin
  }
}

trait EDQLConfig {
  lazy val config: Config =
    ConfigFactory.load("eql.conf").getConfig("eql")

  def buildClient(config: Config): EDQLClient =
    buildRestClient(URI.create(config.getString("host")))

  def buildRestClient(uri: URI,
                      authInfo: Option[AuthInfo] = None,
                      timeout: Option[Int] = None,
                      kibanaProxy: Boolean = false): EDQLClient = {
    val restClientBuilder =
      RestClient.builder(new HttpHost(uri.getHost, uri.getPort, uri.getScheme))
        .setRequestConfigCallback(
          new RestClientBuilder.RequestConfigCallback() {
            override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = {
              return requestConfigBuilder
                .setConnectTimeout(timeout.getOrElse(5000))
                .setConnectionRequestTimeout(timeout.getOrElse(5000))
                .setSocketTimeout(timeout.getOrElse(5000))
            }
          })

    val sslContext: SSLContext = initSSLContext(restClientBuilder)
    val credentialsProvider = initAuthInfo(uri, authInfo, sslContext, restClientBuilder)
    if (StringUtils.isNotBlank(uri.getPath) && !StringUtils.equals(uri.getPath, "/") && !kibanaProxy) {
      restClientBuilder.setPathPrefix(uri.getPath)
    }
    this.initKibanaProxy(kibanaProxy, sslContext, restClientBuilder, credentialsProvider)
    val client = restClientBuilder.build()
    EDQLClient(client, kibanaProxy, uri.getPath)
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
                           sslContext: SSLContext, restClientBuilder: RestClientBuilder): CredentialsProvider = {
    if (authInfo.isEmpty) {
      return null
    }

    val a = authInfo.get
    if (a.auth.isDefined) {
      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", a.auth.get)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    }

    if (a.apiKeyId.isDefined && a.apiKeySecret.isDefined) {
      val apiKeyAuth = Base64.getEncoder.encodeToString((a.apiKeyId.get + ":" + a.apiKeySecret.get).getBytes(StandardCharsets.UTF_8))

      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", "ApiKey " + apiKeyAuth)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    }

    var credentialsProvider: BasicCredentialsProvider = null
    if (StringUtils.isNotBlank(uri.getUserInfo)) {
      credentialsProvider = new BasicCredentialsProvider
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(uri.getUserInfo))
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

    if (a.username.isDefined && a.password.isDefined) {
      credentialsProvider = new BasicCredentialsProvider
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(a.username.get, a.password.get))
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

    if (a.awsRegion.isDefined) {
      val signer = new AWS4Signer
      val serviceName = "es"
      signer.setServiceName(serviceName)
      signer.setRegionName(a.awsRegion.get)

      val credentialsProvider = a.apiKeyId.map(i => {
        new EDQLAWSCredentialsProviderChain(
          i,
          a.apiKeySecret.orNull,
          a.apiSessionToken.orNull)
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
