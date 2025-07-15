package com.github.chengpohi.script

import com.github.chengpohi.context.HostInfo
import com.github.chengpohi.edql.parser.json.JsonCollection
import org.apache.commons.codec.binary.Base64
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ScriptContext(val hostInfo: HostInfo) {
    val client: EDQLClient by lazy { EDQLClient(hostInfo) }
    var variables: MutableMap<String, JsonCollection.Val> = mutableMapOf()
    val timeout: Duration = hostInfo.timeout.milliseconds
    val kibanaProxy: Boolean = hostInfo.kibanaProxy
    val readOnly: Boolean = hostInfo.readOnly
    val pathPrefix: String = hostInfo.uri.path

    fun clear() {
        variables.clear()
    }

    companion object {
        val cache: MutableMap<String, Pair<Long, ScriptContext>> = ConcurrentHashMap()

        fun apply(
            hostInfo: HostInfo,
            vars: Map<String, JsonCollection.Val>
        ): ScriptContext {
            val cacheInfo = getCacheConnection(hostInfo)
            val cacheKey = cacheInfo.first
            val cacheContext = cacheInfo.second
            if (cacheContext != null) {
                val c = cacheContext.second
                c.variables = vars.toMutableMap()
                return c
            }

            val context = ScriptContext(hostInfo)
            context.variables = vars.toMutableMap()
            cache[cacheKey] = Pair(System.currentTimeMillis(), context)
            return context
        }

        fun getCacheConnection(hostInfo: HostInfo): Pair<String, Pair<Long, ScriptContext>?> {
            val cacheKey = buildString {
                append(hostInfo.host)
                append("-")
                append(hostInfo.authInfo?.cacheKey() ?: "")
                append("-")
                append(hostInfo.timeout)
                append("-")
                append(hostInfo.kibanaProxy)
                append("-")
                append(hostInfo.readOnly)
                append("-")
                append(hostInfo.proxyInfo?.cacheKey() ?: "")
            }

            val cacheContext = cache[cacheKey]
            return when {
                cacheContext == null -> Pair(cacheKey, null)
                cacheContext.second.client.isRunning() -> Pair(cacheKey, cacheContext)
                else -> Pair(cacheKey, null)
            }
        }

        fun removeCacheConnection(hostInfo: HostInfo) {
            val keyBytes = buildString {
                append(hostInfo)
                append(".endpoint-")
                append(hostInfo.authInfo?.cacheKey() ?: "")
                append("-")
                append(hostInfo.timeout)
                append("-")
                append(hostInfo.kibanaProxy)
                append("-")
                append(hostInfo.readOnly)
                append("-")
                append(hostInfo.proxyInfo?.cacheKey() ?: "")
            }.toByteArray()

            val cacheKey = Base64.encodeBase64String(keyBytes)
            cache.remove(cacheKey)
        }
    }
} 