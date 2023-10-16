package com.github.chengpohi.edql;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EDQLFileType extends LanguageFileType {
    public static final EDQLFileType INSTANCE = new EDQLFileType();

    //    public static final @NotNull Icon MINI_ICON = AllIcons.FileTypes.Json;

    private EDQLFileType() {
        super(EDQLLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "EDQL";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "EDQL script language";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "edql";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

}
