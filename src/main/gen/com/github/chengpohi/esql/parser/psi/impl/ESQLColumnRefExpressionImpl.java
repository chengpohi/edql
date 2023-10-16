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

public class ESQLColumnRefExpressionImpl extends ESQLExpressionImpl implements ESQLColumnRefExpression {

  public ESQLColumnRefExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ESQLVisitor visitor) {
    visitor.visitColumnRefExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ESQLVisitor) accept((ESQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ESQLColumnName getColumnName() {
    return findNotNullChildByClass(ESQLColumnName.class);
  }

  @Override
  @Nullable
  public ESQLDatabaseName getDatabaseName() {
    return findChildByClass(ESQLDatabaseName.class);
  }

  @Override
  @Nullable
  public ESQLSelectedTableName getSelectedTableName() {
    return findChildByClass(ESQLSelectedTableName.class);
  }

}
