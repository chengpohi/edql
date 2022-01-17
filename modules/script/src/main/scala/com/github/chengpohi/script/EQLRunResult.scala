package com.github.chengpohi.script

import scala.collection.SeqMap
import scala.util.{Success, Try}


case class EQLRunResult(response: Try[Seq[Seq[String]]],
                        context: Map[String, Any] = Map()) {
  def isSuccess: Boolean = response.isSuccess

  def isFail: Boolean = response.isFailure

  def failed: Throwable = response.failed.get

  def success: Seq[Seq[String]] = response.get
}

object EQLRunResult {
  def apply(response: Try[Seq[Seq[String]]]): EQLRunResult = {
    new EQLRunResult(response)
  }

  def apply(response: Seq[Seq[String]],
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
