package com.github.chengpohi.edql.parser

import com.intellij.lang._
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.mock._
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.impl.ExtensionPointImpl
import com.intellij.openapi.extensions.{DefaultPluginDescriptor, PluginDescriptor, PluginId}
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerBase
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.{PsiDocumentManager, PsiManager}
import com.intellij.util.KeyedLazyInstance

class MockPsiFactoryBuilder(val myFileExt: String,
                            val myDefinition: ParserDefinition) {
  private val disposable: Disposable = new Disposable() {
    override def dispose(): Unit = {}
  }

  private val app: MockApplication = MockApplication.setUp(disposable)

  private val project: MockProjectEx = new MockProjectEx(disposable)

  private val myPsiManager = new MockPsiManager(project)

  private val myFileFactory: PsiFileFactoryImpl = new PsiFileFactoryImpl(myPsiManager);

  private val pluginDescriptor: PluginDescriptor = new DefaultPluginDescriptor(PluginId.getId(getClass.getName), classOf[EDQLParserFactory].getClassLoader)

  private val myLangParserDefinition: ExtensionPointImpl[KeyedLazyInstance[ParserDefinition]] = app.getExtensionArea.registerFakeBeanPoint(LanguageParserDefinitions.INSTANCE.getName, pluginDescriptor)

  private val myLanguage: Language = myDefinition.getFileNodeType.getLanguage

  {
    val appContainer = app.getPicoContainer
    val component = appContainer.getComponentAdapter(classOf[ProgressManager].getName)
    if (component == null) {
      appContainer.registerComponentInstance(classOf[ProgressManager].getName, new ProgressManagerImpl)
    }

    val editorFactory = new MockEditorFactory
    appContainer.registerComponentInstance(classOf[EditorFactory], editorFactory)
    app.registerService(classOf[FileDocumentManager], new MockFileDocumentManagerImpl(FileDocumentManagerBase.HARD_REF_TO_DOCUMENT_KEY, editorFactory.createDocument))

    app.registerService(classOf[PsiBuilderFactory], new PsiBuilderFactoryImpl)
    app.registerService(classOf[DefaultASTFactory], new DefaultASTFactoryImpl)
    project.registerService(classOf[PsiDocumentManager], new MockPsiDocumentManager)
    project.registerService(classOf[PsiManager], myPsiManager)

    app.registerService(classOf[FileTypeManager], new MockFileTypeManager(new MockLanguageFileType(myLanguage, myFileExt)))

    myLangParserDefinition.registerExtension(new KeyedLazyInstance[ParserDefinition]() {
      override def getKey: String = myDefinition.getFileNodeType.getLanguage.getID

      override def getInstance: ParserDefinition = myDefinition
    })

    LanguageParserDefinitions.INSTANCE.clearCache(myDefinition.getFileNodeType.getLanguage)

    System.getProperties.setProperty("psi.incremental.reparse.depth.limit", "1000")
  }
}

object MockPsiFactoryBuilder {
  def apply(myFileExt: String,myDefinition: ParserDefinition): PsiFileFactoryImpl = {
    new MockPsiFactoryBuilder(myFileExt, myDefinition).myFileFactory
  }
}
