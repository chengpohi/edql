// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.github.chengpohi.edql.parser.psi.impl.EDQLHeaderInterface;

public class EDQLVisitor extends PsiElementVisitor {

  public void visitBool(@NotNull EDQLBool o) {
    visitPsiElement(o);
  }

  public void visitIdentifier0(@NotNull EDQLIdentifier0 o) {
    visitNamedElement(o);
    // visitPsiNamedElement(o);
  }

  public void visitActionExpr(@NotNull EDQLActionExpr o) {
    visitPsiElement(o);
  }

  public void visitArg(@NotNull EDQLArg o) {
    visitPsiElement(o);
  }

  public void visitArgs(@NotNull EDQLArgs o) {
    visitPsiElement(o);
  }

  public void visitArr(@NotNull EDQLArr o) {
    visitPsiElement(o);
  }

  public void visitArrcomp(@NotNull EDQLArrcomp o) {
    visitPsiElement(o);
  }

  public void visitAssertStmt(@NotNull EDQLAssertStmt o) {
    visitPsiElement(o);
  }

  public void visitAuthExpr(@NotNull EDQLAuthExpr o) {
    visitHeaderInterface(o);
  }

  public void visitBinaryop(@NotNull EDQLBinaryop o) {
    visitPsiElement(o);
  }

  public void visitBind(@NotNull EDQLBind o) {
    visitPsiElement(o);
  }

  public void visitBinsuffix(@NotNull EDQLBinsuffix o) {
    visitPsiElement(o);
  }

  public void visitComment(@NotNull EDQLComment o) {
    visitPsiElement(o);
  }

  public void visitCompspec(@NotNull EDQLCompspec o) {
    visitPsiElement(o);
  }

  public void visitExpr(@NotNull EDQLExpr o) {
    visitPsiElement(o);
  }

  public void visitField(@NotNull EDQLField o) {
    visitPsiElement(o);
  }

  public void visitFieldname(@NotNull EDQLFieldname o) {
    visitPsiElement(o);
  }

  public void visitForExpr(@NotNull EDQLForExpr o) {
    visitPsiElement(o);
  }

  public void visitForeach(@NotNull EDQLForeach o) {
    visitPsiElement(o);
  }

  public void visitFunctionBody(@NotNull EDQLFunctionBody o) {
    visitPsiElement(o);
  }

  public void visitFunctionExpr(@NotNull EDQLFunctionExpr o) {
    visitPsiElement(o);
  }

  public void visitFunctionInvokeExpr(@NotNull EDQLFunctionInvokeExpr o) {
    visitPsiElement(o);
  }

  public void visitH(@NotNull EDQLH o) {
    visitPsiElement(o);
  }

  public void visitHeadExpr(@NotNull EDQLHeadExpr o) {
    visitPsiElement(o);
  }

  public void visitHostExpr(@NotNull EDQLHostExpr o) {
    visitPsiElement(o);
  }

  public void visitIfelse(@NotNull EDQLIfelse o) {
    visitPsiElement(o);
  }

  public void visitIfspec(@NotNull EDQLIfspec o) {
    visitPsiElement(o);
  }

  public void visitImportop(@NotNull EDQLImportop o) {
    visitPsiElement(o);
  }

  public void visitLoadop(@NotNull EDQLLoadop o) {
    visitPsiElement(o);
  }

  public void visitMapIter(@NotNull EDQLMapIter o) {
    visitPsiElement(o);
  }

  public void visitMember(@NotNull EDQLMember o) {
    visitPsiElement(o);
  }

  public void visitMembers(@NotNull EDQLMembers o) {
    visitPsiElement(o);
  }

  public void visitMethod(@NotNull EDQLMethod o) {
    visitPsiElement(o);
  }

  public void visitObj(@NotNull EDQLObj o) {
    visitPsiElement(o);
  }

  public void visitObjvar(@NotNull EDQLObjvar o) {
    visitPsiElement(o);
  }

  public void visitOutervar(@NotNull EDQLOutervar o) {
    visitPsiElement(o);
  }

  public void visitParam(@NotNull EDQLParam o) {
    visitPsiElement(o);
  }

  public void visitParams(@NotNull EDQLParams o) {
    visitPsiElement(o);
  }

  public void visitPath(@NotNull EDQLPath o) {
    visitPsiElement(o);
  }

  public void visitReturnExpr(@NotNull EDQLReturnExpr o) {
    visitPsiElement(o);
  }

  public void visitTimeoutExpr(@NotNull EDQLTimeoutExpr o) {
    visitPsiElement(o);
  }

  public void visitUnaryop(@NotNull EDQLUnaryop o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull EDQLNamedElement o) {
    visitPsiElement(o);
  }

  public void visitHeaderInterface(@NotNull EDQLHeaderInterface o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
