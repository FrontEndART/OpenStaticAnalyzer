package coderepair.generator.transformation;

import coderepair.generator.transformation.scpr.SCPHeuristicsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ContainmentComparatorTest.class, SubsequentComparatorTest.class, SCPHeuristicsTest.class })
public class AllTransformationTests {

}
