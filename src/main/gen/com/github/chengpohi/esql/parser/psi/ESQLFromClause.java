// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ESQLFromClause extends PsiElement {

  @NotNull
  List<ESQLJoinConstraint> getJoinConstraintList();

  @NotNull
  List<ESQLJoinOperator> getJoinOperatorList();

  @NotNull
  List<ESQLTableOrSubquery> getTableOrSubqueryList();

}
