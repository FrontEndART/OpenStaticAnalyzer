package coderepair.repair.rules;

import coderepair.repair.base.ModelRepairing;
import coderepair.repair.base.ProblemRepairOverASG;
import coderepair.repair.model.RemoveNullPath;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SCPHeuristics;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.generator.transformation.scpr.SourceCodePositionReverterExcepiton;
import coderepair.communication.base.ProblemPosition;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.java.asg.Factory;
import columbus.java.asg.expr.Expression;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class RepairRule_NP_NULL extends ProblemRepairOverASG {
    @Override
    public String getToolName() {
        return "NP_NULL_ON_SOME_PATH";
    }

    @Override
    protected void doRepairOfProblemOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                            SourceCodePositionReverter reverter, ProblemPosition problemPos, URI srcDirURI, int tabSize) throws RepairAlgorithmException {
        List<Expression> nodes;
        try {
            nodes = reverter.search(problemPos, Expression.class);
            if (nodes.size() == 0) {
                throw new RepairAlgorithmException("No matching expression found at " + problemPos + ".");
            }
            Expression expr = SCPHeuristics.findBestMatch(problemPos, srcDirURI, tabSize, nodes);
            ModelRepairing ci = new RemoveNullPath(expr);
            ci.repair(modifiedNodes);
        } catch (SourceCodePositionReverterExcepiton e) {
            throw new RepairAlgorithmException(e);
        } catch (IOException e) {
            throw new RepairAlgorithmException(e);
        }
    }
}
