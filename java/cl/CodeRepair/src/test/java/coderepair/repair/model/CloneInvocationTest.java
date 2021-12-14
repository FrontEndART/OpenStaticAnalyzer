package coderepair.repair.model;

import coderepair.communication.base.ProblemPosition;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.generator.transformation.scpr.SCPHeuristics;
import coderepair.generator.transformation.scpr.SourceCodePositionReverterExcepiton;
import columbus.java.asg.base.Base;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.Identifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;


public class CloneInvocationTest extends AbstractModelRepairTest {

    private static final int IDENTIFIER1 =  121; //line 5. col. 16. name=date, type identidfier
    private static final int IDENTIFIER2 = 129; //line 9. col. 21. name=date, type identidfier

    @Override
    protected String getJsiLocation() {
        return baseDir + "examples/programs/cloneinvocationtest/.columbus_java/cloneinvocationtest.ljsi";
    }

    @Override
    @Before
    public void before() throws IOException {
        super.before();
        // do something before all test
    }

    @Override
    @After
    public void after() {
        // do something after all test
        super.after();
    }

    @Test
    public void testNormal1() throws RepairAlgorithmException {
        checkNormalUse(IDENTIFIER1);
    }

    @Test
    public void testNormal2() throws RepairAlgorithmException {
        checkNormalUse(IDENTIFIER2);

    }

    private void checkNormalUse(int id) throws RepairAlgorithmException {
        Base ref = this.factory.getRef(id);
        Assert.assertNotNull("Reference from the factory should not be null!", ref);

        Identifier identifier = (Identifier) ref;
        CloneInvocation cloneInvocation = new CloneInvocation(identifier);
        cloneInvocation.repair(this.modifiedNodes);
        Assert.assertFalse(this.modifiedNodes.isEmpty());

        boolean containsIdentifier = false;
        Collection<ModifiedNodes.ReplacedNode> replacedNodes = this.modifiedNodes.getReplacedNodes();
        for (ModifiedNodes.ModifiedNode replacedNode : replacedNodes) {
            containsIdentifier |= replacedNode.getNodeId().intValue() == id;
        }
        Assert.assertTrue(containsIdentifier);

    }

}
