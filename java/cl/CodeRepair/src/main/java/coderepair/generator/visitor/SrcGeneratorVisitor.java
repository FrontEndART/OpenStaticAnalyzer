package coderepair.generator.visitor;

import java.util.Stack;
import java.util.StringTokenizer;

import coderepair.generator.support.Indentator;
import coderepair.generator.support.ModifiedNodesPrinter;
import coderepair.generator.support.SourceLiner;

import coderepair.generator.fileeditor.FileEditorUtil.Position;

import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Range;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Comment;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.enums.AccessibilityKind;
import columbus.java.asg.enums.AssignmentOperatorKind;
import columbus.java.asg.enums.InfixOperatorKind;
import columbus.java.asg.enums.PostfixOperatorKind;
import columbus.java.asg.enums.PrefixOperatorKind;
import columbus.java.asg.enums.PrimitiveTypeKind;
import columbus.java.asg.enums.TypeBoundKind;
import columbus.java.asg.expr.Annotation;
import columbus.java.asg.expr.ArrayTypeExpression;
import columbus.java.asg.expr.Assignment;
import columbus.java.asg.expr.Binary;
import columbus.java.asg.expr.BooleanLiteral;
import columbus.java.asg.expr.CharacterLiteral;
import columbus.java.asg.expr.ClassLiteral;
import columbus.java.asg.expr.Conditional;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.FieldAccess;
import columbus.java.asg.expr.Identifier;
import columbus.java.asg.expr.InfixExpression;
import columbus.java.asg.expr.InstanceOf;
import columbus.java.asg.expr.MethodInvocation;
import columbus.java.asg.expr.NewArray;
import columbus.java.asg.expr.NewClass;
import columbus.java.asg.expr.NormalAnnotation;
import columbus.java.asg.expr.NullLiteral;
import columbus.java.asg.expr.NumberLiteral;
import columbus.java.asg.expr.ParenthesizedExpression;
import columbus.java.asg.expr.PostfixExpression;
import columbus.java.asg.expr.PrefixExpression;
import columbus.java.asg.expr.PrimitiveTypeExpression;
import columbus.java.asg.expr.QualifiedTypeExpression;
import columbus.java.asg.expr.SimpleTypeExpression;
import columbus.java.asg.expr.SingleElementAnnotation;
import columbus.java.asg.expr.StringLiteral;
import columbus.java.asg.expr.Super;
import columbus.java.asg.expr.This;
import columbus.java.asg.expr.TypeApplyExpression;
import columbus.java.asg.expr.TypeCast;
import columbus.java.asg.expr.TypeExpression;
import columbus.java.asg.expr.TypeUnionExpression;
import columbus.java.asg.expr.Unary;
import columbus.java.asg.expr.WildcardExpression;
import columbus.java.asg.statm.Assert;
import columbus.java.asg.statm.BasicFor;
import columbus.java.asg.statm.Block;
import columbus.java.asg.statm.Break;
import columbus.java.asg.statm.Case;
import columbus.java.asg.statm.Continue;
import columbus.java.asg.statm.Default;
import columbus.java.asg.statm.Do;
import columbus.java.asg.statm.EnhancedFor;
import columbus.java.asg.statm.Handler;
import columbus.java.asg.statm.If;
import columbus.java.asg.statm.LabeledStatement;
import columbus.java.asg.statm.Return;
import columbus.java.asg.statm.Statement;
import columbus.java.asg.statm.Switch;
import columbus.java.asg.statm.SwitchLabel;
import columbus.java.asg.statm.Synchronized;
import columbus.java.asg.statm.Throw;
import columbus.java.asg.statm.Try;
import columbus.java.asg.statm.While;
import columbus.java.asg.struc.AnnotatedElement;
import columbus.java.asg.struc.AnnotationType;
import columbus.java.asg.struc.AnnotationTypeElement;
import columbus.java.asg.struc.AnonymousClass;
import columbus.java.asg.struc.ClassDeclaration;
import columbus.java.asg.struc.CompilationUnit;
import columbus.java.asg.struc.EnumConstant;
import columbus.java.asg.struc.GenericDeclaration;
import columbus.java.asg.struc.Import;
import columbus.java.asg.struc.InterfaceDeclaration;
import columbus.java.asg.struc.Member;
import columbus.java.asg.struc.Method;
import columbus.java.asg.struc.MethodDeclaration;
import columbus.java.asg.struc.NamedDeclaration;
import columbus.java.asg.struc.NormalMethod;
import columbus.java.asg.struc.PackageDeclaration;
import columbus.java.asg.struc.Parameter;
import columbus.java.asg.struc.Scope;
import columbus.java.asg.struc.StaticInitializerBlock;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.struc.TypeParameter;
import columbus.java.asg.struc.Variable;
import columbus.java.asg.struc.VariableDeclaration;
import columbus.java.asg.visitors.VisitorAbstractNodes;

/**
 * Class for generates source code elements. Not supported nodes: Erroneous, ErroneousTypeExpression
 */
public class SrcGeneratorVisitor extends VisitorAbstractNodes {

    public static final String TAB = "\t";
    final private SourceLiner out;
    protected int indent;
    final private Stack<Integer> paramList;
    final private Stack<Integer> fakeParamList;
    final private Stack<Boolean> hasEnumConst;
    private int printBlocker;
    private boolean wasEnumConst;
    private String indentType;
    private boolean printIndentForFirst;
    private boolean first;
    private boolean printIndentForLast;
    private boolean isAddLast;
    private Position rootBeginPosition;
    final private Indentator indentator;

    public SrcGeneratorVisitor() {
        this(new SourceLiner());
    }

    public SrcGeneratorVisitor(SourceLiner liner) {
        super();
        this.out = liner;
        this.indent = 0;
        this.paramList = new Stack<>();
        this.fakeParamList = new Stack<>();
        this.hasEnumConst = new Stack<>();
        this.printBlocker = 0;
        this.wasEnumConst = false;
        this.indentType = TAB;
        this.printIndentForFirst = true;
        this.first = true;
        this.printIndentForLast = false;
        this.isAddLast = false;
        this.rootBeginPosition = null;
        this.indentator = new Indentator();
    }

    @Override
    public void beginVisit() {
        this.out.appendHeader();
    }

    @Override
    public void finishVisit() {
        this.out.appendFooter();
    }

    @Override
    public String toString() {
        return getOut().toString();
    }

    public void setRootBeginPosition(Position begin) {
        if (begin.getLine() == 0 && begin.getCol() == 0) {
            return;
        }

        this.rootBeginPosition = begin;
    }

    private boolean isRootBeginPositionSet() {
        return this.rootBeginPosition != null;
    }

    public void setPrintIndentFirst(boolean newBool) {
        this.printIndentForFirst = newBool;
    }

    public void setPrintIndentLast(boolean newBool) {
        this.printIndentForLast = newBool;
    }

    public void setIsAddLast(boolean newBool) {
        this.isAddLast = newBool;
    }

    public void setIndentType(String indentType) {
        this.indentType = indentType;
    }

    public SourceLiner getOut() {
        return this.out;
    }

    private void setWasEnumConst() {
        this.wasEnumConst = true;
    }

    private boolean getWasEnumConst() {
        return this.wasEnumConst;
    }

    private void unsetWasEnumConst() {
        this.wasEnumConst = false;
    }

    private void initHasEnumConst() {
        this.hasEnumConst.push(true);
        this.wasEnumConst = false;
    }

    private boolean getHasEnumConst() {
        return this.hasEnumConst.peek().booleanValue();
    }

    private void setFalseHasEnumConst() {
        this.hasEnumConst.pop();
        this.hasEnumConst.push(false);
    }

    private void deleteHasEnumConst() {
        this.hasEnumConst.pop();
    }

    protected void incPrintBlocker() {
        this.printBlocker++;
    }

    protected void decPrintBlocker() {
        this.printBlocker--;

        if (this.printBlocker < 0) {
            throw new IllegalArgumentException("The printBlocker should be positive or 0.");
        }
    }

    protected int getPrintBlocker() {
        return this.printBlocker;
    }

    protected boolean isPrintBlockerOff() {
        return getPrintBlocker() == 0;
    }

    public void printIndent() {
        for (int i = 0; i < this.indent; i++) {
            print(this.indentType);
        }
    }

    private void printPreIndent() {
        this.indent--;
        printIndent();
        this.indent++;
    }

    protected void begin(Base node) {
        begin(node, true);
    }

    protected void begin(Base node, boolean checkIndent) {
        if (isNeedNewLine(node)) {
            println();
        }

        if (isNeedDoubleNl(node)) {
            println();
        }

        if (isNeedIndent(node)) {
            printIndent();
        }

        if (needIncIndent(node) && checkIndent) {
            this.indent++;
        }
    }

    protected void end(Base node) {
        printSemicolon(node);

        if (needIncIndent(node)) {
            this.indent--;
        }
    }

    protected void printSemicolon(Base node) {
        if (isNeedSemicolon(node)) {
            print(";");
        }
    }

    private void increaseParamList() {
        Integer tmp = this.paramList.pop();
        tmp++;
        this.paramList.push(tmp);
    }

    private boolean isFirstParam() {
        return getParamIndex() == 0;
    }

    private int getRealParamIndex() {
        return getParamIndex() + this.fakeParamList.peek().intValue();
    }

    private int getParamIndex() {
        return this.paramList.peek().intValue();
    }

    public void initNewParamList() {
        this.paramList.push(0);
        this.fakeParamList.push(0);
    }

    public void deleteParamList() {
        this.paramList.pop();
    }

    protected void printMultiLine(String text, Comment node) {
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        while (tokenizer.hasMoreTokens()) {
            print(tokenizer.nextToken(), node);
            if (tokenizer.hasMoreTokens()) {
                println();
            }
        }
    }

    protected void printMultiLine(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        while (tokenizer.hasMoreTokens()) {
            print(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
                println();
            }
        }
    }

    // -- Base
    @Override
    public void visit(Base node, boolean callVirtualBase) {
        if (getIsCompilerGenerated(node) && !Common.getIsNewClass(node)) {
            incPrintBlocker();
        }

        if (this.isAddLast && doNotPrintIndent() && Indentator.needIncIndent(node.getParent())) {
            print(this.indentType);
        }

        if (isGenerateComment(node)) {
            super.visit(node, callVirtualBase);
            begin(node);
            this.first = false;
        }
    }

    @Override
    public void visitEnd(Base node, boolean callVirtualBase) {
        if (isGenerateComment(node)) {
            end(node);
            if (getIsFirstDepth() && this.printIndentForLast) {
                boolean needDecIndent = this.isAddLast && Indentator.needIncIndent(node.getParent());
                if (needDecIndent) {
                    this.indent--;
                }
                begin(node, false);
                if (needDecIndent) {
                    this.indent++;
                }
            }
            super.visitEnd(node, callVirtualBase);
        }

        if (getIsCompilerGenerated(node) && !Common.getIsNewClass(node)) {
            decPrintBlocker();
        }
    }

    private boolean getIsCompilerGenerated(Base node) {
        if (!Common.getIsPositioned(node)) {
            return false;
        }

        Positioned posed = (Positioned) node;

        return posed.getIsCompilerGenerated();
    }

    private boolean getIsFirstDepth() {
        return getDepth() == 0;
    }

    // -- AnnotatedElement

    @Override
    public void visitAnnotatedElement_HasAnnotations(AnnotatedElement begin, Annotation end) {
        incPrintBlocker();
    }

    @Override
    public void visitEndAnnotatedElement_HasAnnotations(AnnotatedElement begin, Annotation end) {
        decPrintBlocker();
    }

    @Override
    public void visitEnd(AnnotatedElement node, boolean callVirtualBase) {
        super.visitEnd(node, callVirtualBase);
    }

    // -- Annotation
    @Override
    public void visit(Annotation node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("@", node);
    }

    @Override
    public void visitEnd(Annotation node, boolean callVirtualBase) {
        if (!Common.getIsTypeDeclaration(node.getParent()) && !Common.getIsMethodDeclaration(node.getParent())) {
            print(" ");
        }
        super.visit(node, callVirtualBase);
    }

    // -- MarkerAnnotation

    // -- NormalAnnotation
    @Override
    public void visit(NormalAnnotation node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
    }

    @Override
    public void visitNormalAnnotation_HasArguments(NormalAnnotation begin,
            Expression end) {
        if (isFirstParam()) {
            print("(");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndNormalAnnotation_HasArguments(NormalAnnotation begin,
            Expression end) {
        if (getRealParamIndex() == begin.getArgumentsSize()) {
            print(")");
        }
    }

    @Override
    public void visitEnd(NormalAnnotation node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- SingleElementAnnotation
    @Override
    public void visit(SingleElementAnnotation node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
    }

    @Override
    public void visitSingleElementAnnotation_HasArgument(
            SingleElementAnnotation begin, Expression end) {
        print("(");
    }

    @Override
    public void visitEndSingleElementAnnotation_HasArgument(
            SingleElementAnnotation begin, Expression end) {
        print(")");
    }

    // -- AnonymousClass
    @Override
    public void visit(AnonymousClass node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("{");
    }

    // -- Comment
    @Override
    public void visit(Comment node, boolean callVirtualBase) {
        if (isGenerateComment(node)) {
            super.visit(node, callVirtualBase);
            if (this.indentator.isNeedDoubleNL(node)) {
                println();
            }
            println();
            printIndent();
            printMultiLine(node.getText(), node);
        }
    }

    @Override
    public void visitEnd(Comment node, boolean callVirtualBase) {
        if (isGenerateComment(node)) {
            super.visitEnd(node, callVirtualBase);
        }
    }

    private boolean isGenerateComment(Base base) {
        if (!Common.getIsComment(base)) {
            return true;
        }

        Comment node = (Comment) base;

        String positionPath = node.getPosition().getPath();
        if (positionPath == null || positionPath.equals("")) {
            return true;
        }

        if (!isRootBeginPositionSet()) {
            return true;
        }

        Position commentBegin = ModifiedNodesPrinter.getPositionFromRange(node.getPosition(), true);
        if (this.rootBeginPosition.isLowerThan(commentBegin) || this.rootBeginPosition.equals(commentBegin)) {
            return true;
        }

        return false;
    }

    // -- ArrayTypeExpression
    @Override
    public void visitEnd(ArrayTypeExpression node, boolean callVirtualBase) {
        if (!varargType(node)) {
            ArrayTypeExpression notArrayTypeParent = getFirstNotArrayTypeParented(node);
            if (isNeedBracesForArrayTypeExpression(notArrayTypeParent)) {
                print("[]", node);
            }
        } else {
            print("...", node);
        }
        super.visitEnd(node, callVirtualBase);
    }

    private boolean varargType(ArrayTypeExpression node) {
        if (!Common.getIsParameter(node.getParent())) {
            return false;
        }

        Parameter param = (Parameter) node.getParent();

        return param.getIsVarargs();
    }

    protected boolean isNeedBracesForArrayTypeExpression(ArrayTypeExpression notArrayTypeParent) {
        return !Common.getIsNewArray(notArrayTypeParent.getParent())
                || ((NewArray) notArrayTypeParent.getParent())
                        .getComponentType() != notArrayTypeParent;
    }

    private ArrayTypeExpression getFirstNotArrayTypeParented(
            ArrayTypeExpression node) {
        Base parent = node.getParent();
        if (Common.getIsArrayTypeExpression(parent)) {
            return getFirstNotArrayTypeParented((ArrayTypeExpression) parent);
        } else {
            return node;
        }
    }

    // -- BooleanLiteral
    @Override
    public void visit(BooleanLiteral node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(String.valueOf(node.getBooleanValue()), node);
    }

    // -- CharacterLiteral
    @Override
    public void visit(CharacterLiteral node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        char charValue = node.getCharValue();

        if (eightLongChar(node) || (!isEscapeCharacter(charValue) && notWritableCharacter(charValue))) {
            print("'" + convertHexEscapedString(charValue) + "'", node);
        } else {
            print("'" + replaceEscapeCharacters(charValue) + "'", node);
        }
    }

    private boolean notWritableCharacter(char charValue) {
        return charValue <= 31 || charValue >= 125;
    }

    private boolean eightLongChar(CharacterLiteral node) {
        Range pos = node.getPosition();
        if ("".equals(pos.getPath())) {
            return true;
        }

        return pos.getEndCol() - pos.getCol() == 8;
    }

    private String convertHexEscapedString(char charValue) {
        StringBuilder builder = new StringBuilder();
        String hex = Integer.toHexString(charValue);

        while (hex.length() < 4) {
            hex = "0" + hex;
        }

        builder.append("\\u");
        builder.append(hex);

        return builder.toString();
    }

    private boolean isEscapeCharacter(char charValue) {
        switch (charValue) {
            case '\0':
            case '\\':
            case '\r':
            case '\b':
            case '\t':
            case '\f':
            case '\'':
            case '\n':
                return true;
            default:
                return false;
        }
    }

    private String replaceEscapeCharacters(char charValue) {
        String result = "";

        switch (charValue) {
            case '\0':
                result = "\\0";
                break;
            case '\\':
                result = "\\\\";
                break;
            case '\r':
                result = "\\r";
                break;
            case '\b':
                result = "\\b";
                break;
            case '\t':
                result = "\\t";
                break;
            case '\f':
                result = "\\f";
                break;
            case '\'':
                result = "\\'";
                break;
            case '\n':
                result = "\\n";
                break;
            default:
                result = String.valueOf(charValue);
                break;
        }

        return result;
    }

    // -- ClassLiteral
    @Override
    public void visitEndClassLiteral_HasComponentType(ClassLiteral begin,
            TypeExpression end) {
        print(".");
        print("class", begin);
    }

    // -- Conditional
    @Override
    public void visitEndConditional_HasCondition(Conditional begin,
            Expression end) {
        print("?", begin);
    }

    @Override
    public void visitEndConditional_HasTrueExpression(Conditional begin,
            Expression end) {
        print(":", begin);
    }

    // -- Identifier
    @Override
    public void visit(Identifier node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        MethodInvocation invocation = getMethodInvocation(node);
        if (invocation != null) {
            print(node.getName(), node, invocation);
        } else {
            print(node.getName(), node);
        }
    }

    private MethodInvocation getMethodInvocation(Identifier node) {
        Base parent = node.getParent();
        if (getIsMethodInvocationOperand(node, parent)) {
            return (MethodInvocation) parent;
        } else if (Common.getIsFieldAccess(parent) && ((FieldAccess) parent).getRightOperand() == node) {
            if (getIsMethodInvocationOperand(parent, parent.getParent())) {
                return (MethodInvocation) parent.getParent();
            }
        }
        return null;
    }

    private boolean getIsMethodInvocationOperand(Base node, Base parent) {
        return Common.getIsMethodInvocation(parent) && ((MethodInvocation) parent).getOperand() == node;
    }

    // -- InstanceOf
    @Override
    public void visitInstanceOf_HasTypeOperand(InstanceOf begin,
            TypeExpression end) {
        print(" instanceof ", begin);
    }

    // -- NumberLiteral
    @Override
    public void visit(NumberLiteral node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(node.getValue(), node);
    }

    // -- MethodInvocation
    @Override
    public void visit(MethodInvocation node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
    }

    @Override
    public void visitEnd(MethodInvocation node, boolean callVirtualBase) {
        print(")");
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    @Override
    public void visitMethodInvocation_HasArguments(MethodInvocation begin,
            Expression end) {
        if (!isFirstParam()) {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitMethodInvocation_HasTypeArguments(MethodInvocation begin, TypeExpression end) {
        if (isFirstParam()) {
            print("<");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndMethodInvocation_HasTypeArguments(MethodInvocation begin, TypeExpression end) {
        if (getRealParamIndex() == begin.getTypeArgumentsSize()) {
            print(">");
        }
    }

    // -- NewArray
    @Override
    public void visit(NewArray node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        if (node.getComponentType() != null) {
            print("new ", node);
        }
        initNewParamList();
    }

    @Override
    public void visitNewArray_HasDimensions(NewArray begin, Expression end) {
        print("[");
    }

    @Override
    public void visitEndNewArray_HasDimensions(NewArray begin, Expression end) {
        print("]");
    }

    @Override
    public void visitNewArray_HasInitializers(NewArray begin, Expression end) {
        if (isFirstParam()) {
            if (begin.getComponentType() != null) {
                printDimension(begin.getComponentType());
                print("[]");
            }

            print("{");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEnd(NewArray node, boolean callVirtualBase) {
        if (node.getInitializersIsEmpty()) {
            if (node.getDimensionsIsEmpty()) {
                if (node.getComponentType() != null) {
                    printDimension(node.getComponentType());
                    print("[]");
                }

                print("{}");
            } else {
                printDimension(node.getComponentType());
            }
        } else {
            print("}");
        }

        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- NewClass
    @Override
    public void visit(NewClass node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        if (!Common.getIsEnumConstant(node.getParent())) {
            if (node.getEnclosingExpression() == null) {
                print("new ", node);
            }
        } else {
            if (node.getArgumentsIsEmpty() && node.getAnonymousClass() == null) {
                incPrintBlocker();
            }
        }
        initNewParamList();
    }

    @Override
    public void visitEndNewClass_HasEnclosingExpression(NewClass begin, Expression end) {
        print(".");
        print("new ", begin);
    }

    @Override
    public void visitNewClass_HasTypeName(NewClass begin, TypeExpression end) {
        if (Common.getIsEnumConstant(begin.getParent())) {
            incPrintBlocker();
        }
    }

    @Override
    public void visitEndNewClass_HasTypeName(NewClass begin, TypeExpression end) {
        if (Common.getIsEnumConstant(begin.getParent())) {
            decPrintBlocker();
        } else {
            isTypeArgumentListEmpty(begin);
        }
    }

    private void isTypeArgumentListEmpty(NewClass begin) {
        if (begin.getTypeArgumentsIsEmpty()) {
            isArgumentsEmpty(begin);
        }
    }

    protected void isArgumentsEmpty(NewClass begin) {
        if (begin.getArgumentsIsEmpty()) {
            isNewClassHasAnonymousClass(begin);
        }
    }

    private void isNewClassHasAnonymousClass(NewClass begin) {
        if (begin.getAnonymousClass() != null) {
            print("()");
        }
    }

    @Override
    public void visitNewClass_HasTypeArguments(NewClass begin,
            TypeExpression end) {
        if (isFirstParam()) {
            print("<");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndNewClass_HasTypeArguments(NewClass begin,
            TypeExpression end) {
        if (getRealParamIndex() == begin.getTypeArgumentsSize()) {
            print(">");
            deleteParamList();
            isArgumentsEmpty(begin);
            initNewParamList();
        }
    }

    @Override
    public void visitNewClass_HasArguments(NewClass begin, Expression end) {
        if (isFirstParam()) {
            print("(");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndNewClass_HasArguments(NewClass begin, Expression end) {
        if (getRealParamIndex() == begin.getArgumentsSize()) {
            print(")");
        }
    }

    @Override
    public void visitEnd(NewClass node, boolean callVirtualBase) {
        if (node.getArgumentsIsEmpty()) {
            if (!Common.getIsEnumConstant(node.getParent())) {
                if (node.getAnonymousClass() == null) {
                    print("()");
                }
            } else {
                if (node.getArgumentsIsEmpty() && node.getAnonymousClass() == null) {
                    decPrintBlocker();
                }
            }
        }

        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- NullLiteral
    @Override
    public void visit(NullLiteral node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("null", node);
    }

    // -- ParenthesizedExpression
    @Override
    public void visit(ParenthesizedExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("(", node);
    }

    @Override
    public void visitEnd(ParenthesizedExpression node, boolean callVirtualBase) {
        print(")", node);
        super.visitEnd(node, callVirtualBase);
    }

    // -- PostfixExpression
    @Override
    public void visitEnd(PostfixExpression node, boolean callVirtualBase) {
        print(toString(node.getOperator()), node);
        super.visitEnd(node, callVirtualBase);
    }

    // -- PrefixExpression
    @Override
    public void visit(PrefixExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(toString(node.getOperator()), node);
    }

    // -- PrimitiveTypeExpression
    @Override
    public void visit(PrimitiveTypeExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(toString(node.getKind()), node);
    }

    // -- QualifiedTypeExpression
    @Override
    public void visitEndQualifiedTypeExpression_HasQualifierType(
            QualifiedTypeExpression begin, TypeExpression end) {
        print(".", begin);
    }

    // -- SimpleTypeExpression
    @Override
    public void visit(SimpleTypeExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(node.getName(), node);
    }

    // -- StringLiteral
    @Override
    public void visit(StringLiteral node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        // print(replaceEscapeCharacters(node.getValue()), STRING_COLOR, node);
        print(node.getValue(), node);
    }

    // -- Super
    @Override
    public void visit(Super node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("super", node);
    }

    // -- This
    @Override
    public void visit(This node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("this", node);
    }

    // -- TypeApplyExpression
    @Override
    public void visit(TypeApplyExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
    }

    @Override
    public void visitEnd(TypeApplyExpression node, boolean callVirtualBase) {
        if (node.getTypeArgumentsIsEmpty()) {
            print("<>");
        }

        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    @Override
    public void visitTypeApplyExpression_HasTypeArguments(
            TypeApplyExpression begin, TypeExpression end) {
        if (isFirstParam()) {
            print("<", begin);
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndTypeApplyExpression_HasTypeArguments(
            TypeApplyExpression begin, TypeExpression end) {
        if (getRealParamIndex() == begin.getTypeArgumentsSize()) {
            print(">", begin);
        }
    }

    // -- TypeCast
    @Override
    public void visitTypeCast_HasTypeOperand(TypeCast begin, TypeExpression end) {
        print("(", begin);
    }

    @Override
    public void visitEndTypeCast_HasTypeOperand(TypeCast begin,
            TypeExpression end) {
        print(")", begin);
    }

    // -- TypeUnionExpression
    @Override
    public void visit(TypeUnionExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
    }

    @Override
    public void visitEnd(TypeUnionExpression node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    @Override
    public void visitTypeUnionExpression_HasAlternatives(
            TypeUnionExpression begin, TypeExpression end) {
        if (!isFirstParam()) {
            print(" | ", begin);
        }

        increaseParamList();
    }

    // -- WildcardExpression
    @Override
    public void visit(WildcardExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("?", node);
    }

    @Override
    public void visitWildcardExpression_HasBound(WildcardExpression begin,
            TypeExpression end) {
        print(toString(begin.getKind()), begin);
    }

    // -- Assert
    @Override
    public void visit(Assert node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("assert", node);
    }

    @Override
    public void visitAssert_HasCondition(Assert begin, Expression end) {
        if (!Common.getIsParenthesizedExpression(end)) {
            print(" ");
        }
    }

    @Override
    public void visitAssert_HasDetail(Assert begin, Expression end) {
        print(": ");
    }

    // -- BasicFor
    @Override
    public void visit(BasicFor node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("for", node);
        print("(");
        initNewParamList();
        isInitializersIsEmpty(node);
    }

    @Override
    public void visitEnd(BasicFor node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    private void isInitializersIsEmpty(BasicFor node) {
        if (node.getInitializersIsEmpty()) {
            print("; ");
            isConditionEmpty(node);
        }
    }

    private void isConditionEmpty(BasicFor node) {
        if (node.getCondition() == null) {
            print("; ");
            isUpdateEmpty(node);
        }
    }

    private void isUpdateEmpty(BasicFor node) {
        if (node.getUpdatesIsEmpty()) {
            print(")");
        }
    }

    @Override
    public void visitBasicFor_HasInitializers(BasicFor begin, Statement end) {
        if (!isFirstParam()) {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndBasicFor_HasInitializers(BasicFor begin, Statement end) {
        if (getRealParamIndex() == begin.getInitializersSize()) {
            deleteParamList();
            initNewParamList();
            print("; ");
            isConditionEmpty(begin);
        }
    }

    @Override
    public void visitEndBasicFor_HasCondition(BasicFor begin, Expression end) {
        print("; ");
        isUpdateEmpty(begin);
    }

    @Override
    public void visitBasicFor_HasUpdates(BasicFor begin, Statement end) {
        if (!isFirstParam()) {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndBasicFor_HasUpdates(BasicFor begin, Statement end) {
        if (getRealParamIndex() == begin.getUpdatesSize()) {
            deleteParamList();
            initNewParamList();
            print(")");
        }
    }

    // -- Block
    @Override
    public void visit(Block node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("{", node);
    }

    @Override
    public void visitEnd(Block node, boolean callVirtualBase) {
        println();
        printPreIndent();
        print("}", node);
        super.visitEnd(node, callVirtualBase);
    }

    // -- Break
    @Override
    public void visit(Break node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("break", node);

        if (!"".equals(node.getLabel())) {
            print(" ");
            print(node.getLabel());
        }
    }

    // -- Case
    @Override
    public void visit(Case node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("case ", node);
    }

    @Override
    public void visitEndCase_HasExpression(Case begin, Expression end) {
        print(":");
    }

    // -- Continue
    @Override
    public void visit(Continue node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("continue ", node);

        if (!"".equals(node.getLabel())) {
            print(" ");
            print(node.getLabel());
        }
    }

    // -- Default
    @Override
    public void visit(Default node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("default:", node);
    }

    // -- Do
    @Override
    public void visit(Do node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("do ", node);
    }

    @Override
    public void visitDo_HasCondition(Do begin, Expression end) {
        print(" while", begin);
    }

    // -- EnchantedFor
    @Override
    public void visit(EnhancedFor node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("for", node);
        print("(");
    }

    @Override
    public void visitEnhancedFor_HasExpression(EnhancedFor begin, Expression end) {
        print(" : ");
    }

    @Override
    public void visitEndEnhancedFor_HasExpression(EnhancedFor begin,
            Expression end) {
        print(")");
    }

    // -- ExpressionStatement
    // Nothing to do here

    // -- Handler
    @Override
    public void visit(Handler node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(" catch", node);
    }

    @Override
    public void visitHandler_HasParameter(Handler begin, Parameter end) {
        print("(");
    }

    @Override
    public void visitEndHandler_HasParameter(Handler begin, Parameter end) {
        print(")");
    }

    // -- If
    @Override
    public void visit(If node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("if", node);
    }

    @Override
    public void visitIf_HasFalseSubstatement(If begin, Statement end) {
        if (Common.getIsBlock(begin.getSubstatement())) {
            print(" ");
        } else {
            println();
            this.indent--;
            printIndent();
        }
        print("else", begin);
        if (Common.getIsBlock(begin.getFalseSubstatement()) || isElseIfWithoutComment(begin)) {
            print(" ");
        } else {
            this.indent++;
        }
    }

    private boolean isElseIfWithoutComment(If node) {
        return Common.getIsIf(node.getFalseSubstatement()) && node.getFalseSubstatement().getCommentsIsEmpty();
    }

    @Override
    public void visitEndIf_HasFalseSubstatement(If begin, Statement end) {
        if (!Common.getIsBlock(begin.getSubstatement())) {
            this.indent++;
        }

        if (!Common.getIsBlock(begin.getFalseSubstatement()) && !isElseIfWithoutComment(begin)) {
            this.indent--;
        }
    }

    // -- LabeledStatement
    @Override
    public void visit(LabeledStatement node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(node.getLabel(), node);
        print(":");
    }

    // -- Return
    @Override
    public void visit(Return node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("return", node);
    }

    @Override
    public void visitReturn_HasExpression(Return begin, Expression end) {
        print(" ");
    }

    // -- Switch
    @Override
    public void visit(Switch node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("switch", node);

        initNewParamList();
    }

    @Override
    public void visitEnd(Switch node, boolean callVirtualBase) {
        deleteParamList();
        if (node.getCasesIsEmpty()) {
            print("{");

            println();
            printIndent();
            print("}");
        }
        super.visitEnd(node, callVirtualBase);
    }

    @Override
    public void visitSwitch_HasCases(Switch begin, SwitchLabel end) {
        if (isFirstParam()) {
            print("{");
        }

        increaseParamList();
    }

    @Override
    public void visitEndSwitch_HasCases(Switch begin, SwitchLabel end) {
        if (getRealParamIndex() == begin.getCasesSize()) {
            println();
            printIndent();
            print("}");
        }
    }

    // -- Synchronized
    @Override
    public void visit(Synchronized node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("synchronized", node);
    }

    // -- Throw
    @Override
    public void visit(Throw node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("throw ", node);
    }

    // -- Try
    @Override
    public void visit(Try node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
        print("try ", node);
    }

    @Override
    public void visitEnd(Try node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    @Override
    public void visitTry_HasResources(Try begin, columbus.java.asg.base.Base end) {
        if (isFirstParam()) {
            print("(");
        }

        increaseParamList();
    }

    @Override
    public void visitEndTry_HasResources(Try begin, columbus.java.asg.base.Base end) {
        print(";");
        if (getRealParamIndex() == begin.getResourcesSize()) {
            print(")");
        }
    }

    @Override
    public void visitTry_HasFinallyBlock(Try begin, Block end) {
        print(" finally ", begin);
    }

    // -- While
    @Override
    public void visit(While node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("while", node);
    }

    // -- AnnotationType
    @Override
    public void visit(AnnotationType node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printModifiers(node);
        print("@interface ", node);
        print(node.getName(), node);
        isSuperClassEmpty(node);
        initNewParamList();
    }

    @Override
    public void visitEnd(AnnotationType node, boolean callVirtualBase) {
        deleteParamList();
        println();
        printPreIndent();
        print("}");
        super.visitEnd(node, callVirtualBase);
    }

    // -- AnnotationTypeElement
    @Override
    public void visit(AnnotationTypeElement node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printModifiers(node);
    }

    @Override
    public void visitAnnotationTypeElement_HasDefaultValue(AnnotationTypeElement begin, Expression end) {
        print("() ");
        print("default ");
    }

    @Override
    public void visitEnd(AnnotationTypeElement node, boolean callVirtualBase) {
        if (node.getDefaultValue() == null) {
            print("()");
        }
        super.visitEnd(node, callVirtualBase);
    }

    // -- ClassDeclaration
    @Override
    public void visit(ClassDeclaration node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        if (!Common.getIsAnonymousClass(node)) {
            printModifiers(node);
            print("class ", node);
            print(node.getName(), node);
            isGenericDeclarationEmpty(node);
        }
        initNewParamList();
    }

    private void isGenericDeclarationEmpty(TypeDeclaration node) {
        if (getIsGenericDeclarationEmpty(node)) {
            isSuperClassEmpty(node);
        }
    }

    private boolean getIsGenericDeclarationEmpty(TypeDeclaration node) {
        if (!Common.getIsGenericDeclaration(node)) {
            return true;
        }

        GenericDeclaration genDeclaration = (GenericDeclaration) node;

        return genDeclaration.getTypeParametersIsEmpty();
    }

    private void isSuperClassEmpty(TypeDeclaration node) {
        if (getHasSuperClass(node)) {
            isSuperInterfacesEmpty(node);
        }
    }

    private boolean getHasSuperClass(TypeDeclaration node) {
        return node.getSuperClass() == null || getIsSuperClassGenerated(node);
    }

    private boolean getIsSuperClassGenerated(TypeDeclaration node) {
        return node.getSuperClass().getIsToolGenerated() || node.getSuperClass().getIsCompilerGenerated();
    }

    private void isSuperInterfacesEmpty(TypeDeclaration node) {
        if (node.getSuperInterfacesIsEmpty()) {
            print(" {");
        }
    }

    @Override
    public void visitEnd(ClassDeclaration node, boolean callVirtualBase) {
        deleteParamList();
        println();
        printIndent();
        print("}");
        super.visitEnd(node, callVirtualBase);
    }

    @Override
    public void visitGenericDeclaration_HasTypeParameters(
            GenericDeclaration begin, TypeParameter end) {
        if (isFirstParam()) {
            print("<");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndGenericDeclaration_HasTypeParameters(
            GenericDeclaration begin, TypeParameter end) {
        if (getRealParamIndex() == begin.getTypeParametersSize()) {
            print(">");
            deleteParamList();
            initNewParamList();

            if (Common.getIsClassDeclaration(begin) || Common.getIsInterfaceDeclaration(begin)) {
                isSuperClassEmpty((TypeDeclaration) begin);
            }
        }
    }

    @Override
    public void visitTypeDeclaration_HasSuperClass(TypeDeclaration begin,
            TypeExpression end) {
        if (!getIsSuperClassGenerated(begin)) {
            print(" extends ");
        } else {
            incPrintBlocker();
        }
    }

    @Override
    public void visitEndTypeDeclaration_HasSuperClass(TypeDeclaration begin,
            TypeExpression end) {
        if (getIsSuperClassGenerated(begin)) {
            decPrintBlocker();
        } else {
            isSuperInterfacesEmpty(begin);
        }
    }

    @Override
    public void visitTypeDeclaration_HasSuperInterfaces(TypeDeclaration begin,
            TypeExpression end) {
        if (!end.getIsCompilerGenerated()) {
            if (isFirstParam()) {
                if (Common.getIsInterfaceDeclaration(begin)) {
                    print(" extends ");
                } else {
                    print(" implements ");
                }
            } else {
                print(", ");
            }
        } else {
            incPrintBlocker();
        }

        increaseParamList();
    }

    @Override
    public void visitEndTypeDeclaration_HasSuperInterfaces(
            TypeDeclaration begin, TypeExpression end) {
        if (end.getIsCompilerGenerated()) {
            decPrintBlocker();
        }

        if (getRealParamIndex() == begin.getSuperInterfacesSize()) {
            print(" {");
            deleteParamList();
            initNewParamList();
        }
    }

    // -- CompilationUnit
    // class-t.
    @Override
    public void visit(CompilationUnit node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
    }

    @Override
    public void visitCompilationUnit_HasImports(CompilationUnit begin,
            Import end) {
        increaseParamList();
    }

    @Override
    public void visitEndCompilationUnit_HasImports(CompilationUnit begin,
            Import end) {
        if (getRealParamIndex() == begin.getImportsSize()) {
            println();
        }
    }

    @Override
    public void visitEndCompilationUnit_HasPackageDeclaration(
            CompilationUnit begin, PackageDeclaration end) {
        println();
    }

    @Override
    public void visitEnd(CompilationUnit node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- Enum
    @Override
    public void visit(columbus.java.asg.struc.Enum node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printModifiers(node);
        print("enum ", node);
        print(node.getName(), node);
        isSuperClassEmpty(node);
        initNewParamList();
        initHasEnumConst();
    }

    @Override
    public void visitScope_HasMembers(Scope begin, Member end) {
        if (Common.getIsEnum(begin)) {
            if (Common.getIsEnumConstant(end)) {
                if (getWasEnumConst()) {
                    print(",");
                }
                setWasEnumConst();
            } else if (getHasEnumConst()) {
                if (getWasEnumConst()) {
                    print(";");
                    setFalseHasEnumConst();
                }
                unsetWasEnumConst();
            }
        }

        increaseParamList();
    }

    @Override
    public void visitEnd(columbus.java.asg.struc.Enum node,
            boolean callVirtualBase) {
        deleteHasEnumConst();
        deleteParamList();
        println();
        printPreIndent();
        print("}");
        super.visitEnd(node, callVirtualBase);
    }

    // -- EnumConstant
    @Override
    public void visit(EnumConstant node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(node.getName());
    }

    // -- Import
    @Override
    public void visit(Import node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("import ", node);
        if (node.getIsStatic()) {
            print("static ",  node);
        }
    }

    // -- InstanceInitializerBlock
    // Nothing to do here

    // -- InterfaceDeclaration
    @Override
    public void visit(InterfaceDeclaration node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printModifiers(node);
        print("interface ", node);
        print(node.getName(), node);
        isGenericDeclarationEmpty(node);
        initNewParamList();
    }

    @Override
    public void visitEnd(InterfaceDeclaration node, boolean callVirtualBase) {
        deleteParamList();
        println();
        printIndent();
        print("}");
        super.visitEnd(node, callVirtualBase);
    }

    // -- NormalMethod
    @Override
    public void visit(Method node, boolean callVirtualBase) {
        if (node.getIsCompilerGenerated()) {
            incPrintBlocker();
        }

        super.visit(node, callVirtualBase);
    }

    @Override
    public void visitEnd(Method node, boolean callVirtualBase) {
        super.visitEnd(node, callVirtualBase);
        if (node.getIsCompilerGenerated()) {
            decPrintBlocker();
        }
    }

    @Override
    public void visit(NormalMethod node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printModifiers(node);
        if (node.getReturnType() == null) {
            print(node.getName(), node);
            if (node.getParametersIsEmpty()) {
                print("()");
            }
        }
        initNewParamList();
    }

    @Override
    public void visitEndMethodDeclaration_HasReturnType(
            MethodDeclaration begin, TypeExpression end) {
        print(" ");
        print(begin.getName(), begin);
        if (Common.getIsNormalMethod(begin) && ((NormalMethod) begin).getParametersIsEmpty()) {
            print("()");
        }
    }

    @Override
    public void visitNormalMethod_HasParameters(NormalMethod begin,
            Parameter end) {
        if (isFirstParam()) {
            print("(");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndNormalMethod_HasParameters(NormalMethod begin,
            Parameter end) {
        if (getRealParamIndex() == begin.getParametersSize()) {
            print(")");
            deleteParamList();
            initNewParamList();
        }
    }

    @Override
    public void visitNormalMethod_HasThrownExceptions(NormalMethod begin,
            TypeExpression end) {
        if (isFirstParam()) {
            print(" throws ");
        } else {
            print(", ");
        }

        increaseParamList();
    }

    @Override
    public void visitEndNormalMethod_HasThrownExceptions(NormalMethod begin,
            TypeExpression end) {
        if (getRealParamIndex() == begin.getThrownExceptionsSize()) {
            deleteParamList();
            initNewParamList();
        }
    }

    @Override
    public void visitEnd(NormalMethod node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- PackageDeclaration
    @Override
    public void visit(PackageDeclaration node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("package ", node);
    }

    // -- Parameter
    @Override
    public void visit(Parameter node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printNamedModifiers(node);
    }

    @Override
    public void visitEnd(Parameter node, boolean callVirtualBase) {
        print(node.getName(), node);
        super.visitEnd(node, callVirtualBase);
    }

    // -- StaticInitializerBlock
    @Override
    public void visit(StaticInitializerBlock node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print("static ", node);
    }

    // -- TypeParameter
    @Override
    public void visit(TypeParameter node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        print(node.getName(), node);
        initNewParamList();
    }

    @Override
    public void visitTypeParameter_HasBounds(TypeParameter begin,
            TypeExpression end) {
        if (!end.getIsCompilerGenerated()) {
            if (isFirstParam()) {
                print(" extends ");
            } else {
                print(" & ");
            }
        } else {
            incPrintBlocker();
        }

        increaseParamList();
    }

    @Override
    public void visitEndTypeParameter_HasBounds(TypeParameter begin,
            TypeExpression end) {
        if (end.getIsCompilerGenerated()) {
            decPrintBlocker();
        }

        increaseParamList();
    }

    @Override
    public void visitEnd(TypeParameter node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- Variable
    @Override
    public void visit(Variable node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        printNamedModifiers(node);
        if (node.getIsTransient()) {
            print("transient ");
        }
        if (node.getIsVolatile()) {
            print("volatile ");
        }
    }

    @Override
    public void visitVariableDeclaration_HasType(VariableDeclaration begin, TypeExpression end) {
        if (needToSkip(begin)) {
            incPrintBlocker();
        }
    }

    @Override
    public void visitEndVariableDeclaration_HasType(VariableDeclaration begin,
            TypeExpression end) {
        if (needToSkip(begin)) {
            decPrintBlocker();
        } else {
            print(" ");
        }
    }

    private boolean needToSkip(VariableDeclaration begin) {
        if (!Common.getIsVariable(begin)) {
            return false;
        }

        if (!Common.getIsBasicFor(begin.getParent())) {
            return false;
        }

        BasicFor basicFor = (BasicFor) begin.getParent();

        int index = getInitializersIndex(basicFor, begin);

        if (index <= 0) {
            return false;
        }

        return true;
    }

    private int getInitializersIndex(BasicFor basicFor, VariableDeclaration begin) {
        EdgeIterator<Statement> iter = basicFor.getInitializersIterator();
        int index = 0;
        while (iter.hasNext()) {
            Statement stat = iter.next();
            if (stat.getId() == begin.getId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public void visitVariable_HasInitialValue(Variable begin, Expression end) {
        print(begin.getName(), begin);
        print(" = ");
    }

    @Override
    public void visitEnd(Variable node, boolean callVirtualBase) {
        if (node.getInitialValue() == null) {
            print(node.getName(), node);
        }
        super.visitEnd(node, callVirtualBase);
    }

    // -- Package
    @Override
    public void visit(columbus.java.asg.struc.Package node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);
        initNewParamList();
    }

    @Override
    public void visitEnd(columbus.java.asg.struc.Package node, boolean callVirtualBase) {
        deleteParamList();
        super.visitEnd(node, callVirtualBase);
    }

    // -- Common
    @Override
    public void visitEndUnary_HasOperand(Unary begin, Expression end) {
        if (Common.getIsMethodInvocation(begin)) {
            print("(");
        }
    }

    @Override
    public void visitBinary_HasRightOperand(Binary begin, Expression end) {
        if (Common.getIsArrayAccess(begin)) {
            print("[", begin);
        } else if (Common.getIsAssignment(begin)) {
            print(toString(((Assignment) begin).getOperator()), begin);
        } else if (Common.getIsFieldAccess(begin)) {
            print(".", begin);
        } else if (Common.getIsInfixExpression(begin)) {
            print(toString(((InfixExpression) begin).getOperator()), begin);
        }
    }

    @Override
    public void visitEndBinary_HasRightOperand(Binary begin, Expression end) {
        if (Common.getIsArrayAccess(begin)) {
            print("]", begin);
        }
    }

    // -- Non-visitor functions
    private void printNamedModifiers(NamedDeclaration named) {
        print(generateNamedModifiers(named));
    }

    private void printModifiers(TypeDeclaration cls) {
        print(generateModifiers(cls));
    }

    public static String generateModifiers(TypeDeclaration cls) {
        StringBuilder builder = new StringBuilder();

        builder.append(generateNamedModifiers(cls));
        if (cls.getIsAbstract()) {
            builder.append("abstract ");
        }
        if (cls.getIsStrictfp()) {
            builder.append("strictfp ");
        }

        return builder.toString();
    }

    private void printModifiers(MethodDeclaration method) {
        print(generateModifiers(method));
    }

    public static String generateModifiers(MethodDeclaration method) {
        StringBuilder builder = new StringBuilder();

        builder.append(generateNamedModifiers(method));
        if (method.getIsAbstract()) {
            builder.append("abstract ");
        }
        if (method.getIsStrictfp()) {
            builder.append("strictfp ");
        }
        if (Common.getIsNormalMethod(method)) {
            NormalMethod normalMethod = (NormalMethod) method;
            if (normalMethod.getIsSynchronized()) {
                builder.append("synchronized ");
            }
            if (normalMethod.getIsNative()) {
                builder.append("native ");
            }
        }

        return builder.toString();
    }

    private static String generateNamedModifiers(NamedDeclaration named) {
        StringBuilder builder = new StringBuilder();

        builder.append(generateAccessibility(named.getAccessibility()));
        if (named.getIsStatic()) {
            builder.append("static ");
        }
        if (named.getIsFinal()) {
            builder.append("final ");
        }

        return builder.toString();
    }

    private static String generateAccessibility(AccessibilityKind accessibility) {
        StringBuilder builder = new StringBuilder();

        switch (accessibility) {
            case ackPrivate:
                builder.append("private ");
                break;
            case ackProtected:
                builder.append("protected ");
                break;
            case ackPublic:
                builder.append("public ");
                break;
            default:
                break;
        }

        return builder.toString();
    }

    private String toString(TypeBoundKind kind) {
        switch (kind) {
            case tbkExtends:
                return " extends ";
            case tbkSuper:
                return " super ";
            case tbkWildcard:
                return " /* tbkWildcard */ ";
        }
        return "";
    }

    private String toString(PrimitiveTypeKind kind) {
        switch (kind) {
            case ptkBoolean:
                return "boolean";
            case ptkByte:
                return "byte";
            case ptkChar:
                return "char";
            case ptkDouble:
                return "double";
            case ptkFloat:
                return "float";
            case ptkInt:
                return "int";
            case ptkLong:
                return "long";
            case ptkShort:
                return "short";
            case ptkVoid:
                return "void";
        }
        return "";
    }

    private String toString(PrefixOperatorKind operator) {
        switch (operator) {
            case peokComplement:
                return "~";
            case peokDecrement:
                return "--";
            case peokIncrement:
                return "++";
            case peokMinus:
                return "-";
            case peokNot:
                return "!";
            case peokPlus:
                return "+";
        }
        return "";
    }

    private String toString(PostfixOperatorKind operator) {
        switch (operator) {
            case pookDecrement:
                return "--";
            case pookIncrement:
                return "++";
        }

        return "";
    }

    private void printDimension(TypeExpression componentType) {
        if (Common.getIsArrayTypeExpression(componentType)) {
            ArrayTypeExpression at = (ArrayTypeExpression) componentType;
            print("[]");
            printDimension(at.getComponentType());
        }
    }

    private String toString(InfixOperatorKind operator) {
        switch (operator) {
            case iokBitwiseAndLogicalAnd:
                return " & ";
            case iokBitwiseAndLogicalOr:
                return " | ";
            case iokBitwiseAndLogicalXor:
                return " ^ ";
            case iokConditionalAnd:
                return " && ";
            case iokConditionalOr:
                return " || ";
            case iokDivide:
                return " / ";
            case iokEqualTo:
                return " == ";
            case iokGreaterThan:
                return " > ";
            case iokGreaterThanOrEqualTo:
                return " >= ";
            case iokLeftShift:
                return " << ";
            case iokLessThan:
                return " < ";
            case iokLessThanOrEqualTo:
                return " <= ";
            case iokMinus:
                return " - ";
            case iokNotEqualTo:
                return " != ";
            case iokPlus:
                return " + ";
            case iokRemainder:
                return " % ";
            case iokSignedRightShift:
                return " >> ";
            case iokTimes:
                return " * ";
            case iokUnsignedRightShift:
                return " >>> ";
        }

        return "";
    }

    private String toString(AssignmentOperatorKind operator) {
        switch (operator) {
            case askAndAssign:
                return " &= ";
            case askAssign:
                return " = ";
            case askDivideAssign:
                return " /= ";
            case askLeftShiftAssign:
                return " <<= ";
            case askMinusAssign:
                return " -= ";
            case askOrAssign:
                return " |= ";
            case askPlusAssign:
                return " += ";
            case askSignedRightShiftAssign:
                return " >>= ";
            case askUnsignedRightShiftAssign:
                return " >>>= ";
            case askTimesAssign:
                return " *= ";
            case askXorAssign:
                return " ^= ";
            case askRemainderAssign:
                return " %= ";
        }
        return "";
    }

    public void print(String str) {
        if (isPrintBlockerOff()) {
            this.out.append(str);
        }
    }

    public void print(String str, Base... base) {
        if (isPrintBlockerOff()) {
            this.out.appendIdTitledStr(str, base);
        }
    }

    public void println(String str) {
        print(str);
        println();
    }

    public void println() {
        if (isPrintBlockerOff()) {
            this.out.newLine();
        }
    }

    private boolean isNeedNewLine(Base node) {
        if (doNotPrintIndent()) {
            return false;
        }

        return this.indentator.isNeedNewLine(node);
    }

    private boolean doNotPrintIndent() {
        return this.first && !this.printIndentForFirst;
    }

    protected boolean isNeedDoubleNl(Base node) {
        if (doNotPrintIndent()) {
            return false;
        }

        return this.indentator.isNeedDoubleNL(node);
    }

    public static boolean needIncIndent(Base node) {
        return Indentator.needIncIndent(node);
    }

    private boolean isNeedIndent(Base node) {
        if (doNotPrintIndent()) {
            return false;
        }

        return this.indentator.isNeedIndent(node);
    }

    protected boolean isNeedSemicolon(Base node) {
        return this.indentator.isNeedSemicolon(node);
    }
}
