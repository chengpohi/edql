package com.github.chengpohi.edql.parser.psi;

import com.github.chengpohi.edql.EDQLFileType;
import com.github.chengpohi.edql.EDQLLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class EDQLFile extends PsiFileBase {
    public EDQLFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, EDQLLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return EDQLFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "EDQL";
    }
}
