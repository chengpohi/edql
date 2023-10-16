package com.github.chengpohi.esql.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ESQLTokenType extends IElementType {
    public ESQLTokenType(@NonNls @NotNull String debugName) {
        super(debugName, ESQLLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        String token = super.toString();
        if (token.equalsIgnoreCase(",")) {
            return "comma";
        }
        if (token.equalsIgnoreCase(";")) {
            return "semicolon";
        }
        if (token.equalsIgnoreCase("'")) {
            return "single quote";
        }
        if (token.equalsIgnoreCase("\"")) {
            return "double quote";
        }
        return token;
    }
}
