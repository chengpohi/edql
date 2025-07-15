package com.github.chengpohi.context

import java.net.URI

data class AuthInfo(
    val auth: String?,
    val cookie: String?,
    val username: String?,
    val password: String?,
    val apiKeyId: String?,
    val apiKeySecret: String?,
    val awsService: String?,
    val awsRegion: String?,
    val awsProfile: String?
) {
    fun cacheKey(): String {
        return buildString {
            append(auth ?: "")
            append("-")
            append(username ?: "")
            append("-")
            append(apiKeyId ?: "")
            append("-")
            append(apiKeySecret ?: "")
            append("-")
            append(awsService ?: "")
            append("-")
            append(awsRegion ?: "")
            append("-")
            append(awsProfile ?: "")
            append("-")
            append(cookie ?: "")
        }
    }
}

data class ProxyInfo(
    val httpHost: String?,
    val httpPort: Int?,
    val username: String?,
    val password: String?
) {
    fun cacheKey(): String {
        return buildString {
            append(httpHost ?: "")
            append("-")
            append(httpPort ?: "")
            append("-")
            append(username ?: "")
            append("-")
            append(password ?: "")
        }
    }
}

data class HostInfo(
    val host: String,
    val uri: URI,
    val timeout: Int = 5000,
    val kibanaProxy: Boolean = false,
    val readOnly: Boolean = false,
    val authInfo: AuthInfo? = null,
    val proxyInfo: ProxyInfo? = null,
    val version: String? = null,
    val name: String? = null
) 