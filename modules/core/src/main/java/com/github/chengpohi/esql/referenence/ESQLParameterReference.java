package com.github.chengpohi.esql.referenence;

import com.github.chengpohi.esql.parser.psi.ESQLBindParameter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESQLParameterReference extends PsiReferenceBase<ESQLBindParameter> {
    public ESQLParameterReference(@NotNull ESQLBindParameter element) {
        super(element);
    }

    @Override
    public @Nullable PsiElement resolve() {
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return super.getVariants();
    }
}
