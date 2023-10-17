// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EDQLForeach extends PsiElement {

  @NotNull
  EDQLIdentifier0 getIdentifier0();

  @NotNull
  List<EDQLExpr> getExprList();

  @Nullable
  EDQLReturnExpr getReturnExpr();

}
