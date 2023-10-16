// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.github.chengpohi.esql.parser.psi.AbstractESQLNameElement;
import com.github.chengpohi.esql.parser.psi.*;

public class ESQLDatabaseNameImpl extends AbstractESQLNameElement implements ESQLDatabaseName {

  public ESQLDatabaseNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitDatabaseName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

}
