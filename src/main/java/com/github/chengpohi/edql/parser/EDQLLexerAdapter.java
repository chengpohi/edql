package com.github.chengpohi.edql.parser;

import com.intellij.lexer.FlexAdapter;

public class EDQLLexerAdapter extends FlexAdapter {
    public EDQLLexerAdapter() {
        super(new _EDQLLexer(null));
    }
}
