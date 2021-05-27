package com.github.chengpohi.script

import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.collection.JsonCollection

import java.net.URL
import scala.collection.mutable

class ScriptEQLContext(host: String, port: Int, auth: Option[String], timeout: Option[Int]) extends EQLConfig with EQLContext {
  override implicit lazy val eqlClient: EQLClient =
    buildRestClient(host, port, auth, timeout)
}

object ScriptEQLContext {
  def apply(endpoint: String, auth: Option[String] = None, timeout: Option[Int], vars: Map[String, JsonCollection.Val]): ScriptEQLContext = {
    val url = new URL(endpoint)
    val context = new ScriptEQLContext(url.getHost, url.getPort, auth, timeout)
    context.variables = mutable.Map[String, JsonCollection.Val](vars.toSeq: _*)
    context
  }


}

