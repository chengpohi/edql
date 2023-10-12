package com.github.chengpohi.esql.parser.psi;

import com.intellij.psi.PsiElement;

public interface ESQLNameElement extends PsiElement {
    String nameAsString();
    Boolean nameIsQuoted();
}
