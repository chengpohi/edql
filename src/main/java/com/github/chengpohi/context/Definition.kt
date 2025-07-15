package com.github.chengpohi.context

import com.github.chengpohi.script.ScriptContext
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

interface Definition<A> {
    suspend fun execute(context: ScriptContext): A
    
    fun json(context: ScriptContext): String
}

data class ErrorHealthRequestDefinition(val error: String) : Definition<String> {
    override suspend fun execute(context: ScriptContext): String {
        throw RuntimeException(error)
    }
    
    override fun json(context: ScriptContext): String = error
}

data class PureStringDefinition(val s: String) : Definition<String> {
    override suspend fun execute(context: ScriptContext): String = s
    
    override fun json(context: ScriptContext): String = s
} 