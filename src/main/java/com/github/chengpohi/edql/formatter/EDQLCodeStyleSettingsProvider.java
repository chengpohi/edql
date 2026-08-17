package com.github.chengpohi.edql.formatter;

import com.github.chengpohi.edql.EDQLLanguage;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;

public class EDQLCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    @Override
    public String getConfigurableId() {
        return "EDQL";
    }

    @Override
    public Language getLanguage() {
        return EDQLLanguage.INSTANCE;
    }
}
