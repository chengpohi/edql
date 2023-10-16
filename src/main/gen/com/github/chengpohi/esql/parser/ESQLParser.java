// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.esql.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.chengpohi.esql.parser.psi.ESQLPsiTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ESQLParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType type, PsiBuilder builder) {
    parseLight(type, builder);
    return builder.getTreeBuilt();
  }

  public void parseLight(IElementType type, PsiBuilder builder) {
    boolean result;
    builder = adapt_builder_(type, builder, this, EXTENDS_SETS_);
    Marker marker = enter_section_(builder, 0, _COLLAPSE_, null);
    result = parse_root_(type, builder);
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType type, PsiBuilder builder) {
    return parse_root_(type, builder, 0);
  }

  static boolean parse_root_(IElementType type, PsiBuilder builder, int level) {
    return root(builder, level + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ADD_EXPRESSION, AND_EXPRESSION, BETWEEN_EXPRESSION, BIT_EXPRESSION,
      CASE_EXPRESSION, CAST_EXPRESSION, COLLATE_EXPRESSION, COLUMN_REF_EXPRESSION,
      COMPARISON_EXPRESSION, CONCAT_EXPRESSION, EQUIVALENCE_EXPRESSION, EXISTS_EXPRESSION,
      EXPRESSION, FUNCTION_CALL_EXPRESSION, IN_EXPRESSION, ISNULL_EXPRESSION,
      LIKE_EXPRESSION, LITERAL_EXPRESSION, MUL_EXPRESSION, OR_EXPRESSION,
      PAREN_EXPRESSION, RAISE_FUNCTION_EXPRESSION, UNARY_EXPRESSION),
  };

  /* ********************************************************** */
  // NUMBERED_PARAMETER | NAMED_PARAMETER
  public static boolean bind_parameter(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bind_parameter")) return false;
    if (!nextTokenIs(builder, "<bind parameter>", NAMED_PARAMETER, NUMBERED_PARAMETER)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BIND_PARAMETER, "<bind parameter>");
    result = consumeToken(builder, NUMBERED_PARAMETER);
    if (!result) result = consumeToken(builder, NAMED_PARAMETER);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // name
  public static boolean collation_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "collation_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COLLATION_NAME, "<collation name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // name
  public static boolean column_definition_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "column_definition_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COLUMN_DEFINITION_NAME, "<column definition name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // name
  public static boolean column_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "column_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COLUMN_NAME, "<column name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // UNION ALL? | INTERSECT | EXCEPT
  public static boolean compound_operator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compound_operator")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COMPOUND_OPERATOR, "<compound operator>");
    result = compound_operator_0(builder, level + 1);
    if (!result) result = consumeToken(builder, INTERSECT);
    if (!result) result = consumeToken(builder, EXCEPT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // UNION ALL?
  private static boolean compound_operator_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compound_operator_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, UNION);
    result = result && compound_operator_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ALL?
  private static boolean compound_operator_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compound_operator_0_1")) return false;
    consumeToken(builder, ALL);
    return true;
  }

  /* ********************************************************** */
  // name
  public static boolean database_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "database_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, DATABASE_NAME, "<database name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // name
  public static boolean defined_table_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "defined_table_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, DEFINED_TABLE_NAME, "<defined table name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // string_literal
  public static boolean error_message(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "error_message")) return false;
    if (!nextTokenIs(builder, "<error message>", DOUBLE_QUOTE_STRING_LITERAL, SINGLE_QUOTE_STRING_LITERAL)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, ERROR_MESSAGE, "<error message>");
    result = string_literal(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // EXPLAIN ( QUERY PLAN )?
  public static boolean explain_prefix(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "explain_prefix")) return false;
    if (!nextTokenIs(builder, EXPLAIN)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, EXPLAIN);
    result = result && explain_prefix_1(builder, level + 1);
    exit_section_(builder, marker, EXPLAIN_PREFIX, result);
    return result;
  }

  // ( QUERY PLAN )?
  private static boolean explain_prefix_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "explain_prefix_1")) return false;
    explain_prefix_1_0(builder, level + 1);
    return true;
  }

  // QUERY PLAN
  private static boolean explain_prefix_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "explain_prefix_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, QUERY, PLAN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // &(WITH|SELECT|VALUES) subquery_greedy
  static boolean expression_subquery(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expression_subquery")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = expression_subquery_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && subquery_greedy(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // &(WITH|SELECT|VALUES)
  private static boolean expression_subquery_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expression_subquery_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_);
    result = expression_subquery_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // WITH|SELECT|VALUES
  private static boolean expression_subquery_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expression_subquery_0_0")) return false;
    boolean result;
    result = consumeToken(builder, WITH);
    if (!result) result = consumeToken(builder, SELECT);
    if (!result) result = consumeToken(builder, VALUES);
    return result;
  }

  /* ********************************************************** */
  // name ('(' ( ( DISTINCT )? expression ( ',' expression )* | '*' )? ')')? (AS name)?
  public static boolean field(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FIELD, "<field>");
    result = name(builder, level + 1);
    result = result && field_1(builder, level + 1);
    result = result && field_2(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ('(' ( ( DISTINCT )? expression ( ',' expression )* | '*' )? ')')?
  private static boolean field_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1")) return false;
    field_1_0(builder, level + 1);
    return true;
  }

  // '(' ( ( DISTINCT )? expression ( ',' expression )* | '*' )? ')'
  private static boolean field_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LPAREN);
    result = result && field_1_0_1(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ( DISTINCT )? expression ( ',' expression )* | '*' )?
  private static boolean field_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0_1")) return false;
    field_1_0_1_0(builder, level + 1);
    return true;
  }

  // ( DISTINCT )? expression ( ',' expression )* | '*'
  private static boolean field_1_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = field_1_0_1_0_0(builder, level + 1);
    if (!result) result = consumeToken(builder, STAR);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( DISTINCT )? expression ( ',' expression )*
  private static boolean field_1_0_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = field_1_0_1_0_0_0(builder, level + 1);
    result = result && expression(builder, level + 1, -1);
    result = result && field_1_0_1_0_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( DISTINCT )?
  private static boolean field_1_0_1_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0_1_0_0_0")) return false;
    consumeToken(builder, DISTINCT);
    return true;
  }

  // ( ',' expression )*
  private static boolean field_1_0_1_0_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0_1_0_0_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!field_1_0_1_0_0_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "field_1_0_1_0_0_2", pos)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean field_1_0_1_0_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_1_0_1_0_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (AS name)?
  private static boolean field_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_2")) return false;
    field_2_0(builder, level + 1);
    return true;
  }

  // AS name
  private static boolean field_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "field_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, AS);
    result = result && name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // FROM table_or_subquery ( join_operator table_or_subquery join_constraint? )*
  public static boolean from_clause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_clause")) return false;
    if (!nextTokenIs(builder, FROM)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FROM);
    result = result && table_or_subquery(builder, level + 1);
    result = result && from_clause_2(builder, level + 1);
    exit_section_(builder, marker, FROM_CLAUSE, result);
    return result;
  }

  // ( join_operator table_or_subquery join_constraint? )*
  private static boolean from_clause_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_clause_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!from_clause_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "from_clause_2", pos)) break;
    }
    return true;
  }

  // join_operator table_or_subquery join_constraint?
  private static boolean from_clause_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_clause_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = join_operator(builder, level + 1);
    result = result && table_or_subquery(builder, level + 1);
    result = result && from_clause_2_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // join_constraint?
  private static boolean from_clause_2_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_clause_2_0_2")) return false;
    join_constraint(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // ( database_name '.' )? defined_table_name ( ( AS )? name )? ( INDEXED BY index_name | NOT INDEXED )? {
  // }
  public static boolean from_table(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FROM_TABLE, "<from table>");
    result = from_table_0(builder, level + 1);
    result = result && defined_table_name(builder, level + 1);
    result = result && from_table_2(builder, level + 1);
    result = result && from_table_3(builder, level + 1);
    result = result && from_table_4(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( database_name '.' )?
  private static boolean from_table_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_0")) return false;
    from_table_0_0(builder, level + 1);
    return true;
  }

  // database_name '.'
  private static boolean from_table_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = database_name(builder, level + 1);
    result = result && consumeToken(builder, DOT);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ( AS )? name )?
  private static boolean from_table_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_2")) return false;
    from_table_2_0(builder, level + 1);
    return true;
  }

  // ( AS )? name
  private static boolean from_table_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = from_table_2_0_0(builder, level + 1);
    result = result && name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( AS )?
  private static boolean from_table_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_2_0_0")) return false;
    consumeToken(builder, AS);
    return true;
  }

  // ( INDEXED BY index_name | NOT INDEXED )?
  private static boolean from_table_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_3")) return false;
    from_table_3_0(builder, level + 1);
    return true;
  }

  // INDEXED BY index_name | NOT INDEXED
  private static boolean from_table_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = from_table_3_0_0(builder, level + 1);
    if (!result) result = parseTokens(builder, 0, NOT, INDEXED);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // INDEXED BY index_name
  private static boolean from_table_3_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "from_table_3_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, INDEXED, BY);
    result = result && index_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // {
  // }
  private static boolean from_table_4(PsiBuilder builder, int level) {
    return true;
  }

  /* ********************************************************** */
  // name
  public static boolean function_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FUNCTION_NAME, "<function name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // GROUP BY expression ( ',' expression )* ( HAVING expression )?
  public static boolean group_by_clause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "group_by_clause")) return false;
    if (!nextTokenIs(builder, GROUP)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, GROUP, BY);
    result = result && expression(builder, level + 1, -1);
    result = result && group_by_clause_3(builder, level + 1);
    result = result && group_by_clause_4(builder, level + 1);
    exit_section_(builder, marker, GROUP_BY_CLAUSE, result);
    return result;
  }

  // ( ',' expression )*
  private static boolean group_by_clause_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "group_by_clause_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!group_by_clause_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "group_by_clause_3", pos)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean group_by_clause_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "group_by_clause_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( HAVING expression )?
  private static boolean group_by_clause_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "group_by_clause_4")) return false;
    group_by_clause_4_0(builder, level + 1);
    return true;
  }

  // HAVING expression
  private static boolean group_by_clause_4_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "group_by_clause_4_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, HAVING);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // name
  static boolean index_name(PsiBuilder builder, int level) {
    return name(builder, level + 1);
  }

  /* ********************************************************** */
  // ON expression | USING '(' column_name ( ',' column_name )* ')'
  public static boolean join_constraint(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_constraint")) return false;
    if (!nextTokenIs(builder, "<join constraint>", ON, USING)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, JOIN_CONSTRAINT, "<join constraint>");
    result = join_constraint_0(builder, level + 1);
    if (!result) result = join_constraint_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ON expression
  private static boolean join_constraint_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_constraint_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, ON);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // USING '(' column_name ( ',' column_name )* ')'
  private static boolean join_constraint_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_constraint_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, USING, LPAREN);
    result = result && column_name(builder, level + 1);
    result = result && join_constraint_1_3(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ',' column_name )*
  private static boolean join_constraint_1_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_constraint_1_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!join_constraint_1_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "join_constraint_1_3", pos)) break;
    }
    return true;
  }

  // ',' column_name
  private static boolean join_constraint_1_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_constraint_1_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && column_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // ',' | ( NATURAL )? ( LEFT ( OUTER )? | INNER | CROSS )? JOIN
  public static boolean join_operator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, JOIN_OPERATOR, "<join operator>");
    result = consumeToken(builder, COMMA);
    if (!result) result = join_operator_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( NATURAL )? ( LEFT ( OUTER )? | INNER | CROSS )? JOIN
  private static boolean join_operator_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = join_operator_1_0(builder, level + 1);
    result = result && join_operator_1_1(builder, level + 1);
    result = result && consumeToken(builder, JOIN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( NATURAL )?
  private static boolean join_operator_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator_1_0")) return false;
    consumeToken(builder, NATURAL);
    return true;
  }

  // ( LEFT ( OUTER )? | INNER | CROSS )?
  private static boolean join_operator_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator_1_1")) return false;
    join_operator_1_1_0(builder, level + 1);
    return true;
  }

  // LEFT ( OUTER )? | INNER | CROSS
  private static boolean join_operator_1_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator_1_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = join_operator_1_1_0_0(builder, level + 1);
    if (!result) result = consumeToken(builder, INNER);
    if (!result) result = consumeToken(builder, CROSS);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LEFT ( OUTER )?
  private static boolean join_operator_1_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator_1_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT);
    result = result && join_operator_1_1_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( OUTER )?
  private static boolean join_operator_1_1_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "join_operator_1_1_0_0_1")) return false;
    consumeToken(builder, OUTER);
    return true;
  }

  /* ********************************************************** */
  // LIMIT expression ( ( OFFSET | ',' ) expression )?
  public static boolean limit_clause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "limit_clause")) return false;
    if (!nextTokenIs(builder, LIMIT)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LIMIT);
    result = result && expression(builder, level + 1, -1);
    result = result && limit_clause_2(builder, level + 1);
    exit_section_(builder, marker, LIMIT_CLAUSE, result);
    return result;
  }

  // ( ( OFFSET | ',' ) expression )?
  private static boolean limit_clause_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "limit_clause_2")) return false;
    limit_clause_2_0(builder, level + 1);
    return true;
  }

  // ( OFFSET | ',' ) expression
  private static boolean limit_clause_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "limit_clause_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = limit_clause_2_0_0(builder, level + 1);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // OFFSET | ','
  private static boolean limit_clause_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "limit_clause_2_0_0")) return false;
    boolean result;
    result = consumeToken(builder, OFFSET);
    if (!result) result = consumeToken(builder, COMMA);
    return result;
  }

  /* ********************************************************** */
  // NUMERIC_LITERAL
  //   | string_literal // X marks a blob literal
  //   | NULL
  //   | CURRENT_TIME
  //   | CURRENT_DATE
  //   | CURRENT_TIMESTAMP
  static boolean literal_value(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "literal_value")) return false;
    boolean result;
    result = consumeToken(builder, NUMERIC_LITERAL);
    if (!result) result = string_literal(builder, level + 1);
    if (!result) result = consumeToken(builder, NULL);
    if (!result) result = consumeToken(builder, CURRENT_TIME);
    if (!result) result = consumeToken(builder, CURRENT_DATE);
    if (!result) result = consumeToken(builder, CURRENT_TIMESTAMP);
    return result;
  }

  /* ********************************************************** */
  // IDENTIFIER | BRACKET_LITERAL | BACKTICK_LITERAL | string_literal
  static boolean name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "name")) return false;
    boolean result;
    result = consumeToken(builder, IDENTIFIER);
    if (!result) result = consumeToken(builder, BRACKET_LITERAL);
    if (!result) result = consumeToken(builder, BACKTICK_LITERAL);
    if (!result) result = string_literal(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // ORDER BY ordering_term ( ',' ordering_term )*
  public static boolean order_clause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "order_clause")) return false;
    if (!nextTokenIs(builder, ORDER)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, ORDER, BY);
    result = result && ordering_term(builder, level + 1);
    result = result && order_clause_3(builder, level + 1);
    exit_section_(builder, marker, ORDER_CLAUSE, result);
    return result;
  }

  // ( ',' ordering_term )*
  private static boolean order_clause_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "order_clause_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!order_clause_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "order_clause_3", pos)) break;
    }
    return true;
  }

  // ',' ordering_term
  private static boolean order_clause_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "order_clause_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && ordering_term(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // expression ( COLLATE collation_name )? ( ASC | DESC )?
  public static boolean ordering_term(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ordering_term")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, ORDERING_TERM, "<ordering term>");
    result = expression(builder, level + 1, -1);
    result = result && ordering_term_1(builder, level + 1);
    result = result && ordering_term_2(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( COLLATE collation_name )?
  private static boolean ordering_term_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ordering_term_1")) return false;
    ordering_term_1_0(builder, level + 1);
    return true;
  }

  // COLLATE collation_name
  private static boolean ordering_term_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ordering_term_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COLLATE);
    result = result && collation_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ASC | DESC )?
  private static boolean ordering_term_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ordering_term_2")) return false;
    ordering_term_2_0(builder, level + 1);
    return true;
  }

  // ASC | DESC
  private static boolean ordering_term_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ordering_term_2_0")) return false;
    boolean result;
    result = consumeToken(builder, ASC);
    if (!result) result = consumeToken(builder, DESC);
    return result;
  }

  /* ********************************************************** */
  // (statement ';' ?)*
  static boolean root(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "root")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!root_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "root", pos)) break;
    }
    return true;
  }

  // statement ';' ?
  private static boolean root_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "root_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = statement(builder, level + 1);
    result = result && root_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ';' ?
  private static boolean root_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "root_0_1")) return false;
    consumeToken(builder, SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // select_core_select | select_core_values
  public static boolean select_core(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core")) return false;
    if (!nextTokenIs(builder, "<select core>", SELECT, VALUES)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SELECT_CORE, "<select core>");
    result = select_core_select(builder, level + 1);
    if (!result) result = select_core_values(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // SELECT select_fields from_clause? where_clause? group_by_clause?
  public static boolean select_core_select(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_select")) return false;
    if (!nextTokenIs(builder, SELECT)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SELECT);
    result = result && select_fields(builder, level + 1);
    result = result && select_core_select_2(builder, level + 1);
    result = result && select_core_select_3(builder, level + 1);
    result = result && select_core_select_4(builder, level + 1);
    exit_section_(builder, marker, SELECT_CORE_SELECT, result);
    return result;
  }

  // from_clause?
  private static boolean select_core_select_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_select_2")) return false;
    from_clause(builder, level + 1);
    return true;
  }

  // where_clause?
  private static boolean select_core_select_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_select_3")) return false;
    where_clause(builder, level + 1);
    return true;
  }

  // group_by_clause?
  private static boolean select_core_select_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_select_4")) return false;
    group_by_clause(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // VALUES '(' expression ( ',' expression )* ')' ( ',' '(' expression ( ',' expression )* ')' )*
  public static boolean select_core_values(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values")) return false;
    if (!nextTokenIs(builder, VALUES)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, VALUES, LPAREN);
    result = result && expression(builder, level + 1, -1);
    result = result && select_core_values_3(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    result = result && select_core_values_5(builder, level + 1);
    exit_section_(builder, marker, SELECT_CORE_VALUES, result);
    return result;
  }

  // ( ',' expression )*
  private static boolean select_core_values_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!select_core_values_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "select_core_values_3", pos)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean select_core_values_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ',' '(' expression ( ',' expression )* ')' )*
  private static boolean select_core_values_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values_5")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!select_core_values_5_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "select_core_values_5", pos)) break;
    }
    return true;
  }

  // ',' '(' expression ( ',' expression )* ')'
  private static boolean select_core_values_5_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values_5_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, COMMA, LPAREN);
    result = result && expression(builder, level + 1, -1);
    result = result && select_core_values_5_0_3(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ',' expression )*
  private static boolean select_core_values_5_0_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values_5_0_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!select_core_values_5_0_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "select_core_values_5_0_3", pos)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean select_core_values_5_0_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_core_values_5_0_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // ( DISTINCT | ALL | '*' ) | ( field (',' field)*)
  public static boolean select_fields(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_fields")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SELECT_FIELDS, "<select fields>");
    result = select_fields_0(builder, level + 1);
    if (!result) result = select_fields_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // DISTINCT | ALL | '*'
  private static boolean select_fields_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_fields_0")) return false;
    boolean result;
    result = consumeToken(builder, DISTINCT);
    if (!result) result = consumeToken(builder, ALL);
    if (!result) result = consumeToken(builder, STAR);
    return result;
  }

  // field (',' field)*
  private static boolean select_fields_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_fields_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = field(builder, level + 1);
    result = result && select_fields_1_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (',' field)*
  private static boolean select_fields_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_fields_1_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!select_fields_1_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "select_fields_1_1", pos)) break;
    }
    return true;
  }

  // ',' field
  private static boolean select_fields_1_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_fields_1_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && field(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // select_core (compound_operator select_core)* order_clause? limit_clause?
  public static boolean select_statement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_statement")) return false;
    if (!nextTokenIs(builder, "<select statement>", SELECT, VALUES)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SELECT_STATEMENT, "<select statement>");
    result = select_core(builder, level + 1);
    result = result && select_statement_1(builder, level + 1);
    result = result && select_statement_2(builder, level + 1);
    result = result && select_statement_3(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // (compound_operator select_core)*
  private static boolean select_statement_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_statement_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!select_statement_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "select_statement_1", pos)) break;
    }
    return true;
  }

  // compound_operator select_core
  private static boolean select_statement_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_statement_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = compound_operator(builder, level + 1);
    result = result && select_core(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // order_clause?
  private static boolean select_statement_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_statement_2")) return false;
    order_clause(builder, level + 1);
    return true;
  }

  // limit_clause?
  private static boolean select_statement_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_statement_3")) return false;
    limit_clause(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' &(select|VALUES|WITH) subquery_greedy ')' ( ( AS )? )?
  public static boolean select_subquery(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_subquery")) return false;
    if (!nextTokenIs(builder, LPAREN)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, SELECT_SUBQUERY, null);
    result = consumeToken(builder, LPAREN);
    result = result && select_subquery_1(builder, level + 1);
    pinned = result; // pin = 2
    result = result && report_error_(builder, subquery_greedy(builder, level + 1));
    result = pinned && report_error_(builder, consumeToken(builder, RPAREN)) && result;
    result = pinned && select_subquery_4(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // &(select|VALUES|WITH)
  private static boolean select_subquery_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_subquery_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_);
    result = select_subquery_1_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // select|VALUES|WITH
  private static boolean select_subquery_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_subquery_1_0")) return false;
    boolean result;
    result = consumeToken(builder, SELECT);
    if (!result) result = consumeToken(builder, VALUES);
    if (!result) result = consumeToken(builder, WITH);
    return result;
  }

  // ( ( AS )? )?
  private static boolean select_subquery_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_subquery_4")) return false;
    select_subquery_4_0(builder, level + 1);
    return true;
  }

  // ( AS )?
  private static boolean select_subquery_4_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "select_subquery_4_0")) return false;
    consumeToken(builder, AS);
    return true;
  }

  /* ********************************************************** */
  // name
  public static boolean selected_table_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "selected_table_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SELECTED_TABLE_NAME, "<selected table name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // ( '+' | '-' )? NUMERIC_LITERAL
  public static boolean signed_number(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "signed_number")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SIGNED_NUMBER, "<signed number>");
    result = signed_number_0(builder, level + 1);
    result = result && consumeToken(builder, NUMERIC_LITERAL);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( '+' | '-' )?
  private static boolean signed_number_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "signed_number_0")) return false;
    signed_number_0_0(builder, level + 1);
    return true;
  }

  // '+' | '-'
  private static boolean signed_number_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "signed_number_0_0")) return false;
    boolean result;
    result = consumeToken(builder, PLUS);
    if (!result) result = consumeToken(builder, MINUS);
    return result;
  }

  /* ********************************************************** */
  // explain_prefix ?
  //   (
  //   select_statement
  //   | with_clause_statement
  //   )
  static boolean statement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statement")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, null, "<statement>");
    result = statement_0(builder, level + 1);
    result = result && statement_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // explain_prefix ?
  private static boolean statement_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statement_0")) return false;
    explain_prefix(builder, level + 1);
    return true;
  }

  // select_statement
  //   | with_clause_statement
  private static boolean statement_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statement_1")) return false;
    boolean result;
    result = select_statement(builder, level + 1);
    if (!result) result = with_clause_statement(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // SINGLE_QUOTE_STRING_LITERAL | DOUBLE_QUOTE_STRING_LITERAL
  static boolean string_literal(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "string_literal")) return false;
    if (!nextTokenIs(builder, "", DOUBLE_QUOTE_STRING_LITERAL, SINGLE_QUOTE_STRING_LITERAL)) return false;
    boolean result;
    result = consumeToken(builder, SINGLE_QUOTE_STRING_LITERAL);
    if (!result) result = consumeToken(builder, DOUBLE_QUOTE_STRING_LITERAL);
    return result;
  }

  /* ********************************************************** */
  // select_statement | with_clause_select_statement
  static boolean subquery_greedy(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "subquery_greedy")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = select_statement(builder, level + 1);
    if (!result) result = with_clause_select_statement(builder, level + 1);
    exit_section_(builder, level, marker, result, false, ESQLParser::subquery_recover);
    return result;
  }

  /* ********************************************************** */
  // !')'
  static boolean subquery_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "subquery_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !consumeToken(builder, RPAREN);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // name
  public static boolean table_definition_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "table_definition_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, TABLE_DEFINITION_NAME, "<table definition name>");
    result = name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // from_table | select_subquery
  public static boolean table_or_subquery(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "table_or_subquery")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, TABLE_OR_SUBQUERY, "<table or subquery>");
    result = from_table(builder, level + 1);
    if (!result) result = select_subquery(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // name ( '(' signed_number ')' | '(' signed_number ',' signed_number ')' )?
  public static boolean type_name(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_name")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, TYPE_NAME, "<type name>");
    result = name(builder, level + 1);
    result = result && type_name_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( '(' signed_number ')' | '(' signed_number ',' signed_number ')' )?
  private static boolean type_name_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_name_1")) return false;
    type_name_1_0(builder, level + 1);
    return true;
  }

  // '(' signed_number ')' | '(' signed_number ',' signed_number ')'
  private static boolean type_name_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_name_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = type_name_1_0_0(builder, level + 1);
    if (!result) result = type_name_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '(' signed_number ')'
  private static boolean type_name_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_name_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LPAREN);
    result = result && signed_number(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '(' signed_number ',' signed_number ')'
  private static boolean type_name_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_name_1_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LPAREN);
    result = result && signed_number(builder, level + 1);
    result = result && consumeToken(builder, COMMA);
    result = result && signed_number(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // WHERE expression
  public static boolean where_clause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "where_clause")) return false;
    if (!nextTokenIs(builder, WHERE)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, WHERE);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, WHERE_CLAUSE, result);
    return result;
  }

  /* ********************************************************** */
  // &WITH with_clause_greedy
  public static boolean with_clause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause")) return false;
    if (!nextTokenIs(builder, WITH)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = with_clause_0(builder, level + 1);
    result = result && with_clause_greedy(builder, level + 1);
    exit_section_(builder, marker, WITH_CLAUSE, result);
    return result;
  }

  // &WITH
  private static boolean with_clause_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_);
    result = consumeToken(builder, WITH);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // WITH ( RECURSIVE )? with_clause_table ( ',' with_clause_table )*
  static boolean with_clause_greedy(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_greedy")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, WITH);
    pinned = result; // pin = 1
    result = result && report_error_(builder, with_clause_greedy_1(builder, level + 1));
    result = pinned && report_error_(builder, with_clause_table(builder, level + 1)) && result;
    result = pinned && with_clause_greedy_3(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, ESQLParser::with_clause_recover);
    return result || pinned;
  }

  // ( RECURSIVE )?
  private static boolean with_clause_greedy_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_greedy_1")) return false;
    consumeToken(builder, RECURSIVE);
    return true;
  }

  // ( ',' with_clause_table )*
  private static boolean with_clause_greedy_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_greedy_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!with_clause_greedy_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "with_clause_greedy_3", pos)) break;
    }
    return true;
  }

  // ',' with_clause_table
  private static boolean with_clause_greedy_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_greedy_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && with_clause_table(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // !(DELETE | INSERT | REPLACE | SELECT | UPDATE | VALUES | ')')
  static boolean with_clause_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !with_clause_recover_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // DELETE | INSERT | REPLACE | SELECT | UPDATE | VALUES | ')'
  private static boolean with_clause_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_recover_0")) return false;
    boolean result;
    result = consumeToken(builder, DELETE);
    if (!result) result = consumeToken(builder, INSERT);
    if (!result) result = consumeToken(builder, REPLACE);
    if (!result) result = consumeToken(builder, SELECT);
    if (!result) result = consumeToken(builder, UPDATE);
    if (!result) result = consumeToken(builder, VALUES);
    if (!result) result = consumeToken(builder, RPAREN);
    return result;
  }

  /* ********************************************************** */
  // with_clause select_statement
  public static boolean with_clause_select_statement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_select_statement")) return false;
    if (!nextTokenIs(builder, WITH)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = with_clause(builder, level + 1);
    result = result && select_statement(builder, level + 1);
    exit_section_(builder, marker, WITH_CLAUSE_SELECT_STATEMENT, result);
    return result;
  }

  /* ********************************************************** */
  // with_clause (select_statement)
  public static boolean with_clause_statement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_statement")) return false;
    if (!nextTokenIs(builder, WITH)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = with_clause(builder, level + 1);
    result = result && with_clause_statement_1(builder, level + 1);
    exit_section_(builder, marker, WITH_CLAUSE_STATEMENT, result);
    return result;
  }

  // (select_statement)
  private static boolean with_clause_statement_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_statement_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = select_statement(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // with_clause_table_def AS with_clause_table_def_subquery
  public static boolean with_clause_table(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, WITH_CLAUSE_TABLE, "<with clause table>");
    result = with_clause_table_def(builder, level + 1);
    result = result && consumeToken(builder, AS);
    result = result && with_clause_table_def_subquery(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // table_definition_name ( '(' column_definition_name ( ',' column_definition_name )* ')' )?
  public static boolean with_clause_table_def(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, WITH_CLAUSE_TABLE_DEF, "<with clause table def>");
    result = table_definition_name(builder, level + 1);
    result = result && with_clause_table_def_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( '(' column_definition_name ( ',' column_definition_name )* ')' )?
  private static boolean with_clause_table_def_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_1")) return false;
    with_clause_table_def_1_0(builder, level + 1);
    return true;
  }

  // '(' column_definition_name ( ',' column_definition_name )* ')'
  private static boolean with_clause_table_def_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LPAREN);
    result = result && column_definition_name(builder, level + 1);
    result = result && with_clause_table_def_1_0_2(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ',' column_definition_name )*
  private static boolean with_clause_table_def_1_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_1_0_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!with_clause_table_def_1_0_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "with_clause_table_def_1_0_2", pos)) break;
    }
    return true;
  }

  // ',' column_definition_name
  private static boolean with_clause_table_def_1_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_1_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COMMA);
    result = result && column_definition_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // '(' &(SELECT|VALUES|WITH) subquery_greedy ')'
  static boolean with_clause_table_def_subquery(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_subquery")) return false;
    if (!nextTokenIs(builder, LPAREN)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, LPAREN);
    result = result && with_clause_table_def_subquery_1(builder, level + 1);
    pinned = result; // pin = 2
    result = result && report_error_(builder, subquery_greedy(builder, level + 1));
    result = pinned && consumeToken(builder, RPAREN) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // &(SELECT|VALUES|WITH)
  private static boolean with_clause_table_def_subquery_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_subquery_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_);
    result = with_clause_table_def_subquery_1_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // SELECT|VALUES|WITH
  private static boolean with_clause_table_def_subquery_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "with_clause_table_def_subquery_1_0")) return false;
    boolean result;
    result = consumeToken(builder, SELECT);
    if (!result) result = consumeToken(builder, VALUES);
    if (!result) result = consumeToken(builder, WITH);
    return result;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: ATOM(raise_function_expression)
  // 1: BINARY(or_expression)
  // 2: BINARY(and_expression)
  // 3: ATOM(case_expression)
  // 4: ATOM(exists_expression)
  // 5: POSTFIX(in_expression)
  // 6: POSTFIX(isnull_expression)
  // 7: BINARY(like_expression)
  // 8: PREFIX(cast_expression)
  // 9: ATOM(function_call_expression)
  // 10: BINARY(equivalence_expression) BINARY(between_expression)
  // 11: BINARY(comparison_expression)
  // 12: BINARY(bit_expression)
  // 13: BINARY(add_expression)
  // 14: BINARY(mul_expression)
  // 15: BINARY(concat_expression)
  // 16: PREFIX(unary_expression)
  // 17: POSTFIX(collate_expression)
  // 18: ATOM(literal_expression)
  // 19: ATOM(column_ref_expression)
  // 20: PREFIX(paren_expression)
  public static boolean expression(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "expression")) return false;
    addVariant(builder, "<expression>");
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, "<expression>");
    result = raise_function_expression(builder, level + 1);
    if (!result) result = case_expression(builder, level + 1);
    if (!result) result = exists_expression(builder, level + 1);
    if (!result) result = cast_expression(builder, level + 1);
    if (!result) result = function_call_expression(builder, level + 1);
    if (!result) result = unary_expression(builder, level + 1);
    if (!result) result = literal_expression(builder, level + 1);
    if (!result) result = column_ref_expression(builder, level + 1);
    if (!result) result = paren_expression(builder, level + 1);
    pinned = result;
    result = result && expression_0(builder, level + 1, priority);
    exit_section_(builder, level, marker, null, result, pinned, null);
    return result || pinned;
  }

  public static boolean expression_0(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "expression_0")) return false;
    boolean result = true;
    while (true) {
      Marker marker = enter_section_(builder, level, _LEFT_, null);
      if (priority < 1 && consumeTokenSmart(builder, OR)) {
        result = expression(builder, level, 1);
        exit_section_(builder, level, marker, OR_EXPRESSION, result, true, null);
      }
      else if (priority < 2 && consumeTokenSmart(builder, AND)) {
        result = expression(builder, level, 2);
        exit_section_(builder, level, marker, AND_EXPRESSION, result, true, null);
      }
      else if (priority < 5 && in_expression_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, IN_EXPRESSION, result, true, null);
      }
      else if (priority < 6 && isnull_expression_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, ISNULL_EXPRESSION, result, true, null);
      }
      else if (priority < 7 && like_expression_0(builder, level + 1)) {
        result = report_error_(builder, expression(builder, level, 7));
        result = like_expression_1(builder, level + 1) && result;
        exit_section_(builder, level, marker, LIKE_EXPRESSION, result, true, null);
      }
      else if (priority < 10 && equivalence_expression_0(builder, level + 1)) {
        result = expression(builder, level, 10);
        exit_section_(builder, level, marker, EQUIVALENCE_EXPRESSION, result, true, null);
      }
      else if (priority < 10 && between_expression_0(builder, level + 1)) {
        result = report_error_(builder, expression(builder, level, 10));
        result = between_expression_1(builder, level + 1) && result;
        exit_section_(builder, level, marker, BETWEEN_EXPRESSION, result, true, null);
      }
      else if (priority < 11 && comparison_expression_0(builder, level + 1)) {
        result = expression(builder, level, 11);
        exit_section_(builder, level, marker, COMPARISON_EXPRESSION, result, true, null);
      }
      else if (priority < 12 && bit_expression_0(builder, level + 1)) {
        result = expression(builder, level, 12);
        exit_section_(builder, level, marker, BIT_EXPRESSION, result, true, null);
      }
      else if (priority < 13 && add_expression_0(builder, level + 1)) {
        result = expression(builder, level, 13);
        exit_section_(builder, level, marker, ADD_EXPRESSION, result, true, null);
      }
      else if (priority < 14 && mul_expression_0(builder, level + 1)) {
        result = expression(builder, level, 14);
        exit_section_(builder, level, marker, MUL_EXPRESSION, result, true, null);
      }
      else if (priority < 15 && consumeTokenSmart(builder, CONCAT)) {
        result = expression(builder, level, 15);
        exit_section_(builder, level, marker, CONCAT_EXPRESSION, result, true, null);
      }
      else if (priority < 17 && collate_expression_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, COLLATE_EXPRESSION, result, true, null);
      }
      else {
        exit_section_(builder, level, marker, null, false, false, null);
        break;
      }
    }
    return result;
  }

  // RAISE '(' ( IGNORE | ( ROLLBACK | ABORT | FAIL ) ',' error_message ) ')'
  public static boolean raise_function_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "raise_function_expression")) return false;
    if (!nextTokenIsSmart(builder, RAISE)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokensSmart(builder, 0, RAISE, LPAREN);
    result = result && raise_function_expression_2(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, RAISE_FUNCTION_EXPRESSION, result);
    return result;
  }

  // IGNORE | ( ROLLBACK | ABORT | FAIL ) ',' error_message
  private static boolean raise_function_expression_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "raise_function_expression_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, IGNORE);
    if (!result) result = raise_function_expression_2_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ROLLBACK | ABORT | FAIL ) ',' error_message
  private static boolean raise_function_expression_2_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "raise_function_expression_2_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = raise_function_expression_2_1_0(builder, level + 1);
    result = result && consumeToken(builder, COMMA);
    result = result && error_message(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ROLLBACK | ABORT | FAIL
  private static boolean raise_function_expression_2_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "raise_function_expression_2_1_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, ROLLBACK);
    if (!result) result = consumeTokenSmart(builder, ABORT);
    if (!result) result = consumeTokenSmart(builder, FAIL);
    return result;
  }

  // CASE expression? ( WHEN expression THEN expression )+ ( ELSE expression )? END
  public static boolean case_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "case_expression")) return false;
    if (!nextTokenIsSmart(builder, CASE)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, CASE);
    result = result && case_expression_1(builder, level + 1);
    result = result && case_expression_2(builder, level + 1);
    result = result && case_expression_3(builder, level + 1);
    result = result && consumeToken(builder, END);
    exit_section_(builder, marker, CASE_EXPRESSION, result);
    return result;
  }

  // expression?
  private static boolean case_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "case_expression_1")) return false;
    expression(builder, level + 1, -1);
    return true;
  }

  // ( WHEN expression THEN expression )+
  private static boolean case_expression_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "case_expression_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = case_expression_2_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!case_expression_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "case_expression_2", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // WHEN expression THEN expression
  private static boolean case_expression_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "case_expression_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, WHEN);
    result = result && expression(builder, level + 1, -1);
    result = result && consumeToken(builder, THEN);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ELSE expression )?
  private static boolean case_expression_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "case_expression_3")) return false;
    case_expression_3_0(builder, level + 1);
    return true;
  }

  // ELSE expression
  private static boolean case_expression_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "case_expression_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, ELSE);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ( NOT )? EXISTS )? '(' expression_subquery ')'
  public static boolean exists_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exists_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, EXISTS_EXPRESSION, "<exists expression>");
    result = exists_expression_0(builder, level + 1);
    result = result && consumeToken(builder, LPAREN);
    result = result && expression_subquery(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( ( NOT )? EXISTS )?
  private static boolean exists_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exists_expression_0")) return false;
    exists_expression_0_0(builder, level + 1);
    return true;
  }

  // ( NOT )? EXISTS
  private static boolean exists_expression_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exists_expression_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = exists_expression_0_0_0(builder, level + 1);
    result = result && consumeToken(builder, EXISTS);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( NOT )?
  private static boolean exists_expression_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exists_expression_0_0_0")) return false;
    consumeTokenSmart(builder, NOT);
    return true;
  }

  // ( NOT )? IN ( '(' ( expression_subquery | expression ( ',' expression )* )? ')' | ( database_name '.' )? defined_table_name )
  private static boolean in_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = in_expression_0_0(builder, level + 1);
    result = result && consumeToken(builder, IN);
    result = result && in_expression_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( NOT )?
  private static boolean in_expression_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_0")) return false;
    consumeTokenSmart(builder, NOT);
    return true;
  }

  // '(' ( expression_subquery | expression ( ',' expression )* )? ')' | ( database_name '.' )? defined_table_name
  private static boolean in_expression_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = in_expression_0_2_0(builder, level + 1);
    if (!result) result = in_expression_0_2_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '(' ( expression_subquery | expression ( ',' expression )* )? ')'
  private static boolean in_expression_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LPAREN);
    result = result && in_expression_0_2_0_1(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( expression_subquery | expression ( ',' expression )* )?
  private static boolean in_expression_0_2_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_0_1")) return false;
    in_expression_0_2_0_1_0(builder, level + 1);
    return true;
  }

  // expression_subquery | expression ( ',' expression )*
  private static boolean in_expression_0_2_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expression_subquery(builder, level + 1);
    if (!result) result = in_expression_0_2_0_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // expression ( ',' expression )*
  private static boolean in_expression_0_2_0_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_0_1_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expression(builder, level + 1, -1);
    result = result && in_expression_0_2_0_1_0_1_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( ',' expression )*
  private static boolean in_expression_0_2_0_1_0_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_0_1_0_1_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!in_expression_0_2_0_1_0_1_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "in_expression_0_2_0_1_0_1_1", pos)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean in_expression_0_2_0_1_0_1_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_0_1_0_1_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COMMA);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( database_name '.' )? defined_table_name
  private static boolean in_expression_0_2_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = in_expression_0_2_1_0(builder, level + 1);
    result = result && defined_table_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( database_name '.' )?
  private static boolean in_expression_0_2_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_1_0")) return false;
    in_expression_0_2_1_0_0(builder, level + 1);
    return true;
  }

  // database_name '.'
  private static boolean in_expression_0_2_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "in_expression_0_2_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = database_name(builder, level + 1);
    result = result && consumeToken(builder, DOT);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ISNULL | NOTNULL | NOT NULL
  private static boolean isnull_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "isnull_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, ISNULL);
    if (!result) result = consumeTokenSmart(builder, NOTNULL);
    if (!result) result = parseTokensSmart(builder, 0, NOT, NULL);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // NOT? ( LIKE | GLOB | REGEXP | MATCH )
  private static boolean like_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "like_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = like_expression_0_0(builder, level + 1);
    result = result && like_expression_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // NOT?
  private static boolean like_expression_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "like_expression_0_0")) return false;
    consumeTokenSmart(builder, NOT);
    return true;
  }

  // LIKE | GLOB | REGEXP | MATCH
  private static boolean like_expression_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "like_expression_0_1")) return false;
    boolean result;
    result = consumeTokenSmart(builder, LIKE);
    if (!result) result = consumeTokenSmart(builder, GLOB);
    if (!result) result = consumeTokenSmart(builder, REGEXP);
    if (!result) result = consumeTokenSmart(builder, MATCH);
    return result;
  }

  // ( ESCAPE expression )?
  private static boolean like_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "like_expression_1")) return false;
    like_expression_1_0(builder, level + 1);
    return true;
  }

  // ESCAPE expression
  private static boolean like_expression_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "like_expression_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, ESCAPE);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean cast_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "cast_expression")) return false;
    if (!nextTokenIsSmart(builder, CAST)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = parseTokensSmart(builder, 0, CAST, LPAREN);
    pinned = result;
    result = pinned && expression(builder, level, 8);
    result = pinned && report_error_(builder, cast_expression_1(builder, level + 1)) && result;
    exit_section_(builder, level, marker, CAST_EXPRESSION, result, pinned, null);
    return result || pinned;
  }

  // AS type_name ')'
  private static boolean cast_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "cast_expression_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, AS);
    result = result && type_name(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // function_name '(' ( ( DISTINCT )? expression ( ',' expression )* | '*' )? ')'
  public static boolean function_call_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FUNCTION_CALL_EXPRESSION, "<function call expression>");
    result = function_name(builder, level + 1);
    result = result && consumeToken(builder, LPAREN);
    result = result && function_call_expression_2(builder, level + 1);
    result = result && consumeToken(builder, RPAREN);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ( ( DISTINCT )? expression ( ',' expression )* | '*' )?
  private static boolean function_call_expression_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression_2")) return false;
    function_call_expression_2_0(builder, level + 1);
    return true;
  }

  // ( DISTINCT )? expression ( ',' expression )* | '*'
  private static boolean function_call_expression_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = function_call_expression_2_0_0(builder, level + 1);
    if (!result) result = consumeTokenSmart(builder, STAR);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( DISTINCT )? expression ( ',' expression )*
  private static boolean function_call_expression_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression_2_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = function_call_expression_2_0_0_0(builder, level + 1);
    result = result && expression(builder, level + 1, -1);
    result = result && function_call_expression_2_0_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ( DISTINCT )?
  private static boolean function_call_expression_2_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression_2_0_0_0")) return false;
    consumeTokenSmart(builder, DISTINCT);
    return true;
  }

  // ( ',' expression )*
  private static boolean function_call_expression_2_0_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression_2_0_0_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!function_call_expression_2_0_0_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "function_call_expression_2_0_0_2", pos)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean function_call_expression_2_0_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_call_expression_2_0_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COMMA);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '==' | '=' | '!=' | '<>' | IS NOT?
  private static boolean equivalence_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "equivalence_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, EQEQ);
    if (!result) result = consumeTokenSmart(builder, EQ);
    if (!result) result = consumeTokenSmart(builder, NOT_EQ);
    if (!result) result = consumeTokenSmart(builder, UNEQ);
    if (!result) result = equivalence_expression_0_4(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // IS NOT?
  private static boolean equivalence_expression_0_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "equivalence_expression_0_4")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, IS);
    result = result && equivalence_expression_0_4_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // NOT?
  private static boolean equivalence_expression_0_4_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "equivalence_expression_0_4_1")) return false;
    consumeTokenSmart(builder, NOT);
    return true;
  }

  // NOT? BETWEEN
  private static boolean between_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "between_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = between_expression_0_0(builder, level + 1);
    result = result && consumeToken(builder, BETWEEN);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // NOT?
  private static boolean between_expression_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "between_expression_0_0")) return false;
    consumeTokenSmart(builder, NOT);
    return true;
  }

  // AND expression
  private static boolean between_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "between_expression_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, AND);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '<' | '<=' | '>' | '>='
  private static boolean comparison_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comparison_expression_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, LT);
    if (!result) result = consumeTokenSmart(builder, LTE);
    if (!result) result = consumeTokenSmart(builder, GT);
    if (!result) result = consumeTokenSmart(builder, GTE);
    return result;
  }

  // '<<' | '>>' | '&' | '|'
  private static boolean bit_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bit_expression_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, SHL);
    if (!result) result = consumeTokenSmart(builder, SHR);
    if (!result) result = consumeTokenSmart(builder, AMP);
    if (!result) result = consumeTokenSmart(builder, BAR);
    return result;
  }

  // '+' | '-'
  private static boolean add_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "add_expression_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, PLUS);
    if (!result) result = consumeTokenSmart(builder, MINUS);
    return result;
  }

  // '*' | '/' | '%'
  private static boolean mul_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "mul_expression_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, STAR);
    if (!result) result = consumeTokenSmart(builder, DIV);
    if (!result) result = consumeTokenSmart(builder, MOD);
    return result;
  }

  public static boolean unary_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unary_expression")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = unary_expression_0(builder, level + 1);
    pinned = result;
    result = pinned && expression(builder, level, 16);
    exit_section_(builder, level, marker, UNARY_EXPRESSION, result, pinned, null);
    return result || pinned;
  }

  // '-' | '+' | '~' | NOT
  private static boolean unary_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unary_expression_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, MINUS);
    if (!result) result = consumeTokenSmart(builder, PLUS);
    if (!result) result = consumeTokenSmart(builder, TILDE);
    if (!result) result = consumeTokenSmart(builder, NOT);
    return result;
  }

  // COLLATE collation_name
  private static boolean collate_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "collate_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COLLATE);
    result = result && collation_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // literal_value | bind_parameter
  public static boolean literal_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "literal_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, LITERAL_EXPRESSION, "<literal expression>");
    result = literal_value(builder, level + 1);
    if (!result) result = bind_parameter(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // database_name '.' selected_table_name '.' column_name
  //   | selected_table_name '.' column_name
  //   | column_name
  public static boolean column_ref_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "column_ref_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COLUMN_REF_EXPRESSION, "<column ref expression>");
    result = column_ref_expression_0(builder, level + 1);
    if (!result) result = column_ref_expression_1(builder, level + 1);
    if (!result) result = column_name(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // database_name '.' selected_table_name '.' column_name
  private static boolean column_ref_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "column_ref_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = database_name(builder, level + 1);
    result = result && consumeToken(builder, DOT);
    result = result && selected_table_name(builder, level + 1);
    result = result && consumeToken(builder, DOT);
    result = result && column_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // selected_table_name '.' column_name
  private static boolean column_ref_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "column_ref_expression_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = selected_table_name(builder, level + 1);
    result = result && consumeToken(builder, DOT);
    result = result && column_name(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean paren_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "paren_expression")) return false;
    if (!nextTokenIsSmart(builder, LPAREN)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = consumeTokenSmart(builder, LPAREN);
    pinned = result;
    result = pinned && expression(builder, level, -1);
    result = pinned && report_error_(builder, consumeToken(builder, RPAREN)) && result;
    exit_section_(builder, level, marker, PAREN_EXPRESSION, result, pinned, null);
    return result || pinned;
  }

}
