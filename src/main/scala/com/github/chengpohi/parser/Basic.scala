package com.github.chengpohi.parser

import fastparse.all._

/**
  * elasticshell
  * Created by chengpohi on 7/11/16.
  */
trait Basic {
  val WSChars = P(CharsWhile("\u0020\u0009".contains(_)))
  val Newline = P(StringIn("\r\n", "\n"))
  val WL0 = P(NoTrace((WSChars | Newline).rep))
  val WhitespaceApi = new fastparse.WhitespaceApi.Wrapper(WL0)
}
