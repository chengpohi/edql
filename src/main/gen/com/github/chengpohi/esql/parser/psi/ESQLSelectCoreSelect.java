// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ESQLSelectCoreSelect extends PsiElement {

  @Nullable
  ESQLFromClause getFromClause();

  @Nullable
  ESQLGroupByClause getGroupByClause();

  @NotNull
  ESQLSelectFields getSelectFields();

  @Nullable
  ESQLWhereClause getWhereClause();

}
