package com.github.chengpohi.esql.parser;

import com.github.chengpohi.esql.parser.psi.ESQLPsiTypes;
import com.intellij.lexer.FlexAdapter;

public class ESQLLexer extends FlexAdapter {
    public ESQLLexer() {
        super(new _ESQLLexer());
    }

    public static Boolean needsQuoting(String name) {
        ESQLLexer lexer = new ESQLLexer();
        lexer.start(name);
        return lexer.getTokenType() != ESQLPsiTypes.IDENTIFIER || lexer.getTokenEnd() != lexer.getBufferEnd();
    }

    public static String getValidName(String name) {
        if (!needsQuoting(name)) {
            return name;
        }
        return "`" + name.replace("`", "``") + "`";
    }

    public static String getValidStringValue(String name) {
        // new We can't use the back tick character (;`) for strings because it's not a valid character to create strings
        if (!needsQuoting(name)) {
            return "'$name'";
        }
        return "'" + name.replace(" '", "' '") + "'";
    }
}
