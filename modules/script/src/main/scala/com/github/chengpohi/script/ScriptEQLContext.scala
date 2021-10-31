package com.github.chengpohi.script

import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.collection.JsonCollection

import java.net.URL
import scala.collection.mutable

case class ScriptEQLContext(endpoint: String,
                            host: String,
                            port: Int,
                            auth: Option[String],
                            username: Option[String],
                            password: Option[String],
                            apiKeyId: Option[String],
                            apiKeySecret: Option[String],
                            timeout: Option[Int]) extends EQLConfig with EQLContext {
  override implicit lazy val eqlClient: EQLClient =
    buildRestClient(host, port, auth, username, password, apiKeyId, apiKeySecret, timeout)
}

object ScriptEQLContext {
  def apply(endpoint: String,
            auth: Option[String] = None,
            username: Option[String] = None,
            password: Option[String] = None,
            apiKeyId: Option[String] = None,
            apiKeySecret: Option[String] = None,
            timeout: Option[Int],
            vars: Map[String, JsonCollection.Val]): ScriptEQLContext = {
    val url = new URL(endpoint)
    val context = new ScriptEQLContext(endpoint, url.getHost, url.getPort,
      auth,
      username,
      password,
      apiKeyId,
      apiKeySecret,
      timeout)
    context.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
    context
  }
}

