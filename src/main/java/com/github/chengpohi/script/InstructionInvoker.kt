package com.github.chengpohi.script

import com.github.chengpohi.edql.parser.EDQLPsiInterceptor
import com.github.chengpohi.edql.parser.InterceptFunction
import com.github.chengpohi.edql.parser.json.JsonCollection
import org.apache.commons.lang3.StringUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.stream.Collectors

class InstructionInvoker(private val libs: List<URL>, private val parser: EDQLPsiInterceptor) {
    private val httpClient: HttpClient by lazy { HttpClient.newHttpClient() }

    fun invokeInstruction(
        invokeIns: List<InterceptFunction.Instruction2>,
        scriptContextIns: List<InterceptFunction.Instruction2>,
        runContext: EDQLRunContext
    ): EDQLRunResult {
        val (functions, context) = buildContext(scriptContextIns, runContext)

        val invokeResult = runInstructions(functions, context, invokeIns)

        context.clear()
        return EDQLRunResult(
            Result.success(invokeResult.map {
                when (it) {
                    is JsonCollection.Str -> it.raw()
                    is JsonCollection.Var -> it.realValue?.let { value ->
                        when (value) {
                            is JsonCollection.Str -> value.raw()
                            else -> value.toJson()
                        }
                    } ?: ""
                    else -> it.toJson()
                }
            }.filter { it.isNotEmpty() }),
            runContext
        )
    }

    private fun buildContext(
        cIns: List<InterceptFunction.Instruction2>,
        runContext: EDQLRunContext
    ): Pair<Map<String, InterceptFunction.FunctionInstruction>, ScriptContext> {
        val importIns = parseImports(cIns + libs.map { InterceptFunction.ImportInstruction(it) }, runContext.runDir)

        val invokeIns = cIns + importIns

        val duplicateVariables = invokeIns.filterIsInstance<InterceptFunction.VariableInstruction>()
            .groupBy { it.variableName }
            .filter { it.value.size >= 2 }

        if (duplicateVariables.isNotEmpty()) {
            throw RuntimeException("duplicate variable: ${duplicateVariables.keys.joinToString(",")}")
        }

        val duplicateFunctions = invokeIns.filterIsInstance<InterceptFunction.FunctionInstruction>()
            .groupBy { it.funcName + it.variableNames.size }
            .filter { it.value.size >= 2 }

        if (duplicateFunctions.isNotEmpty()) {
            throw RuntimeException("duplicate function: ${duplicateFunctions.keys.joinToString(",")}")
        }

        val globalFunctions = invokeIns.filterIsInstance<InterceptFunction.FunctionInstruction>()
                                  .associateBy { it.funcName + it.variableNames.size } + InterceptFunction.systemFunction

        val vars = invokeIns.filterIsInstance<InterceptFunction.VariableInstruction>()
            .map { it.variableName to it.value }

        val globalVars = vars + ("CONTEXT_PATH" to JsonCollection.Str(runContext.runDir))
        val context = ScriptContext.apply(runContext.hostInfo, globalVars.toMap().toMutableMap())
        evalFunParams(globalFunctions, context, vars)
        return Pair(globalFunctions, context)
    }

    private fun parseImports(cIns: List<InterceptFunction.Instruction2>, runDir: String): List<InterceptFunction.Instruction2> {
        val imports = cIns.filterIsInstance<InterceptFunction.ImportInstruction>()

        if (imports.isEmpty()) {
            return emptyList()
        }

        val importStr = imports.joinToString("") { handleImport(runDir, httpClient, it) }.trim()

        val importIns = parser.parse(importStr).getOrThrow()
        return importIns + parseImports(
            importIns, runDir
        ).filter { it is InterceptFunction.FunctionInstruction || it is InterceptFunction.VariableInstruction }
    }

    private fun handleImport(runDir: String, client: HttpClient, i: InterceptFunction.ImportInstruction): String {
        val imp = i.imp
        val content = readFile(runDir, imp)
        if (!StringUtils.isEmpty(content)) {
            return content
        }
        val body = readFromWeb(client, imp)

        if (!StringUtils.isEmpty(body)) {
            return body
        }

        throw RuntimeException("could not found content from $imp")
    }

    private fun readFromWeb(client: HttpClient, imp: URL): String {
        return try {
            val httpRequest = HttpRequest.newBuilder()
                .uri(imp.toURI())
                .GET()
                .build()
            val response = client.send(httpRequest, BodyHandlers.ofString())
            response.body()
        } catch (e: Throwable) {
            ""
        }
    }

    private fun readFile(runDir: String, url: URL): String {
        return try {
            val reader = BufferedReader(InputStreamReader(url.openStream()))
            reader.lines().collect(Collectors.joining(System.lineSeparator()))
        } catch (e: Throwable) {
            ""
        }
    }

    private fun evalFunParams(
        globalFunctions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        parms: List<Pair<String, JsonCollection.Val>>,
        funName: String? = null
    ) {
        parms.forEach { (name, value) ->
            when (value) {
                is JsonCollection.Fun -> {
                    if (value.realValue == null) {
                        val fParam = value
                        val result = invokeFunction(
                            globalFunctions,
                            context,
                            InterceptFunction.FunctionInvokeInstruction(fParam.value.first, fParam.value.second),
                            funName
                        ).lastOrNull()
                        fParam.realValue = result
                    }
                    context.variables[name] = value.realValue ?: JsonCollection.Null
                }
                is JsonCollection.Var -> {
                    mapRealValue(globalFunctions, context, value, funName)
                    context.variables[name] = value.realValue!!
                }
                is JsonCollection.ArithTree -> {
                    context.variables[name] = value
                }
                else -> {
                    context.variables[name] = value
                }
            }
        }

        val arithes = parms.filter { it.second is JsonCollection.ArithTree }
            .map { it.second as JsonCollection.ArithTree }
        arithes.forEach { a ->
            evalArith(globalFunctions, context, a, funName)
        }

        val vals = parms.filter { it.second !is JsonCollection.Fun }.map { (name, value) ->
            when (value) {
                is JsonCollection.Var -> {
                    mapRealValue(globalFunctions, context, value, funName)
                    name to value.realValue!!
                }
                else -> name to value
            }
        }
        context.variables.putAll(vals)
    }

    fun extractCollection(iterVariable: JsonCollection.Val): JsonCollection.Arr {
        return when (iterVariable) {
            is JsonCollection.Arr -> iterVariable
            is JsonCollection.Var -> {
                if (iterVariable.realValue is JsonCollection.Arr) {
                    iterVariable.realValue as JsonCollection.Arr
                } else {
                    throw RuntimeException("iter variable not a collection")
                }
            }
            else -> throw RuntimeException("iter variable not a collection")
        }
    }

    fun iterCollection(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        r: InterceptFunction.ForInstruction
    ): List<JsonCollection.Val> {
        val cachedVariables = context.variables.toMutableMap()

        val iterVariable = r.iterVariable

        val instructions = extractCollection(iterVariable).value.flatMap { i ->
            context.variables = mutableMapOf<String, JsonCollection.Val>().apply {
                putAll(cachedVariables)
            }
            context.variables[r.tempVariable] = i
            runInstructions(functions, context, r.instructions)
        }

        context.variables = cachedVariables
        return instructions
    }

    fun invokeFunction(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        invoke: InterceptFunction.FunctionInvokeInstruction,
        parentFunName: String? = null
    ): List<JsonCollection.Val> {
        val cachedVariables = context.variables.toMutableMap()
        val values = invoke.vals

        val foundFunction = functions[invoke.funcName + values.size]
                            ?: throw RuntimeException("could not found method: ${invoke.funcName} with parameters ${values.size}")

        val f = foundFunction
        val funName = "${f.funcName}_${f.variableNames.size}"

        setInvokePath(context, funName)

        try {
            val funParams = f.variableNames.mapIndexed { index, name ->
                val path = context.variables["INVOKE_PATH"]?.value ?: funName
                "$path\$$name" to values[index]
            }
            clearContextBeforeInvoke(context, f.instructions, funParams)

            funParams.filter { it.second is JsonCollection.Var }.forEach { (_, value) ->
                (value as JsonCollection.Var).realValue = null
            }

            evalFunParams(functions, context, funParams, parentFunName ?: funName)

            val instructions = f.instructions

            val nestFunctions = instructions.filterIsInstance<InterceptFunction.FunctionInstruction>().associate { f ->
                "${f.funcName}${f.variableNames.size}" to f
            }

            val funcBodyVars = instructions.filterIsInstance<InterceptFunction.VariableInstruction>().map { i ->
                val path = context.variables["INVOKE_PATH"]?.value ?: funName
                "$path\$${i.variableName}" to i.value
            }

            val nestVars = (funcBodyVars + funParams).flatMap { (key, value) ->
                nestFunctions.map { (funcKey, func) ->
                    key.replace(funName, "${func.funcName}_${func.variableNames.size}") to value
                }
            }

            context.variables.putAll(nestVars)

            evalFunParams(functions + nestFunctions, context, funcBodyVars, funName)

            val response = runInstructions(functions + nestFunctions, context, instructions, funName)

            context.variables = cachedVariables

            if (invoke.map == null) {
                return response
            }

            return when (response) {
                listOf<JsonCollection.Val>() -> response
                else -> {
                    when (val r = response.first()) {
                        is JsonCollection.Arr -> {
                            val m = invoke.map.copy(a = r)
                            invokeMapIter(functions, context, m, parentFunName)
                        }
                        else -> response
                    }
                }
            }
        } finally {
            dropOnePath(context, funName)
        }
    }

    private fun clearContextBeforeInvoke(
        context: ScriptContext,
        instructions: List<InterceptFunction.Instruction2>,
        funParams: List<Pair<String, JsonCollection.Val>>
    ) {
        instructions.forEach { fi ->
            fi.ds.forEach { di ->
                di.clean()
            }
        }

        funParams.forEach { (key, _) ->
            context.variables.remove(key)
        }
    }

    fun invokeMapIter(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        m: InterceptFunction.MapIterInstruction,
        funName: String?
    ): List<JsonCollection.Val> {
        fun mapArr(a: JsonCollection.Arr): List<JsonCollection.Val> {
            val res: List<JsonCollection.Val> = a.value.map { i ->
                val f = m.f
                val fs: MutableMap<String, InterceptFunction.FunctionInstruction> = mutableMapOf()
                fs.putAll(functions)
                fs["${f.funcName}${f.variableNames.size}"] = f
                clearContextBeforeInvoke(context, f.instructions, emptyList())
                val invoke = InterceptFunction.FunctionInvokeInstruction(f.funcName, listOf(i))
                runInstructions(fs, context, listOf(invoke), funName).lastOrNull()
            }.filterNotNull()
            return listOf(JsonCollection.Arr(*res.toTypedArray()))
        }

        return when (m.a) {
            is JsonCollection.Arr -> mapArr(m.a)
            is JsonCollection.Var -> {
                when {
                    m.a.realValue != null && m.a.realValue is JsonCollection.Arr -> {
                        mapArr(m.a.realValue as JsonCollection.Arr)
                    }
                    m.a.realValue == null -> {
                        mapRealValue(functions, context, m.a, funName)
                        when (val realValue = m.a.realValue) {
                            is JsonCollection.Arr -> mapArr(realValue)
                            else -> throw RuntimeException("unsupported map")
                        }
                    }
                    else -> emptyList()
                }
            }
            is JsonCollection.Fun -> {
                val res = invokeFunction(
                    functions,
                    context,
                    InterceptFunction.FunctionInvokeInstruction(m.a.value.first, m.a.value.second),
                    funName
                ).last()
                when (res) {
                    is JsonCollection.Arr -> mapArr(res)
                    else -> throw RuntimeException("unsupported map")
                }
            }
            else -> {
                emptyList()
            }
        }
    }

    fun runInstructions(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        instructions: List<InterceptFunction.Instruction2>,
        funName: String? = null
    ): List<JsonCollection.Val> {
        setInvokePath(context, funName)
        try {
            instructions.forEach { po ->
                po.ds.forEach { ds ->
                    ds.vars().forEach { j -> j.realValue = null }
                }
                evalDs(functions, context, po.ds, funName)
            }

            return instructions.filterNot { it is InterceptFunction.ScriptContextInstruction2 }.flatMap {
                when (it) {
                    is InterceptFunction.ForInstruction -> iterCollection(functions, context, it)
                    is InterceptFunction.FunctionInvokeInstruction -> invokeFunction(functions, context, it, funName)
                    is InterceptFunction.ReturnInstruction -> listOf(it.value.copy())
                    is InterceptFunction.MapIterInstruction -> invokeMapIter(functions, context, it, funName)
                    else -> {
                        val json = it.execute(context).json(context)
                        val parsed = parser.parseJson(json)
                        if (parsed.isSuccess) {
                            return listOf(parsed.getOrNull()!!)
                        } else {
                            return listOf(JsonCollection.Str(json))
                        }
                    }
                }
            }
        } finally {
            dropOnePath(context, funName)
        }
    }

    fun evalDs(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        vars: List<JsonCollection.Dynamic>,
        funName: String? = null
    ) {
        vars.forEach { var_ ->
            when (var_) {
                is JsonCollection.Var -> mapRealValue(functions, context, var_, funName)
                is JsonCollection.ArithTree -> evalArith(functions, context, var_, funName)
                is JsonCollection.Fun -> {
                    val vss = invokeFunction(
                        functions,
                        context,
                        InterceptFunction.FunctionInvokeInstruction(var_.value.first, var_.value.second),
                        funName
                    )
                    val res = vss.lastOrNull()
                    var_.realValue = res
                }
            }
        }
    }

    fun evalArith(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        v: JsonCollection.ArithTree,
        funName: String? = null
    ) {
        when (v.op) {
            "+" -> {
                val v1 = evalBasicValue(functions, context, v.a, funName)
                when (v.b) {
                    null -> v.realValue = v1
                    else -> {
                        val v2 = evalBasicValue(functions, context, v.b!!, funName)
                        v.realValue = v1.plus(v2)
                    }
                }
            }
            "-" -> {
                val v1 = evalBasicValue(functions, context, v.a, funName)
                when (v.b) {
                    null -> v.realValue = v1
                    else -> {
                        when (v.b) {
                            is JsonCollection.ArithTree -> {
                                if ((v.b!! as JsonCollection.ArithTree).op != null && (v.b!! as JsonCollection.ArithTree).order == null) {
                                    val op = when ((v.b!! as JsonCollection.ArithTree).op) {
                                        "-" -> "+"
                                        "+" -> "-"
                                        else -> (v.b!! as JsonCollection.ArithTree).op
                                    }
                                    val v2 = evalBasicValue(
                                        functions,
                                        context,
                                        JsonCollection.ArithTree(
                                            (v.b!! as JsonCollection.ArithTree).a, op, (v.b!! as JsonCollection.ArithTree).b
                                        ),
                                        funName
                                    )
                                    v.realValue = v1.minus(v2)
                                } else {
                                    val v2 = evalBasicValue(functions, context, v.b!!, funName)
                                    v.realValue = v1.minus(v2)
                                }
                            }
                            else -> {
                                val v2 = evalBasicValue(functions, context, v.b!!, funName)
                                v.realValue = v1.minus(v2)
                            }
                        }
                    }
                }
            }
            "*" -> {
                val v1 = evalBasicValue(functions, context, v.a, funName)
                when (v.b) {
                    null -> v.realValue = v1
                    else -> {
                        val v2 = evalBasicValue(functions, context, v.b!!, funName)
                        v.realValue = v1.multiply(v2)
                    }
                }
            }
            "/" -> {
                val v1 = evalBasicValue(functions, context, v.a, funName)
                when (v.b) {
                    null -> v.realValue = v1
                    else -> {
                        val v2 = evalBasicValue(functions, context, v.b!!, funName)
                        v.realValue = v1.div(v2)
                    }
                }
            }
            null -> {
                when (v.a) {
                    is JsonCollection.ArithTree -> {
                        evalArith(functions, context, v.a as JsonCollection.ArithTree, funName)
                        v.realValue = (v.a as JsonCollection.ArithTree).realValue
                    }
                    is JsonCollection.Arith -> v.realValue = v.a
                    else -> {}
                }
            }
        }
    }

    fun evalBasicValue(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        v: JsonCollection.Val,
        funName: String?
    ): JsonCollection.Arith {
        return when (v) {
            is JsonCollection.ArithTree -> {
                when (v.op) {
                    null -> evalBasicValue(functions, context, v.a, funName)
                    else -> {
                        evalArith(functions, context, v, funName)
                        v.realValue!! as JsonCollection.Arith
                    }
                }
            }
            is JsonCollection.Num -> v
            is JsonCollection.Str -> v
            is JsonCollection.Obj -> {
                mapRealValue(functions, context, v, funName)
                v
            }
            is JsonCollection.Var -> {
                mapRealValue(functions, context, v, funName)
                evalBasicValue(functions, context, v.realValue!!, funName)
            }
            is JsonCollection.Fun -> {
                val res = invokeFunction(
                    functions,
                    context,
                    InterceptFunction.FunctionInvokeInstruction(v.value.first, v.value.second),
                    funName
                ).lastOrNull()
                v.realValue = res
                evalBasicValue(functions, context, res ?: JsonCollection.Null, funName)
            }
            else -> throw RuntimeException("only support num and str arith expression")
        }
    }

    fun mapRealValue(
        functions: Map<String, InterceptFunction.FunctionInstruction>,
        context: ScriptContext,
        v: JsonCollection.Val,
        funName: String? = null
    ) {
        val variables = context.variables
        if (v is JsonCollection.Fun) {
            val f = v
            if (f.realValue != null) {
                return
            }
            val last = invokeFunction(
                functions,
                context,
                InterceptFunction.FunctionInvokeInstruction(f.value.first, f.value.second),
                funName
            ).lastOrNull()

            f.realValue = last
            return
        }

        if (v.vars().isNotEmpty()) {
            v.vars().forEach { k ->
                if (k.realValue == null) {
                    var vl = findVariable(funName, k, variables)
                    if (vl == null) {
                        throw RuntimeException("could not find variable: ${getInvokePath(variables) ?: ""}: ${k.value}")
                    }

                    when (vl) {
                        is JsonCollection.Fun -> {
                            if (vl.realValue != null) {
                                vl = vl.realValue
                            } else {
                                val res = invokeFunction(
                                    functions,
                                    context,
                                    InterceptFunction.FunctionInvokeInstruction(vl.value.first, vl.value.second),
                                    funName
                                ).last()
                                vl = res
                            }
                        }
                        is JsonCollection.ArithTree -> {
                            if (vl.realValue != null) {
                                vl = vl.realValue
                            }
                        }
                        is JsonCollection.Var -> {
                            mapRealValue(functions, context, vl, funName)
                            vl = vl.realValue
                        }
                        is JsonCollection.Arr -> {
                            vl.value.forEach { i ->
                                mapRealValue(functions, context, i, funName)
                            }
                        }
                        else -> {}
                    }

                    vl?.let { r ->
                        if (r.vars().isNotEmpty()) {
                            r.vars().forEach { t ->
                                mapRealValue(functions, context, t, funName)
                            }
                        }

                        if (r.funs().isNotEmpty()) {
                            r.funs().forEach { f ->
                                mapRealValue(functions, context, f, funName)
                            }
                        }
                    }
                    k.realValue = vl
                }
            }
        }
    }

    //plotSid_1$anonymousFun_dGifdRBcMk_0$anonymousFun_dGifdRBcMk_0$term_2$term_2$field
    //plotSid_1$anonymousFun_OcZrmzJNyl_0$anonymousFun_OcZrmzJNyl_0$term_2$field
    fun findVariable(funName: String?, k: JsonCollection.Var, variables: MutableMap<String, JsonCollection.Val>): JsonCollection.Val? {
        val ipt = getInvokePath(variables) ?: return variables[k.value]

        val ipts = ipt.split("\$")
        val option = (0..ipts.size).map { i ->
            val pts = ipts.slice(0 until ipts.size - i)
            variables["${pts.joinToString("$")}\$${k.value}"]
        }.find { it != null }

        if (option == null) {
            val scopeVal = variables[funName.let { "${it}\$" } + k.value]
            if (scopeVal != null) {
                return scopeVal
            }
            return variables[k.value]
        }
        return option
    }


    private fun dropOnePath(context: ScriptContext, funName: String?) {
        val invokePathStr = (context.variables["INVOKE_PATH"] as? JsonCollection.Str)?.value ?: return
        if (funName != null && !invokePathStr.endsWith("$$funName")) {
            return
        }
        val strs = invokePathStr.split("\$")
        val invokePath = strs.dropLast(1).joinToString("$")
        context.variables["INVOKE_PATH"] = JsonCollection.Str(invokePath)

        context.variables.keys.filter { it.startsWith(strs.joinToString("$")) }.forEach { key ->
            context.variables.remove(key)
        }
    }

    private fun setInvokePath(context: ScriptContext, funName: String?) {
        val invokePath = getInvokePath(context.variables)
        val path = listOfNotNull(invokePath, funName).filter { it.isNotBlank() }.joinToString("$")
        context.variables["INVOKE_PATH"] = JsonCollection.Str(path.split("\$").joinToString("$"))
    }

    private fun getInvokePath(variables: MutableMap<String, JsonCollection.Val>): String? {
        return (variables["INVOKE_PATH"] as? JsonCollection.Str)?.value
    }
} 