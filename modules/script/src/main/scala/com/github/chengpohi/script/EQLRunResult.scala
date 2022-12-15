package com.github.chengpohi.script

import com.github.chengpohi.parser.collection.JsonCollection

import scala.collection.SeqMap
import scala.util.{Success, Try}


case class EQLRunResult(response: Try[Seq[ExecuteInfo]],
                        context: Map[String, Any] = Map()) {
  def isSuccess: Boolean = response.isSuccess

  def isFail: Boolean = response.isFailure

  def failed: Throwable = response.failed.get

  def success: Seq[ExecuteInfo] = response.get
}

case class ExecuteInfo(request: String, value: JsonCollection.Val, json: String)

object EQLRunResult {
  def apply(response: Try[Seq[ExecuteInfo]]): EQLRunResult = {
    new EQLRunResult(response)
  }

  def apply(response: Seq[ExecuteInfo],
            context: ScriptEQLContext): EQLRunResult = {
    new EQLRunResult(Success(response), SeqMap(
      "HOST" -> context.endpoint,
      "Authorization" -> context.auth.map(i => s""""$i"""").orNull,
      "Username" -> context.username.map(i => s""""$i"""").orNull,
      "Password" -> context.password.map(i => s""""$i"""").orNull,
      "ApiKeyId" -> context.apiKeyId.map(i => s""""$i"""").orNull,
      "ApiKeySecret" -> context.apiKeySecret.map(i => s""""$i"""").orNull,
      "ApiSessionToken" -> context.apiSessionToken.map(i => s""""$i"""").orNull,
      "AWSRegion" -> context.awsRegion.map(i => s""""$i"""").orNull,
      "Timeout" -> context.timeout.getOrElse(5000)
    ).filter(_._2 != null))
  }
}

object ExecuteInfo {
  def apply(response: String, value: JsonCollection.Val): ExecuteInfo = {
    ExecuteInfo(response, value, value.toJson)
  }
}
