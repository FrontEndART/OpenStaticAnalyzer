package coderepair.repair.model;

import java.io.IOException;
import java.net.URL;

import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;
import coderepair.repair.base.RepairOverASG;
import org.junit.After;
import org.junit.Before;

import columbus.java.asg.Factory;


public abstract class AbstractModelRepairTest {

    protected Factory factory;
    protected ModifiedNodes modifiedNodes;
    protected SourceCodePositionReverter reverter;
    static final String baseDir = "src/test/inputs/repairTests/";

    protected AbstractModelRepairTest() {
        super();
    }

    @Before
    public void before() throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(getJsiLocation());
        this.factory = RepairOverASG.loadFactoryToASG(getJsiLocation());
        this.reverter = new SourceCodePositionReverter(factory);
        this.modifiedNodes = new ModifiedNodes();
    }

    protected abstract String getJsiLocation();

    @After
    public void after() {
        this.modifiedNodes.clear();
        this.modifiedNodes = null;
        this.factory = null;
    }
}