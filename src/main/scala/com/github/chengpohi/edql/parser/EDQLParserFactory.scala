package com.github.chengpohi.edql.parser

import com.github.chengpohi.edql.EDQLLanguage
import com.intellij.lang._
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.testFramework.LightVirtualFile

import java.nio.charset.StandardCharsets

class EDQLParserFactory(val myFileExt: String,
                        val myDefinition: ParserDefinition,
                        val psiFileFactory: PsiFileFactoryImpl) {

  def createFile(name: String, text: String): PsiFile = {
    val virtualFile = new LightVirtualFile(name, EDQLLanguage.INSTANCE, text)
    virtualFile.setCharset(StandardCharsets.UTF_8)
    createFile(virtualFile)
  }

  def createFile(virtualFile: LightVirtualFile): PsiFile = {
    psiFileFactory.trySetupPsiForFile(virtualFile, EDQLLanguage.INSTANCE, true, false)
  }
}

object EDQLParserFactory {
  def apply(ext: String,
            parserDefinition: ParserDefinition,
            psiFileFactory: PsiFileFactoryImpl): EDQLParserFactory = {
    new EDQLParserFactory(ext, parserDefinition, psiFileFactory)
  }
}
