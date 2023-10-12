package com.github.chengpohi.esql.parser.psi;

import com.github.chengpohi.esql.ESQLFileType;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class ESQLFile extends PsiFileBase{
    public ESQLFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ESQLLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return ESQLFileType.INSTANCE;
    }
}
