package coderepair.generator.transformation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import coderepair.generator.transformation.exceptions.IncomparableNodePositionsException;

public class ContainmentComparatorTest {

    private static ContainmentComparator comparator;
    private static TestPositioned posClass; // outermost
    private static TestPositioned posTest;
    private static TestPositioned posGomba;
    private static TestPositioned posTest2;
    private static TestPositioned posParameterList;
    private static TestPositioned posDecl;
    private static TestPositioned posVariable;
    private static TestPositioned posName;
    private static TestPositioned posMethStart;
    private static TestPositioned posBadTest;

    @BeforeClass
    public static void setupBeforeClass() {
        comparator = new ContainmentComparator();
        posClass = new TestPositioned(1, 1, 100, 1);
        posTest = new TestPositioned(20, 5, 40, 5);
        posBadTest = new TestPositioned(25, 5, 40, 9);
        posParameterList = new TestPositioned(20, 22, 20, 54);
        posName = new TestPositioned(20, 22, 20, 38);
        posMethStart = new TestPositioned(20, 5, 22, 22);
        posDecl = new TestPositioned(20, 40, 20, 53);
        posGomba = new TestPositioned(25, 9, 35, 37);
        posVariable = new TestPositioned(35, 9, 35, 36);
        posTest2 = new TestPositioned(50, 10, 70, 1);
    }

    @Test
    public void testContainment() {
        int compare;
        compare = comparator.compare(posClass, posTest);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posTest, posGomba);
        Assert.assertEquals(-1, compare);

        compare = comparator.compare(posTest, posParameterList);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posTest, posDecl);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posParameterList, posDecl);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posParameterList, posName);
        Assert.assertEquals(-1, compare);

        compare = comparator.compare(posTest, posMethStart);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posGomba, posVariable);
        Assert.assertEquals(-1, compare);

        compare = comparator.compare(posClass, posTest2);
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void testReverseContainment() {
        int compare;
        compare = comparator.compare(posTest, posClass);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posGomba, posTest);
        Assert.assertEquals(1, compare);

        compare = comparator.compare(posParameterList, posTest);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posDecl, posTest);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posDecl, posParameterList);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posName, posParameterList);
        Assert.assertEquals(1, compare);

        compare = comparator.compare(posMethStart, posTest);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posVariable, posGomba);
        Assert.assertEquals(1, compare);

        compare = comparator.compare(posTest2, posClass);
        Assert.assertEquals(1, compare);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testDisjunction() {
        comparator.compare(posTest, posTest2);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testBadPosition() {
        comparator.compare(posTest, posBadTest);
    }

    @Test
    public void testTransitivity() {
        int compare;
        compare = comparator.compare(posClass, posTest);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posTest, posGomba);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posClass, posGomba);
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void testEquality() {
        int compare;
        compare = comparator.compare(posClass, posClass);
        Assert.assertEquals(0, compare);
        compare = comparator.compare(posTest, posTest);
        Assert.assertEquals(0, compare);
        compare = comparator.compare(posGomba, posGomba);
        Assert.assertEquals(0, compare);
        compare = comparator.compare(posTest2, posTest2);
        Assert.assertEquals(0, compare);
        compare = comparator.compare(posParameterList, posParameterList);
        Assert.assertEquals(0, compare);
        compare = comparator.compare(posDecl, posDecl);
        Assert.assertEquals(0, compare);
    }

}
