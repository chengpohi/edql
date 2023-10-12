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

public class ESQLExistsExpressionImpl extends ESQLExpressionImpl implements ESQLExistsExpression {

  public ESQLExistsExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitExistsExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
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
