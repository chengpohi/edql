package com.github.chengpohi.edql.parser

import com.github.chengpohi.edql.parser.json.{JsonCollection, JsonValParser}
import com.github.chengpohi.edql.parser.psi._
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiComment, PsiErrorElement, PsiFile, PsiWhiteSpace}
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.RandomStringUtils

import java.net.URL
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

class EDQLPsiInterceptor(val parserFactory: EDQLParserFactory) extends InterceptFunction with JsonValParser {
  import parserFactory._

  def parseJson(text: String): Try[JsonCollection.Val] = {
    ApplicationManager.getApplication.runReadAction(new Computable[Try[JsonCollection.Val]] {
      override def compute(): Try[JsonCollection.Val] = {
        try {
          val psiFile = createFile("dummmy", text)

          val element = PsiTreeUtil.findChildOfType(psiFile, classOf[PsiErrorElement])
          if (element != null) {
            throw new RuntimeException(element.getErrorDescription)
          }

          val expr = PsiTreeUtil.findChildOfType(psiFile, classOf[EDQLExpr])
          if (!expr.textMatches(text)) {
            throw new RuntimeException("parse failed: " + text)
          }
          scala.util.Success(toJsonVal(expr))
        } catch {
          case ex: Throwable => {
            scala.util.Failure(ex)
          }
        }
      }
    })
  }

  def parseExpr(text: String): Try[PsiFile] = {
    ApplicationManager.getApplication.runReadAction(new Computable[Try[PsiFile]] {
      override def compute(): Try[PsiFile] = {
        try {
          val psiFile = createFile("dummmy", text)
          val element = PsiTreeUtil.findChildOfType(psiFile, classOf[PsiErrorElement])
          if (element != null) {
            throw new RuntimeException(element.getErrorDescription)
          }

          scala.util.Success(psiFile)
        } catch {
          case ex: Exception => scala.util.Failure(ex)
        }
      }
    })
  }

  def parse(text: String): Try[Seq[Instruction2]] = {
    ApplicationManager.getApplication.runReadAction(new Computable[Try[Seq[Instruction2]]] {
      override def compute(): Try[Seq[Instruction2]] = {
        try {
          val psiFile = createFile("dummmy", text)
          val element = PsiTreeUtil.findChildOfType(psiFile, classOf[PsiErrorElement])
          if (element != null) {
            throw new RuntimeException(element.getErrorDescription)
          }

          val children = psiFile.getChildren
          val ins = children.flatMap {
            case im: EDQLImportop => {
              Seq(ImportInstruction(new URL(im.getDoubleQuotedString.getText)))
            }
            case ti: EDQLTimeoutExpr => {
              Seq(TimeoutInstruction(ti.getNumber.getText.toInt))
            }
            case hi: EDQLHeadExpr => {
              throw new RuntimeException("unsupported instruction: " + hi.getText)
            }
            case ex: EDQLExpr => parseExpr(ex)
            case w: PsiWhiteSpace => Seq()
            case c: PsiComment => Seq()
            case j => {
              throw new RuntimeException("unsupported instruction: " + j.getText)
            }
          }.toList
          scala.util.Success(ins)
        } catch {
          case ex: Throwable => scala.util.Failure(ex)
        }
      }
    })
  }


  private def parseExpr(expr: EDQLExpr): Seq[Instruction2] = {
    if (expr.getActionExpr != null) {
      return Seq(parseAction(expr.getActionExpr))
    }

    if (expr.getComment != null) {
      return Seq()
    }

    if (expr.getFunctionExpr != null) {
      val fun = expr.getFunctionExpr
      val ps = fun.getParams match {
        case null => Seq()
        case p =>
          p.getParamList.asScala.map(i => {
            i.getIdentifier0.getText
          }).toSeq
      }
      val bs = fun.getFunctionBody.getExprList.asScala.flatMap(i => parseExpr(i)).toSeq
      fun.getFunctionBody.getReturnExpr match {
        case null =>
          return Seq(FunctionInstruction(fun.getIdentifier0.getText, ps, bs))
        case r =>
          val rs = ReturnInstruction(toJsonVal(r))
          return Seq(FunctionInstruction(fun.getIdentifier0.getText, ps, bs :+ rs))
      }
    }

    if (expr.getForExpr != null) {
      val vname = expr.getForExpr.getIdentifier0.getText
      val v = toJsonVal(expr.getForExpr.getExpr)
      val bs = expr.getForExpr.getFunctionBody.getExprList.asScala.flatMap(i => parseExpr(i)).toSeq
      return Seq(ForInstruction(vname, v, bs))
    }

    if (expr.getFunctionInvokeExpr != null) {
      val funcName = expr.getFunctionInvokeExpr.getIdentifier0.getText
      val ins = expr.getFunctionInvokeExpr.getExprList.asScala.map(i => toJsonVal(i)).toSeq

      val mapIter = expr.getFunctionInvokeExpr.getMapIter
      if (mapIter != null) {
        return Seq(FunctionInvokeInstruction(funcName, ins, Some(buildMapIterInstruction(JsonCollection.Arr(), mapIter))))
      }

      return Seq(FunctionInvokeInstruction(funcName, ins))
    }

    if (expr.getOutervar != null) {
      val outervar = expr.getOutervar
      val varName = outervar.getBind.getIdentifier0.getText

      val mapIterInstruction: Option[MapIterInstruction] = if (outervar.getBind.getMapIter != null) {
        Some(buildMapIterInstruction(JsonCollection.Arr(), outervar.getBind.getMapIter))
      } else None


      if (CollectionUtils.isNotEmpty(outervar.getBind.getBinsuffixList)) {
        val j = toJsonVal(outervar.getBind)
        return Seq(VariableInstruction(varName, j))
      }

      val anonymousFun = "anonymousFun_" + RandomStringUtils.randomAlphabetic(10)

      val e = outervar.getBind.getExpr
      val v = Try.apply(toJsonVal(e))
      if (v.isSuccess) {
        v.get match {
          case arr: JsonCollection.Arr if mapIterInstruction.isDefined =>
            return Seq(
              VariableInstruction(varName, JsonCollection.Fun((anonymousFun, Seq()))),
              FunctionInstruction(anonymousFun, Seq(), Seq(mapIterInstruction.get.copy(a = arr)))
            )
          case v: JsonCollection.Var if mapIterInstruction.isDefined =>
            return Seq(
              VariableInstruction(varName, JsonCollection.Fun((anonymousFun, Seq()))),
              FunctionInstruction(anonymousFun, Seq(), Seq(mapIterInstruction.get.copy(a = v)))
            )
          case f: JsonCollection.Fun =>
            val iter = e.getFunctionInvokeExpr.getMapIter
            if (iter != null) {
              val instruction = buildMapIterInstruction(JsonCollection.Arr(), iter)
              return Seq(
                VariableInstruction(varName, JsonCollection.Fun((anonymousFun, Seq()))),
                FunctionInstruction(anonymousFun, Seq(), Seq(instruction.copy(a = f)))
              )
            }
            return Seq(VariableInstruction(varName, v.get))
          case _ =>
            return Seq(VariableInstruction(varName, v.get))
        }
      }


      return Seq(
        VariableInstruction(varName, JsonCollection.Fun((anonymousFun, Seq()))),
        FunctionInstruction(anonymousFun, Seq(), parseExpr(outervar.getBind.getExpr)))
    }


    if (expr.getArr != null && expr.getArr.getMapIter != null) {
      val arr = expr.getArr.getExprList.asScala.map(i => toJsonVal(i)).toSeq
      return Seq(buildMapIterInstruction(JsonCollection.Arr(arr: _*), expr.getArr.getMapIter))
    }

    throw new RuntimeException("unsupported instruction: " + expr.getText)
  }

  private def buildMapIterInstruction(arr: JsonCollection.Arr, mapIter: EDQLMapIter): MapIterInstruction = {
    val anonymousFun = "anonymousFun_" + RandomStringUtils.randomAlphabetic(10)
    val bs: Seq[Instruction2] = mapIter.getExprList.asScala.flatMap(i => parseExpr(i)).toSeq

    mapIter.getReturnExpr match {
      case null =>
        MapIterInstruction(arr, FunctionInstruction(anonymousFun, Seq("it"), bs))
      case r =>
        val rs = ReturnInstruction(toJsonVal(r))
        MapIterInstruction(arr, FunctionInstruction(anonymousFun, Seq("it"), bs :+ rs))
    }
  }

  private def parseAction(expr: EDQLActionExpr): Instruction2 = {
    val v = toJsonVal(expr.getObjList)

    expr.getMethod.getFirstChild.getNode.getElementType match {
      case EDQLTypes.POST => {
        PostActionInstruction(expr.getPath.getText + Option.apply(expr.getQuery).map(i => i.getText).getOrElse(""), v)
      }
      case EDQLTypes.GET => {
        GetActionInstruction(expr.getPath.getText + Option.apply(expr.getQuery).map(i => i.getText).getOrElse(""), v.headOption)
      }
      case EDQLTypes.PUT => {
        PutActionInstruction(expr.getPath.getText + Option.apply(expr.getQuery).map(i => i.getText).getOrElse(""), v.headOption)
      }
      case EDQLTypes.DELETE => {
        DeleteActionInstruction(expr.getPath.getText + Option.apply(expr.getQuery).map(i => i.getText).getOrElse(""), v.headOption)
      }
      case EDQLTypes.HEAD => {
        HeadActionInstruction(expr.getPath.getText + Option.apply(expr.getQuery).map(i => i.getText).getOrElse(""), v.headOption)
      }
      case _ => null
    }
  }

}
