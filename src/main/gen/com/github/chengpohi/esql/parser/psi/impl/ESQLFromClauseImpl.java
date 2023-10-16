// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.chengpohi.esql.parser.psi.ESQLPsiTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.chengpohi.esql.parser.psi.*;

public class ESQLFromClauseImpl extends ASTWrapperPsiElement implements ESQLFromClause {

  public ESQLFromClauseImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitFromClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ESQLJoinConstraint> getJoinConstraintList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLJoinConstraint.class);
  }

  @Override
  @NotNull
  public List<ESQLJoinOperator> getJoinOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLJoinOperator.class);
  }

  @Override
  @NotNull
  public List<ESQLTableOrSubquery> getTableOrSubqueryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLTableOrSubquery.class);
  }

}
