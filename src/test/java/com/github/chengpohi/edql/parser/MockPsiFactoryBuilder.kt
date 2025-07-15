package com.github.chengpohi.edql.parser

import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.mock.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.impl.ExtensionPointImpl
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerBase
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.util.KeyedLazyInstance

class MockPsiFactoryBuilder(
    private val myFileExt: String,
    private val myDefinition: ParserDefinition
) {
    private val disposable: Disposable = object : Disposable {
        override fun dispose() {}
    }

    private val app: MockApplication = MockApplication.setUp(disposable)

    private val project: MockProjectEx = MockProjectEx(disposable)

    private val myPsiManager = MockPsiManager(project)

    private val myFileFactory: PsiFileFactoryImpl = PsiFileFactoryImpl(myPsiManager)

    private val pluginDescriptor: PluginDescriptor = DefaultPluginDescriptor(
        PluginId.getId(javaClass.name),
        EDQLParserFactory::class.java.classLoader
    )

    private val myLangParserDefinition: ExtensionPointImpl<KeyedLazyInstance<ParserDefinition>> =
        app.extensionArea.registerFakeBeanPoint(
            LanguageParserDefinitions.INSTANCE.name,
            pluginDescriptor
        )

    private val myLanguage: Language = myDefinition.fileNodeType.language

    init {
        val appContainer = app.picoContainer
        val component = appContainer.getComponentAdapter(ProgressManager::class.java.name)
        if (component == null) {
            appContainer.registerComponentInstance(ProgressManager::class.java.name, ProgressManagerImpl())
        }

        val editorFactory = MockEditorFactory()
        appContainer.registerComponentInstance(EditorFactory::class.java, editorFactory)
        app.registerService(
            FileDocumentManager::class.java,
            MockFileDocumentManagerImpl(
                FileDocumentManagerBase.HARD_REF_TO_DOCUMENT_KEY,
                { str: CharSequence -> editorFactory.createDocument(str) }
            )
        )

        app.registerService(PsiBuilderFactory::class.java, PsiBuilderFactoryImpl())
        app.registerService(DefaultASTFactory::class.java, DefaultASTFactoryImpl())
        project.registerService(PsiDocumentManager::class.java, MockPsiDocumentManager())
        project.registerService(PsiManager::class.java, myPsiManager)

        app.registerService(
            FileTypeManager::class.java,
            MockFileTypeManager(MockLanguageFileType(myLanguage, myFileExt))
        )

        myLangParserDefinition.registerExtension(object : KeyedLazyInstance<ParserDefinition> {
            override fun getKey(): String = myDefinition.fileNodeType.language.id

            override fun getInstance(): ParserDefinition = myDefinition
        })

        LanguageParserDefinitions.INSTANCE.clearCache(myDefinition.fileNodeType.language)

        System.setProperty("psi.incremental.reparse.depth.limit", "1000")
    }

    companion object {
        fun apply(myFileExt: String, myDefinition: ParserDefinition): PsiFileFactoryImpl {
            return MockPsiFactoryBuilder(myFileExt, myDefinition).myFileFactory
        }
    }
} 