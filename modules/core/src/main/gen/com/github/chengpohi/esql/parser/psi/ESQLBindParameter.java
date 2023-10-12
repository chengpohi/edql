// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface ESQLBindParameter extends PsiElement {

  @Nullable
  PsiElement getNamedParameter();

  String getParameterNameAsString();

  PsiReference getReference();

  Boolean isColonNamedParameter();

}
