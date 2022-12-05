package com.github.chengpohi.context

import com.amazonaws.auth.AWS4Signer
import com.github.chengpohi.aws.{AWSRequestSigningApacheInterceptor, EDQLAWSCredentialsProviderChain, UnsafeX509ExtendedTrustManager}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.http.KibanaProxyApacheInterceptor
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang3.StringUtils
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.message.BasicHeader
import org.apache.http.{Header, HttpHost}
import org.elasticsearch.client.{RestClient, RestClientBuilder}

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.net.ssl.{SSLContext, TrustManager}

/**
 * eql
 * Created by chengpohi on 16/06/16.
 */
trait EQLConfig {
  lazy val config: Config =
    ConfigFactory.load("eql.conf").getConfig("eql")


  def buildClient(config: Config): EQLClient =
    buildRestClient(URI.create(config.getString("host")))

  def buildRestClient(uri: URI,
                      auth: Option[String] = None,
                      username: Option[String] = None,
                      password: Option[String] = None,
                      apiKeyId: Option[String] = None,
                      apiKeySecret: Option[String] = None,
                      apiSessionToken: Option[String] = None,
                      awsRegion: Option[String] = None,
                      timeout: Option[Int] = None,
                      kibanaProxy: Boolean = false): EQLClient = {
    val restClientBuilder =
      RestClient.builder(new HttpHost(uri.getHost, uri.getPort, uri.getScheme))
        .setRequestConfigCallback(
          new RestClientBuilder.RequestConfigCallback() {
            override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = return requestConfigBuilder
              .setConnectTimeout(timeout.getOrElse(5000))
              .setSocketTimeout(timeout.getOrElse(5000));
          })

    val sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, Array[TrustManager](UnsafeX509ExtendedTrustManager.INSTANCE), null);
    restClientBuilder.setHttpClientConfigCallback(
      new RestClientBuilder.HttpClientConfigCallback() {
        override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
          httpClientBuilder.setSSLContext(sslContext)
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        }
      })

    if (auth.isDefined) {
      val defaultHeaders = Array[Header](
        new BasicHeader("Authorization", auth.get)
      )
      restClientBuilder.setDefaultHeaders(defaultHeaders)
    }

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
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
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
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          }
        })
    }

    if (awsRegion.isDefined) {
      val signer = new AWS4Signer
      val serviceName = "es"
      signer.setServiceName(serviceName)
      signer.setRegionName(awsRegion.get)

      val credentialsProvider = apiKeyId.map(i => {
        new EDQLAWSCredentialsProviderChain(
          i,
          apiKeySecret.orNull,
          apiSessionToken.orNull)
      }).getOrElse(new EDQLAWSCredentialsProviderChain())

      val interceptor =
        new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider)
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.addInterceptorLast(interceptor)
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          }
        })
    }
    if (kibanaProxy) {
      restClientBuilder.setHttpClientConfigCallback(
        new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
            httpClientBuilder.addInterceptorLast(new KibanaProxyApacheInterceptor)
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          }
        })
    }

    EQLClient(restClientBuilder.build())
  }
}
