// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.chengpohi.edql.parser.psi.EDQLTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.chengpohi.edql.parser.psi.*;

public class EDQLReturnExprImpl extends ASTWrapperPsiElement implements EDQLReturnExpr {

  public EDQLReturnExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EDQLVisitor visitor) {
    visitor.visitReturnExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EDQLVisitor) accept((EDQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EDQLBinsuffix> getBinsuffixList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EDQLBinsuffix.class);
  }

  @Override
  @NotNull
  public EDQLExpr getExpr() {
    return findNotNullChildByClass(EDQLExpr.class);
  }

  @Override
  @Nullable
  public EDQLMapIter getMapIter() {
    return findChildByClass(EDQLMapIter.class);
  }

}
