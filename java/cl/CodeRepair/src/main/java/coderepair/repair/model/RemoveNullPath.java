package coderepair.repair.model;
import coderepair.generator.transformation.TransformationAPI;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Common;

import columbus.java.asg.Factory;
import columbus.java.asg.base.Base;
import columbus.java.asg.enums.InfixOperatorKind;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.*;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.repair.base.ModelRepairing;
import columbus.java.asg.statm.BasicFor;
import columbus.java.asg.statm.Do;
import columbus.java.asg.statm.Return;
import columbus.java.asg.statm.While;
import columbus.java.asg.struc.Variable;
import columbus.java.asg.support.DeepCopyAlgorithm;
import columbus.java.asg.type.Type;

import java.util.Map;

/**
 * RemoveNullPath modifies code so that the result is less sensitive to a null pointer occurring on a given path.
 */
public class RemoveNullPath implements ModelRepairing {

    final private Expression expr;
    final private Factory factory;

    /**
     * The Constructor.
     *
     * @param expr The reference of a potential null pointer.
     * @throws RepairAlgorithmException If the MethodInvocation is not correct.
     */
    public RemoveNullPath(Expression expr) throws RepairAlgorithmException {
        if (expr == null) {
            throw new RepairAlgorithmException("The identifier should not be null!");
        }
        this.expr = expr;
        factory = expr.getFactory();
    }

    private Base getParentCondition(Expression expression) {
        Base base = expression;
        while (true) {
            Base parent = base.getParent();
            if (parent != null && Common.getIsBasicFor(parent) && ((BasicFor)parent).getCondition() == base) return base;
            if (parent != null && Common.getIsDo(parent) && ((Do)parent).getCondition() == base) return base;
            if (parent != null && Common.getIsWhile(parent) && ((While)parent).getCondition() == base) return base;
            if (parent != null && Common.getIsConditional(parent) && ((Conditional)parent).getCondition() == base) return base;
            if (Common.getIsStatement(base)) return null;
            base = parent;
        }
    }

    private Base getParentAssignmentRightSide(Expression expression) {
        Base base = expression;
        while (true) {
            Base parent = base.getParent();
            if (parent != null && Common.getIsAssignment(parent) && ((Assignment)parent).getRightOperand() == base) return base;
            if (Common.getIsStatement(base)) return null;
            base = parent;
        }
    }

    private Base getExpressionOfReturnStatement(Expression expression) {
        Base base = expression;
        while (true) {
            Base parent = base.getParent();
            if (parent != null && Common.getIsReturn(parent) && ((Return)parent).getExpression() == base) return base;
            if (Common.getIsStatement(base)) return null;
            base = parent;
        }
    }

    private Base getInitValueOfVariable(Expression expression) {
        Base base = expression;
        while (true) {
            Base parent = base.getParent();
            if (parent != null && Common.getIsVariable(parent) && ((Variable)parent).getInitialValue() == base) return base;
            if (Common.getIsStatement(base)) return null;
            base = parent;
        }
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) {
        Expression cond = (Expression) getParentCondition(expr);
        if (cond != null) {
            DeepCopyAlgorithm dca = new DeepCopyAlgorithm(this.factory);
            Map<Integer, Integer> idMap;
            idMap = dca.runDeepCopy(expr.getId());
            Expression left = (Expression) this.factory.getRef(idMap.get(expr.getId()));
            idMap.clear();

            InfixExpression infix1 = (InfixExpression) this.factory.createNode(NodeKind.ndkInfixExpression);
            NullLiteral nullLiteral = (NullLiteral) this.factory.createNode(NodeKind.ndkNullLiteral);
            infix1.setOperator(InfixOperatorKind.iokNotEqualTo);
            infix1.setLeftOperand(left);
            infix1.setRightOperand(nullLiteral);


            InfixExpression infix2 = (InfixExpression) this.factory.createNode(NodeKind.ndkInfixExpression);
            infix2.setOperator(InfixOperatorKind.iokConditionalAnd);
            infix2.setLeftOperand(infix1);

            TransformationAPI.swapStatements(cond, infix2);
            infix2.setRightOperand(cond);
            modifiedNodes.markNodeReplaced(cond.getId(), (infix2.getId()));
            modifiedNodes.markNodeAsUnchanged(cond.getId());
        } else {
            Expression parentExpression = (Expression)getExpressionOfReturnStatement(expr);
            if (parentExpression == null) {
                parentExpression = (Expression)getParentAssignmentRightSide(expr);
            }
            if (parentExpression == null) {
                parentExpression = (Expression)getInitValueOfVariable(expr);
            }
            if (parentExpression != null) {
                Type parentType = parentExpression.getType();
                Expression literal=null;
                if (Common.getIsPrimitiveType(parentType)) {
                    literal = (IntegerLiteral)factory.createNode(NodeKind.ndkIntegerLiteral);
                    ((IntegerLiteral)literal).setIntValue(0);
                    ((IntegerLiteral)literal).setValue("0");
                } else {
                    literal = (NullLiteral) factory.createNode(NodeKind.ndkNullLiteral);
                }

                DeepCopyAlgorithm dca = new DeepCopyAlgorithm(this.factory);
                Map<Integer, Integer> idMap;
                idMap = dca.runDeepCopy(expr.getId());
                Expression left = (Expression) this.factory.getRef(idMap.get(expr.getId()));
                idMap.clear();

                InfixExpression infix1 = (InfixExpression) this.factory.createNode(NodeKind.ndkInfixExpression);
                NullLiteral nullLiteral = (NullLiteral) this.factory.createNode(NodeKind.ndkNullLiteral);
                infix1.setOperator(InfixOperatorKind.iokNotEqualTo);
                infix1.setLeftOperand(left);
                infix1.setRightOperand(nullLiteral);

                Conditional cexpr = (Conditional)factory.createNode(NodeKind.ndkConditional);
                cexpr.setCondition(infix1);

                cexpr.setFalseExpression(literal);

                TransformationAPI.swapStatements(parentExpression, cexpr);
                cexpr.setTrueExpression(parentExpression);
                modifiedNodes.markNodeReplaced(parentExpression.getId(), (cexpr.getId()));
                modifiedNodes.markNodeAsUnchanged(parentExpression.getId());
            }
        }
    }

    @Override
    public String getToolName() {
        return "RemoveNullPath";
    }

}
