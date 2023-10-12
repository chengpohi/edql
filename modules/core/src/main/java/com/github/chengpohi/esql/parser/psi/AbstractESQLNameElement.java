package com.github.chengpohi.esql.parser.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractESQLNameElement extends ASTWrapperPsiElement implements ESQLNameElement {
    public AbstractESQLNameElement(@NotNull ASTNode node) {
        super(node);
    }

    public String nameAsString() {
        ASTNode leaf = getNode().getFirstChildNode();
        IElementType elementType = leaf.getElementType();
        if (elementType.equals(ESQLPsiTypes.BRACKET_LITERAL)) {
            return leaf.getText().substring(1, leaf.getTextLength() - 1);
        }

        if (elementType.equals(ESQLPsiTypes.BACKTICK_LITERAL)) {
            return leaf.getText().substring(1, leaf.getTextLength() - 1).replace("``", "`");
        }

        if (elementType.equals(ESQLPsiTypes.SINGLE_QUOTE_STRING_LITERAL)) {
            return leaf.getText().substring(1, leaf.getTextLength() - 1).replace("''", "'");
        }

        if (elementType.equals(ESQLPsiTypes.DOUBLE_QUOTE_STRING_LITERAL)) {
            return leaf.getText().substring(1, leaf.getTextLength() - 1).replace("\"\"", "\"");
        }

        return leaf.getText();
    }

    public Boolean nameIsQuoted() {
        return getNode().getFirstChildNode().getElementType() != ESQLPsiTypes.IDENTIFIER;
    }
}
