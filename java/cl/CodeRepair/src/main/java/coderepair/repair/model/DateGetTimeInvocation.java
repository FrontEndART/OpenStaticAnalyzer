package coderepair.repair.model;

import coderepair.repair.base.ModelRepairing;
import coderepair.repair.utils.TypeExpressionExtractor;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.TransformationAPI;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Common;
import columbus.java.asg.Factory;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.*;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.type.ClassType;
import columbus.java.asg.type.Type;


/**
 * DateGetTimeInvocation repair which replace the Date object with a new Date(obj.getTime()).
 */
public class DateGetTimeInvocation implements ModelRepairing {
    final private Factory fact;
    final private Expression expr;

    /**
     * The Constructor.
     *
     * @param expr The actual Identifier.
     * @throws RepairAlgorithmException If the Identifier is not correct.
     */
    public DateGetTimeInvocation(Expression expr) throws RepairAlgorithmException {
        if (expr == null) {
            throw new RepairAlgorithmException("The expression should not be null!");
        }
        this.expr = expr;
        fact = this.expr.getFactory();
    }

    public boolean isDateExpression () {
        Type type = expr.getType();
        if (Common.getIsClassType(type)) {
            TypeDeclaration td = ((ClassType) type).getRefersTo();
            if ("Date".equals(td.getName())) {
                return true;
            }
            //TODO checking fully qualified name
        }
        return false;
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) {
        if (!isDateExpression())
            return;

        Type type=this.expr.getType();

        TypeExpression stype = TypeExpressionExtractor.createTypeExpressionFromType(type, fact);
        NewClass newClass = (NewClass) this.fact.createNode(NodeKind.ndkNewClass);
        newClass.setTypeName(stype);
        TransformationAPI.swapStatements(this.expr, newClass);

        MethodInvocation mi = (MethodInvocation) this.fact.createNode(NodeKind.ndkMethodInvocation);
        newClass.addArguments(mi);
        FieldAccess fa = (FieldAccess) this.fact.createNode(NodeKind.ndkFieldAccess);
        mi.setOperand(fa);
        fa.setLeftOperand(expr);
        Identifier identifier = (Identifier) this.fact.createNode(NodeKind.ndkIdentifier);
        identifier.setName("getTime");
        fa.setRightOperand(identifier);
        modifiedNodes.markNodeReplaced(this.expr.getId(), (newClass.getId()));
        modifiedNodes.markNodeAsUnchanged(this.expr.getId());
    }

    @Override
    public String getToolName() {
        return "DateGetTimeInvocation";
    }

}
