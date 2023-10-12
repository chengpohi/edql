package com.github.chengpohi.esql.parser.psi;

import com.intellij.lang.Language;

public class ESQLLanguage extends Language {
    public static final ESQLLanguage INSTANCE = new ESQLLanguage();

    private ESQLLanguage() {
        super("ESQL");
    }
}
