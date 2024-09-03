package com.github.chengpohi.edql.parser.json

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.chengpohi.edql.parser.psi._
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.collections.CollectionUtils

import java.util
import scala.jdk.CollectionConverters.ListHasAsScala

trait JsonValParser {
  private val mapper: ObjectMapper = new ObjectMapper()
  mapper.configure(
    JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
    true
  );

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
      if (expr.getChildren != null) {
        val binsuffixes = expr.getChildren.filter(i => i.isInstanceOf[EDQLExpr])
          .filter(i => i.asInstanceOf[EDQLExpr].getBinsuffix != null)
          .map(_.asInstanceOf[EDQLExpr].getBinsuffix)
        if (binsuffixes.length > 0) {
          val paren = if (expr.getFirstChild.getNode.getElementType == EDQLTypes.L_PAREN) true else false
          val value = toJsonVal(util.Arrays.asList(binsuffixes: _*), toJsonVal(expr.getExpr))
          return value match {
            case tree: JsonCollection.ArithTree =>
              if (paren) {
                tree.order = Some(100)
              }
              tree
            case _ => value
          }
        }
      }

      return toJsonVal(expr.getExpr)
    }

    if (expr.getBinsuffix != null) {
      val v = toJsonVal(expr.getBinsuffix.getExpr)
      if (v.isInstanceOf[JsonCollection.Num]) {
        return JsonCollection.Num((expr.getBinsuffix.getBinaryop.getText + v.toJson).toDouble)
      }
      throw new RuntimeException("unsupported syntax: " + expr.getText)
    }

    if (expr.getDoubleQuotedString != null) {
      val str = expr.getDoubleQuotedString.getText
      checkParse(expr, str)
      return JsonCollection.Str(mapper.readTree(str).textValue())
    }

    if (expr.getTripleQuotedString != null) {
      val str = expr.getTripleQuotedString.getText
      checkParse(expr, str)
      return JsonCollection.Str(mapper.readTree(str.substring(2, str.length - 2)).textValue())
    }

    if (expr.getSingleQuotedString != null) {
      val str = expr.getSingleQuotedString.getText
      checkParse(expr, str)
      return JsonCollection.Str(mapper.readTree(str).textValue())
    }

    if (expr.getNumber != null) {
      checkParse(expr, expr.getNumber.getText)
      return JsonCollection.Num(BigDecimal(expr.getNumber.getText))
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
      if (expr.getArr.getMapIter != null) {
        throw new RuntimeException("parse failed: " + expr.getText)
      }
      val vs = expr.getArr.getExprList.asScala.map(i => toJsonVal(i)).toSeq
      checkParse(expr, expr.getArr.getText)
      return JsonCollection.Arr(vs: _*)
    }

    if (expr.getBinsuffix != null) {
      checkParse(expr, expr.getBinsuffix.getText)
      return JsonCollection.Num(expr.getBinsuffix.getText.toDouble)
    }

    if (expr.getNil != null) {
      checkParse(expr, expr.getNil.getText)
      return JsonCollection.Null
    }

    throw new RuntimeException("parse failed: " + expr.getText)
  }


  def toJsonVal(re: EDQLReturnExpr): JsonCollection.Val = {
    val v = toJsonVal(re.getExpr)
    if (CollectionUtils.isEmpty(re.getBinsuffixList)) {
      return v
    }

    toJsonVal(re.getBinsuffixList, v)
  }

  def toJsonVal(bind: EDQLBind): JsonCollection.Val = {
    if (CollectionUtils.isEmpty(bind.getBinsuffixList)) {
      return toJsonVal(bind.getExpr)
    }

    toJsonVal(bind.getBinsuffixList, toJsonVal(bind.getExpr))
  }


  def toJsonVal(list: util.List[EDQLBinsuffix], init: JsonCollection.Val): JsonCollection.Val = {
    val i = JsonCollection.ArithTree(init, None, None)
    var point: (JsonCollection.ArithTree, JsonCollection.ArithTree) = (null, i)
    for (elem <- list.asScala) {
      val (pre, cur) = point
      val value: JsonCollection.Val = toJsonVal(elem.getExpr)
      elem.getBinaryop.getText match {
        case ("*" | "/" | "%") => {
          val op = elem.getBinaryop.getText
          val now = cur.b match {
            case Some(j) => {
              JsonCollection.ArithTree(cur.a, cur.op, Some(JsonCollection.ArithTree(j, Some(op), Some(value))))
            }
            case None =>
              JsonCollection.ArithTree(cur.a, Some(op), Some(value))
          }

          if (pre != null) {
            pre.b = Some(now)
            point = (pre, now)
          } else {
            cur.a = now.a
            cur.op = now.op
            cur.b = now.b
            point = (null, cur)
          }
        }
        case op => {
          var now: JsonCollection.ArithTree = null
          cur.op match {
            case Some(o) =>
              now = JsonCollection.ArithTree(cur.copy, Some(op), Some(value))
              pre match {
                case null =>
                  cur.a = now.a
                  cur.op = now.op
                  cur.b = now.b
                  point = (null, cur)
                case p =>
                  p.b = Some(now)
                  point = (p, now)
              }
            case None =>
              now = JsonCollection.ArithTree(value, None, None)
              cur.op = Some(op)
              cur.b = Some(now)
              point = (cur, now)
          }
        }
      }
    }
    flatten(i)
  }


  def flatten(i: JsonCollection.Val): JsonCollection.Val = {
    i match {
      case a: JsonCollection.ArithTree => {
        a.a = flatten(a.a)
        a.op match {
          case Some(o) =>
            a.b = Some(flatten(a.b.get))
            a
          case None =>
            a.a
        }
      }
      case j => j
    }

  }

  private def checkParse(expr: PsiElement, str: String): Unit = {
    if (!expr.textMatches(str)) {
      throw new RuntimeException("parse failed: " + expr.getText)
    }
  }
}
