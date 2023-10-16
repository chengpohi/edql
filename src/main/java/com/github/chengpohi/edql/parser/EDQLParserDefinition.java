package com.github.chengpohi.edql.parser;

import com.github.chengpohi.edql.EDQLLanguage;
import com.github.chengpohi.edql.parser.psi.EDQLFile;
import com.github.chengpohi.edql.parser.psi.EDQLTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import static com.github.chengpohi.edql.parser.psi.EDQLTypes.DOUBLE_QUOTED_STRING;
import static com.github.chengpohi.edql.parser.psi.EDQLTypes.SINGLE_QUOTED_STRING;


public class EDQLParserDefinition implements ParserDefinition {
    public static final TokenSet COMMENTS = TokenSet.create(EDQLTypes.LINE_COMMENT);

    public static final IFileElementType FILE = new IFileElementType(EDQLLanguage.INSTANCE);

    public static final TokenSet STRING_LITERALS = TokenSet.create(EDQLTypes.SINGLE_QUOTED_STRING, EDQLTypes.DOUBLE_QUOTED_STRING);

    public static final TokenSet JSON_CONTAINERS = TokenSet.create(EDQLTypes.OBJ);
    public static final TokenSet FUNCTION = TokenSet.create(EDQLTypes.FUNCTION_BODY);
    public static final TokenSet FUNCTION_IDENT = TokenSet.create(EDQLTypes.FUNCTION_BODY, EDQLTypes.FUNCTION_EXPR);


    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new EDQLLexerAdapter();
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new EDQLParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new EDQLFile(viewProvider);
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return EDQLTypes.Factory.createElement(node);
    }
}
