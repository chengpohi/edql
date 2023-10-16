package com.github.chengpohi.edql.parser.psi;

import com.github.chengpohi.edql.EDQLLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class EDQLElementType extends IElementType {
    public EDQLElementType(@NotNull @NonNls String debugName) {
        super(debugName, EDQLLanguage.INSTANCE);
    }
}
