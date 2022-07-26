package coderepair.repair.rules;

import coderepair.communication.base.ProblemPosition;
import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SCPHeuristics;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.generator.transformation.scpr.SourceCodePositionReverterExcepiton;
import coderepair.repair.base.ProblemRepairOverASG;
import coderepair.repair.model.ChangeInstanceModifier;
import columbus.java.asg.Factory;
import columbus.java.asg.struc.Method;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class RepairRule_FI_PUBLIC_SHOULD_BE_PROTECTED extends ProblemRepairOverASG {
    @Override
    public String getToolName() {
        return "Repair of Findbugs FI_PUBLIC_SHOULD_BE_PROTECTED";
    }

    @Override
    protected void doRepairOfProblemOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                            SourceCodePositionReverter reverter, ProblemPosition problemPos, URI srcDirURI, int tabSize) throws RepairAlgorithmException {
        List<Method> nodes;
        try {
            nodes = reverter.search(problemPos, Method.class);
            if (nodes.size() == 0) {
                throw new RepairAlgorithmException("No matching method found at " + problemPos + ".");
            }
            Method id = SCPHeuristics.findBestMatch(problemPos, srcDirURI, tabSize, nodes);
            ChangeInstanceModifier ci = new ChangeInstanceModifier(id);
            ci.setShouldBeProtected(true);
            ci.repair(modifiedNodes);
        } catch (SourceCodePositionReverterExcepiton e) {
            throw new RepairAlgorithmException(e);
        } catch (IOException e) {
            throw new RepairAlgorithmException(e);
        }
    }
}
