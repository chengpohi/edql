// This is a generated file. Not intended for manual editing.
package com.github.chengpohi.edql.parser.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.chengpohi.edql.parser.psi.impl.*;

public interface EDQLTypes {

  IElementType ACTION_EXPR = new EDQLElementType("ACTION_EXPR");
  IElementType ARG = new EDQLElementType("ARG");
  IElementType ARGS = new EDQLElementType("ARGS");
  IElementType ARR = new EDQLElementType("ARR");
  IElementType ARRCOMP = new EDQLElementType("ARRCOMP");
  IElementType ASSERT_STMT = new EDQLElementType("ASSERT_STMT");
  IElementType AUTH_EXPR = new EDQLElementType("AUTH_EXPR");
  IElementType BINARYOP = new EDQLElementType("BINARYOP");
  IElementType BIND = new EDQLElementType("BIND");
  IElementType BINSUFFIX = new EDQLElementType("BINSUFFIX");
  IElementType BOOL = new EDQLElementType("BOOL");
  IElementType COMMENT = new EDQLElementType("COMMENT");
  IElementType COMPSPEC = new EDQLElementType("COMPSPEC");
  IElementType EXPR = new EDQLElementType("EXPR");
  IElementType FIELD = new EDQLElementType("FIELD");
  IElementType FIELDNAME = new EDQLElementType("FIELDNAME");
  IElementType FOREACH = new EDQLElementType("FOREACH");
  IElementType FOR_EXPR = new EDQLElementType("FOR_EXPR");
  IElementType FUNCTION_BODY = new EDQLElementType("FUNCTION_BODY");
  IElementType FUNCTION_EXPR = new EDQLElementType("FUNCTION_EXPR");
  IElementType FUNCTION_INVOKE_EXPR = new EDQLElementType("FUNCTION_INVOKE_EXPR");
  IElementType H = new EDQLElementType("H");
  IElementType HEAD_EXPR = new EDQLElementType("HEAD_EXPR");
  IElementType HOST_EXPR = new EDQLElementType("HOST_EXPR");
  IElementType IDENTIFIER_0 = new EDQLElementType("IDENTIFIER_0");
  IElementType IFELSE = new EDQLElementType("IFELSE");
  IElementType IFSPEC = new EDQLElementType("IFSPEC");
  IElementType IMPORTOP = new EDQLElementType("IMPORTOP");
  IElementType LOADOP = new EDQLElementType("LOADOP");
  IElementType MAP_ITER = new EDQLElementType("MAP_ITER");
  IElementType MEMBER = new EDQLElementType("MEMBER");
  IElementType MEMBERS = new EDQLElementType("MEMBERS");
  IElementType METHOD = new EDQLElementType("METHOD");
  IElementType OBJ = new EDQLElementType("OBJ");
  IElementType OBJVAR = new EDQLElementType("OBJVAR");
  IElementType OUTERVAR = new EDQLElementType("OUTERVAR");
  IElementType PARAM = new EDQLElementType("PARAM");
  IElementType PARAMS = new EDQLElementType("PARAMS");
  IElementType PATH = new EDQLElementType("PATH");
  IElementType PATH_MATCH = new EDQLElementType("PATH_MATCH");
  IElementType RETURN_EXPR = new EDQLElementType("RETURN_EXPR");
  IElementType TIMEOUT_EXPR = new EDQLElementType("TIMEOUT_EXPR");
  IElementType UNARYOP = new EDQLElementType("UNARYOP");

  IElementType AND = new EDQLTokenType("AND");
  IElementType APIKEYID = new EDQLTokenType("ApiKeyId");
  IElementType APIKEYSECRET = new EDQLTokenType("ApiKeySecret");
  IElementType APISESSIONTOKEN = new EDQLTokenType("ApiSessionToken");
  IElementType ASSERT = new EDQLTokenType("ASSERT");
  IElementType ASTERISK = new EDQLTokenType("*");
  IElementType AUTH = new EDQLTokenType("Authorization");
  IElementType AWSREGION = new EDQLTokenType("AWSRegion");
  IElementType BAR = new EDQLTokenType("BAR");
  IElementType CARAT = new EDQLTokenType("CARAT");
  IElementType CLUSTER = new EDQLTokenType("cluster");
  IElementType COLON = new EDQLTokenType(":");
  IElementType COLON2 = new EDQLTokenType("COLON2");
  IElementType COLON3 = new EDQLTokenType("COLON3");
  IElementType COMMA = new EDQLTokenType(",");
  IElementType COUNT = new EDQLTokenType("count");
  IElementType DELETE = new EDQLTokenType("DELETE");
  IElementType DOLLAR = new EDQLTokenType("$");
  IElementType DOT = new EDQLTokenType(".");
  IElementType DOUBLE_AND = new EDQLTokenType("DOUBLE_AND");
  IElementType DOUBLE_BAR = new EDQLTokenType("DOUBLE_BAR");
  IElementType DOUBLE_EQUAL = new EDQLTokenType("DOUBLE_EQUAL");
  IElementType DOUBLE_GREATER = new EDQLTokenType("DOUBLE_GREATER");
  IElementType DOUBLE_LESS = new EDQLTokenType("DOUBLE_LESS");
  IElementType DOUBLE_QUOTED_STRING = new EDQLTokenType("DOUBLE_QUOTED_STRING");
  IElementType ELSE = new EDQLTokenType("ELSE");
  IElementType EQUAL = new EDQLTokenType("=");
  IElementType ERROR = new EDQLTokenType("ERROR");
  IElementType EXCLAMATION = new EDQLTokenType("EXCLAMATION");
  IElementType FALSE = new EDQLTokenType("false");
  IElementType FOR = new EDQLTokenType("for");
  IElementType FUNCTION = new EDQLTokenType("function");
  IElementType GET = new EDQLTokenType("GET");
  IElementType GREATER_EQUAL = new EDQLTokenType("GREATER_EQUAL");
  IElementType GREATER_THAN = new EDQLTokenType("GREATER_THAN");
  IElementType HEAD = new EDQLTokenType("HEAD");
  IElementType HOST = new EDQLTokenType("HOST");
  IElementType HOST_PATH = new EDQLTokenType("HOST_PATH");
  IElementType IDENTIFIER = new EDQLTokenType("IDENTIFIER");
  IElementType IF = new EDQLTokenType("IF");
  IElementType IMPORT = new EDQLTokenType("import");
  IElementType IN = new EDQLTokenType("in");
  IElementType KIBANA_HOST = new EDQLTokenType("KIBANA_HOST");
  IElementType LESS_EQUAL = new EDQLTokenType("LESS_EQUAL");
  IElementType LESS_THAN = new EDQLTokenType("LESS_THAN");
  IElementType LINE_COMMENT = new EDQLTokenType("LINE_COMMENT");
  IElementType LOAD = new EDQLTokenType("LOAD");
  IElementType L_BRACKET = new EDQLTokenType("[");
  IElementType L_CURLY = new EDQLTokenType("{");
  IElementType L_PAREN = new EDQLTokenType("(");
  IElementType MAPPING = new EDQLTokenType("->");
  IElementType MINUS = new EDQLTokenType("-");
  IElementType NEWLINE = new EDQLTokenType("NEWLINE");
  IElementType NODE = new EDQLTokenType("node");
  IElementType NOT_EQUAL = new EDQLTokenType("NOT_EQUAL");
  IElementType NULL = new EDQLTokenType("null");
  IElementType NUMBER = new EDQLTokenType("NUMBER");
  IElementType PASSWORD = new EDQLTokenType("Password");
  IElementType PERCENT = new EDQLTokenType("PERCENT");
  IElementType PLUS = new EDQLTokenType("+");
  IElementType POST = new EDQLTokenType("POST");
  IElementType PUT = new EDQLTokenType("PUT");
  IElementType QUERY = new EDQLTokenType("QUERY");
  IElementType RETURN = new EDQLTokenType("return");
  IElementType R_BRACKET = new EDQLTokenType("]");
  IElementType R_CURLY = new EDQLTokenType("}");
  IElementType R_PAREN = new EDQLTokenType(")");
  IElementType SELF = new EDQLTokenType("SELF");
  IElementType SEMICOLON = new EDQLTokenType(";");
  IElementType SINGLE_QUOTED_STRING = new EDQLTokenType("SINGLE_QUOTED_STRING");
  IElementType SLASH = new EDQLTokenType("/");
  IElementType SUPER = new EDQLTokenType("SUPER");
  IElementType THEN = new EDQLTokenType("THEN");
  IElementType TILDE = new EDQLTokenType("TILDE");
  IElementType TIMEOUT = new EDQLTokenType("Timeout");
  IElementType TRIPLE_BAR_QUOTED_STRING = new EDQLTokenType("TRIPLE_BAR_QUOTED_STRING");
  IElementType TRIPLE_QUOTED_STRING = new EDQLTokenType("TRIPLE_QUOTED_STRING");
  IElementType TRUE = new EDQLTokenType("true");
  IElementType USERNAME = new EDQLTokenType("Username");
  IElementType VAR = new EDQLTokenType("VAR");
  IElementType VERBATIM_DOUBLE_QUOTED_STRING = new EDQLTokenType("VERBATIM_DOUBLE_QUOTED_STRING");
  IElementType VERBATIM_SINGLE_QUOTED_STRING = new EDQLTokenType("VERBATIM_SINGLE_QUOTED_STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ACTION_EXPR) {
        return new EDQLActionExprImpl(node);
      }
      else if (type == ARG) {
        return new EDQLArgImpl(node);
      }
      else if (type == ARGS) {
        return new EDQLArgsImpl(node);
      }
      else if (type == ARR) {
        return new EDQLArrImpl(node);
      }
      else if (type == ARRCOMP) {
        return new EDQLArrcompImpl(node);
      }
      else if (type == ASSERT_STMT) {
        return new EDQLAssertStmtImpl(node);
      }
      else if (type == AUTH_EXPR) {
        return new EDQLAuthExprImpl(node);
      }
      else if (type == BINARYOP) {
        return new EDQLBinaryopImpl(node);
      }
      else if (type == BIND) {
        return new EDQLBindImpl(node);
      }
      else if (type == BINSUFFIX) {
        return new EDQLBinsuffixImpl(node);
      }
      else if (type == BOOL) {
        return new EDQLBoolImpl(node);
      }
      else if (type == COMMENT) {
        return new EDQLCommentImpl(node);
      }
      else if (type == COMPSPEC) {
        return new EDQLCompspecImpl(node);
      }
      else if (type == EXPR) {
        return new EDQLExprImpl(node);
      }
      else if (type == FIELD) {
        return new EDQLFieldImpl(node);
      }
      else if (type == FIELDNAME) {
        return new EDQLFieldnameImpl(node);
      }
      else if (type == FOREACH) {
        return new EDQLForeachImpl(node);
      }
      else if (type == FOR_EXPR) {
        return new EDQLForExprImpl(node);
      }
      else if (type == FUNCTION_BODY) {
        return new EDQLFunctionBodyImpl(node);
      }
      else if (type == FUNCTION_EXPR) {
        return new EDQLFunctionExprImpl(node);
      }
      else if (type == FUNCTION_INVOKE_EXPR) {
        return new EDQLFunctionInvokeExprImpl(node);
      }
      else if (type == H) {
        return new EDQLHImpl(node);
      }
      else if (type == HEAD_EXPR) {
        return new EDQLHeadExprImpl(node);
      }
      else if (type == HOST_EXPR) {
        return new EDQLHostExprImpl(node);
      }
      else if (type == IDENTIFIER_0) {
        return new EDQLIdentifier0Impl(node);
      }
      else if (type == IFELSE) {
        return new EDQLIfelseImpl(node);
      }
      else if (type == IFSPEC) {
        return new EDQLIfspecImpl(node);
      }
      else if (type == IMPORTOP) {
        return new EDQLImportopImpl(node);
      }
      else if (type == LOADOP) {
        return new EDQLLoadopImpl(node);
      }
      else if (type == MAP_ITER) {
        return new EDQLMapIterImpl(node);
      }
      else if (type == MEMBER) {
        return new EDQLMemberImpl(node);
      }
      else if (type == MEMBERS) {
        return new EDQLMembersImpl(node);
      }
      else if (type == METHOD) {
        return new EDQLMethodImpl(node);
      }
      else if (type == OBJ) {
        return new EDQLObjImpl(node);
      }
      else if (type == OBJVAR) {
        return new EDQLObjvarImpl(node);
      }
      else if (type == OUTERVAR) {
        return new EDQLOutervarImpl(node);
      }
      else if (type == PARAM) {
        return new EDQLParamImpl(node);
      }
      else if (type == PARAMS) {
        return new EDQLParamsImpl(node);
      }
      else if (type == PATH) {
        return new EDQLPathImpl(node);
      }
      else if (type == PATH_MATCH) {
        return new EDQLPathMatchImpl(node);
      }
      else if (type == RETURN_EXPR) {
        return new EDQLReturnExprImpl(node);
      }
      else if (type == TIMEOUT_EXPR) {
        return new EDQLTimeoutExprImpl(node);
      }
      else if (type == UNARYOP) {
        return new EDQLUnaryopImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
