package com.github.chengpohi.edql;

import com.intellij.lang.Language;

public class EDQLLanguage extends Language {
    public static final EDQLLanguage INSTANCE = new EDQLLanguage();

    private EDQLLanguage() {
        super("EDQL");
    }
}
