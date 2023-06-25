package com.github.chengpohi.script

import scala.collection.SeqMap
import scala.util.{Success, Try}


case class EDQLRunResult(response: Try[Seq[String]],
                         context: Map[String, Any] = Map()) {
  def isSuccess: Boolean = response.isSuccess

  def isFail: Boolean = response.isFailure

  def failed: Throwable = response.failed.get

  def success: Seq[String] = response.get
}

object EDQLRunResult {
  def apply(response: Try[Seq[String]]): EDQLRunResult = {
    new EDQLRunResult(response)
  }

  def apply(response: Seq[String],
            context: ScriptContext): EDQLRunResult = {
    new EDQLRunResult(Success(response), SeqMap(
      "HOST" -> context.endpoint,
      "Authorization" -> context.authInfo.map(j => j.auth.map(i => s""""$i"""")).orNull,
      "Username" -> context.authInfo.map(j => j.username.map(i => s""""$i"""")).orNull,
      "Password" -> context.authInfo.map(j => j.password.map(i => s""""$i"""")).orNull,
      "ApiKeyId" -> context.authInfo.map(j => j.apiKeyId.map(i => s""""$i"""")).orNull,
      "ApiKeySecret" -> context.authInfo.map(j => j.apiKeySecret.map(i => s""""$i"""")).orNull,
      "ApiSessionToken" -> context.authInfo.map(j => j.apiSessionToken.map(i => s""""$i"""")).orNull,
      "AWSRegion" -> context.authInfo.map(j => j.awsRegion.map(i => s""""$i"""")).orNull,
      "Timeout" -> context.timeout.getOrElse(5000)
    ).filter(_._2 != null))
  }
}
