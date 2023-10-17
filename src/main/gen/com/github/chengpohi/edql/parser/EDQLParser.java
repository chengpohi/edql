// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.chengpohi.edql.parser.psi.EDQLTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class EDQLParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean BOOL(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BOOL")) return false;
    if (!nextTokenIs(b, "<bool>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOL, "<bool>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (DOLLAR)? (IDENTIFIER | '/')
  public static boolean IDENTIFIER0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IDENTIFIER0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER_0, "<identifier 0>");
    r = IDENTIFIER0_0(b, l + 1);
    r = r && IDENTIFIER0_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOLLAR)?
  private static boolean IDENTIFIER0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IDENTIFIER0_0")) return false;
    consumeToken(b, DOLLAR);
    return true;
  }

  // IDENTIFIER | '/'
  private static boolean IDENTIFIER0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IDENTIFIER0_1")) return false;
    boolean r;
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, SLASH);
    return r;
  }

  /* ********************************************************** */
  // method path (QUERY)? (obj | arr | arrcomp)*
  public static boolean actionExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACTION_EXPR, "<action expr>");
    r = method(b, l + 1);
    r = r && path(b, l + 1);
    r = r && actionExpr_2(b, l + 1);
    r = r && actionExpr_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (QUERY)?
  private static boolean actionExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionExpr_2")) return false;
    consumeToken(b, QUERY);
    return true;
  }

  // (obj | arr | arrcomp)*
  private static boolean actionExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionExpr_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!actionExpr_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "actionExpr_3", c)) break;
    }
    return true;
  }

  // obj | arr | arrcomp
  private static boolean actionExpr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionExpr_3_0")) return false;
    boolean r;
    r = obj(b, l + 1);
    if (!r) r = arr(b, l + 1);
    if (!r) r = arrcomp(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (IDENTIFIER0 EQUAL)? expr
  public static boolean arg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARG, "<arg>");
    r = arg_0(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (IDENTIFIER0 EQUAL)?
  private static boolean arg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0")) return false;
    arg_0_0(b, l + 1);
    return true;
  }

  // IDENTIFIER0 EQUAL
  private static boolean arg_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = IDENTIFIER0(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // arg ( COMMA arg )* ( COMMA )?
  public static boolean args(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "args")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARGS, "<args>");
    r = arg(b, l + 1);
    r = r && args_1(b, l + 1);
    r = r && args_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( COMMA arg )*
  private static boolean args_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "args_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!args_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "args_1", c)) break;
    }
    return true;
  }

  // COMMA arg
  private static boolean args_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "args_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && arg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA )?
  private static boolean args_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "args_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // L_BRACKET (expr (COMMA expr)* COMMA?)? R_BRACKET mapIter?
  public static boolean arr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && arr_1(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    r = r && arr_3(b, l + 1);
    exit_section_(b, m, ARR, r);
    return r;
  }

  // (expr (COMMA expr)* COMMA?)?
  private static boolean arr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr_1")) return false;
    arr_1_0(b, l + 1);
    return true;
  }

  // expr (COMMA expr)* COMMA?
  private static boolean arr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && arr_1_0_1(b, l + 1);
    r = r && arr_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expr)*
  private static boolean arr_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arr_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arr_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean arr_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean arr_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr_1_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // mapIter?
  private static boolean arr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arr_3")) return false;
    mapIter(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // L_BRACKET expr COMMA? R_BRACKET
  public static boolean arrcomp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrcomp")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && expr(b, l + 1);
    r = r && arrcomp_2(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, ARRCOMP, r);
    return r;
  }

  // COMMA?
  private static boolean arrcomp_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrcomp_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // ASSERT expr ( COLON expr )?
  public static boolean assertStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assertStmt")) return false;
    if (!nextTokenIs(b, ASSERT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASSERT);
    r = r && expr(b, l + 1);
    r = r && assertStmt_2(b, l + 1);
    exit_section_(b, m, ASSERT_STMT, r);
    return r;
  }

  // ( COLON expr )?
  private static boolean assertStmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assertStmt_2")) return false;
    assertStmt_2_0(b, l + 1);
    return true;
  }

  // COLON expr
  private static boolean assertStmt_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assertStmt_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (AUTH (DOUBLE_QUOTED_STRING | IDENTIFIER0)) | (USERNAME (DOUBLE_QUOTED_STRING | IDENTIFIER0))
  // | (PASSWORD (DOUBLE_QUOTED_STRING | IDENTIFIER0)) | (APIKEYID (DOUBLE_QUOTED_STRING | IDENTIFIER0))
  // | (AWSREGION (DOUBLE_QUOTED_STRING | IDENTIFIER0)) | (APISESSIONTOKEN (DOUBLE_QUOTED_STRING | IDENTIFIER0))
  // | (APIKEYSECRET (DOUBLE_QUOTED_STRING | IDENTIFIER0))
  public static boolean authExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AUTH_EXPR, "<auth expr>");
    r = authExpr_0(b, l + 1);
    if (!r) r = authExpr_1(b, l + 1);
    if (!r) r = authExpr_2(b, l + 1);
    if (!r) r = authExpr_3(b, l + 1);
    if (!r) r = authExpr_4(b, l + 1);
    if (!r) r = authExpr_5(b, l + 1);
    if (!r) r = authExpr_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AUTH (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AUTH);
    r = r && authExpr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_0_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  // USERNAME (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USERNAME);
    r = r && authExpr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_1_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  // PASSWORD (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PASSWORD);
    r = r && authExpr_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_2_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  // APIKEYID (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, APIKEYID);
    r = r && authExpr_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_3_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  // AWSREGION (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AWSREGION);
    r = r && authExpr_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_4_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  // APISESSIONTOKEN (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, APISESSIONTOKEN);
    r = r && authExpr_5_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_5_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_5_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  // APIKEYSECRET (DOUBLE_QUOTED_STRING | IDENTIFIER0)
  private static boolean authExpr_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, APIKEYSECRET);
    r = r && authExpr_6_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | IDENTIFIER0
  private static boolean authExpr_6_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "authExpr_6_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = IDENTIFIER0(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ASTERISK | SLASH | PERCENT | PLUS | MINUS | DOUBLE_LESS | DOUBLE_GREATER | LESS_THAN | LESS_EQUAL | GREATER_THAN | GREATER_EQUAL | DOUBLE_EQUAL | NOT_EQUAL | IN | AND | CARAT | BAR | DOUBLE_AND | DOUBLE_BAR
  public static boolean binaryop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryop")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARYOP, "<binaryop>");
    r = consumeToken(b, ASTERISK);
    if (!r) r = consumeToken(b, SLASH);
    if (!r) r = consumeToken(b, PERCENT);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, DOUBLE_LESS);
    if (!r) r = consumeToken(b, DOUBLE_GREATER);
    if (!r) r = consumeToken(b, LESS_THAN);
    if (!r) r = consumeToken(b, LESS_EQUAL);
    if (!r) r = consumeToken(b, GREATER_THAN);
    if (!r) r = consumeToken(b, GREATER_EQUAL);
    if (!r) r = consumeToken(b, DOUBLE_EQUAL);
    if (!r) r = consumeToken(b, NOT_EQUAL);
    if (!r) r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, AND);
    if (!r) r = consumeToken(b, CARAT);
    if (!r) r = consumeToken(b, BAR);
    if (!r) r = consumeToken(b, DOUBLE_AND);
    if (!r) r = consumeToken(b, DOUBLE_BAR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER0 EQUAL expr mapIter? (binsuffix)* {
  // }
  public static boolean bind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bind")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BIND, "<bind>");
    r = IDENTIFIER0(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    r = r && bind_3(b, l + 1);
    r = r && bind_4(b, l + 1);
    r = r && bind_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // mapIter?
  private static boolean bind_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bind_3")) return false;
    mapIter(b, l + 1);
    return true;
  }

  // (binsuffix)*
  private static boolean bind_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bind_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!bind_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bind_4", c)) break;
    }
    return true;
  }

  // (binsuffix)
  private static boolean bind_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bind_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binsuffix(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // {
  // }
  private static boolean bind_5(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // binaryop expr
  public static boolean binsuffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binsuffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINSUFFIX, "<binsuffix>");
    r = binaryop(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LINE_COMMENT
  public static boolean comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment")) return false;
    if (!nextTokenIs(b, LINE_COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LINE_COMMENT);
    exit_section_(b, m, COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // ( ifspec )*
  public static boolean compspec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compspec")) return false;
    Marker m = enter_section_(b, l, _NONE_, COMPSPEC, "<compspec>");
    while (true) {
      int c = current_position_(b);
      if (!compspec_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "compspec", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ( ifspec )
  private static boolean compspec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compspec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifspec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // actionExpr | comment
  // |   binsuffix
  // |	obj
  // |   mapIter
  // |   foreach
  // |	arr
  // |	arrcomp
  // |   forExpr
  // |	functionExpr
  // |	functionInvokeExpr
  // |	SUPER DOT IDENTIFIER0
  // |	SUPER L_BRACKET expr R_BRACKET
  // |   outervar
  // |	ifelse
  // |	L_PAREN expr* R_PAREN
  // |	unaryop expr
  // |	assertStmt SEMICOLON expr
  // |	ERROR expr
  // |	IDENTIFIER0
  // |   NUMBER
  // |   BOOL
  // |   SELF
  // |   SINGLE_QUOTED_STRING
  // |   TRIPLE_QUOTED_STRING
  // |   DOUBLE_QUOTED_STRING
  // |   VERBATIM_SINGLE_QUOTED_STRING
  // |   VERBATIM_DOUBLE_QUOTED_STRING
  // |   TRIPLE_BAR_QUOTED_STRING
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR, "<expr>");
    r = actionExpr(b, l + 1);
    if (!r) r = comment(b, l + 1);
    if (!r) r = binsuffix(b, l + 1);
    if (!r) r = obj(b, l + 1);
    if (!r) r = mapIter(b, l + 1);
    if (!r) r = foreach(b, l + 1);
    if (!r) r = arr(b, l + 1);
    if (!r) r = arrcomp(b, l + 1);
    if (!r) r = forExpr(b, l + 1);
    if (!r) r = functionExpr(b, l + 1);
    if (!r) r = functionInvokeExpr(b, l + 1);
    if (!r) r = expr_11(b, l + 1);
    if (!r) r = expr_12(b, l + 1);
    if (!r) r = outervar(b, l + 1);
    if (!r) r = ifelse(b, l + 1);
    if (!r) r = expr_15(b, l + 1);
    if (!r) r = expr_16(b, l + 1);
    if (!r) r = expr_17(b, l + 1);
    if (!r) r = expr_18(b, l + 1);
    if (!r) r = IDENTIFIER0(b, l + 1);
    if (!r) r = consumeToken(b, NUMBER);
    if (!r) r = BOOL(b, l + 1);
    if (!r) r = consumeToken(b, SELF);
    if (!r) r = consumeToken(b, SINGLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, TRIPLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, VERBATIM_SINGLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, VERBATIM_DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, TRIPLE_BAR_QUOTED_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SUPER DOT IDENTIFIER0
  private static boolean expr_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_11")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SUPER, DOT);
    r = r && IDENTIFIER0(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SUPER L_BRACKET expr R_BRACKET
  private static boolean expr_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_12")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SUPER, L_BRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // L_PAREN expr* R_PAREN
  private static boolean expr_15(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_15")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PAREN);
    r = r && expr_15_1(b, l + 1);
    r = r && consumeToken(b, R_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr*
  private static boolean expr_15_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_15_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_15_1", c)) break;
    }
    return true;
  }

  // unaryop expr
  private static boolean expr_16(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_16")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unaryop(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // assertStmt SEMICOLON expr
  private static boolean expr_17(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_17")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = assertStmt(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ERROR expr
  private static boolean expr_18(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_18")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ERROR);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // fieldname ( PLUS )? h expr | fieldname L_PAREN ( params )? R_PAREN h expr
  public static boolean field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD, "<field>");
    r = field_0(b, l + 1);
    if (!r) r = field_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // fieldname ( PLUS )? h expr
  private static boolean field_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fieldname(b, l + 1);
    r = r && field_0_1(b, l + 1);
    r = r && h(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( PLUS )?
  private static boolean field_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_0_1")) return false;
    consumeToken(b, PLUS);
    return true;
  }

  // fieldname L_PAREN ( params )? R_PAREN h expr
  private static boolean field_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fieldname(b, l + 1);
    r = r && consumeToken(b, L_PAREN);
    r = r && field_1_2(b, l + 1);
    r = r && consumeToken(b, R_PAREN);
    r = r && h(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( params )?
  private static boolean field_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_1_2")) return false;
    field_1_2_0(b, l + 1);
    return true;
  }

  // ( params )
  private static boolean field_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = params(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER0
  // | DOUBLE_QUOTED_STRING
  // | SINGLE_QUOTED_STRING
  // | VERBATIM_DOUBLE_QUOTED_STRING
  // | VERBATIM_SINGLE_QUOTED_STRING
  // | TRIPLE_BAR_QUOTED_STRING
  // | L_BRACKET expr R_BRACKET
  public static boolean fieldname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldname")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELDNAME, "<fieldname>");
    r = IDENTIFIER0(b, l + 1);
    if (!r) r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, SINGLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, VERBATIM_DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, VERBATIM_SINGLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, TRIPLE_BAR_QUOTED_STRING);
    if (!r) r = fieldname_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // L_BRACKET expr R_BRACKET
  private static boolean fieldname_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldname_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FOR L_PAREN IDENTIFIER0 IN expr R_PAREN L_CURLY functionBody R_CURLY
  public static boolean forExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forExpr")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FOR, L_PAREN);
    r = r && IDENTIFIER0(b, l + 1);
    r = r && consumeToken(b, IN);
    r = r && expr(b, l + 1);
    r = r && consumeTokens(b, 0, R_PAREN, L_CURLY);
    r = r && functionBody(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, m, FOR_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER0 '.foreach' L_CURLY 'it' MAPPING L_CURLY expr* returnExpr? R_CURLY R_CURLY
  public static boolean foreach(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOREACH, "<foreach>");
    r = IDENTIFIER0(b, l + 1);
    r = r && consumeToken(b, ".foreach");
    r = r && consumeToken(b, L_CURLY);
    r = r && consumeToken(b, "it");
    r = r && consumeTokens(b, 0, MAPPING, L_CURLY);
    r = r && foreach_6(b, l + 1);
    r = r && foreach_7(b, l + 1);
    r = r && consumeTokens(b, 0, R_CURLY, R_CURLY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr*
  private static boolean foreach_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "foreach_6", c)) break;
    }
    return true;
  }

  // returnExpr?
  private static boolean foreach_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_7")) return false;
    returnExpr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (expr)* returnExpr?
  public static boolean functionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_BODY, "<function body>");
    r = functionBody_0(b, l + 1);
    r = r && functionBody_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (expr)*
  private static boolean functionBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionBody_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!functionBody_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "functionBody_0", c)) break;
    }
    return true;
  }

  // (expr)
  private static boolean functionBody_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionBody_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // returnExpr?
  private static boolean functionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionBody_1")) return false;
    returnExpr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // FUNCTION IDENTIFIER0 L_PAREN ( params )? R_PAREN L_CURLY functionBody R_CURLY
  public static boolean functionExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionExpr")) return false;
    if (!nextTokenIs(b, FUNCTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FUNCTION);
    r = r && IDENTIFIER0(b, l + 1);
    r = r && consumeToken(b, L_PAREN);
    r = r && functionExpr_3(b, l + 1);
    r = r && consumeTokens(b, 0, R_PAREN, L_CURLY);
    r = r && functionBody(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, m, FUNCTION_EXPR, r);
    return r;
  }

  // ( params )?
  private static boolean functionExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionExpr_3")) return false;
    functionExpr_3_0(b, l + 1);
    return true;
  }

  // ( params )
  private static boolean functionExpr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionExpr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = params(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER0 L_PAREN (expr* (COMMA expr*)* COMMA?)? R_PAREN mapIter?
  public static boolean functionInvokeExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_INVOKE_EXPR, "<function invoke expr>");
    r = IDENTIFIER0(b, l + 1);
    r = r && consumeToken(b, L_PAREN);
    r = r && functionInvokeExpr_2(b, l + 1);
    r = r && consumeToken(b, R_PAREN);
    r = r && functionInvokeExpr_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (expr* (COMMA expr*)* COMMA?)?
  private static boolean functionInvokeExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2")) return false;
    functionInvokeExpr_2_0(b, l + 1);
    return true;
  }

  // expr* (COMMA expr*)* COMMA?
  private static boolean functionInvokeExpr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = functionInvokeExpr_2_0_0(b, l + 1);
    r = r && functionInvokeExpr_2_0_1(b, l + 1);
    r = r && functionInvokeExpr_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr*
  private static boolean functionInvokeExpr_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "functionInvokeExpr_2_0_0", c)) break;
    }
    return true;
  }

  // (COMMA expr*)*
  private static boolean functionInvokeExpr_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!functionInvokeExpr_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "functionInvokeExpr_2_0_1", c)) break;
    }
    return true;
  }

  // COMMA expr*
  private static boolean functionInvokeExpr_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && functionInvokeExpr_2_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr*
  private static boolean functionInvokeExpr_2_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "functionInvokeExpr_2_0_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA?
  private static boolean functionInvokeExpr_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_2_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // mapIter?
  private static boolean functionInvokeExpr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionInvokeExpr_4")) return false;
    mapIter(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // COLON | COLON2 | COLON3
  public static boolean h(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "h")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, H, "<h>");
    r = consumeToken(b, COLON);
    if (!r) r = consumeToken(b, COLON2);
    if (!r) r = consumeToken(b, COLON3);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // hostExpr | authExpr | timeoutExpr
  public static boolean headExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEAD_EXPR, "<head expr>");
    r = hostExpr(b, l + 1);
    if (!r) r = authExpr(b, l + 1);
    if (!r) r = timeoutExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (HOST | KIBANA_HOST) HOST_PATH
  public static boolean hostExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hostExpr")) return false;
    if (!nextTokenIs(b, "<host expr>", HOST, KIBANA_HOST)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HOST_EXPR, "<host expr>");
    r = hostExpr_0(b, l + 1);
    r = r && consumeToken(b, HOST_PATH);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // HOST | KIBANA_HOST
  private static boolean hostExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hostExpr_0")) return false;
    boolean r;
    r = consumeToken(b, HOST);
    if (!r) r = consumeToken(b, KIBANA_HOST);
    return r;
  }

  /* ********************************************************** */
  // IF expr THEN expr ( ELSE expr )?
  public static boolean ifelse(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifelse")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && expr(b, l + 1);
    r = r && ifelse_4(b, l + 1);
    exit_section_(b, m, IFELSE, r);
    return r;
  }

  // ( ELSE expr )?
  private static boolean ifelse_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifelse_4")) return false;
    ifelse_4_0(b, l + 1);
    return true;
  }

  // ELSE expr
  private static boolean ifelse_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifelse_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IF expr
  public static boolean ifspec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifspec")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && expr(b, l + 1);
    exit_section_(b, m, IFSPEC, r);
    return r;
  }

  /* ********************************************************** */
  // IMPORT DOUBLE_QUOTED_STRING
  public static boolean importop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importop")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IMPORT, DOUBLE_QUOTED_STRING);
    exit_section_(b, m, IMPORTOP, r);
    return r;
  }

  /* ********************************************************** */
  // (LOAD (DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING)) {
  // }
  public static boolean loadop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loadop")) return false;
    if (!nextTokenIs(b, LOAD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = loadop_0(b, l + 1);
    r = r && loadop_1(b, l + 1);
    exit_section_(b, m, LOADOP, r);
    return r;
  }

  // LOAD (DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING)
  private static boolean loadop_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loadop_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LOAD);
    r = r && loadop_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING
  private static boolean loadop_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loadop_0_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, SINGLE_QUOTED_STRING);
    return r;
  }

  // {
  // }
  private static boolean loadop_1(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // '.map' L_CURLY 'it' MAPPING L_CURLY expr* returnExpr? R_CURLY R_CURLY
  public static boolean mapIter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapIter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAP_ITER, "<map iter>");
    r = consumeToken(b, ".map");
    r = r && consumeToken(b, L_CURLY);
    r = r && consumeToken(b, "it");
    r = r && consumeTokens(b, 0, MAPPING, L_CURLY);
    r = r && mapIter_5(b, l + 1);
    r = r && mapIter_6(b, l + 1);
    r = r && consumeTokens(b, 0, R_CURLY, R_CURLY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr*
  private static boolean mapIter_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapIter_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mapIter_5", c)) break;
    }
    return true;
  }

  // returnExpr?
  private static boolean mapIter_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapIter_6")) return false;
    returnExpr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // objvar | assertStmt | field
  public static boolean member(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MEMBER, "<member>");
    r = objvar(b, l + 1);
    if (!r) r = assertStmt(b, l + 1);
    if (!r) r = field(b, l + 1);
    exit_section_(b, l, m, r, false, EDQLParser::member_recover);
    return r;
  }

  /* ********************************************************** */
  // !('}')
  static boolean member_list_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_list_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, R_CURLY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // !(',' | '}')
  static boolean member_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !member_recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ',' | '}'
  private static boolean member_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_recover_0")) return false;
    boolean r;
    r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, R_CURLY);
    return r;
  }

  /* ********************************************************** */
  // member ( COMMA member )* ( COMMA )?
  public static boolean members(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "members")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MEMBERS, "<members>");
    r = member(b, l + 1);
    r = r && members_1(b, l + 1);
    r = r && members_2(b, l + 1);
    exit_section_(b, l, m, r, false, EDQLParser::member_list_recover);
    return r;
  }

  // ( COMMA member )*
  private static boolean members_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "members_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!members_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "members_1", c)) break;
    }
    return true;
  }

  // COMMA member
  private static boolean members_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "members_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && member(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA )?
  private static boolean members_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "members_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // POST | DELETE | PUT | GET | HEAD | CLUSTER | NODE | COUNT
  public static boolean method(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD, "<method>");
    r = consumeToken(b, POST);
    if (!r) r = consumeToken(b, DELETE);
    if (!r) r = consumeToken(b, PUT);
    if (!r) r = consumeToken(b, GET);
    if (!r) r = consumeToken(b, HEAD);
    if (!r) r = consumeToken(b, CLUSTER);
    if (!r) r = consumeToken(b, NODE);
    if (!r) r = consumeToken(b, COUNT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // L_CURLY members? R_CURLY
  public static boolean obj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "obj")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY);
    r = r && obj_1(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, m, OBJ, r);
    return r;
  }

  // members?
  private static boolean obj_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "obj_1")) return false;
    members(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // VAR bind
  public static boolean objvar(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objvar")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VAR);
    r = r && bind(b, l + 1);
    exit_section_(b, m, OBJVAR, r);
    return r;
  }

  /* ********************************************************** */
  // VAR bind
  public static boolean outervar(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "outervar")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VAR);
    r = r && bind(b, l + 1);
    exit_section_(b, m, OUTERVAR, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER0 (EQUAL expr)?
  public static boolean param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM, "<param>");
    r = IDENTIFIER0(b, l + 1);
    r = r && param_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (EQUAL expr)?
  private static boolean param_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_1")) return false;
    param_1_0(b, l + 1);
    return true;
  }

  // EQUAL expr
  private static boolean param_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // param (COMMA param)* ( COMMA )?
  public static boolean params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMS, "<params>");
    r = param(b, l + 1);
    r = r && params_1(b, l + 1);
    r = r && params_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA param)*
  private static boolean params_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!params_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "params_1", c)) break;
    }
    return true;
  }

  // COMMA param
  private static boolean params_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && param(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA )?
  private static boolean params_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // DOT? IDENTIFIER0 (DOT IDENTIFIER0)* (ASTERISK IDENTIFIER0? ASTERISK?)*
  public static boolean path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH, "<path>");
    r = path_0(b, l + 1);
    r = r && IDENTIFIER0(b, l + 1);
    r = r && path_2(b, l + 1);
    r = r && path_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DOT?
  private static boolean path_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_0")) return false;
    consumeToken(b, DOT);
    return true;
  }

  // (DOT IDENTIFIER0)*
  private static boolean path_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_2", c)) break;
    }
    return true;
  }

  // DOT IDENTIFIER0
  private static boolean path_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && IDENTIFIER0(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ASTERISK IDENTIFIER0? ASTERISK?)*
  private static boolean path_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_3", c)) break;
    }
    return true;
  }

  // ASTERISK IDENTIFIER0? ASTERISK?
  private static boolean path_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASTERISK);
    r = r && path_3_0_1(b, l + 1);
    r = r && path_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENTIFIER0?
  private static boolean path_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_3_0_1")) return false;
    IDENTIFIER0(b, l + 1);
    return true;
  }

  // ASTERISK?
  private static boolean path_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_3_0_2")) return false;
    consumeToken(b, ASTERISK);
    return true;
  }

  /* ********************************************************** */
  // RETURN expr (binsuffix)* mapIter?
  public static boolean returnExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RETURN);
    r = r && expr(b, l + 1);
    r = r && returnExpr_2(b, l + 1);
    r = r && returnExpr_3(b, l + 1);
    exit_section_(b, m, RETURN_EXPR, r);
    return r;
  }

  // (binsuffix)*
  private static boolean returnExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!returnExpr_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "returnExpr_2", c)) break;
    }
    return true;
  }

  // (binsuffix)
  private static boolean returnExpr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = binsuffix(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // mapIter?
  private static boolean returnExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_3")) return false;
    mapIter(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (importop | headExpr | comment)* (expr)*
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = root_0(b, l + 1);
    r = r && root_1(b, l + 1);
    register_hook_(b, WS_BINDERS, null, null);
    exit_section_(b, m, null, r);
    return r;
  }

  // (importop | headExpr | comment)*
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_0", c)) break;
    }
    return true;
  }

  // importop | headExpr | comment
  private static boolean root_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0_0")) return false;
    boolean r;
    r = importop(b, l + 1);
    if (!r) r = headExpr(b, l + 1);
    if (!r) r = comment(b, l + 1);
    return r;
  }

  // (expr)*
  private static boolean root_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_1", c)) break;
    }
    return true;
  }

  // (expr)
  private static boolean root_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TIMEOUT NUMBER
  public static boolean timeoutExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timeoutExpr")) return false;
    if (!nextTokenIs(b, TIMEOUT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TIMEOUT, NUMBER);
    exit_section_(b, m, TIMEOUT_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // MINUS | PLUS | EXCLAMATION | TILDE
  public static boolean unaryop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryop")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARYOP, "<unaryop>");
    r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, EXCLAMATION);
    if (!r) r = consumeToken(b, TILDE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
