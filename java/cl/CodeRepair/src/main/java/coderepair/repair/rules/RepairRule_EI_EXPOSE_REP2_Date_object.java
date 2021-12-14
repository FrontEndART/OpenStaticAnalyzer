package coderepair.repair.rules;

import coderepair.repair.base.ModelRepairing;
import coderepair.repair.base.ProblemRepairOverASG;
import coderepair.repair.model.DateGetTimeInvocation;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SCPHeuristics;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.generator.transformation.scpr.SourceCodePositionReverterExcepiton;
import coderepair.communication.base.ProblemPosition;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Factory;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.Identifier;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class RepairRule_EI_EXPOSE_REP2_Date_object extends ProblemRepairOverASG {
    @Override
    public String getToolName() {
        return "EI_EXPOSE_REP2_Date_object";
    }

    @Override
    protected void doRepairOfProblemOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                            SourceCodePositionReverter reverter, ProblemPosition problemPos, URI srcDirURI, int tabSize) throws RepairAlgorithmException {
        List<Identifier> nodes;
        try {
            nodes = reverter.search(problemPos, Identifier.class);
            if (nodes.size() == 0) {
                throw new RepairAlgorithmException("No matching identifier found at " + problemPos + ".");
            }
            Expression expr = SCPHeuristics.findBestMatch(problemPos, srcDirURI, tabSize, nodes);
            ModelRepairing ci = new DateGetTimeInvocation(expr);
            ci.repair(modifiedNodes);
        } catch (SourceCodePositionReverterExcepiton e) {
            throw new RepairAlgorithmException(e);
        } catch (IOException e) {
            throw new RepairAlgorithmException(e);
        }
    }
}
