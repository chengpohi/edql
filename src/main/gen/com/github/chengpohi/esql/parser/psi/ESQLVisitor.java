// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public class ESQLVisitor extends PsiElementVisitor {

  public void visitAddExpression(@NotNull ESQLAddExpression o) {
    visitExpression(o);
  }

  public void visitAndExpression(@NotNull ESQLAndExpression o) {
    visitExpression(o);
  }

  public void visitBetweenExpression(@NotNull ESQLBetweenExpression o) {
    visitExpression(o);
  }

  public void visitBindParameter(@NotNull ESQLBindParameter o) {
    visitPsiElement(o);
  }

  public void visitBitExpression(@NotNull ESQLBitExpression o) {
    visitExpression(o);
  }

  public void visitCaseExpression(@NotNull ESQLCaseExpression o) {
    visitExpression(o);
  }

  public void visitCastExpression(@NotNull ESQLCastExpression o) {
    visitExpression(o);
  }

  public void visitCollateExpression(@NotNull ESQLCollateExpression o) {
    visitExpression(o);
  }

  public void visitCollationName(@NotNull ESQLCollationName o) {
    visitNameElement(o);
  }

  public void visitColumnDefinitionName(@NotNull ESQLColumnDefinitionName o) {
    visitPsiNamedElement(o);
    // visitNameElement(o);
  }

  public void visitColumnName(@NotNull ESQLColumnName o) {
    visitNameElement(o);
  }

  public void visitColumnRefExpression(@NotNull ESQLColumnRefExpression o) {
    visitExpression(o);
  }

  public void visitComparisonExpression(@NotNull ESQLComparisonExpression o) {
    visitExpression(o);
  }

  public void visitCompoundOperator(@NotNull ESQLCompoundOperator o) {
    visitPsiElement(o);
  }

  public void visitConcatExpression(@NotNull ESQLConcatExpression o) {
    visitExpression(o);
  }

  public void visitDatabaseName(@NotNull ESQLDatabaseName o) {
    visitNameElement(o);
  }

  public void visitDefinedTableName(@NotNull ESQLDefinedTableName o) {
    visitNameElement(o);
  }

  public void visitEquivalenceExpression(@NotNull ESQLEquivalenceExpression o) {
    visitExpression(o);
  }

  public void visitErrorMessage(@NotNull ESQLErrorMessage o) {
    visitPsiElement(o);
  }

  public void visitExistsExpression(@NotNull ESQLExistsExpression o) {
    visitExpression(o);
  }

  public void visitExplainPrefix(@NotNull ESQLExplainPrefix o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull ESQLExpression o) {
    visitPsiElement(o);
  }

  public void visitField(@NotNull ESQLField o) {
    visitPsiElement(o);
  }

  public void visitFromClause(@NotNull ESQLFromClause o) {
    visitPsiElement(o);
  }

  public void visitFromTable(@NotNull ESQLFromTable o) {
    visitPsiElement(o);
  }

  public void visitFunctionCallExpression(@NotNull ESQLFunctionCallExpression o) {
    visitExpression(o);
  }

  public void visitFunctionName(@NotNull ESQLFunctionName o) {
    visitNameElement(o);
  }

  public void visitGroupByClause(@NotNull ESQLGroupByClause o) {
    visitPsiElement(o);
  }

  public void visitInExpression(@NotNull ESQLInExpression o) {
    visitExpression(o);
  }

  public void visitIsnullExpression(@NotNull ESQLIsnullExpression o) {
    visitExpression(o);
  }

  public void visitJoinConstraint(@NotNull ESQLJoinConstraint o) {
    visitPsiElement(o);
  }

  public void visitJoinOperator(@NotNull ESQLJoinOperator o) {
    visitPsiElement(o);
  }

  public void visitLikeExpression(@NotNull ESQLLikeExpression o) {
    visitExpression(o);
  }

  public void visitLimitClause(@NotNull ESQLLimitClause o) {
    visitPsiElement(o);
  }

  public void visitLiteralExpression(@NotNull ESQLLiteralExpression o) {
    visitExpression(o);
  }

  public void visitMulExpression(@NotNull ESQLMulExpression o) {
    visitExpression(o);
  }

  public void visitOrExpression(@NotNull ESQLOrExpression o) {
    visitExpression(o);
  }

  public void visitOrderClause(@NotNull ESQLOrderClause o) {
    visitPsiElement(o);
  }

  public void visitOrderingTerm(@NotNull ESQLOrderingTerm o) {
    visitPsiElement(o);
  }

  public void visitParenExpression(@NotNull ESQLParenExpression o) {
    visitExpression(o);
  }

  public void visitRaiseFunctionExpression(@NotNull ESQLRaiseFunctionExpression o) {
    visitExpression(o);
  }

  public void visitSelectCore(@NotNull ESQLSelectCore o) {
    visitPsiElement(o);
  }

  public void visitSelectCoreSelect(@NotNull ESQLSelectCoreSelect o) {
    visitPsiElement(o);
  }

  public void visitSelectCoreValues(@NotNull ESQLSelectCoreValues o) {
    visitPsiElement(o);
  }

  public void visitSelectFields(@NotNull ESQLSelectFields o) {
    visitPsiElement(o);
  }

  public void visitSelectStatement(@NotNull ESQLSelectStatement o) {
    visitPsiElement(o);
  }

  public void visitSelectSubquery(@NotNull ESQLSelectSubquery o) {
    visitPsiElement(o);
  }

  public void visitSelectedTableName(@NotNull ESQLSelectedTableName o) {
    visitNameElement(o);
  }

  public void visitSignedNumber(@NotNull ESQLSignedNumber o) {
    visitPsiElement(o);
  }

  public void visitTableDefinitionName(@NotNull ESQLTableDefinitionName o) {
    visitPsiNamedElement(o);
    // visitNameElement(o);
  }

  public void visitTableOrSubquery(@NotNull ESQLTableOrSubquery o) {
    visitPsiElement(o);
  }

  public void visitTypeName(@NotNull ESQLTypeName o) {
    visitNameElement(o);
  }

  public void visitUnaryExpression(@NotNull ESQLUnaryExpression o) {
    visitExpression(o);
  }

  public void visitWhereClause(@NotNull ESQLWhereClause o) {
    visitPsiElement(o);
  }

  public void visitWithClause(@NotNull ESQLWithClause o) {
    visitPsiElement(o);
  }

  public void visitWithClauseSelectStatement(@NotNull ESQLWithClauseSelectStatement o) {
    visitHasWithClause(o);
  }

  public void visitWithClauseStatement(@NotNull ESQLWithClauseStatement o) {
    visitHasWithClause(o);
  }

  public void visitWithClauseTable(@NotNull ESQLWithClauseTable o) {
    visitPsiElement(o);
  }

  public void visitWithClauseTableDef(@NotNull ESQLWithClauseTableDef o) {
    visitPsiElement(o);
  }

  public void visitNameElement(@NotNull ESQLNameElement o) {
    visitPsiElement(o);
  }

  public void visitHasWithClause(@NotNull HasWithClause o) {
    visitElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
