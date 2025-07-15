package com.github.chengpohi.edql.parser

import com.github.chengpohi.context.HostInfo
import com.github.chengpohi.script.EDQLRunContext
import com.github.chengpohi.script.EDQLScriptRunner
import com.intellij.psi.impl.PsiFileFactoryImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI

class EDQLPsiInterceptorTest {
    private val definition = EDQLParserDefinition()
    private val edql = "edql"
    private val fileFactoryImpl: PsiFileFactoryImpl = MockPsiFactoryBuilder.apply("edql", definition)
    private val factory: EDQLParserFactory = EDQLParserFactory.apply(edql, definition, fileFactoryImpl)
    private val parser = EDQLPsiInterceptor(factory)
    private val runner = EDQLScriptRunner(emptyList(), fileFactoryImpl)

    @Test
    fun `should parse edql`() {
        val text = readResource("test.edql")

        val res = parser.parse(text).getOrThrow()

        res.forEach { println(it.toString()) }
        assertEquals(35, res.size)
    }

    @Test
    fun `should parse json`() {
        val text = readResource("test.json")

        val res = parser.parseJson(text)
        assertTrue(res.isSuccess)
        println(res)
    }

    @Test
    fun `should parse illegal json`() {
        val text = readResource("illegal.json")

        val res = parser.parseJson(text)
        println(res)
        assertTrue(res.isFailure)
    }
    @Test
    fun `should run script`() {
        val res = runner.run("""
            print(1 + 1)
        """.trimIndent(), EDQLRunContext(hostInfo = HostInfo("test-mock", URI("localhost")))
        )
        println(res.response)
        assertTrue(res.isSuccess)
    }

    private fun readResource(resourceName: String): String {
        val inputStream = javaClass.classLoader.getResourceAsStream(resourceName)
            ?: throw IllegalArgumentException("Resource not found: $resourceName")
        
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.lines().toArray().joinToString(System.lineSeparator())
        }
    }
} 