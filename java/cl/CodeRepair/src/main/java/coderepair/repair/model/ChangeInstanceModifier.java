package coderepair.repair.model;

import coderepair.repair.base.ModelRepairing;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.enums.AccessibilityKind;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.struc.NamedDeclaration;

public class ChangeInstanceModifier implements ModelRepairing {
    final private NamedDeclaration declaration;
    private boolean shouldBeFinal = false;
    private boolean shouldBePackagePrivate = false;
    private boolean shouldBeProtected = false;

    /**
     * The Constructor.
     *
     * @param variable The actual Variable.
     * @throws RepairAlgorithmException If the Variable is not correct.
     */
    public ChangeInstanceModifier(NamedDeclaration variable) throws RepairAlgorithmException {
        if (variable == null) {
            throw new RepairAlgorithmException("The declaration should not be null!");
        }

        this.declaration = variable;
    }

    public void setShouldBeFinal(boolean flag) {
        shouldBeFinal = flag;
    }

    public void setShouldBePackagePrivate(boolean flag) {
        shouldBePackagePrivate = flag;
    }

    public void setShouldBeProtected(boolean flag) {
        shouldBeProtected = flag;
    }

    @Override
    public void repair(ModifiedNodes modifiedNodes) {
        if (shouldBeFinal)
            declaration.setIsFinal(shouldBeFinal);
        if (shouldBePackagePrivate)
            declaration.setAccessibility(AccessibilityKind.ackNone);
        if (shouldBeProtected)
            declaration.setAccessibility(AccessibilityKind.ackProtected);
        modifiedNodes.markAttributeAsModified(declaration.getId(), NodeKind.ndkVariable);
    }

    @Override
    public String getToolName() {
        return "ChangeFinalModifier";
    }
}
