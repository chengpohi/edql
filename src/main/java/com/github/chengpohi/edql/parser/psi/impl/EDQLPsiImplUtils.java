package com.github.chengpohi.edql.parser.psi.impl;

import com.github.chengpohi.edql.EDQLFileType;
import com.github.chengpohi.edql.parser.psi.EDQLField;
import com.github.chengpohi.edql.parser.psi.EDQLFile;
import com.github.chengpohi.edql.parser.psi.EDQLFunctionInvokeExpr;
import com.github.chengpohi.edql.parser.psi.EDQLIdentifier0;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public final class EDQLPsiImplUtils {
    public static String getKey(EDQLIdentifier0 element) {
        return element.getText();
    }

    public static String getValue(EDQLIdentifier0 element) {
        return element.getText();
    }

    public static String getName(EDQLIdentifier0 element) {
        return getKey(element);
    }

    public static PsiElement setName(EDQLIdentifier0 element, String newName) {
        if (StringUtils.startsWith(element.getText(), "$")) {
            element.replace(createProperty(element.getProject(), "var " + "$" + newName + "=1"));
        } else {
            element.replace(createProperty(element.getProject(), "var " + newName + "=1"));
        }
        return element;
    }

    public static EDQLIdentifier0 createProperty(Project project, String text) {
        final EDQLFile file = createFile(project, text);
        return PsiTreeUtil.findChildOfType(file, EDQLIdentifier0.class);
    }

    public static EDQLActionExprImpl createActionFromText(Project project, String text) {
        EDQLFile file = createFile(project, text);

        return PsiTreeUtil.findChildOfType(file, EDQLActionExprImpl.class);
    }

    public static EDQLFunctionInvokeExpr createFunctionInvokeFromText(Project project, String text) {
        EDQLFile file = createFile(project, text);

        return PsiTreeUtil.findChildOfType(file, EDQLFunctionInvokeExpr.class);
    }

    public static EDQLHostExprImpl createHostFromText(Project project, String text, boolean isKibanaHost) {
        String h = isKibanaHost ? "KIBANA_HOST" : "HOST";
        EDQLFile file = createFile(project, h + " " + text);

        return PsiTreeUtil.findChildOfType(file, EDQLHostExprImpl.class);
    }

    public static EDQLTimeoutExprImpl createTimeoutFromText(Project project, String text) {
        EDQLFile file = createFile(project, "Timeout " + text);

        return PsiTreeUtil.findChildOfType(file, EDQLTimeoutExprImpl.class);
    }

    public static @NotNull Collection<EDQLAuthExprImpl> createAuthFromText(Project project, String text) {
        EDQLFile file = createFile(project, text);

        return PsiTreeUtil.findChildrenOfType(file, EDQLAuthExprImpl.class);
    }

    public static PsiWhiteSpaceImpl createPsiWhiteSpace(Project project, String text) {
        EDQLFile file = createFile(project, text);

        return PsiTreeUtil.findChildOfType(file, PsiWhiteSpaceImpl.class);
    }

    public static EDQLFile createFile(Project project, String text) {
        String name = "dummy.edql";
        return (EDQLFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, EDQLFileType.INSTANCE, text);
    }

    public static PsiElement getNameIdentifier(EDQLIdentifier0 element) {
        return element;
    }

    public static ItemPresentation getPresentation(final EDQLIdentifier0 element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getKey();
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiFile containingFile = element.getContainingFile();
                return containingFile == null ? null : containingFile.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }

    public static boolean hasElementType(@NotNull ASTNode node, @NotNull TokenSet set) {
        return set.contains(node.getElementType());
    }

    /**
     * @see EDQLPsiImplUtils#hasElementType(ASTNode, TokenSet)
     */
    public static boolean hasElementType(@NotNull ASTNode node, IElementType... types) {
        return hasElementType(node, TokenSet.create(types));
    }

    /**
     * Checks that PSI element represents value of JSON property (key-value pair of JSON object)
     *
     * @param element PSI element to check
     * @return whether this PSI element is property value
     */
    public static boolean isPropertyValue(@NotNull PsiElement element) {
        final PsiElement parent = element.getParent();
        return parent instanceof EDQLField && element == ((EDQLField) parent).getExpr();
    }

    public static boolean isQSharp() {
        return System.getProperty("idea.platform.prefix", "idea").startsWith("QSharp");
    }
}
