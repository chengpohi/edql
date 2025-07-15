package com.github.chengpohi.script

import com.github.chengpohi.edql.parser.InterceptFunction
import com.github.chengpohi.edql.parser.json.JsonCollection
import com.github.chengpohi.edql.parser.EDQLParserDefinition
import com.github.chengpohi.edql.parser.EDQLParserFactory
import com.github.chengpohi.edql.parser.EDQLPsiInterceptor
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiFileFactoryImpl

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.stream.Collectors

class EDQLScriptRunner(ls: List<URL>, psiFileFactoryImpl: PsiFileFactoryImpl) {
    val factory: EDQLParserFactory = EDQLParserFactory("edql", EDQLParserDefinition(), psiFileFactoryImpl)
    val parser: EDQLPsiInterceptor = EDQLPsiInterceptor(factory)
    val instructionInvoker: InstructionInvoker = InstructionInvoker(ls, parser)

    fun readFile(file: File): Result<String> = runCatching {
        Files.readAllLines(file.toPath()).stream().collect(Collectors.joining(System.lineSeparator()))
    }

    fun parseJson(text: String): Result<JsonCollection.Val> {
        return parser.parseJson(text)
    }

    fun parseExpr(text: String): Result<PsiFile> {
        return parser.parseExpr(text)
    }

    fun extractVars(text: String): Map<String, JsonCollection.Val> {
        val instructions = parser.parse(text)
        return instructions.map { ins ->
            val cIns = ins.filterIsInstance<InterceptFunction.ScriptContextInstruction2>()
            cIns.filterIsInstance<InterceptFunction.VariableInstruction>()
                .associate { i -> i.variableName to i.value }
        }.getOrElse { emptyMap() }
    }

    fun run(script: String, runContext: EDQLRunContext): EDQLRunResult {
        val instructions = parser.parse(script)
        val selectedInstruction = runContext.targetInstruction?.let { parser.parse(it) }

        if (selectedInstruction?.isFailure == true) {
            return EDQLRunResult(Result.failure(selectedInstruction.exceptionOrNull()!!))
        }

        return when {
            instructions.isSuccess -> {
                val ins = instructions.getOrThrow()
                val invokeIns = ins.filterNot { it is InterceptFunction.ScriptContextInstruction2 }
                val scriptContextIns = ins.filterIsInstance<InterceptFunction.ScriptContextInstruction2>()

                when (selectedInstruction) {
                    null -> instructionInvoker.invokeInstruction(
                        invokeIns,
                        scriptContextIns as List<InterceptFunction.Instruction2>,
                        runContext
                    )
                    else -> instructionInvoker.invokeInstruction(
                        selectedInstruction.getOrThrow(),
                        scriptContextIns as List<InterceptFunction.Instruction2>,
                        runContext
                    )
                }
            }
            else -> EDQLRunResult(Result.failure(instructions.exceptionOrNull()!!))
        }
    }

    fun close() {
        ScriptContext.cache.values.forEach { (_, context) ->
            context.client.restClient.close()
        }
    }
} 