// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.chengpohi.edql.parser.psi.EDQLTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.chengpohi.edql.parser.psi.*;

public class EDQLExprImpl extends ASTWrapperPsiElement implements EDQLExpr {

  public EDQLExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EDQLVisitor visitor) {
    visitor.visitExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EDQLVisitor) accept((EDQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EDQLBool getBool() {
    return findChildByClass(EDQLBool.class);
  }

  @Override
  @Nullable
  public EDQLIdentifier0 getIdentifier0() {
    return findChildByClass(EDQLIdentifier0.class);
  }

  @Override
  @Nullable
  public EDQLNil getNil() {
    return findChildByClass(EDQLNil.class);
  }

  @Override
  @Nullable
  public EDQLActionExpr getActionExpr() {
    return findChildByClass(EDQLActionExpr.class);
  }

  @Override
  @Nullable
  public EDQLArr getArr() {
    return findChildByClass(EDQLArr.class);
  }

  @Override
  @Nullable
  public EDQLArrcomp getArrcomp() {
    return findChildByClass(EDQLArrcomp.class);
  }

  @Override
  @Nullable
  public EDQLAssertStmt getAssertStmt() {
    return findChildByClass(EDQLAssertStmt.class);
  }

  @Override
  @Nullable
  public EDQLBinsuffix getBinsuffix() {
    return findChildByClass(EDQLBinsuffix.class);
  }

  @Override
  @Nullable
  public EDQLComment getComment() {
    return findChildByClass(EDQLComment.class);
  }

  @Override
  @Nullable
  public EDQLExpr getExpr() {
    return findChildByClass(EDQLExpr.class);
  }

  @Override
  @Nullable
  public EDQLForExpr getForExpr() {
    return findChildByClass(EDQLForExpr.class);
  }

  @Override
  @Nullable
  public EDQLFunctionExpr getFunctionExpr() {
    return findChildByClass(EDQLFunctionExpr.class);
  }

  @Override
  @Nullable
  public EDQLFunctionInvokeExpr getFunctionInvokeExpr() {
    return findChildByClass(EDQLFunctionInvokeExpr.class);
  }

  @Override
  @Nullable
  public EDQLIfelse getIfelse() {
    return findChildByClass(EDQLIfelse.class);
  }

  @Override
  @Nullable
  public EDQLMapIter getMapIter() {
    return findChildByClass(EDQLMapIter.class);
  }

  @Override
  @Nullable
  public EDQLObj getObj() {
    return findChildByClass(EDQLObj.class);
  }

  @Override
  @Nullable
  public EDQLOutervar getOutervar() {
    return findChildByClass(EDQLOutervar.class);
  }

  @Override
  @Nullable
  public EDQLUnaryop getUnaryop() {
    return findChildByClass(EDQLUnaryop.class);
  }

  @Override
  @Nullable
  public PsiElement getDoubleQuotedString() {
    return findChildByType(DOUBLE_QUOTED_STRING);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getSingleQuotedString() {
    return findChildByType(SINGLE_QUOTED_STRING);
  }

  @Override
  @Nullable
  public PsiElement getTripleQuotedString() {
    return findChildByType(TRIPLE_QUOTED_STRING);
  }

}
