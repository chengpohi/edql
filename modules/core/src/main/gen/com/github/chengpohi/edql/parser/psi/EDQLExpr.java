// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EDQLExpr extends PsiElement {

  @Nullable
  EDQLBool getBool();

  @Nullable
  EDQLIdentifier0 getIdentifier0();

  @Nullable
  EDQLActionExpr getActionExpr();

  @Nullable
  EDQLArr getArr();

  @Nullable
  EDQLArrcomp getArrcomp();

  @Nullable
  EDQLAssertStmt getAssertStmt();

  @Nullable
  EDQLBinsuffix getBinsuffix();

  @Nullable
  EDQLComment getComment();

  @Nullable
  EDQLExpr getExpr();

  @Nullable
  EDQLForExpr getForExpr();

  @Nullable
  EDQLFunctionExpr getFunctionExpr();

  @Nullable
  EDQLFunctionInvokeExpr getFunctionInvokeExpr();

  @Nullable
  EDQLIfelse getIfelse();

  @Nullable
  EDQLObj getObj();

  @Nullable
  EDQLOutervar getOutervar();

  @Nullable
  EDQLUnaryop getUnaryop();

  @Nullable
  PsiElement getDoubleQuotedString();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getSingleQuotedString();

  @Nullable
  PsiElement getTripleQuotedString();

}
