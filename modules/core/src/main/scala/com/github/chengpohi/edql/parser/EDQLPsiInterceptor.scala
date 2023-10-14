package com.github.chengpohi.edql.parser

import com.github.chengpohi.edql.parser.json.{JsonCollection, JsonValParser}
import com.github.chengpohi.edql.parser.psi._
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiComment, PsiErrorElement, PsiWhiteSpace}
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
      val ps = fun.getParams.getParamList.asScala.map(i => {
        i.getIdentifier0.getText
      }).toSeq
      val bs = fun.getFunctionBody.getExprList.asScala.flatMap(i => parseExpr(i)).toSeq
      val rs = fun.getFunctionBody.getReturnExprList.asScala.map(i => {
        ReturnInstruction(toJsonVal(i.getExpr))
      }).toSeq
      return Seq(FunctionInstruction(fun.getIdentifier0.getText, ps, bs ++ rs))
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
      return Seq(FunctionInvokeInstruction(funcName, ins))
    }

    if (expr.getOutervar != null) {
      val outervar = expr.getOutervar
      val varName = outervar.getBind.getIdentifier0.getText

      if (CollectionUtils.isNotEmpty(outervar.getBind.getBinsuffixList)) {
        val j = toJsonVal(outervar.getBind)
        return Seq(VariableInstruction(varName, j))
      }

      val e = outervar.getBind.getExpr
      val v = Try.apply(toJsonVal(e))
      if (v.isSuccess) {
        return Seq(VariableInstruction(varName, v.get))
      }

      val anonymousFun = "anonymousFun_" + RandomStringUtils.randomAlphabetic(10)
      return Seq(
        VariableInstruction(varName, JsonCollection.Fun((anonymousFun, Seq()))),
        FunctionInstruction(anonymousFun, Seq(), parseExpr(outervar.getBind.getExpr)))
    }

    throw new RuntimeException("unsupported instruction: " + expr.getText)
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
