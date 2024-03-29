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

public class EDQLActionExprImpl extends ASTWrapperPsiElement implements EDQLActionExpr {

  public EDQLActionExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EDQLVisitor visitor) {
    visitor.visitActionExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EDQLVisitor) accept((EDQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EDQLArr> getArrList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EDQLArr.class);
  }

  @Override
  @NotNull
  public List<EDQLArrcomp> getArrcompList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EDQLArrcomp.class);
  }

  @Override
  @NotNull
  public EDQLMethod getMethod() {
    return findNotNullChildByClass(EDQLMethod.class);
  }

  @Override
  @NotNull
  public List<EDQLObj> getObjList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EDQLObj.class);
  }

  @Override
  @NotNull
  public EDQLPath getPath() {
    return findNotNullChildByClass(EDQLPath.class);
  }

  @Override
  @Nullable
  public PsiElement getQuery() {
    return findChildByType(QUERY);
  }

}
