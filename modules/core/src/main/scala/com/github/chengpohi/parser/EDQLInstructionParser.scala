package com.github.chengpohi.parser

import com.github.chengpohi.parser.Lexical._
import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.NoWhitespace._
import fastparse._
import org.apache.commons.lang3.RandomStringUtils

trait EDQLInstructionParser extends JsonParser with InterceptFunction {
  def comment[_: P] = P(newline.? ~ "#" ~ notNewlineChars.rep(0).! ~/ newline.?).map(
    _ => CommentInstruction())

  def hostBind[_: P] = P(WS ~ "HOST" ~ WS ~ actionPath ~ WS).map(
    c => EndpointBindInstruction(c.extract[String]))

  def kibanaHostBind[_: P] = P(WS ~ "KIBANA_HOST" ~ WS ~ actionPath ~ WS).map(
    c => EndpointBindInstruction(c.extract[String], kibanaProxy = true))

  def timeoutBind[_: P] = P(WS ~ "Timeout" ~ WS ~ number ~ WS).map(
    c => TimeoutInstruction(c.extract[Int]))

  def importExpr[_: P] = P(WS ~ "import" ~ WS ~ stringLiteral ~ WS).map(
    c => ImportInstruction(c.extract[String]))

  def authorizationBind[_: P] = P(WS ~ "Authorization" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      AuthorizationBindInstruction(c.extract[String])
    })

  def usernameBind[_: P] = P(WS ~ "Username" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      UsernameBindInstruction(c.extract[String])
    })

  def passwordBind[_: P] = P(WS ~ "Password" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      PasswordBindInstruction(c.extract[String])
    })

  def apiKeyIdBind[_: P] = P(WS ~ "ApiKeyId" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      ApiKeyIdBindInstruction(c.extract[String])
    })

  def apiKeySecretBind[_: P] = P(WS ~ "ApiKeySecret" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      ApiKeySecretBindInstruction(c.extract[String])
    })

  def apiSessionTokenBind[_: P] = P(WS ~ "ApiSessionToken" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      ApiSessionTokenBindInstruction(c.extract[String])
    })

  def awsRegionBind[_: P] = P(WS ~ "AWSRegion" ~ WS ~ (actionPath | stringLiteral) ~ WS).map(
    c => {
      AWSRegionBindInstruction(c.extract[String])
    })

  def postAction[_: P] = P(WS ~ "POST" ~ WS ~ actionPath ~/ WS ~/ obj.rep(sep = WS_NC) ~ WS).map(
    c => PostActionInstruction(c._1.extract[String], c._2))

  def getAction[_: P] = P(WS ~ "GET" ~ WS ~ actionPath ~/ WS ~/ obj.? ~ WS).map(
    c => GetActionInstruction(c._1.extract[String], c._2))

  def deleteAction[_: P] = P(WS ~ "DELETE" ~ WS ~ actionPath ~/ WS ~/ obj.? ~ WS).map(
    c => DeleteActionInstruction(c._1.extract[String], c._2))

  def putAction[_: P] = P(WS ~ "PUT" ~ WS ~ actionPath ~/ WS ~/ obj.? ~ WS).map(
    c => PutActionInstruction(c._1.extract[String], c._2))

  def headAction[_: P] = P(WS ~ "HEAD" ~ WS ~ actionPath ~/ WS ~/ obj.? ~ WS).map(
    c => HeadActionInstruction(c._1.extract[String], c._2))

  def variableAction[_: P]: P[Seq[Instruction2]] =
    P(WS ~ "var" ~ WS ~ variableName ~ WS ~/ "=" ~ WS ~/ (jsonExpr | getAction | postAction | deleteAction | putAction | headAction) ~ WS).map(
      c => c._2 match {
        case v: JsonCollection.Val =>
          Seq(VariableInstruction(c._1, v))
        case v: Instruction2 =>
          val anonymousFun = "anonymousFun_" + RandomStringUtils.randomAlphabetic(10)
          Seq(VariableInstruction(c._1, JsonCollection.Fun((anonymousFun, Seq()))), FunctionInstruction(anonymousFun, Seq(), Seq(v)))
      })

  def returnExpr[_: P] = P(WS ~ "return" ~ WS ~/ jsonExpr.map(v => ReturnInstruction(v)) ~ WS)

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
      comment | hostBind | kibanaHostBind | timeoutBind | authorizationBind | usernameBind | passwordBind | apiKeyIdBind | apiKeySecretBind | apiSessionTokenBind | awsRegionBind
        | postAction | getAction | deleteAction | putAction | headAction
        | variableAction | functionExpr | forExpr | functionInvokeExpr | returnExpr | importExpr
      ).rep(0).map(_.flatMap {
      case j: Instruction2 => Seq(j)
      case j: Seq[Instruction2] => j
    })
  }
}
