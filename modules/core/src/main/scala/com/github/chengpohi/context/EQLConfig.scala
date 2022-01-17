package com.github.chengpohi.context

import com.github.chengpohi.dsl.EQLClient
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang3.StringUtils
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.message.BasicHeader
import org.apache.http.{Header, HttpHost}
import org.apache.logging.log4j.LogManager
import org.elasticsearch.client.{RestClient, RestClientBuilder}

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * eql
 * Created by chengpohi on 16/06/16.
 */
trait EQLConfig {
  lazy val config: Config =
    ConfigFactory.load("eql.conf").getConfig("eql")


  def buildClient(config: Config): EQLClient =
    buildRestClient(new URI(config.getString("host")))

  def buildRestClient(uri: URI,
                      auth: Option[String] = None,
                      username: Option[String] = None,
                      password: Option[String] = None,
                      apiKeyId: Option[String] = None,
                      apiKeySecret: Option[String] = None,
                      timeout: Option[Int] = None) = {
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


    EQLClient(restClientBuilder.build())
  }
}
