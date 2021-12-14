package coderepair.generator.transformation.scpr;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import coderepair.generator.transformation.exceptions.IncomparableNodePositionsException;
import coderepair.generator.transformation.ContainmentComparator;
import com.google.gdata.util.common.base.CharEscapers;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coderepair.communication.base.ProblemPosition;
import columbus.java.asg.base.PositionedWithoutComment;
import coderepair.generator.transformation.SubsequentComparator;

public class SCPHeuristics {

    private static final int WEIGHT = 100000;

    public static <T extends PositionedWithoutComment> T findBestMatch(ProblemPosition problemPos, URI srcDir, int tabSize, List<T> nodes) throws IOException {
        URI resolve = srcDir.resolve(CharEscapers.uriPathEscaper().escape(problemPos.getPath()));//Utils.resolvePath(srcDir, problemPos.getPath());
        return findBestMatch(problemPos.getStartLine(), problemPos.getStartCol(), problemPos.getEndLine(), problemPos.getEndCol(), resolve,
                tabSize, nodes);
    }

    public static <T extends PositionedWithoutComment> T findBestMatch(
            int expectedStartLine, int expectedStartCol, int expectedEndLine, int expectedEndCol, URI path, int tabSize, Collection<T> nodes)
            throws IOException {
        final BestMatchComperator bmc = new BestMatchComperator(expectedStartLine, expectedStartCol, expectedEndLine, expectedEndCol, path, tabSize);

        Iterator<T> i = nodes.iterator();
        T candidate = i.next();
        boolean equals = false;

        while (i.hasNext()) {
            T next = i.next();
            if (bmc.compare(next, candidate) == 0) {
                equals = true;
            } else if (bmc.compare(next, candidate) < 0) {
                candidate = next;
                equals = false;
            }
        }
        if (equals) {
            throw new IncomparableNodePositionsException("Multiple (best) matches found");
        }

        return candidate;
    }

    private static Map<Integer, String> readFile(URI path) throws IOException {
        final Map<Integer, String> ret = new HashMap<Integer, String>();
        final List<String> readLines = IOUtils.readLines(path.toURL().openStream(), StandardCharsets.UTF_8);
        for (int linenum = 1; linenum <= readLines.size(); linenum++) {
            ret.put(linenum, readLines.get(linenum - 1));
        }
        return ret;
    }

    private static class BestMatchComperator implements Comparator<PositionedWithoutComment> {

        private static final Logger logger = LoggerFactory.getLogger(SCPHeuristics.BestMatchComperator.class);

        private final int startLine;
        private final int startCol;
        private final int endLine;
        private final int endCol;
        private Map<Integer, String> fileContents;

        private int tabSize;

        public BestMatchComperator(final int startLine, final int startCol, final int endLine, final int endCol, URI path, int tabSize) throws IOException {
            super();
            this.startLine = startLine;
            this.startCol = startCol;
            this.endLine = endLine;
            this.endCol = endCol;
            this.tabSize = tabSize;

            if (path == null) {
                throw new IllegalArgumentException("Path cannot be null!");
            }
            this.fileContents = readFile(path);
        }

        @Override
        public int compare(final PositionedWithoutComment o1, final PositionedWithoutComment o2) {
            try {
                Integer o1D = calculateDistance(this.startLine, this.startCol, this.endLine, this.endCol, this.fileContents, this.tabSize, o1);
                final Integer o2D = calculateDistance(this.startLine, this.startCol, this.endLine, this.endCol, this.fileContents, this.tabSize, o2);
                return o1D.compareTo(o2D);
            } catch (IOException e) {
                logger.error("IOError during compare.", e);
            }
            return 0;
        }

    }

    private static int calculateDistance(int expectedStartLine, int expectedStartCol, int expectedEndLine, int expectedEndCol,
            Map<Integer, String> fileContents, int tabSize, PositionedWithoutComment actual) throws IOException {
        int startLine = 0;
        int startCol = 0;
        int endLine = 0;
        int endCol = 0;

        if (expectedStartLine == actual.getPosition().getWideLine()) { // is startline cool?
            if (expectedEndLine == actual.getPosition().getWideEndLine()) { // startline cool, but is endline cool too?
                startCol = Math.abs(expectedStartCol - actual.getPosition().getWideCol()); // best case scenario
                endCol = Math.abs(expectedEndCol - actual.getPosition().getWideEndCol()); // the local distance decides
            } else {
                endLine = Math.abs(expectedEndLine - actual.getPosition().getWideEndLine()); // endline distance decides
            }
        } else if (expectedEndLine == actual.getPosition().getWideEndLine()) { // startline not cool, but endline cool?
            startLine = Math.abs(expectedStartLine - actual.getPosition().getWideLine()); // startline distance decides
        } else { // startline not cool, endline not cool
            startLine = Math.abs(expectedStartLine - actual.getPosition().getWideLine());
            endLine = Math.abs(expectedEndLine - actual.getPosition().getWideEndLine());
            if (startLine == endLine) {
                // worst case, when they are the same distance
                if (expectedStartLine < actual.getPosition().getWideLine()) { // expected outside actual?
                    // calculate proper character distances
                    String line = fileContents.get(expectedStartLine);
                    startCol += line.substring(convertTabbedDistance(line, expectedStartCol, tabSize)).length();
                    for (int i = expectedStartLine + 1; i < actual.getPosition().getWideLine(); i++) {
                        startCol += convertTabbedLength(fileContents.get(i), tabSize);
                    }
                    startCol += actual.getPosition().getWideCol();

                    line = fileContents.get(actual.getPosition().getWideEndLine());
                    endCol += line.substring(convertTabbedDistance(line, actual.getPosition().getWideEndCol(), tabSize)).length();
                    for (int i = actual.getPosition().getWideEndLine() + 1; i < expectedEndLine; i++) {
                        endCol += convertTabbedLength(fileContents.get(i), tabSize);
                    }
                    endCol += expectedEndCol;
                } else { // excepted inside actual
                    // calculate proper character distances
                    String line = fileContents.get(actual.getPosition().getWideLine());
                    startCol += line.substring(convertTabbedDistance(line, actual.getPosition().getWideCol(), tabSize)).length();
                    for (int i = actual.getPosition().getWideLine() + 1; i < expectedStartLine; i++) {
                        startCol += convertTabbedLength(fileContents.get(i), tabSize);
                    }
                    startCol += expectedStartCol;

                    line = fileContents.get(expectedEndLine);
                    endCol += line.substring(convertTabbedDistance(line, expectedEndCol, tabSize)).length();
                    for (int i = expectedEndLine + 1; i < actual.getPosition().getWideEndLine(); i++) {
                        endCol += convertTabbedLength(fileContents.get(i), tabSize);
                    }
                    endCol += actual.getPosition().getWideEndCol();
                }
            }
        }

        return startLine * WEIGHT + startCol + endLine * WEIGHT + endCol;
    }

    public static int convertTabbedDistance(String string, int expectedPosition, int tabSize) {
        int position = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '\t') {
                position += tabSize - (position % tabSize);
            } else {
                position += 1;
            }
            if (position >= expectedPosition) {
                position = i + 1;
                break;
            }
        }
        return position;
    }

    public static int convertTabbedLength(String string, int tabSize) {
        int length = 0;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '\t') {
                length += tabSize - (length % tabSize);
            } else {
                length += 1;
            }
        }

        return length + 1;
    }

    // -------------------------------------------------------------------------------------------------------------------

    public static final SubsequentComparator SUBSEQUENT_COMPARATOR = new SubsequentComparator();
    public static final ContainmentComparator CONTAINMENT_COMPARATOR = new ContainmentComparator();

    public static <T extends PositionedWithoutComment> T findOutermostNode(Collection<T> nodes) {
        return Collections.min(nodes, CONTAINMENT_COMPARATOR);
    }

    public static <T extends PositionedWithoutComment> T findFirstNode(Collection<T> nodes) {
        return Collections.min(nodes, SUBSEQUENT_COMPARATOR);
    }

    public static <T extends PositionedWithoutComment> T findLastNode(Collection<T> nodes) {
        return Collections.max(nodes, SUBSEQUENT_COMPARATOR);
    }

}
