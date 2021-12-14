package coderepair.generator.transformation.scpr;

import gnu.trove.TIntProcedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coderepair.communication.base.ProblemPosition;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Factory;
import columbus.java.asg.Range;
import columbus.java.asg.algorithms.AlgorithmPreorder;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.base.PositionedWithoutComment;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.struc.CompilationUnit;
import columbus.java.asg.struc.Member;
import columbus.java.asg.struc.Package;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.visitors.VisitorAbstractNodes;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;

/**
 * This class helps to retrieve node ids by source code positions.
 */
public class SourceCodePositionReverter {

    private static final Logger logger = LoggerFactory.getLogger(SourceCodePositionReverter.class);

    private static final float MIN_LINE_WIDTH = 0f;
    private static final float MAX_LINE_WIDTH = 1000000f;

    private Factory fact;
    private Map<String, CompilationUnit> fileMap;
    private Map<String, SpatialIndex> siMap;

    public SourceCodePositionReverter(Factory fact) {
        super();
        this.fact = fact;
        this.siMap = new TreeMap<String, SpatialIndex>();
    }

    /**
     * Build the entire search space for the whole factory.<br>
     * This can be used for
     * 
     * @throws SourceCodePositionReverterExcepiton
     */
    public void buildAll() throws SourceCodePositionReverterExcepiton {
        Queue<Package> packages = new LinkedList<Package>();
        packages.add(this.fact.getRoot());

        while (!packages.isEmpty()) {
            Package pack = packages.poll();
            EdgeIterator<CompilationUnit> compilationUnitsIterator = pack.getCompilationUnitsIterator();
            while (compilationUnitsIterator.hasNext()) {
                final CompilationUnit compilationUnit = compilationUnitsIterator.next();
                buildIndex(compilationUnit);
            }

            EdgeIterator<Member> membersIterator = pack.getMembersIterator();
            while (membersIterator.hasNext()) {
                Member member = membersIterator.next();
                if (member.getNodeKind() == NodeKind.ndkPackage) {
                    packages.add((Package) member);
                }
            }
        }
    }

    /**
     * Create the <tt>Filename->{@link CompilationUnit}</tt> map by traversing packages.
     * 
     * @throws SourceCodePositionReverterExcepiton
     */
    private void buildFileMap() throws SourceCodePositionReverterExcepiton {
        this.fileMap = new TreeMap<String, CompilationUnit>();
        Queue<Package> packages = new LinkedList<Package>();
        packages.add(this.fact.getRoot());
        while (!packages.isEmpty()) {
            Package pack = packages.poll();
            EdgeIterator<CompilationUnit> compilationUnitsIterator = pack.getCompilationUnitsIterator();
            while (compilationUnitsIterator.hasNext()) {
                final CompilationUnit compilationUnit = compilationUnitsIterator.next();
                this.fileMap.put(getFileUniqueId(compilationUnit), compilationUnit);
            }
            EdgeIterator<Member> membersIterator = pack.getMembersIterator();
            while (membersIterator.hasNext()) {
                Member member = membersIterator.next();
                if (member.getNodeKind() == NodeKind.ndkPackage) {
                    packages.add((Package) member);
                }
            }
        }
    }

    private String getFileUniqueId(final CompilationUnit compilationUnit) throws SourceCodePositionReverterExcepiton {
        // String encode = URLCODEC.encode(compilationUnit.getFileName().replace("\\", "/"));
        return compilationUnit.getPosition().getPath().replace("\\", "/");
    }

    /**
     * Builds the search space for the given path.
     * 
     * @param path the path will build the search space for.
     * @throws SourceCodePositionReverterExcepiton if there are no {@link CompilationUnit} found for the give path.
     */
    private void buildPath(String path) throws SourceCodePositionReverterExcepiton {
        if (this.fileMap == null) {
            buildFileMap();
        }
        CompilationUnit compilationUnit = this.fileMap.get(path);
        if (compilationUnit != null) {
            buildIndex(compilationUnit);
        } else {
            throw new SourceCodePositionReverterExcepiton("CompilationUnit not found for path: " + path);
        }
    }

    /**
     * Builds the search space for the given {@link CompilationUnit}.
     * 
     * @param compilationUnit the {@link CompilationUnit} to build the search space for.
     * @throws SourceCodePositionReverterExcepiton
     */
    private void buildIndex(CompilationUnit compilationUnit) throws SourceCodePositionReverterExcepiton {
        final SpatialIndex si = new RTree();
        si.init(null);
        AlgorithmPreorder pre = new AlgorithmPreorder();
        pre.setSafeMode();
        pre.setVisitSpecialNodes(false, false); // don't visit special nodes

        EdgeIterator<TypeDeclaration> typeDeclarationsIterator = compilationUnit.getTypeDeclarationsIterator();
        while (typeDeclarationsIterator.hasNext()) {
            TypeDeclaration typeDeclaration = typeDeclarationsIterator.next();

            pre.run(this.fact, new VisitorAbstractNodes() {

                @Override
                public void visit(Positioned node, boolean callVirtualBase) {
                    super.visit(node, callVirtualBase);
                    Range position = node.getPosition();
                    int startLine = position.getWideLine();
                    int startCol = position.getWideCol();
                    int endLine = position.getWideEndLine();
                    int endCol = position.getWideEndCol();
                    int id = node.getId();
                    if (startLine == endLine) { // if there is only one line
                        Rectangle rect = new Rectangle(startLine, startCol, endLine, endCol);
                        si.add(rect, id);
                    } else {
                        Rectangle first = new Rectangle(startLine, startCol, startLine, MAX_LINE_WIDTH);
                        Rectangle last = new Rectangle(endLine, MIN_LINE_WIDTH, endLine, endCol);
                        si.add(first, id); // first line chunked
                        si.add(last, id); // last line truncated
                        if (endLine - startLine > 1) { // if there are lines between
                            Rectangle middle = new Rectangle(startLine - 1, MIN_LINE_WIDTH, endLine - 1, MAX_LINE_WIDTH);
                            si.add(middle, id);
                        }
                    }
                }

            }, typeDeclaration);
        }
        this.siMap.put(getFileUniqueId(compilationUnit), si);
    }

    private <T> List<T> search(String path, Integer startLine, Integer startCol, Integer endLine, Integer endCol, final SearchMode searchMode)
            throws SourceCodePositionReverterExcepiton {
        logger.debug("Searching node for {}@{},{},{},{}::{}", path, startLine, startCol, endLine, endCol, searchMode.toString());

        if (!this.siMap.containsKey(path)) {
            buildPath(path); // build and cache the map for the given path
        }

        final Map<Integer, T> possibleMatches = new HashMap<Integer, T>();
        final SpatialIndex si = this.siMap.get(path);
        si.intersects(new Rectangle(startLine, startCol, endLine, endCol), new TIntProcedure() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean execute(int value) {
                PositionedWithoutComment ref = (PositionedWithoutComment) SourceCodePositionReverter.this.fact.getRef(value);
                boolean found = false;
                if (searchMode.decide(ref)) {
                    found = true;
                    possibleMatches.put(value, (T) ref);
                }
                logger.trace("Found by intersects: {} [{},{},{},{}] {} {}", value, ref.getPosition().getWideLine(), ref.getPosition().getWideCol(), ref
                        .getPosition().getWideEndLine(), ref.getPosition().getWideEndCol(), ref, (found ? " ****" : ""));
                return true;
            }

        });

        return new ArrayList<T>(possibleMatches.values()); // give back list for easier usage on client side
    }

    /**
     * Search for a node's id by the given {@link ProblemPosition} and {@link NodeKind}.
     * 
     * @param problemPos the lineinfo and path of the element to search for
     * @param nodeKind the exact kind of the node to search for
     * @return the possible ids of the nodes at the given position with the given <tt>nodeKind</tt>
     * @throws SourceCodePositionReverterExcepiton if the given path does not exist
     */
    public <T extends PositionedWithoutComment> List<T> search(ProblemPosition problemPos, final NodeKind nodeKind) throws SourceCodePositionReverterExcepiton {
        return search(problemPos.getPath(), problemPos.getStartLine(), problemPos.getStartCol(), problemPos.getEndLine(), problemPos.getEndCol(), nodeKind);
    }

    /**
     * Search for a node's id by the given path and lineinfo and {@link NodeKind}.
     * 
     * @param path the path of the file
     * @param nodeKind the exact kind of the node to search for
     * @return the possible ids of the nodes at the given position
     * @throws SourceCodePositionReverterExcepiton if the given path does not exist
     */
    public <T extends PositionedWithoutComment> List<T> search(String path, Integer startLine, Integer startCol, Integer endLine, Integer endCol,
            final NodeKind nodeKind) throws SourceCodePositionReverterExcepiton {
        return search(path, startLine, startCol, endLine, endCol, new NodeKindMode(nodeKind));
    }

    // ----

    /**
     * Search for a node's id by the given {@link ProblemPosition} and {@link NodeKind}.
     * 
     * @param problemPos the lineinfo and path of the element to search for
     * @param clazz the kind of the node to search for (superclass or interface)
     * @return the possible ids of the nodes at the given position with the given <tt>nodeKind</tt>
     * @throws SourceCodePositionReverterExcepiton if the given path does not exist
     */
    public <T extends PositionedWithoutComment> List<T> search(ProblemPosition problemPos, final Class<T> clazz) throws SourceCodePositionReverterExcepiton {
        return search(problemPos.getPath(), problemPos.getStartLine(), problemPos.getStartCol(), problemPos.getEndLine(), problemPos.getEndCol(), clazz);
    }

    /**
     * Search for a node's id by the given path and lineinfo and {@link NodeKind}.
     * 
     * @param path the path of the file
     * @param clazz the kind of the node to search for (superclass or interface)
     * @return the possible ids of the nodes at the given position
     * @throws SourceCodePositionReverterExcepiton if the given path does not exist
     */
    public <T extends Base> List<T> search(String path, Integer startLine, Integer startCol, Integer endLine, Integer endCol, final Class<T> clazz)
            throws SourceCodePositionReverterExcepiton {
        return search(path, startLine, startCol, endLine, endCol, new InstanceOfMode(clazz));
    }

    // ----

    private interface SearchMode {

        boolean decide(PositionedWithoutComment ref);

    }

    private class NodeKindMode implements SearchMode {

        private final NodeKind nodeKind;

        public NodeKindMode(NodeKind nodeKind) {
            super();
            this.nodeKind = nodeKind;
        }

        @Override
        public boolean decide(PositionedWithoutComment ref) {
            return (ref.getNodeKind() == this.nodeKind);
        }

        @Override
        public String toString() {
            return this.nodeKind.toString();
        }

    }

    private class InstanceOfMode implements SearchMode {

        private final Class<? extends Base> clazz;

        public InstanceOfMode(Class<? extends Base> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public boolean decide(PositionedWithoutComment ref) {
            return this.clazz.isInstance(ref);
        }

        @Override
        public String toString() {
            return this.clazz.toString();
        }

    }

    // ----

    /**
     * Search with nearest neighbor method for a node's id by the given path and lineinfo and {@link NodeKind}.<br>
     * This search method is an alternative to {@link #search(String, Integer, Integer, Integer, Integer, NodeKind)} method.
     * 
     * @param path the path of the file
     * @param nodeKind the kind of the node to search for
     * @return the possible ids of the nodes at the given position
     * @throws SourceCodePositionReverterExcepiton if the given path does not exist
     */
    public List<Integer> searchByNearest(String path, Integer startLine, Integer startCol, Integer endLine, Integer endCol, final NodeKind nodeKind)
            throws SourceCodePositionReverterExcepiton {
        logger.debug("Searching node for {}@{},{},{},{}::{}", path, startLine, startCol, endLine, endCol, nodeKind);

        if (!this.siMap.containsKey(path)) {
            buildPath(path); // build and cache the map for the given path
        }

        final List<Integer> ret = new ArrayList<Integer>();
        final SpatialIndex si = this.siMap.get(path);

        // This method gives back the results ordered.
        Point point = new Point(startLine + ((endLine - startLine) / 2), startCol + ((endCol - startCol) / 2));
        si.nearestN(point, new TIntProcedure() {

            @Override
            public boolean execute(int value) {
                Positioned ref = (Positioned) SourceCodePositionReverter.this.fact.getRef(value);
                boolean found = false;
                if (ref.getNodeKind() == nodeKind) {
                    found = true;
                    ret.add(value);
                }
                logger.trace("Found by nearest: {} [{},{},{},{}] {} {}", value, ref.getPosition().getWideLine(), ref.getPosition().getWideCol(), ref
                        .getPosition().getWideEndLine(), ref.getPosition().getWideEndCol(), ref, (found ? " ****" : ""));
                return true;
            }

        }, 1, Float.MAX_VALUE);

        return ret;
    }

}
