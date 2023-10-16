// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.github.chengpohi.edql.parser.psi.EDQLTypes.*;
import com.github.chengpohi.edql.parser.psi.*;
import com.intellij.navigation.ItemPresentation;

public class EDQLIdentifier0Impl extends EDQLNamedElementImpl implements EDQLIdentifier0 {

  public EDQLIdentifier0Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EDQLVisitor visitor) {
    visitor.visitIdentifier0(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EDQLVisitor) accept((EDQLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  public String getKey() {
    return EDQLPsiImplUtils.getKey(this);
  }

  @Override
  public String getValue() {
    return EDQLPsiImplUtils.getValue(this);
  }

  @Override
  public String getName() {
    return EDQLPsiImplUtils.getName(this);
  }

  @Override
  public PsiElement setName(String newName) {
    return EDQLPsiImplUtils.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return EDQLPsiImplUtils.getNameIdentifier(this);
  }

  @Override
  public ItemPresentation getPresentation() {
    return EDQLPsiImplUtils.getPresentation(this);
  }

}
