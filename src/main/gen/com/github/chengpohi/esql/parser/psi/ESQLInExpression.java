// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ESQLInExpression extends ESQLExpression {

  @Nullable
  ESQLDatabaseName getDatabaseName();

  @Nullable
  ESQLDefinedTableName getDefinedTableName();

  @NotNull
  List<ESQLExpression> getExpressionList();

  @Nullable
  ESQLSelectStatement getSelectStatement();

  @Nullable
  ESQLWithClauseSelectStatement getWithClauseSelectStatement();

}
