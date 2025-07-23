package com.github.chengpohi.edql.parser

import com.github.chengpohi.edql.EDQLLanguage
import com.intellij.lang.ParserDefinition
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.testFramework.LightVirtualFile
import java.nio.charset.StandardCharsets

class EDQLParserFactory(
    val myFileExt: String,
    val myDefinition: ParserDefinition,
    val psiFileFactory: PsiFileFactoryImpl
) {

    fun createFile(name: String, text: String): PsiFile {
        val virtualFile = LightVirtualFile(name, EDQLLanguage.INSTANCE, text)
        virtualFile.charset = StandardCharsets.UTF_8
        return psiFileFactory.trySetupPsiForFile(virtualFile, EDQLLanguage.INSTANCE, false, false)!!
    }

    companion object {
        fun apply(
            ext: String,
            parserDefinition: ParserDefinition,
            psiFileFactory: PsiFileFactoryImpl
        ): EDQLParserFactory {
            return EDQLParserFactory(ext, parserDefinition, psiFileFactory)
        }
    }
} 