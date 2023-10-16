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

public class ESQLSelectStatementImpl extends ASTWrapperPsiElement implements ESQLSelectStatement {

  public ESQLSelectStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitSelectStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ESQLCompoundOperator> getCompoundOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLCompoundOperator.class);
  }

  @Override
  @Nullable
  public ESQLLimitClause getLimitClause() {
    return findChildByClass(ESQLLimitClause.class);
  }

  @Override
  @Nullable
  public ESQLOrderClause getOrderClause() {
    return findChildByClass(ESQLOrderClause.class);
  }

  @Override
  @NotNull
  public List<ESQLSelectCore> getSelectCoreList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLSelectCore.class);
  }

}
