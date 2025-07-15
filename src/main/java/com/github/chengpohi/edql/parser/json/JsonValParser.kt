package com.github.chengpohi.edql.parser.json

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.chengpohi.edql.parser.psi.*
import com.intellij.psi.PsiElement
import org.apache.commons.collections4.CollectionUtils
import java.util.*

interface JsonValParser {
    companion object {
        private val mapper: ObjectMapper = ObjectMapper().apply {
            configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
        }
    }

    fun toJsonVal(getObjList: List<EDQLObj>): List<JsonCollection.Val> {
        return getObjList.map { toJsonVal(it) }
    }

    fun toJsonVal(obj: EDQLObj): JsonCollection.Val {
        if (obj.members == null) {
            return JsonCollection.Obj()
        }
        val vals = obj.members!!.memberList.map { m ->
            val field = toJsonVal(m.field!!.fieldname)
            val v = toJsonVal(m.field!!.expr)
            field to v
        }

        return JsonCollection.Obj(*vals.toTypedArray())
    }

    fun toJsonVal(fieldname: EDQLFieldname): JsonCollection.Val {
        if (fieldname.expr != null) {
            checkParse(fieldname, fieldname.expr!!.text)
            return toJsonVal(fieldname.expr!!)
        }
        if (fieldname.doubleQuotedString != null) {
            val str = fieldname.doubleQuotedString!!.text
            checkParse(fieldname, str)
            return JsonCollection.Str(str.substring(1, str.length - 1))
        }

        if (fieldname.singleQuotedString != null) {
            val str = fieldname.singleQuotedString!!.text
            checkParse(fieldname, str)
            return JsonCollection.Str(str.substring(1, str.length - 1))
        }

        if (fieldname.identifier0 != null) {
            val str = fieldname.identifier0!!.text
            checkParse(fieldname, str)
            return JsonCollection.Var(str)
        }
        throw RuntimeException("parse failed: ${fieldname.text}")
    }

    fun toJsonVal(arg: EDQLArg): JsonCollection.Val {
        if (arg.binsuffixList.isNotEmpty()) {
            val binsuffixes = arg.binsuffixList
            if (binsuffixes.isNotEmpty()) {
                val paren = arg.firstChild?.node?.elementType == EDQLTypes.L_PAREN
                val value = toJsonVal(binsuffixes, toJsonVal(arg.expr))
                return when (value) {
                    is JsonCollection.ArithTree -> {
                        if (paren) {
                            value.order = 100
                        }
                        value
                    }
                    else -> value
                }
            }
        }

        return toJsonVal(arg.expr)
    }


    fun toJsonVal(expr: EDQLExpr): JsonCollection.Val {
        if (expr.expr != null) {
            if (expr.children != null) {
                val binsuffixes = expr.children.filterIsInstance<EDQLExpr>()
                    .filter { it.binsuffix != null }
                    .map { it.binsuffix!! }
                if (binsuffixes.isNotEmpty()) {
                    val paren = expr.firstChild?.node?.elementType == EDQLTypes.L_PAREN
                    val value = toJsonVal(binsuffixes, toJsonVal(expr.expr!!))
                    return when (value) {
                        is JsonCollection.ArithTree -> {
                            if (paren) {
                                value.order = 100
                            }
                            value
                        }
                        else -> value
                    }
                }
            }

            return toJsonVal(expr.expr!!)
        }

        if (expr.binsuffix != null) {
            val v = toJsonVal(expr.binsuffix!!.expr)
            if (v is JsonCollection.Num) {
                return JsonCollection.Num((expr.binsuffix!!.binaryop.text + v.toJson()).toDouble())
            }
            throw RuntimeException("unsupported syntax: ${expr.text}")
        }

        if (expr.doubleQuotedString != null) {
            val str = expr.doubleQuotedString!!.text
            checkParse(expr, str)
            return JsonCollection.Str(mapper.readTree(str).textValue())
        }

        if (expr.tripleQuotedString != null) {
            val str = expr.tripleQuotedString!!.text
            checkParse(expr, str)
            return JsonCollection.Str(mapper.readTree(str.substring(2, str.length - 2)).textValue())
        }

        if (expr.singleQuotedString != null) {
            val str = expr.singleQuotedString!!.text
            checkParse(expr, str)
            return JsonCollection.Str(mapper.readTree(str).textValue())
        }

        if (expr.number != null) {
            checkParse(expr, expr.number!!.text)
            return JsonCollection.Num(expr.number!!.text.toBigDecimal())
        }

        if (expr.bool != null) {
            return when (expr.bool!!.firstChild?.node?.elementType) {
                EDQLTypes.TRUE -> JsonCollection.True
                EDQLTypes.FALSE -> JsonCollection.False
                else -> throw RuntimeException("Unknown boolean value")
            }
        }

        if (expr.identifier0 != null) {
            checkParse(expr, expr.identifier0!!.text)
            return JsonCollection.Var(expr.identifier0!!.text)
        }

        if (expr.functionInvokeExpr != null) {
            val funName = expr.functionInvokeExpr!!.identifier0.text
            val es = expr.functionInvokeExpr!!.argList.map { toJsonVal(it) }
            checkParse(expr, expr.functionInvokeExpr!!.text)
            return JsonCollection.Fun(funName to es)
        }

        if (expr.obj != null) {
            checkParse(expr, expr.obj!!.text)
            return toJsonVal(expr.obj!!)
        }

        if (expr.arr != null) {
            if (expr.arr!!.mapIter != null) {
                throw RuntimeException("parse failed: ${expr.text}")
            }
            val vs = expr.arr!!.exprList.map { toJsonVal(it) }
            checkParse(expr, expr.arr!!.text)
            return JsonCollection.Arr(*vs.toTypedArray())
        }

        if (expr.binsuffix != null) {
            checkParse(expr, expr.binsuffix!!.text)
            return JsonCollection.Num(expr.binsuffix!!.text.toDouble())
        }

        if (expr.nil != null) {
            checkParse(expr, expr.nil!!.text)
            return JsonCollection.Null
        }

        throw RuntimeException("parse failed: ${expr.text}")
    }

    fun toJsonVal(re: EDQLReturnExpr): JsonCollection.Val {
        val v = toJsonVal(re.expr)
        if (CollectionUtils.isEmpty(re.binsuffixList)) {
            return v
        }

        return toJsonVal(re.binsuffixList, v)
    }

    fun toJsonVal(bind: EDQLBind): JsonCollection.Val {
        if (CollectionUtils.isEmpty(bind.binsuffixList)) {
            return toJsonVal(bind.expr)
        }

        return toJsonVal(bind.binsuffixList, toJsonVal(bind.expr))
    }

    fun toJsonVal(list: List<EDQLBinsuffix>, init: JsonCollection.Val): JsonCollection.Val {
        val i = JsonCollection.ArithTree(init, null, null)
        var point: Pair<JsonCollection.ArithTree?, JsonCollection.ArithTree> = null to i
        
        for (elem in list) {
            val (pre, cur) = point
            val value: JsonCollection.Val = toJsonVal(elem.expr)
            when (elem.binaryop.text) {
                "*", "/", "%" -> {
                    val op = elem.binaryop.text
                    val now = when (cur.b) {
                        null -> JsonCollection.ArithTree(cur.a, op, value)
                        else -> JsonCollection.ArithTree(cur.a, cur.op, JsonCollection.ArithTree(cur.b!!, op, value))
                    }

                    if (pre != null) {
                        pre.b = now
                        point = pre to now
                    } else {
                        cur.a = now.a
                        cur.op = now.op
                        cur.b = now.b
                        point = null to cur
                    }
                }
                else -> {
                    val op = elem.binaryop.text
                    val now = when (cur.op) {
                        null -> {
                            val newTree = JsonCollection.ArithTree(value, null, null)
                            cur.op = op
                            cur.b = newTree
                            point = cur to newTree
                            continue
                        }
                        else -> {
                            val newTree = JsonCollection.ArithTree(cur.copy(), op, value)
                            when (pre) {
                                null -> {
                                    cur.a = newTree.a
                                    cur.op = newTree.op
                                    cur.b = newTree.b
                                    point = null to cur
                                }
                                else -> {
                                    pre.b = newTree
                                    point = pre to newTree
                                }
                            }
                            continue
                        }
                    }
                }
            }
        }
        return flatten(i)
    }

    fun flatten(i: JsonCollection.Val): JsonCollection.Val {
        return when (i) {
            is JsonCollection.ArithTree -> {
                i.a = flatten(i.a)
                when (i.op) {
                    null -> i.a
                    else -> {
                        i.b = flatten(i.b!!)
                        i
                    }
                }
            }
            else -> i
        }
    }

    private fun checkParse(expr: PsiElement, str: String) {
        if (!expr.textMatches(str)) {
            throw RuntimeException("parse failed: ${expr.text}")
        }
    }
} 