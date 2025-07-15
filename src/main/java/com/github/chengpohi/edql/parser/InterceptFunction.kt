package com.github.chengpohi.edql.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.github.chengpohi.context.Definition
import com.github.chengpohi.context.EDQLDefinition
import com.github.chengpohi.context.PureStringDefinition
import com.github.chengpohi.edql.parser.json.JsonCollection
import com.github.chengpohi.script.ScriptContext
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object InterceptFunction {
    private val ow: ObjectWriter = ObjectMapper().writer().withDefaultPrettyPrinter()

    interface Instruction2 {
        val name: String
        fun execute(context: ScriptContext): Definition<*>
        val ds: List<JsonCollection.Dynamic> get() = emptyList()
    }

    interface ScriptContextInstruction2 : Instruction2

    data class CommentInstruction(val c: String) : ScriptContextInstruction2 {
        override val name: String = "comment"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
    }

    data class EndpointBindInstruction(
        val endpoint: String, 
        val kibanaProxy: Boolean = false
    ) : ScriptContextInstruction2 {
        override val name: String = "host"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(endpoint)
    }

    data class TimeoutInstruction(val timeout: Int) : ScriptContextInstruction2 {
        override val name: String = "timeout"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("timeout $timeout")
    }

    data class ImportInstruction(val imp: URL) : ScriptContextInstruction2 {
        override val name: String = "import"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(imp.toString())
    }

    data class AuthorizationBindInstruction(val auth: String) : ScriptContextInstruction2 {
        override val name: String = "authorization"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(auth)
    }

    data class UsernameBindInstruction(val username: String) : ScriptContextInstruction2 {
        override val name: String = "Username"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(username)
    }

    data class PasswordBindInstruction(val password: String) : ScriptContextInstruction2 {
        override val name: String = "Password"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(password)
    }

    data class ApiKeyIdBindInstruction(val apikeyId: String) : ScriptContextInstruction2 {
        override val name: String = "apikeyId"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(apikeyId)
    }

    data class ApiKeySecretBindInstruction(val apiSecret: String) : ScriptContextInstruction2 {
        override val name: String = "secret"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(apiSecret)
    }

    data class ApiSessionTokenBindInstruction(val apiSessionToken: String) : ScriptContextInstruction2 {
        override val name: String = "session"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(apiSessionToken)
    }

    data class AWSRegionBindInstruction(val awsRegion: String) : ScriptContextInstruction2 {
        override val name: String = "region"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(awsRegion)
    }

    data class PostActionInstruction(
        val path: String, 
        val action: List<JsonCollection.Val>
    ) : Instruction2 {
        override val name: String = "post"
        override fun execute(context: ScriptContext): Definition<*> {
            val newPath = mapNewPath(context.variables, path)
            return if (newPath.startsWith("/")) {
                EDQLDefinition.PostActionDefinition(newPath, action)
            } else {
                EDQLDefinition.PostActionDefinition("/$newPath", action)
            }
        }
        override val ds: List<JsonCollection.Dynamic> = action.flatMap { extractDynamics(it) }
    }

    data class DeleteActionInstruction(
        val path: String, 
        val action: JsonCollection.Val?
    ) : Instruction2 {
        override val name: String = "delete"
        override fun execute(context: ScriptContext): Definition<*> {
            val newPath = mapNewPath(context.variables, path)
            return if (newPath.startsWith("/")) {
                EDQLDefinition.DeleteActionDefinition(newPath, action?.toJson())
            } else {
                EDQLDefinition.DeleteActionDefinition("/$newPath", action?.toJson())
            }
        }
        override val ds: List<JsonCollection.Dynamic> = action?.let { extractDynamics(it) } ?: emptyList()
    }

    data class PutActionInstruction(
        val path: String, 
        val action: List<JsonCollection.Val>
    ) : Instruction2 {
        override val name: String = "put"
        override fun execute(context: ScriptContext): Definition<*> {
            val newPath = mapNewPath(context.variables, path)
            return if (newPath.startsWith("/")) {
                EDQLDefinition.PutActionDefinition(newPath, action)
            } else {
                EDQLDefinition.PutActionDefinition("/$newPath", action)
            }
        }
        override val ds: List<JsonCollection.Dynamic> = action.flatMap { extractDynamics(it) }
    }

    data class GetActionInstruction(
        val path: String, 
        val action: JsonCollection.Val?
    ) : Instruction2 {
        override val name: String = "get"
        override fun execute(context: ScriptContext): Definition<*> {
            val newPath = mapNewPath(context.variables, path)
            return if (newPath.startsWith("/")) {
                EDQLDefinition.GetActionDefinition(newPath, action)
            } else {
                EDQLDefinition.GetActionDefinition("/$newPath", action)
            }
        }
        override val ds: List<JsonCollection.Dynamic> = action?.let { extractDynamics(it) } ?: emptyList()
    }

    data class HeadActionInstruction(
        val path: String, 
        val action: JsonCollection.Val?
    ) : Instruction2 {
        override val name: String = "head"
        override fun execute(context: ScriptContext): Definition<*> {
            val newPath = mapNewPath(context.variables, path)
            return if (newPath.startsWith("/")) {
                EDQLDefinition.HeadActionDefinition(newPath, action?.toJson())
            } else {
                EDQLDefinition.HeadActionDefinition("/$newPath", action?.toJson())
            }
        }
        override val ds: List<JsonCollection.Dynamic> = action?.let { extractDynamics(it) } ?: emptyList()
    }

    data class VariableInstruction(
        val variableName: String, 
        val value: JsonCollection.Val
    ) : ScriptContextInstruction2 {
        override val name: String = "variable"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
        override val ds: List<JsonCollection.Dynamic> = extractDynamics(value)
    }

    data class FunctionInstruction(
        val funcName: String, 
        val variableNames: List<String>, 
        val instructions: List<Instruction2>
    ) : ScriptContextInstruction2 {
        override val name: String = "function"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
    }

    data class MapIterInstruction(
        val a: JsonCollection.Val, 
        val f: FunctionInstruction
    ) : Instruction2 {
        override val name: String = "mapiter"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
    }

    data class ForInstruction(
        val tempVariable: String,
        val iterVariable: JsonCollection.Val,
        val instructions: List<Instruction2>
    ) : Instruction2 {
        override val name: String = "for"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
        override val ds: List<JsonCollection.Dynamic> = extractDynamics(iterVariable)
    }

    data class ReturnInstruction(val value: JsonCollection.Val) : Instruction2 {
        override val name: String = "return"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
        override val ds: List<JsonCollection.Dynamic> = extractDynamics(value)
    }

    data class FunctionInvokeInstruction(
        val funcName: String, 
        val vals: List<JsonCollection.Val>, 
        val map: MapIterInstruction? = null
    ) : Instruction2 {
        override val name: String = "functionInvoke"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition("")
        override val ds: List<JsonCollection.Dynamic> = vals.flatMap { extractDynamics(it) }
    }

    data class ReadJSONInstruction(val filePath: JsonCollection.Var) : Instruction2 {
        override val name: String = "readJSONInstruction"
        override fun execute(context: ScriptContext): Definition<*> {
            val contextPath = context.variables["CONTEXT_PATH"]
            val currentDir = contextPath?.let { 
                (it as JsonCollection.Str).value + "/" 
            } ?: ""
            val targetPath = filePath.toJson()
            val content = Files.readAllLines(Paths.get(currentDir + targetPath.replace(Regex("^\"|\"$"), "")))
                .joinToString(System.lineSeparator())
            return PureStringDefinition(content)
        }
        override val ds: List<JsonCollection.Dynamic> = extractDynamics(filePath)
    }

    data class WriteJSONInstruction(
        val filePath: JsonCollection.Val, 
        val data: JsonCollection.Val
    ) : Instruction2 {
        override val name: String = "writeJSONInstruction"
        override fun execute(context: ScriptContext): Definition<*> {
            val contextPath = context.variables["CONTEXT_PATH"]
            val currentDir = contextPath?.let { 
                (it as JsonCollection.Str).value + "/" 
            } ?: ""
            Files.write(
                Paths.get(currentDir + filePath.toJson().replace(Regex("^\"|\"$"), "")),
                data.toJson().toByteArray()
            )
            return PureStringDefinition("")
        }
        override val ds: List<JsonCollection.Dynamic> = listOf(filePath, data).flatMap { extractDynamics(it) }
    }

    data class JQInstruction(
        val data: JsonCollection.Val, 
        val path: JsonCollection.Val
    ) : Instruction2 {
        override val name: String = "jqInstruction"
        override fun execute(context: ScriptContext): Definition<*> {
            val jsonO = data.toJson()
            val jsonPath = path.toJson().replace(Regex("^\"|\"$"), "")
            val configuration = Configuration.defaultConfiguration()
                .addOptions(com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL)
            val pathObj: Any = JsonPath.using(configuration).parse(jsonO).read(jsonPath)
            return PureStringDefinition(ow.writeValueAsString(pathObj))
        }
        override val ds: List<JsonCollection.Dynamic> = listOf(path, data).flatMap { extractDynamics(it) }
    }

    data class PrintInstruction(val v: JsonCollection.Val) : Instruction2 {
        override val name: String = "printInstruction"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(v.toJson())
        override val ds: List<JsonCollection.Dynamic> = listOf(v).flatMap { extractDynamics(it) }
    }

    data class PrintlnInstruction(val v: JsonCollection.Val) : Instruction2 {
        override val name: String = "printlnInstruction"
        override fun execute(context: ScriptContext): Definition<*> = PureStringDefinition(v.toJson() + "\n")
        override val ds: List<JsonCollection.Dynamic> = listOf(v).flatMap { extractDynamics(it) }
    }

    private fun mapNewPath(variables: MutableMap<String, JsonCollection.Val>, path: String): String {
        val invokePath = variables["INVOKE_PATH"]?.let { 
            (it as JsonCollection.Str).value 
        }?.split("\$")?.distinct()?.joinToString("$") ?: ""
        
        var resultPath = path
        variables.filter { it.key.startsWith(invokePath) }
            .forEach { entry ->
                val vName = entry.key.replace("$invokePath$", "")
                val v: String = when (val value = entry.value) {
                    is JsonCollection.Str -> value.value
                    is JsonCollection.Var -> value.realValue?.let { realValue ->
                        when (realValue) {
                            is JsonCollection.Str -> realValue.value
                            else -> realValue.toJson()
                        }
                    } ?: value.value
                    is JsonCollection.ArithTree -> value.realValue?.let { realValue ->
                        when (realValue) {
                            is JsonCollection.Str -> realValue.value
                            else -> realValue.toJson()
                        }
                    } ?: value.toJson()
                    else -> value.toJson()
                }
                resultPath = resultPath.replace("$$vName", v)
            }
        return resultPath
    }

    private fun extractDynamics(iterVariable: JsonCollection.Val): List<JsonCollection.Dynamic> {
        return when (iterVariable) {
            is JsonCollection.Dynamic -> listOf(iterVariable)
            is JsonCollection.Obj -> iterVariable.value.flatMap { (k, v) -> 
                extractDynamics(k) + extractDynamics(v) 
            }
            is JsonCollection.Tuple -> iterVariable.value.flatMap { extractDynamics(it) }
            is JsonCollection.Arr -> iterVariable.value.flatMap { extractDynamics(it) }
            else -> emptyList()
        }
    }

    val systemFunction: Map<String, FunctionInstruction> = mapOf(
        "jq2" to FunctionInstruction(
            "jq", 
            listOf("data", "path"), 
            listOf(JQInstruction(JsonCollection.Var("data"), JsonCollection.Var("path")))
        ),
        "print1" to FunctionInstruction(
            "print", 
            listOf("v"), 
            listOf(PrintInstruction(JsonCollection.Var("v")))
        ),
        "println1" to FunctionInstruction(
            "println", 
            listOf("str"), 
            listOf(PrintlnInstruction(JsonCollection.Var("str")))
        ),
        "readJSON1" to FunctionInstruction(
            "readJSON", 
            listOf("filePath"), 
            listOf(ReadJSONInstruction(JsonCollection.Var("filePath")))
        ),
        "writeJSON2" to FunctionInstruction(
            "writeJSON", 
            listOf("filePath", "data"), 
            listOf(WriteJSONInstruction(JsonCollection.Var("filePath"), JsonCollection.Var("data")))
        )
    )
} 