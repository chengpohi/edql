package com.github.chengpohi.edql.parser

import com.github.chengpohi.edql.parser.json.JsonCollection
import com.github.chengpohi.edql.parser.json.JsonValParser
import com.github.chengpohi.edql.parser.psi.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.RandomStringUtils
import java.net.URL

class EDQLPsiInterceptor(val parserFactory: EDQLParserFactory) : JsonValParser {
    fun parseJson(text: String): Result<JsonCollection.Val> {
        return ApplicationManager.getApplication().runReadAction(Computable {
            try {
                val psiFile = parserFactory.createFile("dummy", text)
                
                val element = PsiTreeUtil.findChildOfType(psiFile, PsiErrorElement::class.java)
                if (element != null) {
                    throw RuntimeException(element.errorDescription)
                }
                
                val expr = PsiTreeUtil.findChildOfType(psiFile, EDQLExpr::class.java)
                if (expr == null || !expr.textMatches(text)) {
                    throw RuntimeException("parse failed: $text")
                }
                Result.success(toJsonVal(expr))
            } catch (ex: Throwable) {
                Result.failure(ex)
            }
        })
    }
    
    fun parseExpr(text: String): Result<PsiFile> {
        return ApplicationManager.getApplication().runReadAction(Computable {
            try {
                val psiFile = parserFactory.createFile("dummy", text)
                val element = PsiTreeUtil.findChildOfType(psiFile, PsiErrorElement::class.java)
                if (element != null) {
                    throw RuntimeException(element.errorDescription)
                }
                return@Computable Result.success(psiFile)
            } catch (ex: Exception) {
                return@Computable Result.failure(ex)
            }
        })
    }
    
    fun parse(text: String): Result<List<InterceptFunction.Instruction2>> {
        return ApplicationManager.getApplication().runReadAction(Computable {
            try {
                val psiFile = parserFactory.createFile("dummy", text)
                val element = PsiTreeUtil.findChildOfType(psiFile, PsiErrorElement::class.java)
                if (element != null) {
                    throw RuntimeException(element.errorDescription)
                }
                
                val children = psiFile.children
                val ins = children.flatMap { child ->
                    when (child) {
                        is EDQLImportop -> {
                            listOf(InterceptFunction.ImportInstruction(URL(child.doubleQuotedString.text)))
                        }
                        is EDQLTimeoutExpr -> {
                            listOf(InterceptFunction.TimeoutInstruction(child.number.text.toInt()))
                        }
                        is EDQLHeadExpr -> {
                            throw RuntimeException("unsupported instruction: ${child.text}")
                        }
                        is EDQLExpr -> parseExpr(child)
                        is PsiWhiteSpace -> emptyList()
                        is PsiComment -> emptyList()
                        else -> {
                            throw RuntimeException("unsupported instruction: ${child.text}")
                        }
                    }
                }
                return@Computable Result.success(ins)
            } catch (ex: Throwable) {
                return@Computable Result.failure(ex)
            }
        })
    }
    
    private fun parseExpr(expr: EDQLExpr): List<InterceptFunction.Instruction2> {
        if (expr.actionExpr != null) {
            return listOf(parseAction(expr.actionExpr!!))
        }
        
        if (expr.comment != null) {
            return emptyList()
        }
        
        if (expr.functionExpr != null) {
            val f = expr.functionExpr!!
            val ps = f.params?.paramList?.map { i ->
                i.identifier0.text
            } ?: emptyList()
            
            val bs = f.functionBody.exprList.flatMap { i -> parseExpr(i) }
            
            return when (val returnExpr = f.functionBody.returnExpr) {
                null -> listOf(InterceptFunction.FunctionInstruction(f.identifier0.text, ps, bs))
                else -> {
                    val rs = InterceptFunction.ReturnInstruction(toJsonVal(returnExpr))
                    listOf(InterceptFunction.FunctionInstruction(f.identifier0.text, ps, bs + rs))
                }
            }
        }
        
        if (expr.forExpr != null) {
            val vname = expr.forExpr!!.identifier0.text
            val v = toJsonVal(expr.forExpr!!.expr)
            val bs = expr.forExpr!!.functionBody.exprList.flatMap { i -> parseExpr(i) }
            return listOf(InterceptFunction.ForInstruction(vname, v, bs))
        }

        if (expr.functionInvokeExpr != null) {
            val funcName = expr.functionInvokeExpr!!.identifier0.text
            val ins = expr.functionInvokeExpr!!.argList.map { it -> toJsonVal(it) }
            
            val mapIter = expr.functionInvokeExpr!!.mapIter
            if (mapIter != null) {
                return listOf(InterceptFunction.FunctionInvokeInstruction(
                    funcName, 
                    ins, 
                    buildMapIterInstruction(JsonCollection.Arr(), mapIter)
                ))
            }
            
            return listOf(InterceptFunction.FunctionInvokeInstruction(funcName, ins))
        }
        
        if (expr.outervar != null) {
            val outervar = expr.outervar!!
            val varName = outervar.bind.identifier0.text
            
            val mapIterInstruction: InterceptFunction.MapIterInstruction? = if (outervar.bind.mapIter != null) {
                buildMapIterInstruction(JsonCollection.Arr(), outervar.bind.mapIter!!)
            } else null
            
            if (CollectionUtils.isNotEmpty(outervar.bind.binsuffixList)) {
                val j = toJsonVal(outervar.bind)
                return listOf(InterceptFunction.VariableInstruction(varName, j))
            }
            
            val anonymousFun = "anonymousFun_" + RandomStringUtils.randomAlphabetic(10)
            
            val e = outervar.bind.expr
            val v = try { toJsonVal(e) } catch (ex: Throwable) {null}
            if (v != null) {
                return when (v) {
                    is JsonCollection.Arr -> {
                        if (mapIterInstruction != null) {
                            listOf(
                                InterceptFunction.VariableInstruction(varName, JsonCollection.Fun(anonymousFun to emptyList())),
                                InterceptFunction.FunctionInstruction(anonymousFun, emptyList(), listOf(mapIterInstruction.copy(a = v)))
                            )
                        } else {
                            listOf(InterceptFunction.VariableInstruction(varName, v))
                        }
                    }
                    is JsonCollection.Var -> {
                        if (mapIterInstruction != null) {
                            listOf(
                                InterceptFunction.VariableInstruction(varName, JsonCollection.Fun(anonymousFun to emptyList())),
                                InterceptFunction.FunctionInstruction(anonymousFun, emptyList(), listOf(mapIterInstruction.copy(a = v)))
                            )
                        } else {
                            listOf(InterceptFunction.VariableInstruction(varName, v))
                        }
                    }
                    is JsonCollection.Fun -> {
                        val iter = e.functionInvokeExpr?.mapIter
                        if (iter != null) {
                            val instruction = buildMapIterInstruction(JsonCollection.Arr(), iter)
                            listOf(
                                InterceptFunction.VariableInstruction(varName, JsonCollection.Fun(anonymousFun to emptyList())),
                                InterceptFunction.FunctionInstruction(anonymousFun, emptyList(), listOf(instruction.copy(a = v)))
                            )
                        } else {
                            listOf(InterceptFunction.VariableInstruction(varName, v))
                        }
                    }
                    else -> listOf(InterceptFunction.VariableInstruction(varName, v))
                }
            }
            
            return listOf(
                InterceptFunction.VariableInstruction(varName, JsonCollection.Fun(anonymousFun to emptyList())),
                InterceptFunction.FunctionInstruction(anonymousFun, emptyList(), parseExpr(outervar.bind.expr))
            )
        }
        
        if (expr.arr != null && expr.arr!!.mapIter != null) {
            val arr = expr.arr!!.exprList.map { i -> toJsonVal(i) }
            return listOf(buildMapIterInstruction(JsonCollection.Arr(*arr.toTypedArray()), expr.arr!!.mapIter!!))
        }
        
        throw RuntimeException("unsupported instruction: ${expr.text}")
    }
    
    private fun buildMapIterInstruction(arr: JsonCollection.Arr, mapIter: EDQLMapIter): InterceptFunction.MapIterInstruction {
        val anonymousFun = "anonymousFun_" + RandomStringUtils.randomAlphabetic(10)
        val bs: List<InterceptFunction.Instruction2> = mapIter.mapExpr.exprList.flatMap { i -> parseExpr(i) }
        
        return when (val returnExpr = mapIter.mapExpr.returnExpr) {
            null -> InterceptFunction.MapIterInstruction(arr, InterceptFunction.FunctionInstruction(anonymousFun, listOf("it"), bs))
            else -> {
                val rs = InterceptFunction.ReturnInstruction(toJsonVal(returnExpr))
                InterceptFunction.MapIterInstruction(arr, InterceptFunction.FunctionInstruction(anonymousFun, listOf("it"), bs + rs))
            }
        }
    }
    
    private fun parseAction(expr: EDQLActionExpr): InterceptFunction.Instruction2 {
        val v = toJsonVal(expr.objList)
        
        return when (expr.method.firstChild.node.elementType) {
            EDQLTypes.POST -> {
                InterceptFunction.PostActionInstruction(
                    expr.path.text + (expr.query?.text ?: ""), 
                    v
                )
            }
            EDQLTypes.GET -> {
                InterceptFunction.GetActionInstruction(
                    expr.path.text + (expr.query?.text ?: ""), 
                    v.firstOrNull()
                )
            }
            EDQLTypes.PUT -> {
                InterceptFunction.PutActionInstruction(
                    expr.path.text + (expr.query?.text ?: ""), 
                    v
                )
            }
            EDQLTypes.DELETE -> {
                InterceptFunction.DeleteActionInstruction(
                    expr.path.text + (expr.query?.text ?: ""), 
                    v.firstOrNull()
                )
            }
            EDQLTypes.HEAD -> {
                InterceptFunction.HeadActionInstruction(
                    expr.path.text + (expr.query?.text ?: ""), 
                    v.firstOrNull()
                )
            }
            else -> throw RuntimeException("Unsupported HTTP method: ${expr.method.text}")
        }
    }
} 