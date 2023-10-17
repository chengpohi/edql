package com.github.chengpohi.edql.parser.psi.impl;

import com.github.chengpohi.edql.parser.psi.EDQLIdentifier0;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class EDQLIdentifierImpl extends ASTWrapperPsiElement implements EDQLIdentifier0 {
    public EDQLIdentifierImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference[] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }
}
