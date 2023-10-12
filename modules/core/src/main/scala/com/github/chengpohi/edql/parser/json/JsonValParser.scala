package com.github.chengpohi.edql.parser.json

import com.github.chengpohi.edql.parser.psi.{EDQLExpr, EDQLFieldname, EDQLObj, EDQLTypes}
import com.intellij.psi.PsiElement

import java.util
import scala.jdk.CollectionConverters.ListHasAsScala

trait JsonValParser {
  def toJsonVal(getObjList: util.List[EDQLObj]): Seq[JsonCollection.Val] = {
    getObjList.asScala.map(i => toJsonVal(i)).toSeq
  }

  def toJsonVal(obj: EDQLObj): JsonCollection.Val = {
    if (obj.getMembers == null) {
      return JsonCollection.Obj()
    }
    val vals = obj.getMembers.getMemberList.asScala.map(m => {
      val field = toJsonVal(m.getField.getFieldname)
      val v = toJsonVal(m.getField.getExpr)
      (field, v)
    }).toSeq

    JsonCollection.Obj(vals: _*)
  }

  def toJsonVal(fieldname: EDQLFieldname): JsonCollection.Val = {
    if (fieldname.getExpr != null) {
      checkParse(fieldname, fieldname.getExpr.getText)
      return toJsonVal(fieldname.getExpr)
    }
    if (fieldname.getDoubleQuotedString != null) {
      val str = fieldname.getDoubleQuotedString.getText
      checkParse(fieldname, str)
      return JsonCollection.Str(str.substring(1, str.length - 1))
    }

    if (fieldname.getSingleQuotedString != null) {
      val str = fieldname.getSingleQuotedString.getText
      checkParse(fieldname, str)
      return JsonCollection.Str(str.substring(1, str.length - 1))
    }

    if (fieldname.getIdentifier0 != null) {
      val str = fieldname.getIdentifier0.getText
      checkParse(fieldname, str)
      return JsonCollection.Var(str)
    }
    throw new RuntimeException("parse failed: " + fieldname.getText)
  }

  def toJsonVal(expr: EDQLExpr): JsonCollection.Val = {
    if (expr.getExpr != null) {
      return toJsonVal(expr.getExpr)
    }

    if (expr.getDoubleQuotedString != null) {
      val str = expr.getDoubleQuotedString.getText
      checkParse(expr, str)
      return JsonCollection.Str(str.substring(1, str.length - 1))
    }

    if (expr.getTripleQuotedString != null) {
      val str = expr.getTripleQuotedString.getText
      checkParse(expr, str)
      return JsonCollection.Str(str.substring(3, str.length - 3))
    }

    if (expr.getSingleQuotedString != null) {
      val str = expr.getSingleQuotedString.getText
      checkParse(expr, str)
      return JsonCollection.Str(str.substring(3, str.length - 3))
    }

    if (expr.getNumber != null) {
      checkParse(expr, expr.getNumber.getText)
      return JsonCollection.Num(expr.getNumber.getText.toDouble)
    }

    if (expr.getBool != null) {
      expr.getBool.getFirstChild.getNode.getElementType match {
        case EDQLTypes.TRUE => {
          return JsonCollection.True
        }
        case EDQLTypes.FALSE => {
          return JsonCollection.False
        }
      }
    }

    if (expr.getIdentifier0 != null) {
      checkParse(expr, expr.getIdentifier0.getText)
      return JsonCollection.Var(expr.getIdentifier0.getText)
    }

    if (expr.getFunctionInvokeExpr != null) {
      val funName = expr.getFunctionInvokeExpr.getIdentifier0.getText
      val es = expr.getFunctionInvokeExpr.getExprList.asScala.map(i => toJsonVal(i)).toSeq
      checkParse(expr, expr.getFunctionInvokeExpr.getText)
      return JsonCollection.Fun((funName, es))
    }

    if (expr.getObj != null) {
      checkParse(expr, expr.getObj.getText)
      return toJsonVal(expr.getObj)
    }

    if (expr.getArr != null) {
      val vs = expr.getArr.getExprList.asScala.map(i => toJsonVal(i)).toSeq
      checkParse(expr, expr.getArr.getText)
      return JsonCollection.Arr(vs: _*)
    }

    if (expr.getBinsuffix != null) {
      checkParse(expr, expr.getBinsuffix.getText)
      return JsonCollection.Num(expr.getBinsuffix.getText.toDouble)
    }

    throw new RuntimeException("parse failed: " + expr.getText)
  }


  private def checkParse(expr: PsiElement, str: String): Unit = {
    if (!expr.textMatches(str)) {
      throw new RuntimeException("parse failed: " + expr.getText)
    }
  }
}
