package com.github.chengpohi.esql.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ESQLAstNodeType extends IElementType {
    public ESQLAstNodeType(@NonNls @NotNull String debugName) {
        super(debugName, ESQLLanguage.INSTANCE);
    }
}
