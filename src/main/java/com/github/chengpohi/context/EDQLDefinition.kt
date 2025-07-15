package com.github.chengpohi.context

import com.github.chengpohi.edql.parser.json.JsonCollection
import com.github.chengpohi.script.ScriptContext
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Request
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object EDQLDefinition {
    const val KIBANA_PROXY_METHOD: String = "KIBANA_PROXY_METHOD"
    const val KIBANA_PATH_PREFIX: String = "KIBANA_PATH_PREFIX"
    const val READONLY_MODE: String = "readonly mode"

    data class GetActionDefinition(
        val path: String,
        val action: JsonCollection.Val?
    ) : Definition<String> {
        override suspend fun execute(context: ScriptContext): String = withContext(Dispatchers.IO) {
            val asObj = action?.let { if (it is JsonCollection.Obj) it else null }
            val request = Request(if (context.kibanaProxy) "POST" else "GET", path)

            val builder = RequestOptions.DEFAULT.toBuilder()
            builder.addHeader("Content-Type", "application/json")
            if (context.kibanaProxy) {
                builder.addHeader(KIBANA_PROXY_METHOD, "GET")
                    .addHeader(KIBANA_PATH_PREFIX, context.pathPrefix)
            }
            request.options = builder.build()

            asObj?.let {
                request.setJsonEntity(it.toJson() + System.lineSeparator())
            }

            try {
                val entity = context.client.restClient.performRequest(request).entity
                EntityUtils.toString(entity)
            } catch (ex: ResponseException) {
                val responseEntityStr = EntityUtils.toString(ex.response.entity)
                if (responseEntityStr.isBlank()) {
                    ex.message ?: ""
                } else {
                    responseEntityStr
                }
            }
        }

        override fun json(context: ScriptContext): String {
            return runBlocking { execute(context) }
        }
    }

    data class HeadActionDefinition(
        val path: String,
        val action: String?
    ) : Definition<String> {
        override suspend fun execute(context: ScriptContext): String = withContext(Dispatchers.IO) {
            if (readOnlyPath(path, context.readOnly)) {
                throw RuntimeException(READONLY_MODE)
            }

            val request = Request(if (context.kibanaProxy) "POST" else "HEAD", path)
            if (context.kibanaProxy) {
                request.options = RequestOptions.DEFAULT.toBuilder()
                    .addHeader(KIBANA_PROXY_METHOD, "HEAD")
                    .addHeader(KIBANA_PATH_PREFIX, context.pathPrefix)
                    .build()
            }
            request.setJsonEntity(action)

            try {
                val entity = context.client.restClient.performRequest(request).entity
                EntityUtils.toString(entity)
            } catch (ex: ResponseException) {
                val responseEntityStr = EntityUtils.toString(ex.response.entity)
                if (responseEntityStr.isBlank()) {
                    ex.message ?: ""
                } else {
                    responseEntityStr
                }
            }
        }

        override fun json(context: ScriptContext): String {
            return runBlocking { execute(context) }
        }
    }

    data class PostActionDefinition(
        val path: String,
        val action: List<JsonCollection.Val>
    ) : Definition<String> {
        override suspend fun execute(context: ScriptContext): String = withContext(Dispatchers.IO) {
            if (readOnlyPath(path, context.readOnly)) {
                throw RuntimeException(READONLY_MODE)
            }

            val request = Request("POST", path)

            if (context.kibanaProxy) {
                request.options = RequestOptions.DEFAULT.toBuilder()
                    .addHeader(KIBANA_PROXY_METHOD, "POST")
                    .addHeader(KIBANA_PATH_PREFIX, context.pathPrefix)
                    .build()
            }

            val asObj = action.filterIsInstance<JsonCollection.Obj>()
            when {
                asObj.isEmpty() -> {
                    // Do nothing
                }
                asObj.size > 1 -> {
                    request.setJsonEntity(
                        asObj.joinToString(System.lineSeparator()) { trimQueryClause(it).toJson() } + System.lineSeparator()
                    )
                }
                else -> {
                    val h = trimQueryClause(asObj.first())
                    request.setJsonEntity(h.toJson() + System.lineSeparator())
                }
            }

            try {
                val entity = context.client.restClient.performRequest(request).entity
                EntityUtils.toString(entity)
            } catch (ex: ResponseException) {
                val responseEntityStr = EntityUtils.toString(ex.response.entity)
                if (responseEntityStr.isBlank()) {
                    ex.message ?: ""
                } else {
                    responseEntityStr
                }
            }
        }

        override fun json(context: ScriptContext): String {
            return runBlocking { execute(context) }
        }
    }

    data class PutActionDefinition(
        val path: String,
        val action: List<JsonCollection.Val>
    ) : Definition<String> {
        override suspend fun execute(context: ScriptContext): String = withContext(Dispatchers.IO) {
            if (context.readOnly) {
                throw RuntimeException(READONLY_MODE)
            }
            val request = Request(if (context.kibanaProxy) "POST" else "PUT", path)
            if (context.kibanaProxy) {
                request.options = RequestOptions.DEFAULT.toBuilder()
                    .addHeader(KIBANA_PROXY_METHOD, "PUT")
                    .addHeader(KIBANA_PATH_PREFIX, context.pathPrefix)
                    .build()
            }
            val asObj = action.filterIsInstance<JsonCollection.Obj>()
            when {
                asObj.isEmpty() -> {
                    // Do nothing
                }
                asObj.size > 1 -> {
                    request.setJsonEntity(
                        asObj.joinToString(System.lineSeparator()) { trimQueryClause(it).toJson() } + System.lineSeparator()
                    )
                }
                else -> {
                    val h = trimQueryClause(asObj.first())
                    request.setJsonEntity(h.toJson() + System.lineSeparator())
                }
            }

            try {
                val entity = context.client.restClient.performRequest(request).entity
                EntityUtils.toString(entity)
            } catch (ex: ResponseException) {
                val responseEntityStr = EntityUtils.toString(ex.response.entity)
                if (responseEntityStr.isBlank()) {
                    ex.message ?: ""
                } else {
                    responseEntityStr
                }
            }
        }

        override fun json(context: ScriptContext): String {
            return runBlocking { execute(context) }
        }
    }

    data class DeleteActionDefinition(
        val path: String,
        val action: String?
    ) : Definition<String> {
        override suspend fun execute(context: ScriptContext): String = withContext(Dispatchers.IO) {
            if (context.readOnly) {
                throw RuntimeException(READONLY_MODE)
            }
            val request = Request(if (context.kibanaProxy) "POST" else "DELETE", path)
            if (context.kibanaProxy) {
                request.options = RequestOptions.DEFAULT.toBuilder()
                    .addHeader(KIBANA_PROXY_METHOD, "DELETE")
                    .addHeader(KIBANA_PATH_PREFIX, context.pathPrefix)
                    .build()
            }
            request.setJsonEntity(action)

            try {
                val entity = context.client.restClient.performRequest(request).entity
                EntityUtils.toString(entity)
            } catch (ex: ResponseException) {
                val responseEntityStr = EntityUtils.toString(ex.response.entity)
                if (responseEntityStr.isBlank()) {
                    ex.message ?: ""
                } else {
                    responseEntityStr
                }
            }
        }

        override fun json(context: ScriptContext): String {
            return runBlocking { execute(context) }
        }
    }

    private fun readOnlyPath(path: String, readonly: Boolean): Boolean {
        if (!readonly) {
            return false
        }
        return path.contains("/_update") || path.contains("/_delete") || path.contains("/_bulk")
    }

    private fun trimQueryClause(i: JsonCollection.Obj): JsonCollection.Obj {
        val emptyQueryClause = i.get("query")?.let { value ->
            when (value) {
                is JsonCollection.Var -> value.realValue
                else -> value
            }
        }?.let { it is JsonCollection.Obj && it.value.isEmpty() } ?: false

        return if (emptyQueryClause) {
            i.remove("query")
        } else {
            i
        }
    }
} 