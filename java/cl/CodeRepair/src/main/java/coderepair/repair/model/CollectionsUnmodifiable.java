package coderepair.repair.model;

import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.generator.algorithm.AlgorithmSrcGenerator;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.TransformationAPI;
import coderepair.generator.visitor.SrcGeneratorVisitor;
import coderepair.repair.base.ModelRepairing;

import coderepair.repair.utils.ImportChecker;
import columbus.java.asg.Factory;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.*;
import columbus.java.asg.struc.Variable;
import columbus.java.asg.support.DeepCopyAlgorithm;


public class CollectionsUnmodifiable implements ModelRepairing {

    final private Variable variable;
    final private Factory factory;

    public CollectionsUnmodifiable(Variable id) {
        this.variable = id;
        this.factory = id.getFactory();
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) throws RepairAlgorithmException {
        makeUnmodifiableCollectionCall(variable, modifiedNodes);
    }

    private void makeUnmodifiableCollectionCall(Variable variable, ModifiedNodes modifiedNodes)  {
        FieldAccess fieldA = (FieldAccess) this.factory.createNode(NodeKind.ndkFieldAccess);
        DeepCopyAlgorithm dca = new DeepCopyAlgorithm(this.factory);
        ImportChecker.checkImport(this.factory, variable, "java.util.Collections", modifiedNodes);

        if (variable.getInitialValue() == null)
            return;
        Expression expr = variable.getInitialValue();
        MethodInvocation methInv = (MethodInvocation) this.factory.createNode(NodeKind.ndkMethodInvocation);
        TransformationAPI.swapStatements(expr, methInv);
        Identifier unmodifiableXXX = (Identifier) this.factory.createNode(NodeKind.ndkIdentifier);
        if (variable.getType() != null) {
            SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();
            (new AlgorithmSrcGenerator()).mainRun(visitor, variable.getType());
            String typeString = visitor.toString();
            if (typeString.contains("Set"))
                unmodifiableXXX.setName("unmodifiableSet");
            else if (typeString.contains("List"))
                unmodifiableXXX.setName("unmodifiableList");
            else return;
        }
        Identifier collectionsClass = (Identifier) this.factory.createNode(NodeKind.ndkIdentifier);
        collectionsClass.setName("Collections");

        FieldAccess fieldM = (FieldAccess) this.factory.createNode(NodeKind.ndkFieldAccess);
        fieldM.setLeftOperand(collectionsClass);
        fieldM.setRightOperand(unmodifiableXXX);
        methInv.setOperand(fieldM);

        methInv.addArguments(expr);

        modifiedNodes.markNodeReplaced(expr.getId(), methInv.getId());
    }

    @Override
    public String getToolName() {
        return "MakeUnmodofiableCollection";
    }

}
