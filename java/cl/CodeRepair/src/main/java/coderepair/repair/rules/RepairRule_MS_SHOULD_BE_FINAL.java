package coderepair.repair.rules;

import coderepair.repair.base.ModelRepairing;
import coderepair.repair.base.ProblemRepairOverASG;
import coderepair.repair.model.ChangeFinalModifier;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SCPHeuristics;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.generator.transformation.scpr.SourceCodePositionReverterExcepiton;
import coderepair.communication.base.ProblemPosition;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Factory;

import columbus.java.asg.struc.Variable;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class RepairRule_MS_SHOULD_BE_FINAL extends ProblemRepairOverASG {
    @Override
    public String getToolName() {
        return "Repair of Findbugs MS_SHOULD_BE_FINAL";
    }

    @Override
    protected void doRepairOfProblemOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                            SourceCodePositionReverter reverter, ProblemPosition problemPos, URI srcDirURI, int tabSize) throws RepairAlgorithmException {
        List<Variable> nodes;
        try {
            nodes = reverter.search(problemPos, Variable.class);
            if (nodes.size() == 0) {
                throw new RepairAlgorithmException("No matching identifier found at " + problemPos + ".");
            }
            Variable id = SCPHeuristics.findBestMatch(problemPos, srcDirURI, tabSize, nodes);
            ModelRepairing ci = new ChangeFinalModifier(id, true);
            ci.repair(modifiedNodes);
        } catch (SourceCodePositionReverterExcepiton e) {
            throw new RepairAlgorithmException(e);
        } catch (IOException e) {
            throw new RepairAlgorithmException(e);
        }
    }
}
