package com.github.chengpohi.edql.parser.psi.impl;

import com.github.chengpohi.edql.parser.psi.EDQLIdentifier0;
import com.github.chengpohi.edql.parser.psi.EDQLIdentifierNameReference;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

public abstract class EDQLNamedElementImpl extends ASTWrapperPsiElement implements EDQLIdentifier0 {
    public EDQLNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new EDQLIdentifierNameReference(this);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        final PsiReference[] fromProviders =
                ReferenceProvidersRegistry.getReferencesFromProviders(this);
        return ArrayUtil.prepend(new EDQLIdentifierNameReference(this), fromProviders);
    }
}
