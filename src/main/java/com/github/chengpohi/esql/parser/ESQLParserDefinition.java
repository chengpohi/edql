package com.github.chengpohi.esql.parser;

import com.github.chengpohi.esql.parser.psi.ESQLLanguage;
import com.github.chengpohi.esql.parser.psi.ESQLFile;
import com.github.chengpohi.esql.parser.psi.ESQLPsiTypes;
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


public class ESQLParserDefinition implements ParserDefinition {
    public static TokenSet COMMENTS = TokenSet.create(ESQLPsiTypes.COMMENT, ESQLPsiTypes.LINE_COMMENT);
    public static IFileElementType ESQL_SQL_FILE_NODE_TYPE = new IFileElementType(ESQLLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new ESQLLexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new ESQLParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return ESQL_SQL_FILE_NODE_TYPE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return ESQLPsiTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new ESQLFile(viewProvider);
    }
}
