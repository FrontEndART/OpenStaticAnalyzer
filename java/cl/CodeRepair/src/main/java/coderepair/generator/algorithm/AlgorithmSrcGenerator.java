package coderepair.generator.algorithm;

import coderepair.generator.visitor.SrcGeneratorVisitor;
import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.algorithms.Algorithm;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Comment;
import columbus.java.asg.base.Commentable;
import columbus.java.asg.expr.Annotation;
import columbus.java.asg.expr.ArrayAccess;
import columbus.java.asg.expr.ArrayTypeExpression;
import columbus.java.asg.expr.Assignment;
import columbus.java.asg.expr.Binary;
import columbus.java.asg.expr.ClassLiteral;
import columbus.java.asg.expr.Conditional;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.FieldAccess;
import columbus.java.asg.expr.Identifier;
import columbus.java.asg.expr.InfixExpression;
import columbus.java.asg.expr.InstanceOf;
import columbus.java.asg.expr.MarkerAnnotation;
import columbus.java.asg.expr.MethodInvocation;
import columbus.java.asg.expr.NewArray;
import columbus.java.asg.expr.NewClass;
import columbus.java.asg.expr.NormalAnnotation;
import columbus.java.asg.expr.ParenthesizedExpression;
import columbus.java.asg.expr.PostfixExpression;
import columbus.java.asg.expr.PrefixExpression;
import columbus.java.asg.expr.QualifiedTypeExpression;
import columbus.java.asg.expr.SimpleTypeExpression;
import columbus.java.asg.expr.SingleElementAnnotation;
import columbus.java.asg.expr.TypeApplyExpression;
import columbus.java.asg.expr.TypeCast;
import columbus.java.asg.expr.TypeExpression;
import columbus.java.asg.expr.TypeUnionExpression;
import columbus.java.asg.expr.Unary;
import columbus.java.asg.expr.WildcardExpression;
import columbus.java.asg.statm.Assert;
import columbus.java.asg.statm.BasicFor;
import columbus.java.asg.statm.Block;
import columbus.java.asg.statm.Case;
import columbus.java.asg.statm.Default;
import columbus.java.asg.statm.Do;
import columbus.java.asg.statm.EnhancedFor;
import columbus.java.asg.statm.ExpressionStatement;
import columbus.java.asg.statm.Handler;
import columbus.java.asg.statm.If;
import columbus.java.asg.statm.Iteration;
import columbus.java.asg.statm.LabeledStatement;
import columbus.java.asg.statm.Return;
import columbus.java.asg.statm.Selection;
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
import columbus.java.asg.struc.InitializerBlock;
import columbus.java.asg.struc.InstanceInitializerBlock;
import columbus.java.asg.struc.InterfaceDeclaration;
import columbus.java.asg.struc.Member;
import columbus.java.asg.struc.MethodDeclaration;
import columbus.java.asg.struc.NormalMethod;
import columbus.java.asg.struc.PackageDeclaration;
import columbus.java.asg.struc.Parameter;
import columbus.java.asg.struc.Scope;
import columbus.java.asg.struc.StaticInitializerBlock;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.struc.TypeParameter;
import columbus.java.asg.struc.Variable;
import columbus.java.asg.struc.VariableDeclaration;

public class AlgorithmSrcGenerator extends Algorithm {

    private SrcGeneratorVisitor visitor;

    public AlgorithmSrcGenerator() {
    }

    public void run(SrcGeneratorVisitor visitor, Base node) {
        mainRun(visitor, node);
    }

    public void mainRun(SrcGeneratorVisitor visitor, Base node) {
        this.visitor = visitor;

        visitor.beginVisit();

        recRun(node);

        visitor.finishVisit();
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

    public void recRun(Base node) {
        if (Common.getIsCommentable(node)) {
            traverse((Commentable) node);
        }

        if (Common.getIsAnnotatedElement(node)) {
            traverse((AnnotatedElement) node);
        }

        if (Common.getIsIdentifier(node)) {
            MethodInvocation invocation = getMethodInvocation((Identifier) node);
            if (invocation != null && !invocation.getTypeArgumentsIsEmpty()) {
                this.visitor.initNewParamList();
                for (EdgeIterator<TypeExpression> it = invocation.getTypeArgumentsIterator(); it.hasNext();) {
                    TypeExpression end = it.next();
                    this.visitor.visitMethodInvocation_HasTypeArguments(invocation, end);
                    recRun(end);
                    this.visitor.visitEndMethodInvocation_HasTypeArguments(invocation, end);
                }
                this.visitor.deleteParamList();
            }
        }

        node.accept(this.visitor);
        incVisitorDepth(this.visitor);

        if (Common.getIsPackage(node)) {
            traverse((columbus.java.asg.struc.Package) node);
        } else if (Common.getIsMarkerAnnotation(node)) {
            traverse((MarkerAnnotation) node);
        } else if (Common.getIsNormalAnnotation(node)) {
            traverse((NormalAnnotation) node);
        } else if (Common.getIsSingleElementAnnotation(node)) {
            traverse((SingleElementAnnotation) node);
        } else if (Common.getIsAnonymousClass(node)) {
            traverse((AnonymousClass) node);
        } else if (Common.getIsArrayAccess(node)) {
            traverse((ArrayAccess) node);
        } else if (Common.getIsArrayTypeExpression(node)) {
            traverse((ArrayTypeExpression) node);
        } else if (Common.getIsAssignment(node)) {
            traverse((Assignment) node);
        } else if (Common.getIsClassLiteral(node)) {
            traverse((ClassLiteral) node);
        } else if (Common.getIsConditional(node)) {
            traverse((Conditional) node);
        } else if (Common.getIsFieldAccess(node)) {
            traverse((FieldAccess) node);
        } else if (Common.getIsInfixExpression(node)) {
            traverse((InfixExpression) node);
        } else if (Common.getIsInstanceOf(node)) {
            traverse((InstanceOf) node);
        } else if (Common.getIsMethodInvocation(node)) {
            traverse((MethodInvocation) node);
        } else if (Common.getIsNewArray(node)) {
            traverse((NewArray) node);
        } else if (Common.getIsNewClass(node)) {
            traverse((NewClass) node);
        } else if (Common.getIsParenthesizedExpression(node)) {
            traverse((ParenthesizedExpression) node);
        } else if (Common.getIsPostfixExpression(node)) {
            traverse((PostfixExpression) node);
        } else if (Common.getIsPrefixExpression(node)) {
            traverse((PrefixExpression) node);
        } else if (Common.getIsQualifiedTypeExpression(node)) {
            traverse((QualifiedTypeExpression) node);
        } else if (Common.getIsTypeApplyExpression(node)) {
            traverse((TypeApplyExpression) node);
        } else if (Common.getIsTypeCast(node)) {
            traverse((TypeCast) node);
        } else if (Common.getIsTypeUnionExpression(node)) {
            traverse((TypeUnionExpression) node);
        } else if (Common.getIsWildcardExpression(node)) {
            traverse((WildcardExpression) node);
        } else if (Common.getIsAssert(node)) {
            traverse((Assert) node);
        } else if (Common.getIsBasicFor(node)) {
            traverse((BasicFor) node);
        } else if (Common.getIsBlock(node)) {
            traverse((Block) node);
        } else if (Common.getIsCase(node)) {
            traverse((Case) node);
        } else if (Common.getIsDefault(node)) {
            traverse((Default) node);
        } else if (Common.getIsDo(node)) {
            traverse((Do) node);
        } else if (Common.getIsEnhancedFor(node)) {
            traverse((EnhancedFor) node);
        } else if (Common.getIsExpressionStatement(node)) {
            traverse((ExpressionStatement) node);
        } else if (Common.getIsHandler(node)) {
            traverse((Handler) node);
        } else if (Common.getIsIf(node)) {
            traverse((If) node);
        } else if (Common.getIsLabeledStatement(node)) {
            traverse((LabeledStatement) node);
        } else if (Common.getIsReturn(node)) {
            traverse((Return) node);
        } else if (Common.getIsSwitch(node)) {
            traverse((Switch) node);
        } else if (Common.getIsSynchronized(node)) {
            traverse((Synchronized) node);
        } else if (Common.getIsThrow(node)) {
            traverse((Throw) node);
        } else if (Common.getIsTry(node)) {
            traverse((Try) node);
        } else if (Common.getIsWhile(node)) {
            traverse((While) node);
        } else if (Common.getIsAnnotationType(node)) {
            traverse((AnnotationType) node);
        } else if (Common.getIsAnnotationTypeElement(node)) {
            traverse((AnnotationTypeElement) node);
        } else if (Common.getIsClassDeclaration(node)) {
            traverse((ClassDeclaration) node);
        } else if (Common.getIsCompilationUnit(node)) {
            traverse((CompilationUnit) node);
        } else if (Common.getIsEnum(node)) {
            traverse((columbus.java.asg.struc.Enum) node);
        } else if (Common.getIsEnumConstant(node)) {
            traverse((EnumConstant) node);
        } else if (Common.getIsImport(node)) {
            traverse((Import) node);
        } else if (Common.getIsInitializerBlock(node)) {
            traverse((InitializerBlock) node);
        } else if (Common.getIsInstanceInitializerBlock(node)) {
            traverse((InstanceInitializerBlock) node);
        } else if (Common.getIsInterfaceDeclaration(node)) {
            traverse((InterfaceDeclaration) node);
        } else if (Common.getIsNormalMethod(node)) {
            traverse((NormalMethod) node);
        } else if (Common.getIsPackageDeclaration(node)) {
            traverse((PackageDeclaration) node);
        } else if (Common.getIsParameter(node)) {
            traverse((Parameter) node);
        } else if (Common.getIsStaticInitializerBlock(node)) {
            traverse((StaticInitializerBlock) node);
        } else if (Common.getIsTypeParameter(node)) {
            traverse((TypeParameter) node);
        } else if (Common.getIsVariable(node)) {
            traverse((Variable) node);
        }

        decVisitorDepth(this.visitor);

        node.acceptEnd(this.visitor);
    }

    protected void traverse(columbus.java.asg.struc.Package node) {
        for (EdgeIterator<CompilationUnit> it = node.getCompilationUnitsIterator(); it.hasNext();) {
            CompilationUnit end = it.next();
            this.visitor.visitPackage_HasCompilationUnits(node, end);
            recRun(end);
            this.visitor.visitEndPackage_HasCompilationUnits(node, end);
        }

        for (EdgeIterator<Member> it = node.getMembersIterator(); it.hasNext();) {
            Member end = it.next();
            if (Common.getIsPackage(end)) {
                this.visitor.visitScope_HasMembers(node, end);
                recRun(end);
                this.visitor.visitEndScope_HasMembers(node, end);
            }
        }
    }

    protected void traverse(MarkerAnnotation node) {
        traverse((Annotation) node);
    }

    protected void traverse(NormalAnnotation node) {
        traverse((Annotation) node);

        for (EdgeIterator<Expression> it = node.getArgumentsIterator(); it.hasNext();) {
            Expression end = it.next();
            this.visitor.visitNormalAnnotation_HasArguments(node, end);
            recRun(end);
            this.visitor.visitEndNormalAnnotation_HasArguments(node, end);
        }
    }

    protected void traverse(SingleElementAnnotation node) {
        traverse((Annotation) node);

        if (node.getArgument() != null) {
            Expression end = node.getArgument();
            this.visitor.visitSingleElementAnnotation_HasArgument(node, end);
            recRun(end);
            this.visitor.visitEndSingleElementAnnotation_HasArgument(node, end);
        }
    }

    protected void traverse(AnonymousClass node) {
        traverse((Scope) node);
    }

    protected void traverse(ArrayAccess node) {
        traverse((Binary) node);
    }

    protected void traverse(ArrayTypeExpression node) {

        if (node.getComponentType() != null) {
            TypeExpression end = node.getComponentType();
            this.visitor.visitArrayTypeExpression_HasComponentType(node, end);
            recRun(end);
            this.visitor.visitEndArrayTypeExpression_HasComponentType(node, end);
        }
    }

    protected void traverse(Assignment node) {
        traverse((Binary) node);
    }

    protected void traverse(ClassLiteral node) {

        if (node.getComponentType() != null) {
            TypeExpression end = node.getComponentType();
            this.visitor.visitClassLiteral_HasComponentType(node, end);
            recRun(end);
            this.visitor.visitEndClassLiteral_HasComponentType(node, end);
        }
    }

    protected void traverse(Conditional node) {

        if (node.getCondition() != null) {
            Expression end = node.getCondition();
            this.visitor.visitConditional_HasCondition(node, end);
            recRun(end);
            this.visitor.visitEndConditional_HasCondition(node, end);
        }

        if (node.getTrueExpression() != null) {
            Expression end = node.getTrueExpression();
            this.visitor.visitConditional_HasTrueExpression(node, end);
            recRun(end);
            this.visitor.visitEndConditional_HasTrueExpression(node, end);
        }

        if (node.getFalseExpression() != null) {
            Expression end = node.getFalseExpression();
            this.visitor.visitConditional_HasFalseExpression(node, end);
            recRun(end);
            this.visitor.visitEndConditional_HasFalseExpression(node, end);
        }
    }

    protected void traverse(FieldAccess node) {
        traverse((Binary) node);
    }

    protected void traverse(InfixExpression node) {
        traverse((Binary) node);
    }

    protected void traverse(InstanceOf node) {
        traverse((Unary) node);

        if (node.getTypeOperand() != null) {
            TypeExpression end = node.getTypeOperand();
            this.visitor.visitInstanceOf_HasTypeOperand(node, end);
            recRun(end);
            this.visitor.visitEndInstanceOf_HasTypeOperand(node, end);
        }
    }

    protected void traverse(MethodInvocation node) {
        traverse((Unary) node);

        for (EdgeIterator<Expression> it = node.getArgumentsIterator(); it.hasNext();) {
            Expression end = it.next();
            this.visitor.visitMethodInvocation_HasArguments(node, end);
            recRun(end);
            this.visitor.visitEndMethodInvocation_HasArguments(node, end);
        }
    }

    protected void traverse(NewArray node) {

        if (node.getComponentType() != null) {
            TypeExpression end = node.getComponentType();
            this.visitor.visitNewArray_HasComponentType(node, end);
            recRun(end);
            this.visitor.visitEndNewArray_HasComponentType(node, end);
        }

        for (EdgeIterator<Expression> it = node.getDimensionsIterator(); it.hasNext();) {
            Expression end = it.next();
            this.visitor.visitNewArray_HasDimensions(node, end);
            recRun(end);
            this.visitor.visitEndNewArray_HasDimensions(node, end);
        }

        for (EdgeIterator<Expression> it = node.getInitializersIterator(); it.hasNext();) {
            Expression end = it.next();
            this.visitor.visitNewArray_HasInitializers(node, end);
            recRun(end);
            this.visitor.visitEndNewArray_HasInitializers(node, end);
        }
    }

    protected void traverse(NewClass node) {

        if (node.getEnclosingExpression() != null) {
            Expression end = node.getEnclosingExpression();
            this.visitor.visitNewClass_HasEnclosingExpression(node, end);
            recRun(end);
            this.visitor.visitEndNewClass_HasEnclosingExpression(node, end);
        }

        if (node.getTypeName() != null) {
            TypeExpression end = node.getTypeName();
            this.visitor.visitNewClass_HasTypeName(node, end);
            recRun(end);
            this.visitor.visitEndNewClass_HasTypeName(node, end);
        }

        for (EdgeIterator<TypeExpression> it = node.getTypeArgumentsIterator(); it.hasNext();) {
            TypeExpression end = it.next();
            this.visitor.visitNewClass_HasTypeArguments(node, end);
            recRun(end);
            this.visitor.visitEndNewClass_HasTypeArguments(node, end);
        }

        for (EdgeIterator<Expression> it = node.getArgumentsIterator(); it.hasNext();) {
            Expression end = it.next();
            this.visitor.visitNewClass_HasArguments(node, end);
            recRun(end);
            this.visitor.visitEndNewClass_HasArguments(node, end);
        }

        if (node.getAnonymousClass() != null) {
            AnonymousClass end = node.getAnonymousClass();
            this.visitor.visitNewClass_HasAnonymousClass(node, end);
            recRun(end);
            this.visitor.visitEndNewClass_HasAnonymousClass(node, end);
        }
    }

    protected void traverse(ParenthesizedExpression node) {
        traverse((Unary) node);
    }

    protected void traverse(PostfixExpression node) {
        traverse((Unary) node);
    }

    protected void traverse(PrefixExpression node) {
        traverse((Unary) node);
    }

    protected void traverse(QualifiedTypeExpression node) {

        if (node.getQualifierType() != null) {
            TypeExpression end = node.getQualifierType();
            this.visitor.visitQualifiedTypeExpression_HasQualifierType(node, end);
            recRun(end);
            this.visitor.visitEndQualifiedTypeExpression_HasQualifierType(node, end);
        }

        if (node.getSimpleType() != null) {
            SimpleTypeExpression end = node.getSimpleType();
            this.visitor.visitQualifiedTypeExpression_HasSimpleType(node, end);
            recRun(end);
            this.visitor.visitEndQualifiedTypeExpression_HasSimpleType(node, end);
        }
    }

    protected void traverse(TypeApplyExpression node) {

        if (node.getRawType() != null) {
            TypeExpression end = node.getRawType();
            this.visitor.visitTypeApplyExpression_HasRawType(node, end);
            recRun(end);
            this.visitor.visitEndTypeApplyExpression_HasRawType(node, end);
        }

        for (EdgeIterator<TypeExpression> it = node.getTypeArgumentsIterator(); it.hasNext();) {
            TypeExpression end = it.next();
            this.visitor.visitTypeApplyExpression_HasTypeArguments(node, end);
            recRun(end);
            this.visitor.visitEndTypeApplyExpression_HasTypeArguments(node, end);
        }
    }

    protected void traverse(TypeCast node) {

        if (node.getTypeOperand() != null) {
            TypeExpression end = node.getTypeOperand();
            this.visitor.visitTypeCast_HasTypeOperand(node, end);
            recRun(end);
            this.visitor.visitEndTypeCast_HasTypeOperand(node, end);
        }
        traverse((Unary) node);
    }

    protected void traverse(TypeUnionExpression node) {

        for (EdgeIterator<TypeExpression> it = node.getAlternativesIterator(); it.hasNext();) {
            TypeExpression end = it.next();
            this.visitor.visitTypeUnionExpression_HasAlternatives(node, end);
            recRun(end);
            this.visitor.visitEndTypeUnionExpression_HasAlternatives(node, end);
        }
    }

    protected void traverse(WildcardExpression node) {

        if (node.getBound() != null) {
            TypeExpression end = node.getBound();
            this.visitor.visitWildcardExpression_HasBound(node, end);
            recRun(end);
            this.visitor.visitEndWildcardExpression_HasBound(node, end);
        }
    }

    protected void traverse(Assert node) {

        if (node.getCondition() != null) {
            Expression end = node.getCondition();
            this.visitor.visitAssert_HasCondition(node, end);
            recRun(end);
            this.visitor.visitEndAssert_HasCondition(node, end);
        }

        if (node.getDetail() != null) {
            Expression end = node.getDetail();
            this.visitor.visitAssert_HasDetail(node, end);
            recRun(end);
            this.visitor.visitEndAssert_HasDetail(node, end);
        }
    }

    protected void traverse(BasicFor node) {

        for (EdgeIterator<Statement> it = node.getInitializersIterator(); it.hasNext();) {
            Statement end = it.next();
            this.visitor.visitBasicFor_HasInitializers(node, end);
            recRun(end);
            this.visitor.visitEndBasicFor_HasInitializers(node, end);
        }

        if (node.getCondition() != null) {
            Expression end = node.getCondition();
            this.visitor.visitBasicFor_HasCondition(node, end);
            recRun(end);
            this.visitor.visitEndBasicFor_HasCondition(node, end);
        }

        for (EdgeIterator<Statement> it = node.getUpdatesIterator(); it.hasNext();) {
            Statement end = it.next();
            this.visitor.visitBasicFor_HasUpdates(node, end);
            recRun(end);
            this.visitor.visitEndBasicFor_HasUpdates(node, end);
        }
        traverse((Iteration) node);
    }

    protected void traverse(Block node) {

        for (EdgeIterator<Statement> it = node.getStatementsIterator(); it.hasNext();) {
            Statement end = it.next();
            this.visitor.visitBlock_HasStatements(node, end);
            recRun(end);
            this.visitor.visitEndBlock_HasStatements(node, end);
        }
    }

    protected void traverse(Case node) {

        if (node.getExpression() != null) {
            Expression end = node.getExpression();
            this.visitor.visitCase_HasExpression(node, end);
            recRun(end);
            this.visitor.visitEndCase_HasExpression(node, end);
        }
        traverse((SwitchLabel) node);
    }

    protected void traverse(Default node) {
        traverse((SwitchLabel) node);
    }

    protected void traverse(Do node) {
        traverse((Iteration) node);

        if (node.getCondition() != null) {
            Expression end = node.getCondition();
            this.visitor.visitDo_HasCondition(node, end);
            recRun(end);
            this.visitor.visitEndDo_HasCondition(node, end);
        }
    }

    protected void traverse(EnhancedFor node) {

        if (node.getParameter() != null) {
            Parameter end = node.getParameter();
            this.visitor.visitEnhancedFor_HasParameter(node, end);
            recRun(end);
            this.visitor.visitEndEnhancedFor_HasParameter(node, end);
        }

        if (node.getExpression() != null) {
            Expression end = node.getExpression();
            this.visitor.visitEnhancedFor_HasExpression(node, end);
            recRun(end);
            this.visitor.visitEndEnhancedFor_HasExpression(node, end);
        }
        traverse((Iteration) node);
    }

    protected void traverse(ExpressionStatement node) {

        if (node.getExpression() != null) {
            Expression end = node.getExpression();
            this.visitor.visitExpressionStatement_HasExpression(node, end);
            recRun(end);
            this.visitor.visitEndExpressionStatement_HasExpression(node, end);
        }
    }

    protected void traverse(Handler node) {

        if (node.getParameter() != null) {
            Parameter end = node.getParameter();
            this.visitor.visitHandler_HasParameter(node, end);
            recRun(end);
            this.visitor.visitEndHandler_HasParameter(node, end);
        }

        if (node.getBlock() != null) {
            Block end = node.getBlock();
            this.visitor.visitHandler_HasBlock(node, end);
            recRun(end);
            this.visitor.visitEndHandler_HasBlock(node, end);
        }
    }

    protected void traverse(If node) {
        traverse((Selection) node);

        if (node.getSubstatement() != null) {
            Statement end = node.getSubstatement();
            this.visitor.visitIf_HasSubstatement(node, end);
            recRun(end);
            this.visitor.visitEndIf_HasSubstatement(node, end);
        }

        if (node.getFalseSubstatement() != null) {
            Statement end = node.getFalseSubstatement();
            this.visitor.visitIf_HasFalseSubstatement(node, end);
            recRun(end);
            this.visitor.visitEndIf_HasFalseSubstatement(node, end);
        }
    }

    protected void traverse(LabeledStatement node) {

        if (node.getStatement() != null) {
            Statement end = node.getStatement();
            this.visitor.visitLabeledStatement_HasStatement(node, end);
            recRun(end);
            this.visitor.visitEndLabeledStatement_HasStatement(node, end);
        }
    }

    protected void traverse(Return node) {

        if (node.getExpression() != null) {
            Expression end = node.getExpression();
            this.visitor.visitReturn_HasExpression(node, end);
            recRun(end);
            this.visitor.visitEndReturn_HasExpression(node, end);
        }
    }

    protected void traverse(Switch node) {
        traverse((Selection) node);

        for (EdgeIterator<SwitchLabel> it = node.getCasesIterator(); it.hasNext();) {
            SwitchLabel end = it.next();
            this.visitor.visitSwitch_HasCases(node, end);
            recRun(end);
            this.visitor.visitEndSwitch_HasCases(node, end);
        }
    }

    protected void traverse(Synchronized node) {

        if (node.getLock() != null) {
            Expression end = node.getLock();
            this.visitor.visitSynchronized_HasLock(node, end);
            recRun(end);
            this.visitor.visitEndSynchronized_HasLock(node, end);
        }

        if (node.getBlock() != null) {
            Block end = node.getBlock();
            this.visitor.visitSynchronized_HasBlock(node, end);
            recRun(end);
            this.visitor.visitEndSynchronized_HasBlock(node, end);
        }
    }

    protected void traverse(Throw node) {

        if (node.getExpression() != null) {
            Expression end = node.getExpression();
            this.visitor.visitThrow_HasExpression(node, end);
            recRun(end);
            this.visitor.visitEndThrow_HasExpression(node, end);
        }
    }

    protected void traverse(Try node) {

        for (EdgeIterator<columbus.java.asg.base.Base> it = node.getResourcesIterator(); it.hasNext();) {
            columbus.java.asg.base.Base end = it.next();
            this.visitor.visitTry_HasResources(node, end);
            recRun(end);
            this.visitor.visitEndTry_HasResources(node, end);
        }

        if (node.getBlock() != null) {
            Block end = node.getBlock();
            this.visitor.visitTry_HasBlock(node, end);
            recRun(end);
            this.visitor.visitEndTry_HasBlock(node, end);
        }

        for (EdgeIterator<Handler> it = node.getHandlersIterator(); it.hasNext();) {
            Handler end = it.next();
            this.visitor.visitTry_HasHandlers(node, end);
            recRun(end);
            this.visitor.visitEndTry_HasHandlers(node, end);
        }

        if (node.getFinallyBlock() != null) {
            Block end = node.getFinallyBlock();
            this.visitor.visitTry_HasFinallyBlock(node, end);
            recRun(end);
            this.visitor.visitEndTry_HasFinallyBlock(node, end);
        }
    }

    protected void traverse(While node) {

        if (node.getCondition() != null) {
            Expression end = node.getCondition();
            this.visitor.visitWhile_HasCondition(node, end);
            recRun(end);
            this.visitor.visitEndWhile_HasCondition(node, end);
        }
        traverse((Iteration) node);
    }

    protected void traverse(AnnotationType node) {
        traverse((TypeDeclaration) node);
    }

    protected void traverse(AnnotationTypeElement node) {
        traverse((MethodDeclaration) node);

        if (node.getDefaultValue() != null) {
            Expression end = node.getDefaultValue();
            this.visitor.visitAnnotationTypeElement_HasDefaultValue(node, end);
            recRun(end);
            this.visitor.visitEndAnnotationTypeElement_HasDefaultValue(node, end);
        }
    }

    protected void traverse(ClassDeclaration node) {
        if (Common.getIsGenericDeclaration(node)) {
            traverse((GenericDeclaration) node);
        }
        traverse((TypeDeclaration) node);
    }

    protected void traverse(CompilationUnit node) {

        if (node.getPackageDeclaration() != null) {
            PackageDeclaration end = node.getPackageDeclaration();
            this.visitor.visitCompilationUnit_HasPackageDeclaration(node, end);
            recRun(end);
            this.visitor.visitEndCompilationUnit_HasPackageDeclaration(node, end);
        }

        for (EdgeIterator<Import> it = node.getImportsIterator(); it.hasNext();) {
            Import end = it.next();
            this.visitor.visitCompilationUnit_HasImports(node, end);
            recRun(end);
            this.visitor.visitEndCompilationUnit_HasImports(node, end);
        }

        for (EdgeIterator<TypeDeclaration> it = node.getTypeDeclarationsIterator(); it.hasNext();) {
            TypeDeclaration end = it.next();
            this.visitor.visitCompilationUnit_TypeDeclarations(node, end);
            recRun(end);
            this.visitor.visitEndCompilationUnit_TypeDeclarations(node, end);
        }
    }

    protected void traverse(columbus.java.asg.struc.Enum node) {
        traverse((TypeDeclaration) node);
    }

    protected void traverse(EnumConstant node) {

        if (node.getNewClass() != null) {
            NewClass end = node.getNewClass();
            this.visitor.visitEnumConstant_HasNewClass(node, end);
            recRun(end);
            this.visitor.visitEndEnumConstant_HasNewClass(node, end);
        }
    }

    protected void traverse(Import node) {

        if (node.getTarget() != null) {
            Expression end = node.getTarget();
            this.visitor.visitImport_HasTarget(node, end);
            recRun(end);
            this.visitor.visitEndImport_HasTarget(node, end);
        }
    }

    protected void traverse(InitializerBlock node) {

        if (node.getBody() != null) {
            Block end = node.getBody();
            this.visitor.visitInitializerBlock_HasBody(node, end);
            recRun(end);
            this.visitor.visitEndInitializerBlock_HasBody(node, end);
        }
    }

    protected void traverse(InstanceInitializerBlock node) {
        traverse((InitializerBlock) node);
    }

    protected void traverse(InterfaceDeclaration node) {
        if (Common.getIsGenericDeclaration(node)) {
            traverse((GenericDeclaration) node);
        }
        traverse((TypeDeclaration) node);
    }

    protected void traverse(NormalMethod node) {
        if (Common.getIsGenericDeclaration(node)) {
            traverse((GenericDeclaration) node);
        }
        traverse((MethodDeclaration) node);

        for (EdgeIterator<Parameter> it = node.getParametersIterator(); it.hasNext();) {
            Parameter end = it.next();
            this.visitor.visitNormalMethod_HasParameters(node, end);
            recRun(end);
            this.visitor.visitEndNormalMethod_HasParameters(node, end);
        }

        for (EdgeIterator<TypeExpression> it = node.getThrownExceptionsIterator(); it.hasNext();) {
            TypeExpression end = it.next();
            this.visitor.visitNormalMethod_HasThrownExceptions(node, end);
            recRun(end);
            this.visitor.visitEndNormalMethod_HasThrownExceptions(node, end);
        }

        if (node.getBody() != null) {
            Block end = node.getBody();
            this.visitor.visitNormalMethod_HasBody(node, end);
            recRun(end);
            this.visitor.visitEndNormalMethod_HasBody(node, end);
        }
    }

    protected void traverse(PackageDeclaration node) {

        if (node.getPackageName() != null) {
            Expression end = node.getPackageName();
            this.visitor.visitPackageDeclaration_HasPackageName(node, end);
            recRun(end);
            this.visitor.visitEndPackageDeclaration_HasPackageName(node, end);
        }
    }

    protected void traverse(Parameter node) {
        traverse((VariableDeclaration) node);
    }

    protected void traverse(StaticInitializerBlock node) {
        traverse((InitializerBlock) node);
    }

    protected void traverse(TypeParameter node) {

        for (EdgeIterator<TypeExpression> it = node.getBoundsIterator(); it.hasNext();) {
            TypeExpression end = it.next();
            this.visitor.visitTypeParameter_HasBounds(node, end);
            recRun(end);
            this.visitor.visitEndTypeParameter_HasBounds(node, end);
        }
    }

    protected void traverse(Variable node) {
        traverse((VariableDeclaration) node);

        if (node.getInitialValue() != null) {
            Expression end = node.getInitialValue();
            this.visitor.visitVariable_HasInitialValue(node, end);
            recRun(end);
            this.visitor.visitEndVariable_HasInitialValue(node, end);
        }
    }

    protected void traverse(Commentable node) {

        for (EdgeIterator<Comment> it = node.getCommentsIterator(); it.hasNext();) {
            Comment end = it.next();
            this.visitor.visitCommentable_Comments(node, end);
            recRun(end);
            this.visitor.visitEndCommentable_Comments(node, end);
        }
    }

    protected void traverse(AnnotatedElement node) {

        for (EdgeIterator<Annotation> it = node.getAnnotationsIterator(); it.hasNext();) {
            Annotation end = it.next();
            this.visitor.visitAnnotatedElement_HasAnnotations(node, end);
            recRun(end);
            this.visitor.visitEndAnnotatedElement_HasAnnotations(node, end);
        }
    }

    protected void traverse(Annotation node) {

        if (node.getAnnotationName() != null) {
            TypeExpression end = node.getAnnotationName();
            this.visitor.visitAnnotation_HasAnnotationName(node, end);
            recRun(end);
            this.visitor.visitEndAnnotation_HasAnnotationName(node, end);
        }
    }

    protected void traverse(Binary node) {

        if (node.getLeftOperand() != null) {
            Expression end = node.getLeftOperand();
            this.visitor.visitBinary_HasLeftOperand(node, end);
            recRun(end);
            this.visitor.visitEndBinary_HasLeftOperand(node, end);
        }

        if (node.getRightOperand() != null) {
            Expression end = node.getRightOperand();
            this.visitor.visitBinary_HasRightOperand(node, end);
            recRun(end);
            this.visitor.visitEndBinary_HasRightOperand(node, end);
        }
    }

    protected void traverse(Unary node) {

        if (node.getOperand() != null) {
            Expression end = node.getOperand();
            this.visitor.visitUnary_HasOperand(node, end);
            recRun(end);
            this.visitor.visitEndUnary_HasOperand(node, end);
        }
    }

    protected void traverse(Iteration node) {

        if (node.getSubstatement() != null) {
            Statement end = node.getSubstatement();
            this.visitor.visitIteration_HasSubstatement(node, end);
            recRun(end);
            this.visitor.visitEndIteration_HasSubstatement(node, end);
        }
    }

    protected void traverse(SwitchLabel node) {

        for (EdgeIterator<Statement> it = node.getStatementsIterator(); it.hasNext();) {
            Statement end = it.next();
            this.visitor.visitSwitchLabel_HasStatements(node, end);
            recRun(end);
            this.visitor.visitEndSwitchLabel_HasStatements(node, end);
        }
    }

    protected void traverse(Selection node) {

        if (node.getCondition() != null) {
            Expression end = node.getCondition();
            this.visitor.visitSelection_HasCondition(node, end);
            recRun(end);
            this.visitor.visitEndSelection_HasCondition(node, end);
        }
    }

    protected void traverse(GenericDeclaration node) {

        for (EdgeIterator<TypeParameter> it = node.getTypeParametersIterator(); it.hasNext();) {
            TypeParameter end = it.next();
            this.visitor.visitGenericDeclaration_HasTypeParameters(node, end);
            recRun(end);
            this.visitor.visitEndGenericDeclaration_HasTypeParameters(node, end);
        }
    }

    protected void traverse(Scope node) {

        for (EdgeIterator<Member> it = node.getMembersIterator(); it.hasNext();) {
            Member end = it.next();
            this.visitor.visitScope_HasMembers(node, end);
            recRun(end);
            this.visitor.visitEndScope_HasMembers(node, end);
        }
    }

    protected void traverse(TypeDeclaration node) {

        if (node.getSuperClass() != null) {
            TypeExpression end = node.getSuperClass();
            this.visitor.visitTypeDeclaration_HasSuperClass(node, end);
            recRun(end);
            this.visitor.visitEndTypeDeclaration_HasSuperClass(node, end);
        }

        for (EdgeIterator<TypeExpression> it = node.getSuperInterfacesIterator(); it.hasNext();) {
            TypeExpression end = it.next();
            this.visitor.visitTypeDeclaration_HasSuperInterfaces(node, end);
            recRun(end);
            this.visitor.visitEndTypeDeclaration_HasSuperInterfaces(node, end);
        }
        traverse((Scope) node);
    }

    protected void traverse(MethodDeclaration node) {

        if (node.getReturnType() != null) {
            TypeExpression end = node.getReturnType();
            this.visitor.visitMethodDeclaration_HasReturnType(node, end);
            recRun(end);
            this.visitor.visitEndMethodDeclaration_HasReturnType(node, end);
        }
    }

    protected void traverse(VariableDeclaration node) {

        if (node.getType() != null) {
            TypeExpression end = node.getType();
            this.visitor.visitVariableDeclaration_HasType(node, end);
            recRun(end);
            this.visitor.visitEndVariableDeclaration_HasType(node, end);
        }
    }

}
