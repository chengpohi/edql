// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi.impl;

import com.github.chengpohi.esql.parser.psi.PsiImplUtil;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.github.chengpohi.esql.parser.psi.AbstractESQLNameElement;
import com.github.chengpohi.esql.parser.psi.*;

public class ESQLTableDefinitionNameImpl extends AbstractESQLNameElement implements ESQLTableDefinitionName {

  public ESQLTableDefinitionNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitTableDefinitionName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public String getName() {
    return PsiImplUtil.getName(this);
  }

  @Override
  public ESQLTableDefinitionName setName(String newName) {
    return PsiImplUtil.setName(this, newName);
  }

}
