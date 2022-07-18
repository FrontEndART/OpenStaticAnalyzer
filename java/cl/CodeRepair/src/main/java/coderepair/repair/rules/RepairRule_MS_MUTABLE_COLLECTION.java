package coderepair.repair.rules;

import coderepair.communication.base.ProblemPosition;
import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SCPHeuristics;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.generator.transformation.scpr.SourceCodePositionReverterExcepiton;
import coderepair.repair.base.ModelRepairing;
import coderepair.repair.base.ProblemRepairOverASG;
import coderepair.repair.model.CollectionsUnmodifiable;
import columbus.java.asg.Factory;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.Identifier;
import columbus.java.asg.struc.Variable;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class RepairRule_MS_MUTABLE_COLLECTION extends ProblemRepairOverASG {

    @Override
    public String getToolName() {
        return "MS_MUTABLE_COLLECTION";
    }

    @Override
    protected void doRepairOfProblemOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                            SourceCodePositionReverter reverter, ProblemPosition problemPos, URI srcDirURI, int tabSize) throws RepairAlgorithmException {
        try {
            List<Variable> nodes = reverter.search(problemPos, NodeKind.ndkVariable);
            Variable node = SCPHeuristics.findBestMatch(problemPos, srcDirURI, tabSize, nodes);
            ModelRepairing makeCollectionUnmodifiableXXXCalls = new CollectionsUnmodifiable(node);
            makeCollectionUnmodifiableXXXCalls.repair(modifiedNodes);
        } catch (SourceCodePositionReverterExcepiton e) {
            throw new RepairAlgorithmException(e);
        } catch (IOException e) {
            throw new RepairAlgorithmException(e);
        }
    }
}
