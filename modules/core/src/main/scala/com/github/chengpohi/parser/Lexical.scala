package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.NoWhitespace._
import fastparse._
import org.apache.commons.lang3.StringEscapeUtils

object Lexical {
  val AlphaChars: NamedFunction[Char, Boolean] = NamedFunction(!"\"\\?".contains(_: Char), "StringChars")
  val NewlineChars: NamedFunction[Char, Boolean] = NamedFunction("\n\r\n\r\f".contains(_: Char), "StringChars")
  val NotNewlineChars: NamedFunction[Char, Boolean] = NamedFunction(!"\n\r\n\r\f".contains(_: Char), "StringChars")
  val CollectionChars: NamedFunction[Char, Boolean] =
    NamedFunction(!"[],()\"\\".contains(_: Char), "CollectionChars")

  def commentChars[_: P] = P("#" ~/ notNewlineChars.rep(0) ~/ End.?)

  def WS[_: P] = P(" " | newline | commentChars).rep

  def WS_NC[_: P] = P(" " | newline).rep

  def newline[_: P] = P(CharsWhile(NewlineChars))

  def stringLiteral[_: P]: P[JsonCollection.Str] = P(longstring("\"\"\"") | shortstring("\""))

  def shortstring[_: P](delimiter: String) = P(WS ~ delimiter ~ shortstringitem(delimiter).rep.! ~ delimiter ~ WS).map(i =>
    JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))

  def shortstringitem[_: P](quote: String): P[Unit] = P(shortstringchar(quote) | escapeseq)

  def shortstringchar[_: P](quote: String): P[Unit] = P(CharsWhile(!s"\\${quote(0)}".contains(_)))

  def longstring[_: P](delimiter: String) = P(WS ~ delimiter ~ longstringitem(delimiter).rep.! ~ delimiter ~ WS)
    .map(i => {
      JsonCollection.Str(StringEscapeUtils.escapeJava(i).replaceAll("\\r|\\n|\\\\n|\\\\r", ""))
    })

  def longstringitem[_: P](quote: String): P[Unit] = P(longstringchar(quote) | escapeseq | !quote ~ quote.take(1))

  def longstringchar[_: P](quote: String): P[Unit] = P(CharsWhile(!s"\\${quote(0)}".contains(_)))

  def escapeseq[_: P]: P[Unit] = P("\\" ~ AnyChar)

  def notNewlineChars[_: P] = P(CharsWhile(NotNewlineChars))

  def alphaChars[_: P] = P(CharsWhile(AlphaChars))

  def collectionChars[_: P] = P(CharsWhile(CollectionChars))

  def variableChars[_: P] = P(CharIn("a-zA-Z_"))

  def hexDigit[_: P] = P(CharIn("0-9a-fA-F"))

  def actionChars[_: P] = P(CharIn("0-9a-zA-Z$:/_@?%*= ,\\.\\&\\-"))

  def unicodeEscape[_: P] = P("u" ~ hexDigit ~ hexDigit ~ hexDigit ~ hexDigit)

  def escape[_: P] = P("\\" ~ (CharIn("\"/\\bfnrt") | unicodeEscape))


  def variableName[_: P] = P(variableChars.rep(1)).!

  def variable[_: P] = P(WS ~ "$" ~ variableChars.rep(1).! ~ WS).map(JsonCollection.Var)

  //val parameter: P[String] = P(space ~ string ~ ",".? ~ space)
  def strOrVar[_: P] = P(stringLiteral | variable)

  def Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")

  def digits[_: P] = P(CharsWhile(Digits))

  def exponent[_: P] = P(CharIn("eE") ~ CharIn("+\\-").? ~ digits)

  def fractional[_: P] = P("." ~ digits)

  def integral[_: P] = P("0" | CharIn("1-9") ~ digits.?)

}
