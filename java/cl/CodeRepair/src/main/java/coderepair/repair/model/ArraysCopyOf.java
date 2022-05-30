package coderepair.repair.model;

import java.util.*;

import coderepair.repair.base.ModelRepairing;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.TransformationAPI;

import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.repair.utils.ImportChecker;
import columbus.java.asg.Common;
import columbus.java.asg.Factory;
import columbus.java.asg.base.Base;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.FieldAccess;
import columbus.java.asg.expr.Identifier;
import columbus.java.asg.expr.MethodInvocation;
import columbus.java.asg.support.DeepCopyAlgorithm;

public class ArraysCopyOf implements ModelRepairing {

    final private Expression expression;
    final private Factory factory;

    public ArraysCopyOf(Identifier expression) {
        this.expression = expression;
        this.factory = expression.getFactory();
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) throws RepairAlgorithmException {
            Base parentNode = expression.getParent();
            if (Common.getIsAssignment(parentNode)) {
                makeArrayCopyForAnAssignmentRightOperand(expression, modifiedNodes);
            }
    }

    private void makeArrayCopyForAnAssignmentRightOperand(Expression expr, ModifiedNodes modifiedNodes)  {
        FieldAccess fieldA = (FieldAccess) this.factory.createNode(NodeKind.ndkFieldAccess);
        DeepCopyAlgorithm dca = new DeepCopyAlgorithm(this.factory);
        ImportChecker.checkImport(this.factory, expr, "java.util.Arrays", modifiedNodes);
        Map<Integer, Integer> idMap;
        idMap = dca.runDeepCopy(expr.getId());
        Expression left = (Expression) this.factory.getRef(idMap.get(expr.getId()));
        idMap.clear();
        Identifier identRight = (Identifier) this.factory.createNode(NodeKind.ndkIdentifier);
        identRight.setName("length");

        fieldA.setLeftOperand(left);
        fieldA.setRightOperand(identRight);

        MethodInvocation methInv = (MethodInvocation) this.factory.createNode(NodeKind.ndkMethodInvocation);
        TransformationAPI.swapStatements(expr, methInv);
        Identifier arrayCopyOf = (Identifier) this.factory.createNode(NodeKind.ndkIdentifier);
        arrayCopyOf.setName("copyOf");
        Identifier arraysClass = (Identifier) this.factory.createNode(NodeKind.ndkIdentifier);
        arraysClass.setName("Arrays");


        FieldAccess fieldM = (FieldAccess) this.factory.createNode(NodeKind.ndkFieldAccess);
        fieldM.setLeftOperand(arraysClass);
        fieldM.setRightOperand(arrayCopyOf);
        methInv.setOperand(fieldM);

        methInv.addArguments(expr);
        methInv.addArguments(fieldA);

        modifiedNodes.markNodeReplaced(expr.getId(), methInv.getId());
    }

    @Override
    public String getToolName() {
        return "MakeArrayCopy";
    }

}
