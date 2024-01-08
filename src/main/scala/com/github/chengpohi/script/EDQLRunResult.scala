package com.github.chengpohi.script

import scala.util.{Success, Try}

case class EDQLRunResult(response: Try[Seq[String]], context: EDQLRunContext = null) {

  def isSuccess: Boolean = response.isSuccess

  def isFail: Boolean = response.isFailure

  def failed: Throwable = response.failed.get

  def success: Seq[String] = response.get
}

object EDQLRunResult {
  def apply(response: Try[Seq[String]]): EDQLRunResult = {
    new EDQLRunResult(response)
  }

  def apply(response: Seq[String], runContext: EDQLRunContext): EDQLRunResult = {
    new EDQLRunResult(Success(response), runContext)
  }
}
