package coderepair.repair.base;

import java.net.URI;
import java.util.List;
import java.util.Map;

import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.communication.base.ProblemPosition;
import coderepair.communication.base.ProblemToRepair;
import coderepair.communication.keys.RepairingKeys;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Factory;

/**
 * This abstract class helps to iterate over the problems given by the framework.
 */
public abstract class ProblemRepairOverASG extends RepairOverASG {

    @Override
    protected final void doRepairsOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                          SourceCodePositionReverter reverter, URI srcDirURI, int tabSize) throws RepairAlgorithmException {
        @SuppressWarnings("unchecked")
        final List<ProblemToRepair> problems = (List<ProblemToRepair>) settings.get(RepairingKeys.PROBLEM_LOCATIONS);

        for (ProblemToRepair problem : problems) {
            for (ProblemPosition problemPos : problem.getPositions()) {
                doRepairOfProblemOverAsg(settings, diff, modifiedNodes, factory, reverter, problemPos, srcDirURI, tabSize);
            }
        }
    }

    /**
     * Use this method to repair the given problem over the ASG.
     * 
     * @param settings the settings from the refactoring wizard.
     * @param diff the diff builder which can make unified diff from multiple files.
     * @param modifiedNodes this object can be used to keep track of the nodes the refactoring has modified (or added or deleted).
     * @param factory the factory of the loaded ASG.
     * @param reverter the source code position reverter which can help you find a problem's node-id by a given lineinfo.
     * @param problemPos the problem's position in the source code.
     * @param srcDirURI the location of the directory containing the source code.
     * @param tabSize the default tab size of the document
     * @throws RepairAlgorithmException if some error is happening.
     */
    protected abstract void doRepairOfProblemOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                                     SourceCodePositionReverter reverter, ProblemPosition problemPos, URI srcDirURI, int tabSize) throws RepairAlgorithmException;

}
