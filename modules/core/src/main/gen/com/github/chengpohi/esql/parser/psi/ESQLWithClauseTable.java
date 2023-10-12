// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ESQLWithClauseTable extends PsiElement {

  @Nullable
  ESQLSelectStatement getSelectStatement();

  @Nullable
  ESQLWithClauseSelectStatement getWithClauseSelectStatement();

  @NotNull
  ESQLWithClauseTableDef getWithClauseTableDef();

  //WARNING: getTableDefinition(...) is skipped
  //matching getTableDefinition(ESQLWithClauseTable, ...)
  //methods are not found in PsiImplUtil

}
