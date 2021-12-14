package coderepair.repair.model;

import coderepair.repair.base.ModelRepairing;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.struc.Variable;

public class ChangeFinalModifier implements ModelRepairing {
    final private Variable variable;
    final private boolean shouldBeFinal;

    /**
     * The Constructor.
     *
     * @param variable The actual Variable.
     * @param shouldBeFinal The value of the desired final flag
     * @throws RepairAlgorithmException If the Variable is not correct.
     */
    public ChangeFinalModifier(Variable variable, boolean shouldBeFinal) throws RepairAlgorithmException {
        if (variable == null) {
            throw new RepairAlgorithmException("The variable should not be null!");
        }

        this.variable = variable;
        this.shouldBeFinal = shouldBeFinal;
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) {
        variable.setIsFinal(shouldBeFinal);
        modifiedNodes.markAttributeAsModified(variable.getId(), NodeKind.ndkVariable);
    }

    @Override
    public String getToolName() {
        return "ChangeFinalModifier";
    }
}
