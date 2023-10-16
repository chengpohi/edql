package com.github.chengpohi.edql.parser.psi.impl;

import com.github.chengpohi.edql.parser.psi.EDQLIdentifier0;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface EDQLHeaderInterface extends PsiElement {
    @NotNull
    EDQLIdentifier0 getIdentifier0();
}
