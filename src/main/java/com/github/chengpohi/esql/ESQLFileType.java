package com.github.chengpohi.esql;

import com.github.chengpohi.esql.parser.psi.ESQLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ESQLFileType extends LanguageFileType {

    public static final ESQLFileType INSTANCE = new ESQLFileType();

    protected ESQLFileType() {
        super(ESQLLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "ESQL";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "ESQL";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "esql";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
