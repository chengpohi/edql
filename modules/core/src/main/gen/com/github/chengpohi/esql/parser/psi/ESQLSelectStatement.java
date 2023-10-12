// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ESQLSelectStatement extends PsiElement {

  @NotNull
  List<ESQLCompoundOperator> getCompoundOperatorList();

  @Nullable
  ESQLLimitClause getLimitClause();

  @Nullable
  ESQLOrderClause getOrderClause();

  @NotNull
  List<ESQLSelectCore> getSelectCoreList();

}
