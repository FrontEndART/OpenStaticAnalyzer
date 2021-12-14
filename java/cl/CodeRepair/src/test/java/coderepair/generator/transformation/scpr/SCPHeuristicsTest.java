package coderepair.generator.transformation.scpr;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import columbus.java.asg.base.Base;
import coderepair.generator.transformation.TestPositioned;
import coderepair.generator.transformation.exceptions.IncomparableNodePositionsException;

public class SCPHeuristicsTest {

    private static final int TABSIZE = 4;

    private static TestPositioned posClass;
    private static TestPositioned posTest;
    private static TestPositioned posGomba;
    private static TestPositioned posParameterList;

    private static TestPositioned posTest2;
    private static TestPositioned posDecl;
    private static TestPositioned posName;
    private static TestPositioned posMethStart;
    private static TestPositioned posMethStart2;
    private static TestPositioned posImport;

    private static List<TestPositioned> testList = new ArrayList<TestPositioned>();
    private static URI testFileWithSpaces;
    private static URI testFileWithTabs;

    @BeforeClass
    public static void setupBeforeClass() {
        posClass = new TestPositioned(1, 1, 100, 1);
        posTest = new TestPositioned(20, 5, 40, 5);
        posParameterList = new TestPositioned(20, 22, 20, 54);
        posGomba = new TestPositioned(25, 9, 35, 37);

        posTest2 = new TestPositioned(1, 10, 10, 1);
        posName = new TestPositioned(20, 22, 20, 38);
        posDecl = new TestPositioned(20, 38, 20, 53);
        posMethStart = new TestPositioned(40, 38, 50, 53);
        posMethStart2 = new TestPositioned(40, 18, 50, 23);
        posImport = new TestPositioned(60, 38, 80, 53);

        File dir=new File(".");
        File testFileWithSpacesFile = new File("src/test/inputs/generatorTests/transformation/SCPHeuristicsExample.java");
        File testFileWithTabsFile = new File("src/test/inputs/generatorTests/transformation/SCPHeuristicsExample2.java");
        Assert.assertTrue(testFileWithSpacesFile.exists());
        Assert.assertTrue(testFileWithTabsFile.exists());

        testFileWithSpaces = testFileWithSpacesFile.getAbsoluteFile().toURI();
        testFileWithTabs = testFileWithTabsFile.getAbsoluteFile().toURI();
    }

    @Before
    public void deleteBefore() {
        testList.clear();
    }

    @Test
    public void findBestMatchTest() throws IOException {
        testList.add(posParameterList);
        testList.add(posClass);
        testList.add(posTest);
        testList.add(posGomba);
        testList.add(posDecl);
        testList.add(posName);
        testList.add(posTest2);
        testList.add(posMethStart);
        testList.add(posImport);
        Base bestMatch = SCPHeuristics.findBestMatch(20, 22, 20, 38, testFileWithSpaces, TABSIZE, testList);
        Assert.assertEquals(posName, bestMatch);
    }

    @Test
    public void findBestMatchOnSameLineDistance1WithSpaces() throws IOException {
        testList.add(posDecl);
        testList.add(posName);

        Base bestMatch = SCPHeuristics.findBestMatch(19, 22, 21, 50, testFileWithSpaces, TABSIZE, testList);
        Assert.assertEquals(posName, bestMatch);
    }

    @Test
    public void findBestMatchOnSameLineDistance2WithSpaces() throws IOException {
        testList.add(posDecl);
        testList.add(posName);

        Base bestMatch = SCPHeuristics.findBestMatch(17, 22, 23, 50, testFileWithSpaces, TABSIZE, testList);
        Assert.assertEquals(posName, bestMatch);
    }

    @Test
    public void findBestMatchOnSameLineDistance3WithSpaces() throws IOException {
        testList.add(posMethStart);
        testList.add(posMethStart2);

        Base bestMatch = SCPHeuristics.findBestMatch(43, 22, 47, 50, testFileWithSpaces, TABSIZE, testList);
        Assert.assertEquals(posMethStart2, bestMatch);
    }

    @Test
    public void convertPosTest() {
        int convertTabbedDistance = SCPHeuristics.convertTabbedDistance("\t    \t        asdasd    \t", 21, TABSIZE);
        Assert.assertEquals(15, convertTabbedDistance);
    }

    @Test
    public void convertLengthTest() {
        int convertTabbedLength = SCPHeuristics.convertTabbedLength("\t    \t        asdasd    \t", TABSIZE);
        Assert.assertEquals(33, convertTabbedLength);
    }

    @Test
    public void findBestMatchOnSameLineDistance1WithTabs() throws IOException {
        testList.add(posDecl);
        testList.add(posName);

        Base bestMatch = SCPHeuristics.findBestMatch(19, 22, 21, 50, testFileWithTabs, TABSIZE, testList);
        Assert.assertEquals(posName, bestMatch);
    }

    @Test
    public void findBestMatchOnSameLineDistance2WithTabs() throws IOException {
        testList.add(posDecl);
        testList.add(posName);

        Base bestMatch = SCPHeuristics.findBestMatch(17, 22, 23, 50, testFileWithTabs, TABSIZE, testList);
        Assert.assertEquals(posName, bestMatch);
    }

    @Test
    public void findBestMatchOnSameLineDistance3WithTabs() throws IOException {
        testList.add(posMethStart);
        testList.add(posMethStart2);

        Base bestMatch = SCPHeuristics.findBestMatch(43, 22, 47, 50, testFileWithTabs, TABSIZE, testList);
        Assert.assertEquals(posMethStart2, bestMatch);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void findMultipleBestMatches() throws IOException {
        testList.add(posName);
        testList.add(posName);
        SCPHeuristics.findBestMatch(19, 22, 20, 50, testFileWithSpaces, TABSIZE, testList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument() throws IOException {
        SCPHeuristics.findBestMatch(19, 22, 20, 50, null, TABSIZE, testList);
    }

    @Test
    public void findOutermostNodeTest() {
        testList.add(posParameterList);
        testList.add(posClass);
        testList.add(posTest);
        testList.add(posGomba);

        Base outermostNode = SCPHeuristics.findOutermostNode(testList);
        Assert.assertEquals(posClass, outermostNode);
    }

    @Test
    public void findFirstNodeTest() {
        testList.add(posName);
        testList.add(posDecl);
        testList.add(posTest2);
        testList.add(posMethStart);
        testList.add(posImport);

        Base firstNode = SCPHeuristics.findFirstNode(testList);
        Assert.assertEquals(posTest2, firstNode);
    }

    @Test
    public void findLastNodeTest() {
        testList.add(posName);
        testList.add(posDecl);
        testList.add(posTest2);
        testList.add(posMethStart);
        testList.add(posImport);

        Base lastNode = SCPHeuristics.findLastNode(testList);
        Assert.assertEquals(posImport, lastNode);
    }

}
