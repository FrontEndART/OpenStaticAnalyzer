package coderepair.repair.algs;

import coderepair.generator.transformation.AllTransformationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import coderepair.repair.model.AllModelRepairTests;

@RunWith(Suite.class)
@SuiteClasses({ IntegrationTest.class, AllModelRepairTests.class, AllTransformationTests.class })
public class AllTests {

}
