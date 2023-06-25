package com.github.chengpohi.script

import com.github.chengpohi.context.{AuthInfo, Context, EDQLClient, EDQLConfig}
import com.github.chengpohi.parser.collection.JsonCollection

import java.net.URI
import scala.collection.mutable
import scala.concurrent.duration
import scala.concurrent.duration.Duration


case class ScriptContext(endpoint: String,
                         uri: URI,
                         authInfo: Option[AuthInfo],
                         timeout: Option[Int],
                         override val kibanaProxy: Boolean,
                         proxy: Option[java.net.Proxy] = None) extends EDQLConfig with Context {
  override implicit val resultTimeout: Duration = Duration.apply(timeout.getOrElse(5000), duration.MILLISECONDS)

  override implicit lazy val eqlClient: EDQLClient =
    buildRestClient(uri, authInfo, timeout, kibanaProxy, proxy)
}

object ScriptContext {
  val cache: mutable.Map[String, (Long, ScriptContext)] = mutable.Map[String, (Long, ScriptContext)]()

  def apply(endpoint: String,
            authInfo: Option[AuthInfo],
            timeout: Option[Int],
            vars: Map[String, JsonCollection.Val],
            kibanaProxy: Boolean,
            proxy: Option[java.net.Proxy] = None): ScriptContext = {
    val uri = URI.create(endpoint)

    val cacheKey = s"$endpoint-" + authInfo.map(i => i.cacheKey).getOrElse("") + s"-${timeout.getOrElse("")}" + s"-${kibanaProxy}"

    val cacheContext = cache.get(cacheKey)
    if (isCacheValid(cacheContext)) {
      val c = cacheContext.get._2
      c.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
      return c
    }

    val context = new ScriptContext(endpoint,
      uri,
      authInfo,
      timeout,
      kibanaProxy,
      proxy
    )
    context.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
    cache.put(cacheKey, (System.currentTimeMillis(), context))
    context
  }

  private def isCacheValid(cacheContext: Option[(Long, ScriptContext)]) = {
    cacheContext match {
      case None => false
      case Some(c) => {
        c._2.eqlClient.restClient.isRunning
      }
    }
  }
}

