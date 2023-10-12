// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ESQLLiteralExpression extends ESQLExpression {

  @Nullable
  ESQLBindParameter getBindParameter();

  @Nullable
  PsiElement getDoubleQuoteStringLiteral();

  @Nullable
  PsiElement getNumericLiteral();

  @Nullable
  PsiElement getSingleQuoteStringLiteral();

}
