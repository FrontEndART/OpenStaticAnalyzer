package coderepair.repair.model;

import coderepair.repair.utils.TypeExpressionExtractor;
import coderepair.generator.transformation.TransformationAPI;
import coderepair.repair.base.ModelRepairing;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Factory;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.*;
import columbus.java.asg.type.Type;

/**
 * CloneInvocation refactoring which replace the identifier with its clone.
 */
public class CloneInvocation implements ModelRepairing {
    final private Factory fact;
    final private Expression expr;

    /**
     * The Constructor.
     *
     * @param expr The actual Identifier.
     * @throws RepairAlgorithmException If the Identifier is not correct.
     */
    public CloneInvocation(Expression expr) throws RepairAlgorithmException {
        if (expr == null) {
            throw new RepairAlgorithmException("The expression should not be null!");
        }

        this.expr = expr;
        fact = this.expr.getFactory();
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) {
        Type type=this.expr.getType();

        MethodInvocation mi = (MethodInvocation) this.fact.createNode(NodeKind.ndkMethodInvocation);

        TypeExpression stype = TypeExpressionExtractor.createTypeExpressionFromType(type, fact);
        if (stype != null)
            stype.setType(type);
        TypeCast typecast = (TypeCast) this.fact.createNode(NodeKind.ndkTypeCast);
        if (type != null)
            typecast.setType(type);
        typecast.setOperand(mi);
        if (stype != null)
            typecast.setTypeOperand(stype);

        FieldAccess fa = (FieldAccess) this.fact.createNode(NodeKind.ndkFieldAccess);

        Identifier clone = (Identifier) this.fact.createNode(NodeKind.ndkIdentifier);
        clone.setName("clone");
        fa.setRightOperand(clone);

        mi.setOperand(fa);
        TransformationAPI.swapStatements(this.expr, typecast);
        modifiedNodes.markNodeReplaced(this.expr.getId(), (typecast.getId()));

        fa.setLeftOperand(expr);
        modifiedNodes.markNodeAsUnchanged(this.expr.getId());
    }

    @Override
    public String getToolName() {
        return "CloneInvocation";
    }

}
