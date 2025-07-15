package com.github.chengpohi.script

import com.github.chengpohi.aws.AWSRequestSigningApacheInterceptor
import com.github.chengpohi.aws.UnsafeX509ExtendedTrustManager
import com.github.chengpohi.context.HostInfo
import com.github.chengpohi.http.KibanaProxyApacheInterceptor
import org.apache.commons.lang3.StringUtils
import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.cookie.ClientCookie
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.LaxRedirectStrategy
import org.apache.http.impl.client.SystemDefaultCredentialsProvider
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.impl.nio.reactor.IOReactorConfig
import org.apache.http.message.BasicHeader
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import kotlin.time.Duration.Companion.minutes

class EDQLClient(val hostInfo: HostInfo) {
    val restClient: RestClient = buildRestClient(hostInfo)

    fun shutdown() {
        restClient.close()
    }

    fun isRunning(): Boolean = restClient.isRunning

    private fun buildRestClient(hostInfo: HostInfo): RestClient {
        val restClientBuilder = RestClient.builder(
            HttpHost(hostInfo.uri.host, hostInfo.uri.port, hostInfo.uri.scheme)
        ).setRequestConfigCallback(
            RestClientBuilder.RequestConfigCallback { requestConfigBuilder ->
                requestConfigBuilder
                    .setConnectTimeout(hostInfo.timeout)
                    .setConnectionRequestTimeout(hostInfo.timeout)
                    .setSocketTimeout(hostInfo.timeout)
            }
        )

        val sslContext: SSLContext = initSSLContext(hostInfo, restClientBuilder)
        val credentialsProvider = initAuthInfo(hostInfo, sslContext, restClientBuilder)

        if (StringUtils.isNotBlank(hostInfo.uri.path) &&
            !StringUtils.equals(hostInfo.uri.path, "/") &&
            !hostInfo.kibanaProxy
        ) {
            restClientBuilder.setPathPrefix(hostInfo.uri.path)
        }

        initKibanaProxy(hostInfo, sslContext, restClientBuilder, credentialsProvider)
        return restClientBuilder.build()
    }

    private fun initSSLContext(hostInfo: HostInfo, restClientBuilder: RestClientBuilder): SSLContext {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(UnsafeX509ExtendedTrustManager.INSTANCE), null)
        restClientBuilder.setHttpClientConfigCallback(
            RestClientBuilder.HttpClientConfigCallback { httpClientBuilder ->
                if (hostInfo.proxyInfo != null) {
                    val proxyInfo = hostInfo.proxyInfo
                    httpClientBuilder.setProxy(HttpHost(proxyInfo.httpHost, proxyInfo.httpPort!!, "http"))
                    if (proxyInfo.username != null) {
                        val credentialsProvider = SystemDefaultCredentialsProvider()
                        credentialsProvider.setCredentials(
                            AuthScope(proxyInfo.httpHost, proxyInfo.httpPort),
                            UsernamePasswordCredentials(proxyInfo.username, proxyInfo.password)
                        )
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                    }
                }

                httpClientBuilder.setSSLContext(sslContext)
                    .setDefaultIOReactorConfig(
                        IOReactorConfig.custom()
                            .setSoKeepAlive(true)
                            .build()
                    )
                    .setKeepAliveStrategy(ConnectionKeepAliveStrategy { response, context ->
                        (30.minutes).inWholeMilliseconds
                    })
                    .setConnectionTimeToLive(120, TimeUnit.SECONDS)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            }
        )
        return sslContext
    }

    private fun initKibanaProxy(
        hostInfo: HostInfo,
        sslContext: SSLContext,
        restClientBuilder: RestClientBuilder,
        credentialsProvider: CredentialsProvider?
    ) {
        if (!hostInfo.kibanaProxy) {
            return
        }

        restClientBuilder.setHttpClientConfigCallback(
            RestClientBuilder.HttpClientConfigCallback { httpClientBuilder ->
                if (credentialsProvider != null) {
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                }
                if (hostInfo.authInfo != null) {
                    val cookie = hostInfo.authInfo.cookie
                    if (cookie != null) {
                        val cookies = cookie.split(";\\s+".toRegex())
                        val cookieStore = BasicCookieStore()
                        cookies.forEach { c ->
                            val parts = c.split("=")
                            if (parts.size == 2) {
                                val clientCookie = BasicClientCookie(parts[0], parts[1])
                                clientCookie.domain = hostInfo.uri.host
                                clientCookie.path = "/"
                                clientCookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true")
                                clientCookie.isSecure = true
                                cookieStore.addCookie(clientCookie)
                            } else {
                                val clientCookie = BasicClientCookie(parts[0], "")
                                clientCookie.domain = hostInfo.uri.host
                                clientCookie.path = "/"
                                clientCookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true")
                                clientCookie.isSecure = true
                                cookieStore.addCookie(clientCookie)
                            }
                        }
                        httpClientBuilder.setDefaultCookieStore(cookieStore)
                        httpClientBuilder.setRedirectStrategy(LaxRedirectStrategy())
                    }
                }
                httpClientBuilder.addInterceptorLast(KibanaProxyApacheInterceptor())
                    .setDefaultIOReactorConfig(
                        IOReactorConfig.custom()
                            .setSoKeepAlive(true)
                            .build()
                    )
                    .setKeepAliveStrategy(ConnectionKeepAliveStrategy { response, context ->
                        (30.minutes).inWholeMilliseconds
                    })
                    .setConnectionTimeToLive(120, TimeUnit.SECONDS)
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)

                if (hostInfo.proxyInfo != null) {
                    val proxyInfo = hostInfo.proxyInfo
                    httpClientBuilder.setProxy(HttpHost(proxyInfo.httpHost, proxyInfo.httpPort!!, "http"))
                    if (proxyInfo.username != null && credentialsProvider != null) {
                        credentialsProvider.setCredentials(
                            AuthScope(proxyInfo.httpHost, proxyInfo.httpPort),
                            UsernamePasswordCredentials(proxyInfo.username, proxyInfo.password)
                        )
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                    }
                }
                httpClientBuilder
            }
        )
    }

    private fun initAuthInfo(
        hostInfo: HostInfo,
        sslContext: SSLContext,
        restClientBuilder: RestClientBuilder
    ): CredentialsProvider? {
        if (hostInfo.authInfo == null) {
            return null
        }

        val a = hostInfo.authInfo
        if (a.auth != null) {
            val defaultHeaders = arrayOf<Header>(
                BasicHeader("Authorization", a.auth)
            )
            restClientBuilder.setDefaultHeaders(defaultHeaders)
        }

        if (a.apiKeyId != null && a.apiKeySecret != null) {
            val apiKeyAuth = java.util.Base64.getEncoder()
                .encodeToString("${a.apiKeyId}:${a.apiKeySecret}".toByteArray(StandardCharsets.UTF_8))
            val defaultHeaders = arrayOf<Header>(
                BasicHeader("Authorization", "ApiKey $apiKeyAuth")
            )
            restClientBuilder.setDefaultHeaders(defaultHeaders)
        }
        val uri = hostInfo.uri

        var credentialsProvider: CredentialsProvider? = null
        if (StringUtils.isNotBlank(hostInfo.uri.userInfo)) {
            credentialsProvider = SystemDefaultCredentialsProvider()
            credentialsProvider.setCredentials(
                AuthScope(uri.host, uri.port, null, null),
                UsernamePasswordCredentials(uri.userInfo)
            )
            restClientBuilder.setHttpClientConfigCallback(
                RestClientBuilder.HttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder.disableAuthCaching()
                        .setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                .setSoKeepAlive(true)
                                .build()
                        )
                        .setKeepAliveStrategy(ConnectionKeepAliveStrategy { response, context ->
                            (30.minutes).inWholeMilliseconds
                        })
                        .setConnectionTimeToLive(120, TimeUnit.SECONDS)

                    if (hostInfo.proxyInfo != null) {
                        val proxyInfo = hostInfo.proxyInfo
                        httpClientBuilder.setProxy(HttpHost(proxyInfo.httpHost, proxyInfo.httpPort!!, "http"))
                        if (proxyInfo.username != null && credentialsProvider != null) {
                            (credentialsProvider as SystemDefaultCredentialsProvider).setCredentials(
                                AuthScope(proxyInfo.httpHost, proxyInfo.httpPort),
                                UsernamePasswordCredentials(proxyInfo.username, proxyInfo.password)
                            )
                        }
                    }

                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                }
            )
        }

        if (a.username != null && a.password != null) {
            credentialsProvider = SystemDefaultCredentialsProvider()
            credentialsProvider.setCredentials(
                AuthScope(uri.host, uri.port, null, null),
                UsernamePasswordCredentials(a.username, a.password)
            )
            restClientBuilder.setHttpClientConfigCallback(
                RestClientBuilder.HttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder.disableAuthCaching()
                        .setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                .setSoKeepAlive(true)
                                .build()
                        )
                        .setKeepAliveStrategy(ConnectionKeepAliveStrategy { response, context ->
                            (30.minutes).inWholeMilliseconds
                        })
                        .setConnectionTimeToLive(120, TimeUnit.SECONDS)

                    if (hostInfo.proxyInfo != null) {
                        val proxyInfo = hostInfo.proxyInfo
                        httpClientBuilder.setProxy(HttpHost(proxyInfo.httpHost, proxyInfo.httpPort!!, "http"))
                        if (proxyInfo.username != null && credentialsProvider != null) {
                            credentialsProvider.setCredentials(
                                AuthScope(proxyInfo.httpHost, proxyInfo.httpPort),
                                UsernamePasswordCredentials(proxyInfo.username, proxyInfo.password)
                            )
                        }
                    }

                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                }
            )
        }

        if (a.awsProfile != null) {
            System.setProperty("AWS_PROFILE", a.awsProfile)
            System.setProperty("aws.profile", a.awsProfile)
        }

        if (a.awsRegion != null) {
            val service = if (a.awsService.isNullOrBlank()) "es" else a.awsService
            val awsCredentialsProvider = a.apiKeyId?.let { id ->
                val credentials = AwsBasicCredentials.builder()
                    .accessKeyId(id)
                    .secretAccessKey(a.apiKeySecret)
                    .build()
                StaticCredentialsProvider.create(credentials)
            } ?: DefaultCredentialsProvider.create()

            val interceptor = AWSRequestSigningApacheInterceptor(
                service,
                AwsV4HttpSigner.create(),
                awsCredentialsProvider,
                a.awsRegion
            )
            restClientBuilder.setHttpClientConfigCallback(
                RestClientBuilder.HttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder.addInterceptorLast(interceptor)
                        .setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                .setSoKeepAlive(true)
                                .build()
                        )
                        .setSSLContext(sslContext)
                        .setKeepAliveStrategy(ConnectionKeepAliveStrategy { response, context ->
                            (30.minutes).inWholeMilliseconds
                        })
                        .setConnectionTimeToLive(120, TimeUnit.SECONDS)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)

                    if (hostInfo.proxyInfo != null) {
                        val proxyInfo = hostInfo.proxyInfo
                        httpClientBuilder.setProxy(HttpHost(proxyInfo.httpHost, proxyInfo.httpPort!!, "http"))
                        if (proxyInfo.username != null) {
                            val proxyCredentialsProvider = SystemDefaultCredentialsProvider()
                            proxyCredentialsProvider.setCredentials(
                                AuthScope(proxyInfo.httpHost, proxyInfo.httpPort),
                                UsernamePasswordCredentials(proxyInfo.username, proxyInfo.password)
                            )
                            httpClientBuilder.setDefaultCredentialsProvider(proxyCredentialsProvider)
                        }
                    }
                    httpClientBuilder
                }
            )
        }
        return credentialsProvider
    }

}