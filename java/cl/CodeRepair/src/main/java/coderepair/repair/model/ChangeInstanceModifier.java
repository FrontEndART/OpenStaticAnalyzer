package coderepair.repair.model;

import coderepair.repair.base.ModelRepairing;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.enums.AccessibilityKind;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.struc.Variable;

public class ChangeInstanceModifier implements ModelRepairing {
    final private Variable variable;
    private boolean shouldBeFinal = false;
    private boolean shouldBePackagePrivate = false;

    /**
     * The Constructor.
     *
     * @param variable The actual Variable.
     * @throws RepairAlgorithmException If the Variable is not correct.
     */
    public ChangeInstanceModifier(Variable variable) throws RepairAlgorithmException {
        if (variable == null) {
            throw new RepairAlgorithmException("The variable should not be null!");
        }

        this.variable = variable;
    }

    public void setShouldBeFinal(boolean flag) {
        shouldBeFinal = flag;
    }

    public void setShouldBePackagePrivate(boolean flag) {
        shouldBePackagePrivate = flag;
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) {
        if (shouldBeFinal)
            variable.setIsFinal(shouldBeFinal);
        if (shouldBePackagePrivate)
            variable.setAccessibility(AccessibilityKind.ackNone);
        modifiedNodes.markAttributeAsModified(variable.getId(), NodeKind.ndkVariable);
    }

    @Override
    public String getToolName() {
        return "ChangeFinalModifier";
    }
}
