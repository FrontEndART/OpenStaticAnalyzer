package coderepair.generator.transformation;

import java.util.Comparator;

import coderepair.generator.transformation.exceptions.IncomparableNodePositionsException;
import columbus.java.asg.base.PositionedWithoutComment;

/**
 * Compares nodes by containment. (e.g.: Is one node inside or outside of another?)
 */
public class ContainmentComparator implements Comparator<PositionedWithoutComment> {

    /**
     * Compares two nodes by containment. (It doesn't check that they are the same file or not.)
     * 
     * @return -1 if node1 contains node2, 0 if node1 equals node2, and 1 if node2 contains node1
     */
    @Override
    public int compare(PositionedWithoutComment node1, PositionedWithoutComment node2) {
        if (node1.getPosition().getWideLine() == node2.getPosition().getWideLine()
                && node1.getPosition().getWideEndLine() == node2.getPosition().getWideEndLine()
                && node1.getPosition().getWideCol() == node2.getPosition().getWideCol()
                && node1.getPosition().getWideEndCol() == node2.getPosition().getWideEndCol()) {
            return 0; // same position
        } else {
            if (node1.getPosition().getWideLine() < node2.getPosition().getWideLine()) { // node1 starts before node2?
                if (node1.getPosition().getWideEndLine() > node2.getPosition().getWideEndLine()) { // node1 ends after node2?
                    return -1; // we're cool, node1 is the outer one!
                } else if (node1.getPosition().getWideEndLine() == node2.getPosition().getWideEndLine()) { // same endline mate?
                    if (node1.getPosition().getWideEndCol() >= node2.getPosition().getWideEndCol()) { // node1 ends after node2?
                        return -1; // we're cool, node1 is the outer one!
                    } else {
                        throw new IncomparableNodePositionsException("Disjunct or illegal node positions to compare!"); // not cool bro, should not happen
                    }
                } else {
                    throw new IncomparableNodePositionsException("Disjunct or illegal node positions to compare!"); // not cool bro, should not happen
                }
            } else if (node1.getPosition().getWideLine() == node2.getPosition().getWideLine()) { // same startline?
                if (node1.getPosition().getWideCol() < node2.getPosition().getWideCol()) { // node1 before node2?
                    return -1; // we're cool, node1 is the outer one!
                } else if (node1.getPosition().getWideCol() == node2.getPosition().getWideCol()) { // same startcol?
                    if (node1.getPosition().getWideEndLine() > node2.getPosition().getWideEndLine()) { // node1 ends after node2?
                        return -1; // we're cool, node1 is the outer one!
                    } else if (node1.getPosition().getWideEndLine() == node2.getPosition().getWideEndLine()) { // same endline?
                        if (node1.getPosition().getWideEndCol() >= node2.getPosition().getWideEndCol()) { // node1 ends after node2?
                            return -1; // we're cool, node1 is the outer one!
                        }
                    }
                }
            }
        }
        return 1; // otherwise node2 is the outer node
    }

}
