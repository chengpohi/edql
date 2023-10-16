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

public class EDQLHeadExprImpl extends ASTWrapperPsiElement implements EDQLHeadExpr {

  public EDQLHeadExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EDQLVisitor visitor) {
    visitor.visitHeadExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EDQLVisitor) accept((EDQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EDQLAuthExpr getAuthExpr() {
    return findChildByClass(EDQLAuthExpr.class);
  }

  @Override
  @Nullable
  public EDQLHostExpr getHostExpr() {
    return findChildByClass(EDQLHostExpr.class);
  }

  @Override
  @Nullable
  public EDQLTimeoutExpr getTimeoutExpr() {
    return findChildByClass(EDQLTimeoutExpr.class);
  }

}
