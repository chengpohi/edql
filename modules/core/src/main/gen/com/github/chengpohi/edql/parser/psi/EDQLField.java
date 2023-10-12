// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EDQLField extends PsiElement {

  @NotNull
  EDQLExpr getExpr();

  @NotNull
  EDQLFieldname getFieldname();

  @NotNull
  EDQLH getH();

  @Nullable
  EDQLParams getParams();

}
