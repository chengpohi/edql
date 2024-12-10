package com.github.chengpohi.script

import com.github.chengpohi.context._
import com.github.chengpohi.edql.parser.json.JsonCollection
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.concurrent.duration
import scala.concurrent.duration.Duration


sealed case class ScriptContext(hostInfo: HostInfo) extends EDQLConfig with Context {
  override implicit val resultTimeout: Duration = Duration.apply(hostInfo.timeout, duration.MILLISECONDS)
  override implicit val kibanaProxy: Boolean = hostInfo.kibanaProxy
  override implicit val readOnly: Boolean = hostInfo.readOnly
  override implicit lazy val eqlClient: EDQLClient = buildRestClient(hostInfo)

  def clear(): Unit = variables.clear()
}

object ScriptContext {
  val cache: mutable.Map[String, (Long, ScriptContext)] = mutable.Map[String, (Long, ScriptContext)]()

  def apply(hostInfo: HostInfo,
            vars: Map[String, JsonCollection.Val]): ScriptContext = {
    val keyBytes = (s"$hostInfo.endpoint-" + hostInfo.authInfo.map(i => i.cacheKey).getOrElse("")
      + s"-${hostInfo.timeout}" + s"-${hostInfo.kibanaProxy}" + s"-${hostInfo.readOnly}"
      + s"-${hostInfo.proxyInfo.map(i => i.cacheKey).getOrElse("")}").getBytes

    val cacheKey = Base64.encodeBase64String(keyBytes)

    val cacheContext = cache.get(cacheKey)
    if (isCacheValid(cacheContext)) {
      val c = cacheContext.get._2
      c.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
      return c
    }

    val context = new ScriptContext(hostInfo)
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

