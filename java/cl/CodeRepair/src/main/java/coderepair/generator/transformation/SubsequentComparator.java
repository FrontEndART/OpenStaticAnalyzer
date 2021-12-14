package coderepair.generator.transformation;

import java.util.Comparator;

import coderepair.generator.transformation.exceptions.IncomparableNodePositionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import columbus.java.asg.base.PositionedWithoutComment;

/**
 * Compares nodes by their subsequent position in the graph. (e.g.: Is one node before or after another?)
 */
public class SubsequentComparator implements Comparator<PositionedWithoutComment> {

    private static final Logger logger = LoggerFactory.getLogger(SubsequentComparator.class);

    /**
     * Compares two nodes by subsequent position. (It doesn't check that they are the same file or not.)
     * 
     * @return -1 if node1 is before node2, 0 if node1 equals node2, 1 if node1 is after node2.
     */
    @Override
    public int compare(PositionedWithoutComment node1, PositionedWithoutComment node2) {
        if (node1.getPosition().getWideLine() == node2.getPosition().getWideLine()
                && node1.getPosition().getWideEndLine() == node2.getPosition().getWideEndLine()
                && node1.getPosition().getWideCol() == node2.getPosition().getWideCol()
                && node1.getPosition().getWideEndCol() == node2.getPosition().getWideEndCol()) {
            return 0; // same position
        } else {
            if (node1.getPosition().getWideEndLine() < node2.getPosition().getWideLine()) { // node1 ends before node2 starts?
                return -1; // we're cool, node1 is before node2!
            } else if (node1.getPosition().getWideEndLine() == node2.getPosition().getWideLine()) { // same endline of node1 and startline of node2?
                if (node1.getPosition().getWideEndCol() <= node2.getPosition().getWideCol()) { // node1 ends before node2 starts?
                    return -1; // we're cool, node1 is before node2!
                } else if (node1.getPosition().getWideCol() == node2.getPosition().getWideEndCol()) { // node1 ends before node2 starts?
                    return 1; // we're cool, node1 is after node2!
                }
            } else if (node1.getPosition().getWideLine() > node2.getPosition().getWideEndLine()) { // node2 starts after node1 ends?
                return 1;
            } else if (node1.getPosition().getWideLine() == node2.getPosition().getWideEndLine()) {
                if (node1.getPosition().getWideCol() >= node2.getPosition().getWideEndCol()) { // node1 ends before node2 starts?
                    return 1; // we're cool, node1 is after node2!
                }
            }
        }
        logger.warn("{}:{}:{}:{} <=> {}:{}:{}:{}",
                node1.getPosition().getWideLine(), node1.getPosition().getWideCol(),
                node1.getPosition().getWideEndLine(), node1.getPosition().getWideEndCol(),
                node2.getPosition().getWideLine(), node2.getPosition().getWideCol(),
                node2.getPosition().getWideEndLine(), node2.getPosition().getWideEndCol());
        throw new IncomparableNodePositionsException("Colliding or illegal node positions to compare!"); // not cool bro, should not happen
    }
}
