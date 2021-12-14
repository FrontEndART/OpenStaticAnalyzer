package coderepair.generator.transformation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import coderepair.generator.transformation.exceptions.IncomparableNodePositionsException;

public class SubsequentComparatorTest {

    private static SubsequentComparator comparator;
    private static TestPositioned posClass;
    private static TestPositioned posTest;
    private static TestPositioned posGomba;
    private static TestPositioned posTest2;
    private static TestPositioned posParameterList;
    private static TestPositioned posDecl;
    private static TestPositioned posName;
    private static TestPositioned posMethStart;
    private static TestPositioned posImport;
    private static TestPositioned posWideDecl;

    @BeforeClass
    public static void setupBeforeClass() {
        comparator = new SubsequentComparator();
        posImport = new TestPositioned(7, 1, 7, 19);
        posMethStart = new TestPositioned(20, 5, 22, 22);
        posParameterList = new TestPositioned(22, 22, 24, 54);
        posGomba = new TestPositioned(25, 9, 35, 37);
        posTest2 = new TestPositioned(50, 10, 70, 1);

        posTest = new TestPositioned(20, 5, 40, 5);
        posName = new TestPositioned(20, 22, 20, 38);
        posDecl = new TestPositioned(20, 35, 20, 53);
        posWideDecl = new TestPositioned(20, 38, 20, 53);
        posClass = new TestPositioned(1, 1, 100, 1);
    }

    @Test
    public void testSubsequence() {
        int compare;
        compare = comparator.compare(posMethStart, posImport);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posParameterList, posMethStart);
        Assert.assertEquals(1, compare);

        compare = comparator.compare(posGomba, posParameterList);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posTest2, posGomba);
        Assert.assertEquals(1, compare);

        compare = comparator.compare(posWideDecl, posName);
        Assert.assertEquals(1, compare);
    }

    @Test
    public void testReverseSubsequence() {
        int compare;
        compare = comparator.compare(posImport, posMethStart);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posMethStart, posParameterList);
        Assert.assertEquals(-1, compare);

        compare = comparator.compare(posParameterList, posGomba);
        Assert.assertEquals(-1, compare);
        compare = comparator.compare(posGomba, posTest2);
        Assert.assertEquals(-1, compare);

        compare = comparator.compare(posName, posWideDecl);
        Assert.assertEquals(-1, compare);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testPartialContainment() {
        comparator.compare(posDecl, posName);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testPartialContainmentReverse() {
        comparator.compare(posName, posDecl);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testContainment() {
        comparator.compare(posClass, posTest);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testContainmentReverse() {
        comparator.compare(posTest, posClass);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testBad() {
        comparator.compare(posName, posTest);
    }

    @Test(expected = IncomparableNodePositionsException.class)
    public void testBadReverse() {
        comparator.compare(posTest, posName);
    }

    @Test
    public void testTransitivity() {
        int compare;
        compare = comparator.compare(posMethStart, posImport);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posTest2, posMethStart);
        Assert.assertEquals(1, compare);
        compare = comparator.compare(posTest2, posImport);
        Assert.assertEquals(1, compare);
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
