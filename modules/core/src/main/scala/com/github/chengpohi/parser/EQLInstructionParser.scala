package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.NoWhitespace._
import fastparse._

trait EQLInstructionParser extends JsonParser with InterceptFunction {
  def helpP[_: P] = P(alphaChars.rep(1).! ~ "?")
    .map(s => {
      HelpInstruction(Seq(s))
    })

  def comment[_: P] = P(newline.? ~ "#" ~ notNewlineChars.rep(0).! ~/ newline.?).map(
    _ => CommentInstruction())

  def hostBind[_: P] = P(WS ~ "HOST" ~ WS ~ actionPath ~ WS).map(
    c => EndpointBindInstruction(c.extract[String]))

  def timeoutBind[_: P] = P(WS ~ "Timeout" ~ WS ~ number ~ WS).map(
    c => TimeoutInstruction(c.extract[Int]))

  def importExpr[_: P] = P(WS ~ "import" ~ WS ~ quoteString ~ WS).map(
    c => ImportInstruction(c.extract[String]))

  def authorizationBind[_: P] = P(WS ~ "Authorization" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      AuthorizationBindInstruction(c.extract[String])
    })

  def usernameBind[_: P] = P(WS ~ "Username" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      UsernameBindInstruction(c.extract[String])
    })

  def passwordBind[_: P] = P(WS ~ "Password" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      PasswordBindInstruction(c.extract[String])
    })

  def apiKeyIdBind[_: P] = P(WS ~ "ApiKeyId" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      ApiKeyIdBindInstruction(c.extract[String])
    })

  def apiKeySecretBind[_: P] = P(WS ~ "ApiKeySecret" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      ApiKeySecretBindInstruction(c.extract[String])
    })

  def apiSessionTokenBind[_: P] = P(WS ~ "ApiSessionToken" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      ApiSessionTokenBindInstruction(c.extract[String])
    })

  def awsRegionBind[_: P] = P(WS ~ "AWSRegion" ~ WS ~ (actionPath | quoteString) ~ WS).map(
    c => {
      AWSRegionBindInstruction(c.extract[String])
    })

  def postAction[_: P] = P(WS ~ "POST" ~ WS ~ actionPath ~/ WS ~/ jsonExpr.rep ~ WS).map(
    c => PostActionInstruction(c._1.extract[String], c._2))

  def getAction[_: P] = P(WS ~ "GET" ~ WS ~ actionPath ~/ WS ~/ jsonExpr.? ~ WS).map(
    c => GetActionInstruction(c._1.extract[String], c._2))

  def deleteAction[_: P] = P(WS ~ "DELETE" ~ WS ~ actionPath ~/ WS ~/ jsonExpr.? ~ WS).map(
    c => DeleteActionInstruction(c._1.extract[String], c._2))

  def putAction[_: P] = P(WS ~ "PUT" ~ WS ~ actionPath ~/ WS ~/ jsonExpr.? ~ WS).map(
    c => PutActionInstruction(c._1.extract[String], c._2))

  def headAction[_: P] = P(WS ~ "HEAD" ~ WS ~ actionPath ~/ WS ~/ jsonExpr.? ~ WS).map(
    c => HeadActionInstruction(c._1.extract[String], c._2))

  def variableAction[_: P] =
    P(WS ~ "var" ~ WS ~ variableName ~ WS ~/ "=" ~ WS ~/ jsonExpr ~ WS).map(
      c => c._2 match {
        case v: JsonCollection.Val =>
          VariableInstruction(c._1, v)
      })

  def returnExpr[_: P] = P(WS ~ "return" ~ WS ~/ jsonExpr.map(v => ReturnInstruction(v)) ~ WS)

  def echoExpr[_: P] = P(WS ~ "echo" ~ WS ~/ jsonExpr.map {
    v: JsonCollection.Val =>
      EchoInstruction(v)
  } ~ WS)

  def functionExpr[_: P] = P(WS ~ "function" ~ WS ~ variableName ~ WS ~/ "(" ~
    WS ~ variableName.rep(sep = WS ~ "," ~ WS) ~ WS ~ ")" ~/
    WS ~ "{" ~/ WS ~ inses ~ WS ~ "}" ~ WS)
    .map(c => FunctionInstruction(c._1, c._2, c._3))

  def forExpr[_: P] = P(WS ~ "for" ~ WS ~/ "(" ~
    WS ~ variableName ~ WS ~ "in" ~ WS ~ jsonExpr ~ ")" ~/
    WS ~ "{" ~/ WS ~ inses ~ WS ~ "}")
    .map(c => ForInstruction(c._1, c._2, c._3))

  def functionInvokeExpr[_: P]: P[FunctionInvokeInstruction] =
    P(WS ~ fun ~ WS)
      .map(c => {
        FunctionInvokeInstruction(c.value._1, c.value._2)
      })

  def instrument[_: P]: P[Seq[Instruction2]] = P(
    inses ~ WS.? ~ End
  )

  private def inses[_: P]: P[Seq[Instruction2]] = {
    (
      comment | hostBind | timeoutBind | authorizationBind | usernameBind | passwordBind | apiKeyIdBind | apiKeySecretBind | apiSessionTokenBind | awsRegionBind
        | postAction | getAction | deleteAction | putAction | headAction
        | variableAction | functionExpr | forExpr | functionInvokeExpr | returnExpr | importExpr | echoExpr
      ).rep(0)
  }
}
