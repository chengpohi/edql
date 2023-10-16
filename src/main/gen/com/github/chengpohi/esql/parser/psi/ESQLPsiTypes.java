// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.chengpohi.esql.parser.psi.impl.*;

public interface ESQLPsiTypes {

  IElementType ADD_EXPRESSION = new ESQLAstNodeType("ADD_EXPRESSION");
  IElementType AND_EXPRESSION = new ESQLAstNodeType("AND_EXPRESSION");
  IElementType BETWEEN_EXPRESSION = new ESQLAstNodeType("BETWEEN_EXPRESSION");
  IElementType BIND_PARAMETER = new ESQLAstNodeType("BIND_PARAMETER");
  IElementType BIT_EXPRESSION = new ESQLAstNodeType("BIT_EXPRESSION");
  IElementType CASE_EXPRESSION = new ESQLAstNodeType("CASE_EXPRESSION");
  IElementType CAST_EXPRESSION = new ESQLAstNodeType("CAST_EXPRESSION");
  IElementType COLLATE_EXPRESSION = new ESQLAstNodeType("COLLATE_EXPRESSION");
  IElementType COLLATION_NAME = new ESQLAstNodeType("COLLATION_NAME");
  IElementType COLUMN_DEFINITION_NAME = new ESQLAstNodeType("COLUMN_DEFINITION_NAME");
  IElementType COLUMN_NAME = new ESQLAstNodeType("COLUMN_NAME");
  IElementType COLUMN_REF_EXPRESSION = new ESQLAstNodeType("COLUMN_REF_EXPRESSION");
  IElementType COMPARISON_EXPRESSION = new ESQLAstNodeType("COMPARISON_EXPRESSION");
  IElementType COMPOUND_OPERATOR = new ESQLAstNodeType("COMPOUND_OPERATOR");
  IElementType CONCAT_EXPRESSION = new ESQLAstNodeType("CONCAT_EXPRESSION");
  IElementType DATABASE_NAME = new ESQLAstNodeType("DATABASE_NAME");
  IElementType DEFINED_TABLE_NAME = new ESQLAstNodeType("DEFINED_TABLE_NAME");
  IElementType EQUIVALENCE_EXPRESSION = new ESQLAstNodeType("EQUIVALENCE_EXPRESSION");
  IElementType ERROR_MESSAGE = new ESQLAstNodeType("ERROR_MESSAGE");
  IElementType EXISTS_EXPRESSION = new ESQLAstNodeType("EXISTS_EXPRESSION");
  IElementType EXPLAIN_PREFIX = new ESQLAstNodeType("EXPLAIN_PREFIX");
  IElementType EXPRESSION = new ESQLAstNodeType("EXPRESSION");
  IElementType FIELD = new ESQLAstNodeType("FIELD");
  IElementType FROM_CLAUSE = new ESQLAstNodeType("FROM_CLAUSE");
  IElementType FROM_TABLE = new ESQLAstNodeType("FROM_TABLE");
  IElementType FUNCTION_CALL_EXPRESSION = new ESQLAstNodeType("FUNCTION_CALL_EXPRESSION");
  IElementType FUNCTION_NAME = new ESQLAstNodeType("FUNCTION_NAME");
  IElementType GROUP_BY_CLAUSE = new ESQLAstNodeType("GROUP_BY_CLAUSE");
  IElementType IN_EXPRESSION = new ESQLAstNodeType("IN_EXPRESSION");
  IElementType ISNULL_EXPRESSION = new ESQLAstNodeType("ISNULL_EXPRESSION");
  IElementType JOIN_CONSTRAINT = new ESQLAstNodeType("JOIN_CONSTRAINT");
  IElementType JOIN_OPERATOR = new ESQLAstNodeType("JOIN_OPERATOR");
  IElementType LIKE_EXPRESSION = new ESQLAstNodeType("LIKE_EXPRESSION");
  IElementType LIMIT_CLAUSE = new ESQLAstNodeType("LIMIT_CLAUSE");
  IElementType LITERAL_EXPRESSION = new ESQLAstNodeType("LITERAL_EXPRESSION");
  IElementType MUL_EXPRESSION = new ESQLAstNodeType("MUL_EXPRESSION");
  IElementType ORDERING_TERM = new ESQLAstNodeType("ORDERING_TERM");
  IElementType ORDER_CLAUSE = new ESQLAstNodeType("ORDER_CLAUSE");
  IElementType OR_EXPRESSION = new ESQLAstNodeType("OR_EXPRESSION");
  IElementType PAREN_EXPRESSION = new ESQLAstNodeType("PAREN_EXPRESSION");
  IElementType RAISE_FUNCTION_EXPRESSION = new ESQLAstNodeType("RAISE_FUNCTION_EXPRESSION");
  IElementType SELECTED_TABLE_NAME = new ESQLAstNodeType("SELECTED_TABLE_NAME");
  IElementType SELECT_CORE = new ESQLAstNodeType("SELECT_CORE");
  IElementType SELECT_CORE_SELECT = new ESQLAstNodeType("SELECT_CORE_SELECT");
  IElementType SELECT_CORE_VALUES = new ESQLAstNodeType("SELECT_CORE_VALUES");
  IElementType SELECT_FIELDS = new ESQLAstNodeType("SELECT_FIELDS");
  IElementType SELECT_STATEMENT = new ESQLAstNodeType("SELECT_STATEMENT");
  IElementType SELECT_SUBQUERY = new ESQLAstNodeType("SELECT_SUBQUERY");
  IElementType SIGNED_NUMBER = new ESQLAstNodeType("SIGNED_NUMBER");
  IElementType TABLE_DEFINITION_NAME = new ESQLAstNodeType("TABLE_DEFINITION_NAME");
  IElementType TABLE_OR_SUBQUERY = new ESQLAstNodeType("TABLE_OR_SUBQUERY");
  IElementType TYPE_NAME = new ESQLAstNodeType("TYPE_NAME");
  IElementType UNARY_EXPRESSION = new ESQLAstNodeType("UNARY_EXPRESSION");
  IElementType WHERE_CLAUSE = new ESQLAstNodeType("WHERE_CLAUSE");
  IElementType WITH_CLAUSE = new ESQLAstNodeType("WITH_CLAUSE");
  IElementType WITH_CLAUSE_SELECT_STATEMENT = new ESQLAstNodeType("WITH_CLAUSE_SELECT_STATEMENT");
  IElementType WITH_CLAUSE_STATEMENT = new ESQLAstNodeType("WITH_CLAUSE_STATEMENT");
  IElementType WITH_CLAUSE_TABLE = new ESQLAstNodeType("WITH_CLAUSE_TABLE");
  IElementType WITH_CLAUSE_TABLE_DEF = new ESQLAstNodeType("WITH_CLAUSE_TABLE_DEF");

  IElementType ABORT = new ESQLTokenType("ABORT");
  IElementType ALL = new ESQLTokenType("ALL");
  IElementType AMP = new ESQLTokenType("&");
  IElementType AND = new ESQLTokenType("AND");
  IElementType AS = new ESQLTokenType("AS");
  IElementType ASC = new ESQLTokenType("ASC");
  IElementType BACKTICK_LITERAL = new ESQLTokenType("BACKTICK_LITERAL");
  IElementType BAR = new ESQLTokenType("|");
  IElementType BETWEEN = new ESQLTokenType("BETWEEN");
  IElementType BRACKET_LITERAL = new ESQLTokenType("BRACKET_LITERAL");
  IElementType BY = new ESQLTokenType("BY");
  IElementType CASE = new ESQLTokenType("CASE");
  IElementType CAST = new ESQLTokenType("CAST");
  IElementType COLLATE = new ESQLTokenType("COLLATE");
  IElementType COMMA = new ESQLTokenType(",");
  IElementType COMMENT = new ESQLTokenType("COMMENT");
  IElementType CONCAT = new ESQLTokenType("||");
  IElementType CROSS = new ESQLTokenType("CROSS");
  IElementType CURRENT_DATE = new ESQLTokenType("CURRENT_DATE");
  IElementType CURRENT_TIME = new ESQLTokenType("CURRENT_TIME");
  IElementType CURRENT_TIMESTAMP = new ESQLTokenType("CURRENT_TIMESTAMP");
  IElementType DELETE = new ESQLTokenType("DELETE");
  IElementType DESC = new ESQLTokenType("DESC");
  IElementType DISTINCT = new ESQLTokenType("DISTINCT");
  IElementType DIV = new ESQLTokenType("/");
  IElementType DOT = new ESQLTokenType(".");
  IElementType DOUBLE_QUOTE_STRING_LITERAL = new ESQLTokenType("DOUBLE_QUOTE_STRING_LITERAL");
  IElementType ELSE = new ESQLTokenType("ELSE");
  IElementType END = new ESQLTokenType("END");
  IElementType EQ = new ESQLTokenType("=");
  IElementType EQEQ = new ESQLTokenType("==");
  IElementType ESCAPE = new ESQLTokenType("ESCAPE");
  IElementType EXCEPT = new ESQLTokenType("EXCEPT");
  IElementType EXISTS = new ESQLTokenType("EXISTS");
  IElementType EXPLAIN = new ESQLTokenType("EXPLAIN");
  IElementType FAIL = new ESQLTokenType("FAIL");
  IElementType FROM = new ESQLTokenType("FROM");
  IElementType FULL = new ESQLTokenType("FULL");
  IElementType GLOB = new ESQLTokenType("GLOB");
  IElementType GROUP = new ESQLTokenType("GROUP");
  IElementType GT = new ESQLTokenType(">");
  IElementType GTE = new ESQLTokenType(">=");
  IElementType HAVING = new ESQLTokenType("HAVING");
  IElementType IDENTIFIER = new ESQLTokenType("IDENTIFIER");
  IElementType IGNORE = new ESQLTokenType("IGNORE");
  IElementType IN = new ESQLTokenType("IN");
  IElementType INDEXED = new ESQLTokenType("INDEXED");
  IElementType INNER = new ESQLTokenType("INNER");
  IElementType INSERT = new ESQLTokenType("INSERT");
  IElementType INTERSECT = new ESQLTokenType("INTERSECT");
  IElementType IS = new ESQLTokenType("IS");
  IElementType ISNULL = new ESQLTokenType("ISNULL");
  IElementType JOIN = new ESQLTokenType("JOIN");
  IElementType LEFT = new ESQLTokenType("LEFT");
  IElementType LIKE = new ESQLTokenType("LIKE");
  IElementType LIMIT = new ESQLTokenType("LIMIT");
  IElementType LINE_COMMENT = new ESQLTokenType("LINE_COMMENT");
  IElementType LPAREN = new ESQLTokenType("(");
  IElementType LT = new ESQLTokenType("<");
  IElementType LTE = new ESQLTokenType("<=");
  IElementType MATCH = new ESQLTokenType("MATCH");
  IElementType MINUS = new ESQLTokenType("-");
  IElementType MOD = new ESQLTokenType("%");
  IElementType NAMED_PARAMETER = new ESQLTokenType("NAMED_PARAMETER");
  IElementType NATURAL = new ESQLTokenType("NATURAL");
  IElementType NOT = new ESQLTokenType("NOT");
  IElementType NOTNULL = new ESQLTokenType("NOTNULL");
  IElementType NOT_EQ = new ESQLTokenType("!=");
  IElementType NULL = new ESQLTokenType("NULL");
  IElementType NUMBERED_PARAMETER = new ESQLTokenType("NUMBERED_PARAMETER");
  IElementType NUMERIC_LITERAL = new ESQLTokenType("NUMERIC_LITERAL");
  IElementType OFFSET = new ESQLTokenType("OFFSET");
  IElementType ON = new ESQLTokenType("ON");
  IElementType OR = new ESQLTokenType("OR");
  IElementType ORDER = new ESQLTokenType("ORDER");
  IElementType OUTER = new ESQLTokenType("OUTER");
  IElementType PLAN = new ESQLTokenType("PLAN");
  IElementType PLUS = new ESQLTokenType("+");
  IElementType QUERY = new ESQLTokenType("QUERY");
  IElementType RAISE = new ESQLTokenType("RAISE");
  IElementType RECURSIVE = new ESQLTokenType("RECURSIVE");
  IElementType REGEXP = new ESQLTokenType("REGEXP");
  IElementType REPLACE = new ESQLTokenType("REPLACE");
  IElementType RIGHT = new ESQLTokenType("RIGHT");
  IElementType ROLLBACK = new ESQLTokenType("ROLLBACK");
  IElementType RPAREN = new ESQLTokenType(")");
  IElementType SELECT = new ESQLTokenType("select");
  IElementType SEMICOLON = new ESQLTokenType(";");
  IElementType SHL = new ESQLTokenType("<<");
  IElementType SHR = new ESQLTokenType(">>");
  IElementType SINGLE_QUOTE_STRING_LITERAL = new ESQLTokenType("SINGLE_QUOTE_STRING_LITERAL");
  IElementType STAR = new ESQLTokenType("*");
  IElementType THEN = new ESQLTokenType("THEN");
  IElementType TILDE = new ESQLTokenType("~");
  IElementType UNEQ = new ESQLTokenType("<>");
  IElementType UNION = new ESQLTokenType("UNION");
  IElementType UPDATE = new ESQLTokenType("UPDATE");
  IElementType USING = new ESQLTokenType("USING");
  IElementType VALUES = new ESQLTokenType("VALUES");
  IElementType WHEN = new ESQLTokenType("WHEN");
  IElementType WHERE = new ESQLTokenType("WHERE");
  IElementType WITH = new ESQLTokenType("WITH");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ADD_EXPRESSION) {
        return new ESQLAddExpressionImpl(node);
      }
      else if (type == AND_EXPRESSION) {
        return new ESQLAndExpressionImpl(node);
      }
      else if (type == BETWEEN_EXPRESSION) {
        return new ESQLBetweenExpressionImpl(node);
      }
      else if (type == BIND_PARAMETER) {
        return new ESQLBindParameterImpl(node);
      }
      else if (type == BIT_EXPRESSION) {
        return new ESQLBitExpressionImpl(node);
      }
      else if (type == CASE_EXPRESSION) {
        return new ESQLCaseExpressionImpl(node);
      }
      else if (type == CAST_EXPRESSION) {
        return new ESQLCastExpressionImpl(node);
      }
      else if (type == COLLATE_EXPRESSION) {
        return new ESQLCollateExpressionImpl(node);
      }
      else if (type == COLLATION_NAME) {
        return new ESQLCollationNameImpl(node);
      }
      else if (type == COLUMN_DEFINITION_NAME) {
        return new ESQLColumnDefinitionNameImpl(node);
      }
      else if (type == COLUMN_NAME) {
        return new ESQLColumnNameImpl(node);
      }
      else if (type == COLUMN_REF_EXPRESSION) {
        return new ESQLColumnRefExpressionImpl(node);
      }
      else if (type == COMPARISON_EXPRESSION) {
        return new ESQLComparisonExpressionImpl(node);
      }
      else if (type == COMPOUND_OPERATOR) {
        return new ESQLCompoundOperatorImpl(node);
      }
      else if (type == CONCAT_EXPRESSION) {
        return new ESQLConcatExpressionImpl(node);
      }
      else if (type == DATABASE_NAME) {
        return new ESQLDatabaseNameImpl(node);
      }
      else if (type == DEFINED_TABLE_NAME) {
        return new ESQLDefinedTableNameImpl(node);
      }
      else if (type == EQUIVALENCE_EXPRESSION) {
        return new ESQLEquivalenceExpressionImpl(node);
      }
      else if (type == ERROR_MESSAGE) {
        return new ESQLErrorMessageImpl(node);
      }
      else if (type == EXISTS_EXPRESSION) {
        return new ESQLExistsExpressionImpl(node);
      }
      else if (type == EXPLAIN_PREFIX) {
        return new ESQLExplainPrefixImpl(node);
      }
      else if (type == FIELD) {
        return new ESQLFieldImpl(node);
      }
      else if (type == FROM_CLAUSE) {
        return new ESQLFromClauseImpl(node);
      }
      else if (type == FROM_TABLE) {
        return new ESQLFromTableImpl(node);
      }
      else if (type == FUNCTION_CALL_EXPRESSION) {
        return new ESQLFunctionCallExpressionImpl(node);
      }
      else if (type == FUNCTION_NAME) {
        return new ESQLFunctionNameImpl(node);
      }
      else if (type == GROUP_BY_CLAUSE) {
        return new ESQLGroupByClauseImpl(node);
      }
      else if (type == IN_EXPRESSION) {
        return new ESQLInExpressionImpl(node);
      }
      else if (type == ISNULL_EXPRESSION) {
        return new ESQLIsnullExpressionImpl(node);
      }
      else if (type == JOIN_CONSTRAINT) {
        return new ESQLJoinConstraintImpl(node);
      }
      else if (type == JOIN_OPERATOR) {
        return new ESQLJoinOperatorImpl(node);
      }
      else if (type == LIKE_EXPRESSION) {
        return new ESQLLikeExpressionImpl(node);
      }
      else if (type == LIMIT_CLAUSE) {
        return new ESQLLimitClauseImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new ESQLLiteralExpressionImpl(node);
      }
      else if (type == MUL_EXPRESSION) {
        return new ESQLMulExpressionImpl(node);
      }
      else if (type == ORDERING_TERM) {
        return new ESQLOrderingTermImpl(node);
      }
      else if (type == ORDER_CLAUSE) {
        return new ESQLOrderClauseImpl(node);
      }
      else if (type == OR_EXPRESSION) {
        return new ESQLOrExpressionImpl(node);
      }
      else if (type == PAREN_EXPRESSION) {
        return new ESQLParenExpressionImpl(node);
      }
      else if (type == RAISE_FUNCTION_EXPRESSION) {
        return new ESQLRaiseFunctionExpressionImpl(node);
      }
      else if (type == SELECTED_TABLE_NAME) {
        return new ESQLSelectedTableNameImpl(node);
      }
      else if (type == SELECT_CORE) {
        return new ESQLSelectCoreImpl(node);
      }
      else if (type == SELECT_CORE_SELECT) {
        return new ESQLSelectCoreSelectImpl(node);
      }
      else if (type == SELECT_CORE_VALUES) {
        return new ESQLSelectCoreValuesImpl(node);
      }
      else if (type == SELECT_FIELDS) {
        return new ESQLSelectFieldsImpl(node);
      }
      else if (type == SELECT_STATEMENT) {
        return new ESQLSelectStatementImpl(node);
      }
      else if (type == SELECT_SUBQUERY) {
        return new ESQLSelectSubqueryImpl(node);
      }
      else if (type == SIGNED_NUMBER) {
        return new ESQLSignedNumberImpl(node);
      }
      else if (type == TABLE_DEFINITION_NAME) {
        return new ESQLTableDefinitionNameImpl(node);
      }
      else if (type == TABLE_OR_SUBQUERY) {
        return new ESQLTableOrSubqueryImpl(node);
      }
      else if (type == TYPE_NAME) {
        return new ESQLTypeNameImpl(node);
      }
      else if (type == UNARY_EXPRESSION) {
        return new ESQLUnaryExpressionImpl(node);
      }
      else if (type == WHERE_CLAUSE) {
        return new ESQLWhereClauseImpl(node);
      }
      else if (type == WITH_CLAUSE) {
        return new ESQLWithClauseImpl(node);
      }
      else if (type == WITH_CLAUSE_SELECT_STATEMENT) {
        return new ESQLWithClauseSelectStatementImpl(node);
      }
      else if (type == WITH_CLAUSE_STATEMENT) {
        return new ESQLWithClauseStatementImpl(node);
      }
      else if (type == WITH_CLAUSE_TABLE) {
        return new ESQLWithClauseTableImpl(node);
      }
      else if (type == WITH_CLAUSE_TABLE_DEF) {
        return new ESQLWithClauseTableDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
