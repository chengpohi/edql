package com.github.chengpohi.esql.refactor;

import com.github.chengpohi.esql.parser.ESQLLexer;
import com.github.chengpohi.esql.parser.psi.ESQLNameElement;
import com.github.chengpohi.esql.parser.psi.ESQLPsiTypes;
import com.intellij.lang.ASTFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESQLNameElementManipulator extends AbstractElementManipulator<ESQLNameElement> {
    @Override
    public @Nullable ESQLNameElement handleContentChange(@NotNull ESQLNameElement element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        LeafElement e = leaf(newContent);
        element.getNode().replaceChild(element.getNode().getFirstChildNode(), e);
        return element;
    }

    @NotNull
    private LeafElement leaf(String newContent) {
        if (ESQLLexer.needsQuoting(newContent)) {
            return newLeaf(ESQLPsiTypes.BACKTICK_LITERAL, ESQLLexer.getValidName(newContent));
        } else {
            return newLeaf(ESQLPsiTypes.IDENTIFIER, newContent);
        }
    }

    private LeafElement newLeaf(IElementType type, String text) {
        LeafElement leaf = ASTFactory.leaf(type, text);
        CodeEditUtil.setNodeGenerated(leaf, true);
        return leaf;
    }
}
