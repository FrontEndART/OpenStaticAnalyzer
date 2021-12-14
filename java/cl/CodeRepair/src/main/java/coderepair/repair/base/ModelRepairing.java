package coderepair.repair.base;

import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.generator.transformation.ModifiedNodes;

public interface ModelRepairing {

    void repair(ModifiedNodes modifiedNodes) throws RepairAlgorithmException;

    String getToolName();

}
