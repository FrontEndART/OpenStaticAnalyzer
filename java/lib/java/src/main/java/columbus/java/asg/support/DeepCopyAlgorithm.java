package columbus.java.asg.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Factory;
import columbus.java.asg.Range;
import columbus.java.asg.algorithms.AlgorithmPreorder;
import columbus.java.asg.base.Base;

public class DeepCopyAlgorithm {

    Map<Integer, Integer> idMap;
    Factory fact;

    public DeepCopyAlgorithm(Factory fact) {
        this.fact = fact;
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> T getMappedNode(T node) {
        Integer aktId = node.getId();
        if (!this.idMap.containsKey(aktId)) {
            return node;
        }

        return (T) this.fact.getRef(this.idMap.get(aktId).intValue());
    }

    public Map<Integer, Integer> runDeepCopy(int id) {
        List<Integer> rootList = new ArrayList<Integer>();
        rootList.add(id);

        return runDeepCopy(rootList);
    }

    public Map<Integer, Integer> runDeepCopy(List<Integer> roots) {
        AlgorithmPreorder ap = new AlgorithmPreorder();
        CloneVisitor cVisitor = new CloneVisitor();

        ap.setVisitSpecialNodes(false, false);
        for (Integer aktId : roots) {
            ap.run(this.fact, cVisitor, aktId);
        }

        this.idMap = cVisitor.getIdMap();
        Set<Entry<Integer, Integer>> entrySet = this.idMap.entrySet();

        for (Entry<Integer, Integer> aktEntry : entrySet) {
            int originId = aktEntry.getKey().intValue();
            int cloneId = aktEntry.getValue().intValue();

            Base origin = this.fact.getRef(originId);
            Base clone = this.fact.getRef(cloneId);

            copy(origin, clone);
        }

        return this.idMap;
    }

    private void copy(Base origin, Base clone) {
        if (Common.getIsComment(origin)) {
            copy((columbus.java.asg.base.Comment) origin, (columbus.java.asg.base.Comment) clone);
        }
        if (Common.getIsCommentable(origin)) {
            copy((columbus.java.asg.base.Commentable) origin, (columbus.java.asg.base.Commentable) clone);
        }
        if (Common.getIsNamed(origin)) {
            copy((columbus.java.asg.base.Named) origin, (columbus.java.asg.base.Named) clone);
        }
        if (Common.getIsPositioned(origin)) {
            copy((columbus.java.asg.base.Positioned) origin, (columbus.java.asg.base.Positioned) clone);
        }
        if (Common.getIsPositionedWithoutComment(origin)) {
            copy((columbus.java.asg.base.PositionedWithoutComment) origin, (columbus.java.asg.base.PositionedWithoutComment) clone);
        }
        if (Common.getIsAnnotation(origin)) {
            copy((columbus.java.asg.expr.Annotation) origin, (columbus.java.asg.expr.Annotation) clone);
        }
        if (Common.getIsArrayTypeExpression(origin)) {
            copy((columbus.java.asg.expr.ArrayTypeExpression) origin, (columbus.java.asg.expr.ArrayTypeExpression) clone);
        }
        if (Common.getIsAssignment(origin)) {
            copy((columbus.java.asg.expr.Assignment) origin, (columbus.java.asg.expr.Assignment) clone);
        }
        if (Common.getIsBinary(origin)) {
            copy((columbus.java.asg.expr.Binary) origin, (columbus.java.asg.expr.Binary) clone);
        }
        if (Common.getIsBooleanLiteral(origin)) {
            copy((columbus.java.asg.expr.BooleanLiteral) origin, (columbus.java.asg.expr.BooleanLiteral) clone);
        }
        if (Common.getIsCharacterLiteral(origin)) {
            copy((columbus.java.asg.expr.CharacterLiteral) origin, (columbus.java.asg.expr.CharacterLiteral) clone);
        }
        if (Common.getIsClassLiteral(origin)) {
            copy((columbus.java.asg.expr.ClassLiteral) origin, (columbus.java.asg.expr.ClassLiteral) clone);
        }
        if (Common.getIsConditional(origin)) {
            copy((columbus.java.asg.expr.Conditional) origin, (columbus.java.asg.expr.Conditional) clone);
        }
        if (Common.getIsDoubleLiteral(origin)) {
            copy((columbus.java.asg.expr.DoubleLiteral) origin, (columbus.java.asg.expr.DoubleLiteral) clone);
        }
        if (Common.getIsErroneous(origin)) {
            copy((columbus.java.asg.expr.Erroneous) origin, (columbus.java.asg.expr.Erroneous) clone);
        }
        if (Common.getIsErroneousTypeExpression(origin)) {
            copy((columbus.java.asg.expr.ErroneousTypeExpression) origin, (columbus.java.asg.expr.ErroneousTypeExpression) clone);
        }
        if (Common.getIsExpression(origin)) {
            copy((columbus.java.asg.expr.Expression) origin, (columbus.java.asg.expr.Expression) clone);
        }
        if (Common.getIsFloatLiteral(origin)) {
            copy((columbus.java.asg.expr.FloatLiteral) origin, (columbus.java.asg.expr.FloatLiteral) clone);
        }
        if (Common.getIsIdentifier(origin)) {
            copy((columbus.java.asg.expr.Identifier) origin, (columbus.java.asg.expr.Identifier) clone);
        }
        if (Common.getIsInfixExpression(origin)) {
            copy((columbus.java.asg.expr.InfixExpression) origin, (columbus.java.asg.expr.InfixExpression) clone);
        }
        if (Common.getIsInstanceOf(origin)) {
            copy((columbus.java.asg.expr.InstanceOf) origin, (columbus.java.asg.expr.InstanceOf) clone);
        }
        if (Common.getIsIntegerLiteral(origin)) {
            copy((columbus.java.asg.expr.IntegerLiteral) origin, (columbus.java.asg.expr.IntegerLiteral) clone);
        }
        if (Common.getIsLongLiteral(origin)) {
            copy((columbus.java.asg.expr.LongLiteral) origin, (columbus.java.asg.expr.LongLiteral) clone);
        }
        if (Common.getIsMethodInvocation(origin)) {
            copy((columbus.java.asg.expr.MethodInvocation) origin, (columbus.java.asg.expr.MethodInvocation) clone);
        }
        if (Common.getIsNewArray(origin)) {
            copy((columbus.java.asg.expr.NewArray) origin, (columbus.java.asg.expr.NewArray) clone);
        }
        if (Common.getIsNewClass(origin)) {
            copy((columbus.java.asg.expr.NewClass) origin, (columbus.java.asg.expr.NewClass) clone);
        }
        if (Common.getIsNormalAnnotation(origin)) {
            copy((columbus.java.asg.expr.NormalAnnotation) origin, (columbus.java.asg.expr.NormalAnnotation) clone);
        }
        if (Common.getIsNumberLiteral(origin)) {
            copy((columbus.java.asg.expr.NumberLiteral) origin, (columbus.java.asg.expr.NumberLiteral) clone);
        }
        if (Common.getIsPostfixExpression(origin)) {
            copy((columbus.java.asg.expr.PostfixExpression) origin, (columbus.java.asg.expr.PostfixExpression) clone);
        }
        if (Common.getIsPrefixExpression(origin)) {
            copy((columbus.java.asg.expr.PrefixExpression) origin, (columbus.java.asg.expr.PrefixExpression) clone);
        }
        if (Common.getIsPrimitiveTypeExpression(origin)) {
            copy((columbus.java.asg.expr.PrimitiveTypeExpression) origin, (columbus.java.asg.expr.PrimitiveTypeExpression) clone);
        }
        if (Common.getIsQualifiedTypeExpression(origin)) {
            copy((columbus.java.asg.expr.QualifiedTypeExpression) origin, (columbus.java.asg.expr.QualifiedTypeExpression) clone);
        }
        if (Common.getIsSimpleTypeExpression(origin)) {
            copy((columbus.java.asg.expr.SimpleTypeExpression) origin, (columbus.java.asg.expr.SimpleTypeExpression) clone);
        }
        if (Common.getIsSingleElementAnnotation(origin)) {
            copy((columbus.java.asg.expr.SingleElementAnnotation) origin, (columbus.java.asg.expr.SingleElementAnnotation) clone);
        }
        if (Common.getIsStringLiteral(origin)) {
            copy((columbus.java.asg.expr.StringLiteral) origin, (columbus.java.asg.expr.StringLiteral) clone);
        }
        if (Common.getIsTypeApplyExpression(origin)) {
            copy((columbus.java.asg.expr.TypeApplyExpression) origin, (columbus.java.asg.expr.TypeApplyExpression) clone);
        }
        if (Common.getIsTypeCast(origin)) {
            copy((columbus.java.asg.expr.TypeCast) origin, (columbus.java.asg.expr.TypeCast) clone);
        }
        if (Common.getIsTypeUnionExpression(origin)) {
            copy((columbus.java.asg.expr.TypeUnionExpression) origin, (columbus.java.asg.expr.TypeUnionExpression) clone);
        }
        if (Common.getIsUnary(origin)) {
            copy((columbus.java.asg.expr.Unary) origin, (columbus.java.asg.expr.Unary) clone);
        }
        if (Common.getIsWildcardExpression(origin)) {
            copy((columbus.java.asg.expr.WildcardExpression) origin, (columbus.java.asg.expr.WildcardExpression) clone);
        }
        if (Common.getIsAssert(origin)) {
            copy((columbus.java.asg.statm.Assert) origin, (columbus.java.asg.statm.Assert) clone);
        }
        if (Common.getIsBasicFor(origin)) {
            copy((columbus.java.asg.statm.BasicFor) origin, (columbus.java.asg.statm.BasicFor) clone);
        }
        if (Common.getIsBlock(origin)) {
            copy((columbus.java.asg.statm.Block) origin, (columbus.java.asg.statm.Block) clone);
        }
        if (Common.getIsCase(origin)) {
            copy((columbus.java.asg.statm.Case) origin, (columbus.java.asg.statm.Case) clone);
        }
        if (Common.getIsDo(origin)) {
            copy((columbus.java.asg.statm.Do) origin, (columbus.java.asg.statm.Do) clone);
        }
        if (Common.getIsEnhancedFor(origin)) {
            copy((columbus.java.asg.statm.EnhancedFor) origin, (columbus.java.asg.statm.EnhancedFor) clone);
        }
        if (Common.getIsExpressionStatement(origin)) {
            copy((columbus.java.asg.statm.ExpressionStatement) origin, (columbus.java.asg.statm.ExpressionStatement) clone);
        }
        if (Common.getIsHandler(origin)) {
            copy((columbus.java.asg.statm.Handler) origin, (columbus.java.asg.statm.Handler) clone);
        }
        if (Common.getIsIf(origin)) {
            copy((columbus.java.asg.statm.If) origin, (columbus.java.asg.statm.If) clone);
        }
        if (Common.getIsIteration(origin)) {
            copy((columbus.java.asg.statm.Iteration) origin, (columbus.java.asg.statm.Iteration) clone);
        }
        if (Common.getIsJump(origin)) {
            copy((columbus.java.asg.statm.Jump) origin, (columbus.java.asg.statm.Jump) clone);
        }
        if (Common.getIsLabeledStatement(origin)) {
            copy((columbus.java.asg.statm.LabeledStatement) origin, (columbus.java.asg.statm.LabeledStatement) clone);
        }
        if (Common.getIsReturn(origin)) {
            copy((columbus.java.asg.statm.Return) origin, (columbus.java.asg.statm.Return) clone);
        }
        if (Common.getIsSelection(origin)) {
            copy((columbus.java.asg.statm.Selection) origin, (columbus.java.asg.statm.Selection) clone);
        }
        if (Common.getIsSwitch(origin)) {
            copy((columbus.java.asg.statm.Switch) origin, (columbus.java.asg.statm.Switch) clone);
        }
        if (Common.getIsSwitchLabel(origin)) {
            copy((columbus.java.asg.statm.SwitchLabel) origin, (columbus.java.asg.statm.SwitchLabel) clone);
        }
        if (Common.getIsSynchronized(origin)) {
            copy((columbus.java.asg.statm.Synchronized) origin, (columbus.java.asg.statm.Synchronized) clone);
        }
        if (Common.getIsThrow(origin)) {
            copy((columbus.java.asg.statm.Throw) origin, (columbus.java.asg.statm.Throw) clone);
        }
        if (Common.getIsTry(origin)) {
            copy((columbus.java.asg.statm.Try) origin, (columbus.java.asg.statm.Try) clone);
        }
        if (Common.getIsWhile(origin)) {
            copy((columbus.java.asg.statm.While) origin, (columbus.java.asg.statm.While) clone);
        }
        if (Common.getIsAnnotatedElement(origin)) {
            copy((columbus.java.asg.struc.AnnotatedElement) origin, (columbus.java.asg.struc.AnnotatedElement) clone);
        }
        if (Common.getIsAnnotationTypeElement(origin)) {
            copy((columbus.java.asg.struc.AnnotationTypeElement) origin, (columbus.java.asg.struc.AnnotationTypeElement) clone);
        }
        if (Common.getIsCompilationUnit(origin)) {
            copy((columbus.java.asg.struc.CompilationUnit) origin, (columbus.java.asg.struc.CompilationUnit) clone);
        }
        if (Common.getIsEnumConstant(origin)) {
            copy((columbus.java.asg.struc.EnumConstant) origin, (columbus.java.asg.struc.EnumConstant) clone);
        }
        if (Common.getIsGenericDeclaration(origin)) {
            copy((columbus.java.asg.struc.GenericDeclaration) origin, (columbus.java.asg.struc.GenericDeclaration) clone);
        }
        if (Common.getIsImport(origin)) {
            copy((columbus.java.asg.struc.Import) origin, (columbus.java.asg.struc.Import) clone);
        }
        if (Common.getIsInitializerBlock(origin)) {
            copy((columbus.java.asg.struc.InitializerBlock) origin, (columbus.java.asg.struc.InitializerBlock) clone);
        }
        if (Common.getIsMethodDeclaration(origin)) {
            copy((columbus.java.asg.struc.MethodDeclaration) origin, (columbus.java.asg.struc.MethodDeclaration) clone);
        }
        if (Common.getIsNamedDeclaration(origin)) {
            copy((columbus.java.asg.struc.NamedDeclaration) origin, (columbus.java.asg.struc.NamedDeclaration) clone);
        }
        if (Common.getIsNormalMethod(origin)) {
            copy((columbus.java.asg.struc.NormalMethod) origin, (columbus.java.asg.struc.NormalMethod) clone);
        }
        if (Common.getIsPackage(origin)) {
            copy((columbus.java.asg.struc.Package) origin, (columbus.java.asg.struc.Package) clone);
        }
        if (Common.getIsPackageDeclaration(origin)) {
            copy((columbus.java.asg.struc.PackageDeclaration) origin, (columbus.java.asg.struc.PackageDeclaration) clone);
        }
        if (Common.getIsParameter(origin)) {
            copy((columbus.java.asg.struc.Parameter) origin, (columbus.java.asg.struc.Parameter) clone);
        }
        if (Common.getIsScope(origin)) {
            copy((columbus.java.asg.struc.Scope) origin, (columbus.java.asg.struc.Scope) clone);
        }
        if (Common.getIsTypeDeclaration(origin)) {
            copy((columbus.java.asg.struc.TypeDeclaration) origin, (columbus.java.asg.struc.TypeDeclaration) clone);
        }
        if (Common.getIsTypeParameter(origin)) {
            copy((columbus.java.asg.struc.TypeParameter) origin, (columbus.java.asg.struc.TypeParameter) clone);
        }
        if (Common.getIsVariable(origin)) {
            copy((columbus.java.asg.struc.Variable) origin, (columbus.java.asg.struc.Variable) clone);
        }
        if (Common.getIsVariableDeclaration(origin)) {
            copy((columbus.java.asg.struc.VariableDeclaration) origin, (columbus.java.asg.struc.VariableDeclaration) clone);
        }
        if (Common.getIsArrayType(origin)) {
            copy((columbus.java.asg.type.ArrayType) origin, (columbus.java.asg.type.ArrayType) clone);
        }
        if (Common.getIsClassType(origin)) {
            copy((columbus.java.asg.type.ClassType) origin, (columbus.java.asg.type.ClassType) clone);
        }
        if (Common.getIsMethodType(origin)) {
            copy((columbus.java.asg.type.MethodType) origin, (columbus.java.asg.type.MethodType) clone);
        }
        if (Common.getIsPackageType(origin)) {
            copy((columbus.java.asg.type.PackageType) origin, (columbus.java.asg.type.PackageType) clone);
        }
        if (Common.getIsParameterizedType(origin)) {
            copy((columbus.java.asg.type.ParameterizedType) origin, (columbus.java.asg.type.ParameterizedType) clone);
        }
        if (Common.getIsScopedType(origin)) {
            copy((columbus.java.asg.type.ScopedType) origin, (columbus.java.asg.type.ScopedType) clone);
        }
        if (Common.getIsTypeVariable(origin)) {
            copy((columbus.java.asg.type.TypeVariable) origin, (columbus.java.asg.type.TypeVariable) clone);
        }
        if (Common.getIsUnionType(origin)) {
            copy((columbus.java.asg.type.UnionType) origin, (columbus.java.asg.type.UnionType) clone);
        }
        if (Common.getIsWildcardType(origin)) {
            copy((columbus.java.asg.type.WildcardType) origin, (columbus.java.asg.type.WildcardType) clone);
        }
    }

    private void copy(columbus.java.asg.base.Comment origin, columbus.java.asg.base.Comment clone) {
        clone.setText(origin.getText());
    }

    private void copy(columbus.java.asg.base.Commentable origin, columbus.java.asg.base.Commentable clone) {
        if (!origin.getCommentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.base.Comment> iter = origin.getCommentsIterator();
            while (iter.hasNext()) {
                clone.addComments(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.base.Named origin, columbus.java.asg.base.Named clone) {
        clone.setName(origin.getName());
    }

    private void copy(columbus.java.asg.base.Positioned origin, columbus.java.asg.base.Positioned clone) {
        clone.setIsCompilerGenerated(origin.getIsCompilerGenerated());
        clone.setIsToolGenerated(origin.getIsToolGenerated());
    }

    private void copy(columbus.java.asg.base.PositionedWithoutComment origin, columbus.java.asg.base.PositionedWithoutComment clone) {
        clone.setPosition(new Range(origin.getPosition()));
    }

    private void copy(columbus.java.asg.expr.Annotation origin, columbus.java.asg.expr.Annotation clone) {
        if (origin.getAnnotationName() != null) {
            clone.setAnnotationName(getMappedNode(origin.getAnnotationName()));
        }

    }

    private void copy(columbus.java.asg.expr.ArrayTypeExpression origin, columbus.java.asg.expr.ArrayTypeExpression clone) {
        if (origin.getComponentType() != null) {
            clone.setComponentType(getMappedNode(origin.getComponentType()));
        }

    }

    private void copy(columbus.java.asg.expr.Assignment origin, columbus.java.asg.expr.Assignment clone) {
        clone.setOperator(origin.getOperator());
    }

    private void copy(columbus.java.asg.expr.Binary origin, columbus.java.asg.expr.Binary clone) {
        if (origin.getLeftOperand() != null) {
            clone.setLeftOperand(getMappedNode(origin.getLeftOperand()));
        }

        if (origin.getRightOperand() != null) {
            clone.setRightOperand(getMappedNode(origin.getRightOperand()));
        }

    }

    private void copy(columbus.java.asg.expr.BooleanLiteral origin, columbus.java.asg.expr.BooleanLiteral clone) {
        clone.setBooleanValue(origin.getBooleanValue());
    }

    private void copy(columbus.java.asg.expr.CharacterLiteral origin, columbus.java.asg.expr.CharacterLiteral clone) {
        clone.setCharValue(origin.getCharValue());
        clone.setFormatString(origin.getFormatString());
    }

    private void copy(columbus.java.asg.expr.ClassLiteral origin, columbus.java.asg.expr.ClassLiteral clone) {
        if (origin.getComponentType() != null) {
            clone.setComponentType(getMappedNode(origin.getComponentType()));
        }

    }

    private void copy(columbus.java.asg.expr.Conditional origin, columbus.java.asg.expr.Conditional clone) {
        if (origin.getCondition() != null) {
            clone.setCondition(getMappedNode(origin.getCondition()));
        }

        if (origin.getTrueExpression() != null) {
            clone.setTrueExpression(getMappedNode(origin.getTrueExpression()));
        }

        if (origin.getFalseExpression() != null) {
            clone.setFalseExpression(getMappedNode(origin.getFalseExpression()));
        }

    }

    private void copy(columbus.java.asg.expr.DoubleLiteral origin, columbus.java.asg.expr.DoubleLiteral clone) {
        clone.setDoubleValue(origin.getDoubleValue());
    }

    private void copy(columbus.java.asg.expr.Erroneous origin, columbus.java.asg.expr.Erroneous clone) {
        if (!origin.getErrorsIsEmpty()) {
            EdgeIterator<columbus.java.asg.base.Positioned> iter = origin.getErrorsIterator();
            while (iter.hasNext()) {
                clone.addErrors(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.expr.ErroneousTypeExpression origin, columbus.java.asg.expr.ErroneousTypeExpression clone) {
        if (!origin.getErrorsIsEmpty()) {
            EdgeIterator<columbus.java.asg.base.Positioned> iter = origin.getErrorsIterator();
            while (iter.hasNext()) {
                clone.addErrors(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.expr.Expression origin, columbus.java.asg.expr.Expression clone) {
        if (origin.getType() != null) {
            clone.setType(getMappedNode(origin.getType()));
        }

    }

    private void copy(columbus.java.asg.expr.FloatLiteral origin, columbus.java.asg.expr.FloatLiteral clone) {
        clone.setFloatValue(origin.getFloatValue());
    }

    private void copy(columbus.java.asg.expr.Identifier origin, columbus.java.asg.expr.Identifier clone) {
        clone.setName(origin.getName());
        if (origin.getRefersTo() != null) {
            clone.setRefersTo(getMappedNode(origin.getRefersTo()));
        }

    }

    private void copy(columbus.java.asg.expr.InfixExpression origin, columbus.java.asg.expr.InfixExpression clone) {
        clone.setOperator(origin.getOperator());
    }

    private void copy(columbus.java.asg.expr.InstanceOf origin, columbus.java.asg.expr.InstanceOf clone) {
        if (origin.getTypeOperand() != null) {
            clone.setTypeOperand(getMappedNode(origin.getTypeOperand()));
        }

    }

    private void copy(columbus.java.asg.expr.IntegerLiteral origin, columbus.java.asg.expr.IntegerLiteral clone) {
        clone.setIntValue(origin.getIntValue());
    }

    private void copy(columbus.java.asg.expr.LongLiteral origin, columbus.java.asg.expr.LongLiteral clone) {
        clone.setLongValue(origin.getLongValue());
    }

    private void copy(columbus.java.asg.expr.MethodInvocation origin, columbus.java.asg.expr.MethodInvocation clone) {
        if (!origin.getTypeArgumentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getTypeArgumentsIterator();
            while (iter.hasNext()) {
                clone.addTypeArguments(getMappedNode(iter.next()));
            }
        }

        if (!origin.getArgumentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.Expression> iter = origin.getArgumentsIterator();
            while (iter.hasNext()) {
                clone.addArguments(getMappedNode(iter.next()));
            }
        }

        if (origin.getInvokes() != null) {
            clone.setInvokes(getMappedNode(origin.getInvokes()));
        }

    }

    private void copy(columbus.java.asg.expr.NewArray origin, columbus.java.asg.expr.NewArray clone) {
        if (origin.getComponentType() != null) {
            clone.setComponentType(getMappedNode(origin.getComponentType()));
        }

        if (!origin.getDimensionsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.Expression> iter = origin.getDimensionsIterator();
            while (iter.hasNext()) {
                clone.addDimensions(getMappedNode(iter.next()));
            }
        }

        if (!origin.getInitializersIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.Expression> iter = origin.getInitializersIterator();
            while (iter.hasNext()) {
                clone.addInitializers(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.expr.NewClass origin, columbus.java.asg.expr.NewClass clone) {
        if (origin.getEnclosingExpression() != null) {
            clone.setEnclosingExpression(getMappedNode(origin.getEnclosingExpression()));
        }

        if (!origin.getTypeArgumentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getTypeArgumentsIterator();
            while (iter.hasNext()) {
                clone.addTypeArguments(getMappedNode(iter.next()));
            }
        }

        if (origin.getTypeName() != null) {
            clone.setTypeName(getMappedNode(origin.getTypeName()));
        }

        if (!origin.getArgumentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.Expression> iter = origin.getArgumentsIterator();
            while (iter.hasNext()) {
                clone.addArguments(getMappedNode(iter.next()));
            }
        }

        if (origin.getAnonymousClass() != null) {
            clone.setAnonymousClass(getMappedNode(origin.getAnonymousClass()));
        }

        if (origin.getConstructor() != null) {
            clone.setConstructor(getMappedNode(origin.getConstructor()));
        }

    }

    private void copy(columbus.java.asg.expr.NormalAnnotation origin, columbus.java.asg.expr.NormalAnnotation clone) {
        if (!origin.getArgumentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.Expression> iter = origin.getArgumentsIterator();
            while (iter.hasNext()) {
                clone.addArguments(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.expr.NumberLiteral origin, columbus.java.asg.expr.NumberLiteral clone) {
        clone.setValue(origin.getValue());
    }

    private void copy(columbus.java.asg.expr.PostfixExpression origin, columbus.java.asg.expr.PostfixExpression clone) {
        clone.setOperator(origin.getOperator());
    }

    private void copy(columbus.java.asg.expr.PrefixExpression origin, columbus.java.asg.expr.PrefixExpression clone) {
        clone.setOperator(origin.getOperator());
    }

    private void copy(columbus.java.asg.expr.PrimitiveTypeExpression origin, columbus.java.asg.expr.PrimitiveTypeExpression clone) {
        clone.setKind(origin.getKind());
    }

    private void copy(columbus.java.asg.expr.QualifiedTypeExpression origin, columbus.java.asg.expr.QualifiedTypeExpression clone) {
        if (origin.getQualifierType() != null) {
            clone.setQualifierType(getMappedNode(origin.getQualifierType()));
        }

        if (origin.getSimpleType() != null) {
            clone.setSimpleType(getMappedNode(origin.getSimpleType()));
        }

    }

    private void copy(columbus.java.asg.expr.SimpleTypeExpression origin, columbus.java.asg.expr.SimpleTypeExpression clone) {
        clone.setName(origin.getName());
    }

    private void copy(columbus.java.asg.expr.SingleElementAnnotation origin, columbus.java.asg.expr.SingleElementAnnotation clone) {
        if (origin.getArgument() != null) {
            clone.setArgument(getMappedNode(origin.getArgument()));
        }

    }

    private void copy(columbus.java.asg.expr.StringLiteral origin, columbus.java.asg.expr.StringLiteral clone) {
        clone.setValue(origin.getValue());
        clone.setFormatString(origin.getFormatString());
    }

    private void copy(columbus.java.asg.expr.TypeApplyExpression origin, columbus.java.asg.expr.TypeApplyExpression clone) {
        if (origin.getRawType() != null) {
            clone.setRawType(getMappedNode(origin.getRawType()));
        }

        if (!origin.getTypeArgumentsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getTypeArgumentsIterator();
            while (iter.hasNext()) {
                clone.addTypeArguments(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.expr.TypeCast origin, columbus.java.asg.expr.TypeCast clone) {
        if (origin.getTypeOperand() != null) {
            clone.setTypeOperand(getMappedNode(origin.getTypeOperand()));
        }

    }

    private void copy(columbus.java.asg.expr.TypeUnionExpression origin, columbus.java.asg.expr.TypeUnionExpression clone) {
        if (!origin.getAlternativesIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getAlternativesIterator();
            while (iter.hasNext()) {
                clone.addAlternatives(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.expr.Unary origin, columbus.java.asg.expr.Unary clone) {
        if (origin.getOperand() != null) {
            clone.setOperand(getMappedNode(origin.getOperand()));
        }

    }

    private void copy(columbus.java.asg.expr.WildcardExpression origin, columbus.java.asg.expr.WildcardExpression clone) {
        clone.setKind(origin.getKind());
        if (origin.getBound() != null) {
            clone.setBound(getMappedNode(origin.getBound()));
        }

    }

    private void copy(columbus.java.asg.statm.Assert origin, columbus.java.asg.statm.Assert clone) {
        if (origin.getCondition() != null) {
            clone.setCondition(getMappedNode(origin.getCondition()));
        }

        if (origin.getDetail() != null) {
            clone.setDetail(getMappedNode(origin.getDetail()));
        }

    }

    private void copy(columbus.java.asg.statm.BasicFor origin, columbus.java.asg.statm.BasicFor clone) {
        if (!origin.getInitializersIsEmpty()) {
            EdgeIterator<columbus.java.asg.statm.Statement> iter = origin.getInitializersIterator();
            while (iter.hasNext()) {
                clone.addInitializers(getMappedNode(iter.next()));
            }
        }

        if (origin.getCondition() != null) {
            clone.setCondition(getMappedNode(origin.getCondition()));
        }

        if (!origin.getUpdatesIsEmpty()) {
            EdgeIterator<columbus.java.asg.statm.Statement> iter = origin.getUpdatesIterator();
            while (iter.hasNext()) {
                clone.addUpdates(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.statm.Block origin, columbus.java.asg.statm.Block clone) {
        if (!origin.getStatementsIsEmpty()) {
            EdgeIterator<columbus.java.asg.statm.Statement> iter = origin.getStatementsIterator();
            while (iter.hasNext()) {
                clone.addStatements(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.statm.Case origin, columbus.java.asg.statm.Case clone) {
        if (origin.getExpression() != null) {
            clone.setExpression(getMappedNode(origin.getExpression()));
        }

    }

    private void copy(columbus.java.asg.statm.Do origin, columbus.java.asg.statm.Do clone) {
        if (origin.getCondition() != null) {
            clone.setCondition(getMappedNode(origin.getCondition()));
        }

    }

    private void copy(columbus.java.asg.statm.EnhancedFor origin, columbus.java.asg.statm.EnhancedFor clone) {
        if (origin.getParameter() != null) {
            clone.setParameter(getMappedNode(origin.getParameter()));
        }

        if (origin.getExpression() != null) {
            clone.setExpression(getMappedNode(origin.getExpression()));
        }

    }

    private void copy(columbus.java.asg.statm.ExpressionStatement origin, columbus.java.asg.statm.ExpressionStatement clone) {
        if (origin.getExpression() != null) {
            clone.setExpression(getMappedNode(origin.getExpression()));
        }

    }

    private void copy(columbus.java.asg.statm.Handler origin, columbus.java.asg.statm.Handler clone) {
        if (origin.getParameter() != null) {
            clone.setParameter(getMappedNode(origin.getParameter()));
        }

        if (origin.getBlock() != null) {
            clone.setBlock(getMappedNode(origin.getBlock()));
        }

    }

    private void copy(columbus.java.asg.statm.If origin, columbus.java.asg.statm.If clone) {
        if (origin.getSubstatement() != null) {
            clone.setSubstatement(getMappedNode(origin.getSubstatement()));
        }

        if (origin.getFalseSubstatement() != null) {
            clone.setFalseSubstatement(getMappedNode(origin.getFalseSubstatement()));
        }

    }

    private void copy(columbus.java.asg.statm.Iteration origin, columbus.java.asg.statm.Iteration clone) {
        if (origin.getSubstatement() != null) {
            clone.setSubstatement(getMappedNode(origin.getSubstatement()));
        }

    }

    private void copy(columbus.java.asg.statm.Jump origin, columbus.java.asg.statm.Jump clone) {
        clone.setLabel(origin.getLabel());
        if (origin.getTarget() != null) {
            clone.setTarget(getMappedNode(origin.getTarget()));
        }

    }

    private void copy(columbus.java.asg.statm.LabeledStatement origin, columbus.java.asg.statm.LabeledStatement clone) {
        clone.setLabel(origin.getLabel());
        if (origin.getStatement() != null) {
            clone.setStatement(getMappedNode(origin.getStatement()));
        }

    }

    private void copy(columbus.java.asg.statm.Return origin, columbus.java.asg.statm.Return clone) {
        if (origin.getExpression() != null) {
            clone.setExpression(getMappedNode(origin.getExpression()));
        }

    }

    private void copy(columbus.java.asg.statm.Selection origin, columbus.java.asg.statm.Selection clone) {
        if (origin.getCondition() != null) {
            clone.setCondition(getMappedNode(origin.getCondition()));
        }

    }

    private void copy(columbus.java.asg.statm.Switch origin, columbus.java.asg.statm.Switch clone) {
        if (!origin.getCasesIsEmpty()) {
            EdgeIterator<columbus.java.asg.statm.SwitchLabel> iter = origin.getCasesIterator();
            while (iter.hasNext()) {
                clone.addCases(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.statm.SwitchLabel origin, columbus.java.asg.statm.SwitchLabel clone) {
        if (!origin.getStatementsIsEmpty()) {
            EdgeIterator<columbus.java.asg.statm.Statement> iter = origin.getStatementsIterator();
            while (iter.hasNext()) {
                clone.addStatements(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.statm.Synchronized origin, columbus.java.asg.statm.Synchronized clone) {
        if (origin.getLock() != null) {
            clone.setLock(getMappedNode(origin.getLock()));
        }

        if (origin.getBlock() != null) {
            clone.setBlock(getMappedNode(origin.getBlock()));
        }

    }

    private void copy(columbus.java.asg.statm.Throw origin, columbus.java.asg.statm.Throw clone) {
        if (origin.getExpression() != null) {
            clone.setExpression(getMappedNode(origin.getExpression()));
        }

    }

    private void copy(columbus.java.asg.statm.Try origin, columbus.java.asg.statm.Try clone) {
        if (!origin.getResourcesIsEmpty()) {
            EdgeIterator<columbus.java.asg.base.Base> iter = origin.getResourcesIterator();
            while (iter.hasNext()) {
                clone.addResources(getMappedNode(iter.next()));
            }
        }

        if (origin.getBlock() != null) {
            clone.setBlock(getMappedNode(origin.getBlock()));
        }

        if (!origin.getHandlersIsEmpty()) {
            EdgeIterator<columbus.java.asg.statm.Handler> iter = origin.getHandlersIterator();
            while (iter.hasNext()) {
                clone.addHandlers(getMappedNode(iter.next()));
            }
        }

        if (origin.getFinallyBlock() != null) {
            clone.setFinallyBlock(getMappedNode(origin.getFinallyBlock()));
        }

    }

    private void copy(columbus.java.asg.statm.While origin, columbus.java.asg.statm.While clone) {
        if (origin.getCondition() != null) {
            clone.setCondition(getMappedNode(origin.getCondition()));
        }

    }

    private void copy(columbus.java.asg.struc.AnnotatedElement origin, columbus.java.asg.struc.AnnotatedElement clone) {
        if (!origin.getAnnotationsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.Annotation> iter = origin.getAnnotationsIterator();
            while (iter.hasNext()) {
                clone.addAnnotations(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.AnnotationTypeElement origin, columbus.java.asg.struc.AnnotationTypeElement clone) {
        if (origin.getDefaultValue() != null) {
            clone.setDefaultValue(getMappedNode(origin.getDefaultValue()));
        }

    }

    private void copy(columbus.java.asg.struc.CompilationUnit origin, columbus.java.asg.struc.CompilationUnit clone) {
        clone.setFileEncoding(origin.getFileEncoding());
        if (origin.getPackageDeclaration() != null) {
            clone.setPackageDeclaration(getMappedNode(origin.getPackageDeclaration()));
        }

        if (!origin.getImportsIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.Import> iter = origin.getImportsIterator();
            while (iter.hasNext()) {
                clone.addImports(getMappedNode(iter.next()));
            }
        }

        if (!origin.getTypeDeclarationsIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.TypeDeclaration> iter = origin.getTypeDeclarationsIterator();
            while (iter.hasNext()) {
                clone.addTypeDeclarations(getMappedNode(iter.next()));
            }
        }

        if (!origin.getOthersIsEmpty()) {
            EdgeIterator<columbus.java.asg.base.Positioned> iter = origin.getOthersIterator();
            while (iter.hasNext()) {
                clone.addOthers(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.EnumConstant origin, columbus.java.asg.struc.EnumConstant clone) {
        if (origin.getNewClass() != null) {
            clone.setNewClass(getMappedNode(origin.getNewClass()));
        }

    }

    private void copy(columbus.java.asg.struc.GenericDeclaration origin, columbus.java.asg.struc.GenericDeclaration clone) {
        if (!origin.getTypeParametersIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.TypeParameter> iter = origin.getTypeParametersIterator();
            while (iter.hasNext()) {
                clone.addTypeParameters(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.Import origin, columbus.java.asg.struc.Import clone) {
        clone.setIsStatic(origin.getIsStatic());
        if (origin.getTarget() != null) {
            clone.setTarget(getMappedNode(origin.getTarget()));
        }

    }

    private void copy(columbus.java.asg.struc.InitializerBlock origin, columbus.java.asg.struc.InitializerBlock clone) {
        if (origin.getBody() != null) {
            clone.setBody(getMappedNode(origin.getBody()));
        }

    }

    private void copy(columbus.java.asg.struc.MethodDeclaration origin, columbus.java.asg.struc.MethodDeclaration clone) {
        clone.setIsAbstract(origin.getIsAbstract());
        clone.setIsStrictfp(origin.getIsStrictfp());
        if (origin.getReturnType() != null) {
            clone.setReturnType(getMappedNode(origin.getReturnType()));
        }

        if (origin.getMethodType() != null) {
            clone.setMethodType(getMappedNode(origin.getMethodType()));
        }

        if (!origin.getOverridesIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.MethodDeclaration> iter = origin.getOverridesIterator();
            while (iter.hasNext()) {
                clone.addOverrides(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.NamedDeclaration origin, columbus.java.asg.struc.NamedDeclaration clone) {
        clone.setAccessibility(origin.getAccessibility());
        clone.setIsStatic(origin.getIsStatic());
        clone.setIsFinal(origin.getIsFinal());
    }

    private void copy(columbus.java.asg.struc.NormalMethod origin, columbus.java.asg.struc.NormalMethod clone) {
        clone.setMethodKind(origin.getMethodKind());
        clone.setIsSynchronized(origin.getIsSynchronized());
        clone.setIsNative(origin.getIsNative());
        if (!origin.getParametersIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.Parameter> iter = origin.getParametersIterator();
            while (iter.hasNext()) {
                clone.addParameters(getMappedNode(iter.next()));
            }
        }

        if (origin.getBody() != null) {
            clone.setBody(getMappedNode(origin.getBody()));
        }

        if (!origin.getThrownExceptionsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getThrownExceptionsIterator();
            while (iter.hasNext()) {
                clone.addThrownExceptions(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.Package origin, columbus.java.asg.struc.Package clone) {
        clone.setQualifiedName(origin.getQualifiedName());
        if (!origin.getCompilationUnitsIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.CompilationUnit> iter = origin.getCompilationUnitsIterator();
            while (iter.hasNext()) {
                clone.addCompilationUnits(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.PackageDeclaration origin, columbus.java.asg.struc.PackageDeclaration clone) {
        if (origin.getPackageName() != null) {
            clone.setPackageName(getMappedNode(origin.getPackageName()));
        }

        if (origin.getRefersTo() != null) {
            clone.setRefersTo(getMappedNode(origin.getRefersTo()));
        }

    }

    private void copy(columbus.java.asg.struc.Parameter origin, columbus.java.asg.struc.Parameter clone) {
        clone.setIsVarargs(origin.getIsVarargs());
    }

    private void copy(columbus.java.asg.struc.Scope origin, columbus.java.asg.struc.Scope clone) {
        if (!origin.getMembersIsEmpty()) {
            EdgeIterator<columbus.java.asg.struc.Member> iter = origin.getMembersIterator();
            while (iter.hasNext()) {
                clone.addMembers(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.TypeDeclaration origin, columbus.java.asg.struc.TypeDeclaration clone) {
        clone.setIsAbstract(origin.getIsAbstract());
        clone.setIsStrictfp(origin.getIsStrictfp());
        clone.setBinaryName(origin.getBinaryName());
        if (origin.getIsInCompilationUnit() != null) {
            clone.setIsInCompilationUnit(getMappedNode(origin.getIsInCompilationUnit()));
        }

        if (origin.getSuperClass() != null) {
            clone.setSuperClass(getMappedNode(origin.getSuperClass()));
        }

        if (!origin.getSuperInterfacesIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getSuperInterfacesIterator();
            while (iter.hasNext()) {
                clone.addSuperInterfaces(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.TypeParameter origin, columbus.java.asg.struc.TypeParameter clone) {
        if (!origin.getBoundsIsEmpty()) {
            EdgeIterator<columbus.java.asg.expr.TypeExpression> iter = origin.getBoundsIterator();
            while (iter.hasNext()) {
                clone.addBounds(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.struc.Variable origin, columbus.java.asg.struc.Variable clone) {
        clone.setIsTransient(origin.getIsTransient());
        clone.setIsVolatile(origin.getIsVolatile());
        if (origin.getInitialValue() != null) {
            clone.setInitialValue(getMappedNode(origin.getInitialValue()));
        }

    }

    private void copy(columbus.java.asg.struc.VariableDeclaration origin, columbus.java.asg.struc.VariableDeclaration clone) {
        if (origin.getType() != null) {
            clone.setType(getMappedNode(origin.getType()));
        }

    }

    private void copy(columbus.java.asg.type.ArrayType origin, columbus.java.asg.type.ArrayType clone) {
        clone.setSize(origin.getSize());
        if (origin.getComponentType() != null) {
            clone.setComponentType(getMappedNode(origin.getComponentType()));
        }

    }

    private void copy(columbus.java.asg.type.ClassType origin, columbus.java.asg.type.ClassType clone) {
        if (origin.getRefersTo() != null) {
            clone.setRefersTo(getMappedNode(origin.getRefersTo()));
        }

    }

    private void copy(columbus.java.asg.type.MethodType origin, columbus.java.asg.type.MethodType clone) {
        if (origin.getReturnType() != null) {
            clone.setReturnType(getMappedNode(origin.getReturnType()));
        }

        if (!origin.getParameterTypesIsEmpty()) {
            EdgeIterator<columbus.java.asg.type.Type> iter = origin.getParameterTypesIterator();
            while (iter.hasNext()) {
                clone.addParameterTypes(getMappedNode(iter.next()));
            }
        }

        if (!origin.getThrownTypesIsEmpty()) {
            EdgeIterator<columbus.java.asg.type.Type> iter = origin.getThrownTypesIterator();
            while (iter.hasNext()) {
                clone.addThrownTypes(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.type.PackageType origin, columbus.java.asg.type.PackageType clone) {
        if (origin.getRefersTo() != null) {
            clone.setRefersTo(getMappedNode(origin.getRefersTo()));
        }

    }

    private void copy(columbus.java.asg.type.ParameterizedType origin, columbus.java.asg.type.ParameterizedType clone) {
        if (origin.getRawType() != null) {
            clone.setRawType(getMappedNode(origin.getRawType()));
        }

        if (!origin.getArgumentTypesIsEmpty()) {
            EdgeIterator<columbus.java.asg.type.Type> iter = origin.getArgumentTypesIterator();
            while (iter.hasNext()) {
                clone.addArgumentTypes(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.type.ScopedType origin, columbus.java.asg.type.ScopedType clone) {
        if (origin.getOwner() != null) {
            clone.setOwner(getMappedNode(origin.getOwner()));
        }

    }

    private void copy(columbus.java.asg.type.TypeVariable origin, columbus.java.asg.type.TypeVariable clone) {
        if (origin.getRefersTo() != null) {
            clone.setRefersTo(getMappedNode(origin.getRefersTo()));
        }

    }

    private void copy(columbus.java.asg.type.UnionType origin, columbus.java.asg.type.UnionType clone) {
        if (!origin.getAlternativesIsEmpty()) {
            EdgeIterator<columbus.java.asg.type.Type> iter = origin.getAlternativesIterator();
            while (iter.hasNext()) {
                clone.addAlternatives(getMappedNode(iter.next()));
            }
        }

    }

    private void copy(columbus.java.asg.type.WildcardType origin, columbus.java.asg.type.WildcardType clone) {
        if (origin.getBound() != null) {
            clone.setBound(getMappedNode(origin.getBound()));
        }

    }

}
