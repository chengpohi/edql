package com.github.chengpohi.esql.parser.psi;

import com.github.chengpohi.esql.refactor.ESQLNameElementManipulator;
import com.github.chengpohi.esql.referenence.ESQLParameterReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;

import java.util.Objects;

public class PsiImplUtil {
    public static PsiReference getReference(ESQLBindParameter bindParameter) {
        if (bindParameter.isColonNamedParameter()) {
            return new ESQLParameterReference(bindParameter);
        } else {
            return null;
        }
    }

    public static String getParameterNameAsString(ESQLBindParameter bindParameter) {
        if (isColonNamedParameter(bindParameter)) {
            String text = bindParameter.getText();
            return text.substring(1);
        } else {
            return null;
        }
    }

    public static Boolean isColonNamedParameter(ESQLBindParameter bindParameter) {
        ASTNode node = bindParameter.getNode().getFirstChildNode();
        if (Objects.isNull(node)) {
            return false;
        }
        return node.getElementType() == ESQLPsiTypes.NAMED_PARAMETER && node.getChars().toString().startsWith(":");
    }

    public static String getName(ESQLColumnDefinitionName columnDefinitionName) {
        return columnDefinitionName.nameAsString();
    }

    public static ESQLColumnDefinitionName setName(ESQLColumnDefinitionName columnDefinitionName, String newName) {
        new ESQLNameElementManipulator().handleContentChange(columnDefinitionName, newName);
        return columnDefinitionName;
    }

    public static String  getName(ESQLTableDefinitionName tableDefinitionName) {
        return tableDefinitionName.nameAsString();
    }

    public static ESQLTableDefinitionName setName(ESQLTableDefinitionName tableDefinitionName, String newName) {
        new ESQLNameElementManipulator().handleContentChange(tableDefinitionName, newName);
        return tableDefinitionName;
    }
}
