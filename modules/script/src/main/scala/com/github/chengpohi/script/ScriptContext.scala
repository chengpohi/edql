package com.github.chengpohi.script

import com.github.chengpohi.context.{Context, EDQLClient, EDQLConfig}
import com.github.chengpohi.parser.collection.JsonCollection

import java.net.URI
import scala.collection.mutable
import scala.concurrent.duration
import scala.concurrent.duration.{Duration, DurationInt}

case class ScriptContext(endpoint: String,
                         uri: URI,
                         auth: Option[String],
                         username: Option[String],
                         password: Option[String],
                         apiKeyId: Option[String],
                         apiKeySecret: Option[String],
                         apiSessionToken: Option[String],
                         awsRegion: Option[String],
                         timeout: Option[Int],
                         override val kibanaProxy: Boolean) extends EDQLConfig with Context {
  override implicit val resultTimeout: Duration = Duration.apply(timeout.getOrElse(5000), duration.MILLISECONDS)

  override implicit lazy val eqlClient: EDQLClient =
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

object ScriptContext {
  val cache: mutable.Map[String, (Long, ScriptContext)] = mutable.Map[String, (Long, ScriptContext)]()

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
            kibanaProxy: Boolean): ScriptContext = {
    val uri = URI.create(endpoint)

    val cacheKey = s"$endpoint-${auth.getOrElse("")}-${username.getOrElse("")}" +
      s"-${password.getOrElse("")}" +
      s"-${apiKeyId.getOrElse("")}" +
      s"-${apiKeySecret.getOrElse("")}" +
      s"-${apiSessionToken.getOrElse("")}" +
      s"-${apiRegion.getOrElse("")}" +
      s"-${timeout.getOrElse("")}" +
      s"-${kibanaProxy}"
    val cacheContext = cache.get(cacheKey)
    if (isCacheValid(cacheContext)) {
      val c = cacheContext.get._2
      c.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
      return c
    }

    val context = new ScriptContext(endpoint, uri,
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
    cache.put(cacheKey, (System.currentTimeMillis(), context))
    context
  }

  private def isCacheValid(cacheContext: Option[(Long, ScriptContext)]) = {
    cacheContext match {
      case None => false
      case Some(c) => {
        if ((System.currentTimeMillis() - c._1) > (2 hours).toMillis) {
          false
        } else {
          c._2.eqlClient.restClient.isRunning
        }
      }
    }
  }
}

