package com.github.chengpohi.script

import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.collection.JsonCollection

import java.net.URI
import scala.collection.mutable

case class ScriptEQLContext(endpoint: String,
                            uri: URI,
                            auth: Option[String],
                            username: Option[String],
                            password: Option[String],
                            apiKeyId: Option[String],
                            apiKeySecret: Option[String],
                            apiSessionToken: Option[String],
                            awsRegion: Option[String],
                            timeout: Option[Int],
                            kibanaProxy: Boolean) extends EQLConfig with EQLContext {
  override implicit lazy val eqlClient: EQLClient =
    buildRestClient(uri,
      auth,
      username,
      password,
      apiKeyId,
      apiKeySecret,
      apiSessionToken,
      awsRegion,
      timeout, kibanaProxy)
}

object ScriptEQLContext {
  val cache: mutable.Map[String, ScriptEQLContext] = mutable.Map[String, ScriptEQLContext]()

  def apply(endpoint: String,
            auth: Option[String] = None,
            username: Option[String] = None,
            password: Option[String] = None,
            apiKeyId: Option[String] = None,
            apiKeySecret: Option[String] = None,
            apiSessionToken: Option[String] = None,
            apiRegion: Option[String] = None,
            timeout: Option[Int],
            vars: Map[String, JsonCollection.Val],
            kibanaProxy: Boolean): ScriptEQLContext = {
    val uri = URI.create(endpoint)

    val cacheKey = s"$endpoint-${auth.getOrElse("")}-${username.getOrElse("")}" +
      s"-${password.getOrElse("")}" +
      s"-${apiKeyId.getOrElse("")}" +
      s"-${apiKeySecret.getOrElse("")}" +
      s"-${apiSessionToken.getOrElse("")}" +
      s"-${apiRegion.getOrElse("")}" +
      s"-${timeout.getOrElse("")}"
    val cacheContext = cache.get(cacheKey)
    if (isCacheValid(cacheContext)) {
      val c = cacheContext.get
      c.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
      return c
    }
    val context = new ScriptEQLContext(endpoint, uri,
      auth,
      username,
      password,
      apiKeyId,
      apiKeySecret,
      apiSessionToken,
      apiRegion,
      timeout,
      kibanaProxy
    )
    context.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
    cache.put(cacheKey, context)
    context
  }

  private def isCacheValid(cacheContext: Option[ScriptEQLContext]) = {
    cacheContext match {
      case None => false
      case Some(c) => {
        c.eqlClient.restClient.isRunning
      }
    }
  }
}

