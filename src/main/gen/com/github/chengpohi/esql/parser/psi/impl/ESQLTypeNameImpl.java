// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.github.chengpohi.esql.parser.psi.AbstractESQLNameElement;
import com.github.chengpohi.esql.parser.psi.*;

public class ESQLTypeNameImpl extends AbstractESQLNameElement implements ESQLTypeName {

  public ESQLTypeNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitTypeName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ESQLSignedNumber> getSignedNumberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLSignedNumber.class);
  }

}
