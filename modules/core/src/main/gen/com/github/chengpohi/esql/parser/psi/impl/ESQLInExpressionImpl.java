// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.chengpohi.esql.parser.psi.ESQLPsiTypes.*;
import com.github.chengpohi.esql.parser.psi.*;

public class ESQLInExpressionImpl extends ESQLExpressionImpl implements ESQLInExpression {

  public ESQLInExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitInExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ESQLDatabaseName getDatabaseName() {
    return findChildByClass(ESQLDatabaseName.class);
  }

  @Override
  @Nullable
  public ESQLDefinedTableName getDefinedTableName() {
    return findChildByClass(ESQLDefinedTableName.class);
  }

  @Override
  @NotNull
  public List<ESQLExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ESQLExpression.class);
  }

  @Override
  @Nullable
  public ESQLSelectStatement getSelectStatement() {
    return findChildByClass(ESQLSelectStatement.class);
  }

  @Override
  @Nullable
  public ESQLWithClauseSelectStatement getWithClauseSelectStatement() {
    return findChildByClass(ESQLWithClauseSelectStatement.class);
  }

}
