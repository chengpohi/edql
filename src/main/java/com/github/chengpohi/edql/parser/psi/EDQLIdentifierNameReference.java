package com.github.chengpohi.edql.parser.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class EDQLIdentifierNameReference implements PsiReference {
    private final EDQLIdentifier0 myProperty;

    public EDQLIdentifierNameReference(@NotNull EDQLIdentifier0 property) {
        myProperty = property;
    }

    @NotNull
    @Override
    public PsiElement getElement() {
        return myProperty;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
//    final @NotNull EDQLIdentifier0 nameElement = myProperty.getIdentifier0();
        // Either value of string with quotes stripped or element's text as is
        return ElementManipulators.getValueTextRange(myProperty);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return myProperty;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return myProperty.getName();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myProperty.setName(newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (!(element instanceof EDQLIdentifier0)) {
            return false;
        }
        EDQLIdentifier0 otherProperty = (EDQLIdentifier0) element;
        PsiElement selfResolve = resolve();
        String name = otherProperty.getName();
        Optional<String> firstName = firstPath(name);
        Optional<String> targetName = firstPath(getCanonicalText());

        if (selfResolve == otherProperty) {
            return false;
        }

        if (firstName.isEmpty() || targetName.isEmpty()) {
            return false;
        }

        if (targetName.get().equals("$" + firstName.get())) {
            return true;
        }

        return targetName.get().equals(firstName.get());
    }

    private Optional<String> firstPath(String canonicalText) {
        if (StringUtils.isBlank(canonicalText)) {
            return Optional.empty();
        }
        return Arrays.stream(canonicalText.split("/"))
                .filter(StringUtils::isNotBlank)
                .findFirst();
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
