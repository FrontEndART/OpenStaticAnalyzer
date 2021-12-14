package coderepair.generator.support;

import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Comment;
import columbus.java.asg.base.Commentable;
import columbus.java.asg.statm.Case;
import columbus.java.asg.statm.If;
import columbus.java.asg.statm.Iteration;
import columbus.java.asg.struc.AnnotatedElement;
import columbus.java.asg.struc.NormalMethod;

/**
 * Class for handling indents for the source generator.
 */
public class Indentator {

    /**
     * Default constructor.
     */
    public Indentator() {
        super();
    }

    public boolean isNeedDoubleNL(Base node) {
        if (Common.getIsNormalMethod(node) && !isCommentableAndHasComment(node) && !isAnnotationMemberHasAnnotation(node)) {
            return true;
        }

        if (isFirstCommentOfNormalMethod(node) || isFirstAnnotationOfNormalMethodWithNodeComment(node)) {
            return true;
        }

        return false;
    }

    private boolean isFirstAnnotationOfNormalMethodWithNodeComment(Base node) {
        if (!Common.getIsAnnotation(node)) {
            return false;
        }

        if (!Common.getIsNormalMethod(node.getParent())) {
            return false;
        }

        NormalMethod method = (NormalMethod) node.getParent();

        if (!method.getCommentsIsEmpty() || method.getAnnotationsIsEmpty()) {
            return false;
        }

        return method.getAnnotationsIterator().next() == node;
    }

    private boolean isFirstCommentOfNormalMethod(Base node) {
        if (!Common.getIsComment(node)) {
            return false;
        }

        if (!Common.getIsNormalMethod(node.getParent())) {
            return false;
        }

        NormalMethod method = (NormalMethod) node.getParent();

        if (method.getCommentsIsEmpty()) {
            return false;
        }

        return method.getCommentsIterator().next() == node;
    }

    private boolean isAnnotationMemberHasAnnotation(Base node) {
        return Common.getIsAnnotatedElement(node) && !((AnnotatedElement) node).getAnnotationsIsEmpty();
    }

    public boolean isNeedNewLine(Base node) {
        if (node.getParent() == null) {
            return false;
        }

        if (getIsCommentableAndLastCommentIsSingleLineComment(node)) {
            return true;
        }

        return isNeedIndent(node);
    }

    private boolean getIsCommentableAndLastCommentIsSingleLineComment(Base node) {
        if (!Common.getIsCommentable(node)) {
            return false;
        }

        Commentable commable = (Commentable) node;

        EdgeIterator<Comment> iter = commable.getCommentsIterator();
        Comment comm = null;
        while (iter.hasNext()) {
            comm = iter.next();
        }

        return comm != null && Common.getIsLineComment(comm);
    }

    private boolean isCommentableAndHasComment(Base node) {
        if (!Common.getIsCommentable(node)) {
            return false;
        }

        Commentable comm = (Commentable) node;

        return !comm.getCommentsIsEmpty();
    }

    public boolean isNeedIndent(Base node) {
        Base parent = node.getParent();
        if (parent == null) {
            return false;
        }

        if (Common.getIsIf(node) && getIsIfFalseSubstatementWithOutComment((If) node, node.getParent())) {
            return false;
        }

        if (isCommentableAndHasComment(node)) {
            return true;
        }

        if (Common.getIsAnonymousClass(node)) {
            return false;
        }

        if (isNeedSemicolon(node)) {
            return true;
        }

        if (getIsAnnotationWithDelarationParent(node)) {
            return true;
        }

        if (Common.getIsBlock(parent)
                || notBlockNodeWithIfWhileForParent(node, parent)
                || getHasCaseParentAndNotInExpression(node, parent)
                || Common.getIsDefault(parent)
                || Common.getIsLabeledStatement(parent) || Common.getIsNormalMethod(node) || Common.getIsTypeDeclaration(node)
                || Common.getIsSwitchLabel(node)
                || Common.getIsEnumConstant(node) || (Common.getIsBlock(parent) && Common.getIsBlock(node))) {
            return true;
        }

        return false;
    }

    private boolean notBlockNodeWithIfWhileForParent(Base node, Base parent) {
        if (Common.getIsBlock(node)) {
            return false;
        }

        return getIsIterationSubstatement(node, parent) || getIsIfSubstatementOrFalseSubstatement(node, parent);
    }

    private boolean getIsAnnotationWithDelarationParent(Base node) {
        return Common.getIsAnnotation(node)
                && (Common.getIsTypeDeclaration(node.getParent()) || Common.getIsMethodDeclaration(node.getParent()) || Common.getIsVariable(node.getParent()));
    }

    public static boolean needIncIndent(Base node) {
        if (Common.getIsTypeDeclaration(node)
                || Common.getIsBlock(node)
                || getIsIfWithNoBlockStatement(node)
                || getIsIterationWithNoBlockSubstatement(node)
                || Common.getIsSwitchLabel(node)) {
            return true;
        }

        return false;
    }

    private static boolean getIsIterationWithNoBlockSubstatement(Base node) {
        return Common.getIsIteration(node) && !Common
                .getIsBlock(((Iteration) node).getSubstatement());
    }

    private static boolean getIsIfWithNoBlockStatement(Base node) {
        return Common.getIsIf(node) && !Common.getIsBlock(((If) node)
                .getSubstatement());
    }

    public boolean isNeedSemicolon(Base node) {
        Base parent = node.getParent();
        if (parent == null) {
            return false;
        }

        if (Common.getIsImport(node)
                || Common.getIsPackageDeclaration(node) || Common.getIsDo(node)
                || getIsMethodWithoutBody(node)) {
            return true;
        }

        if (Common.getIsBlock(node) || Common.getIsIteration(node)
                || Common.getIsIf(node) || Common.getIsTypeDeclaration(node)
                || Common.getIsTry(node) || Common.getIsNormalMethod(node)
                || Common.getIsSwitch(node) || Common.getIsEnumConstant(node)
                || Common.getIsLabeledStatement(node)
                || Common.getIsSynchronized(node)) {
            return false;
        }

        if (getHasCaseParentAndNotInExpression(node, parent)
                || getIsIterationSubstatement(node, parent)
                || getIsIfSubstatementOrFalseSubstatement(node, parent)
                || Common.getIsBlock(parent)
                || Common.getIsDefault(parent)
                || getIsMemberWithTypeDeclarationParent(node, parent)) {
            return true;
        }

        return false;
    }

    private boolean getIsMemberWithTypeDeclarationParent(Base node, Base parent) {
        return Common.getIsTypeDeclaration(parent) && Common
                .getIsMember(node) && !Common.getIsInitializerBlock(node);
    }

    private boolean getIsIfSubstatementOrFalseSubstatement(Base node, Base parent) {
        return Common.getIsIf(parent) && (((If) parent).getSubstatement() == node || ((If) parent)
                .getFalseSubstatement() == node);
    }

    private boolean getIsIfFalseSubstatementWithOutComment(If node, Base parent) {
        return Common.getIsIf(parent) && ((If) parent).getFalseSubstatement() == node && node.getCommentsIsEmpty();
    }

    private boolean getIsIterationSubstatement(Base node, Base parent) {
        return Common.getIsIteration(parent) && ((Iteration) parent)
                .getSubstatement() == node;
    }

    private boolean getHasCaseParentAndNotInExpression(Base node, Base parent) {
        return Common.getIsCase(parent) && ((Case) parent).getExpression() != node;
    }

    private boolean getIsMethodWithoutBody(Base node) {
        return Common.getIsNormalMethod(node) && ((NormalMethod) node).getBody() == null;
    }

}
