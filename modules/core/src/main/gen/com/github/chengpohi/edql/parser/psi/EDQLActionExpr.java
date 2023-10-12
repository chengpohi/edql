// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EDQLActionExpr extends PsiElement {

  @NotNull
  EDQLIdentifier0 getIdentifier0();

  @NotNull
  List<EDQLArr> getArrList();

  @NotNull
  List<EDQLArrcomp> getArrcompList();

  @NotNull
  EDQLMethod getMethod();

  @NotNull
  List<EDQLObj> getObjList();

  @Nullable
  PsiElement getQuery();

}
