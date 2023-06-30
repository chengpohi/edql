package com.github.chengpohi.script

import com.github.chengpohi.context.HostInfo

import scala.util.{Success, Try}


case class EDQLRunResult(response: Try[Seq[String]], hostDTO: HostInfo = null) {
  def isSuccess: Boolean = response.isSuccess

  def isFail: Boolean = response.isFailure

  def failed: Throwable = response.failed.get

  def success: Seq[String] = response.get
}

object EDQLRunResult {
  def apply(response: Try[Seq[String]]): EDQLRunResult = {
    new EDQLRunResult(response)
  }

  def apply(response: Seq[String], context: ScriptContext): EDQLRunResult = {
    new EDQLRunResult(Success(response), context.hostInfo)
  }
}
