package coderepair.generator.support;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import coderepair.generator.algorithm.AlgorithmSrcGenerator;
import coderepair.generator.support.iterators.AbstractLetterIterator;
import coderepair.generator.support.iterators.BackLetterIterator;
import coderepair.generator.support.iterators.FrontLetterIterator;
import coderepair.generator.visitor.ModifGeneratorVisitor;
import coderepair.generator.visitor.SrcGeneratorVisitor;

import coderepair.generator.fileeditor.FileEditorUtil.FilePart;
import coderepair.generator.fileeditor.FileEditorUtil.Position;

import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Factory;
import columbus.java.asg.Range;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Comment;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.base.PositionedWithoutComment;
import columbus.java.asg.expr.Annotation;
import columbus.java.asg.statm.If;
import columbus.java.asg.struc.AnnotatedElement;
import columbus.java.asg.struc.CompilationUnit;
import columbus.java.asg.struc.NamedDeclaration;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.ModifiedNodes.ModifiedAttribute;
import coderepair.generator.transformation.ModifiedNodes.ModifiedNode;
import coderepair.generator.transformation.ModifiedNodes.NewPositionKind;
import coderepair.generator.transformation.ModifiedNodes.NewlyAddedNode;
import coderepair.generator.transformation.ModifiedNodes.ReplacedNode;

/**
 * Printer class for prints the modifications.
 */
public class ModifiedNodesPrinter {

    /**
     * Class for store the string to string switches.
     */
    public static class SwitchThis {

        final private FilePart part;
        final private String switchTo;

        public SwitchThis(FilePart part, String switchTo) {
            this.part = part;
            this.switchTo = switchTo;
        }

        public FilePart getPart() {
            return this.part;
        }

        public String getSwitchTo() {
            return this.switchTo;
        }

        public String getFilePath() {
            return this.part.getFilename();
        }
    }

    final private ModifiedNodes modifnodes;
    final private Factory fact;
    final private URI srcDirURI;
    final private int tabSize;
    final private String tabStr;

    public ModifiedNodesPrinter(ModifiedNodes modif, URI srcDirURI, int tabSize, String tabStr) {
        this.modifnodes = modif;
        this.srcDirURI = srcDirURI;
        this.fact = modif.getFactory();
        this.tabSize = tabSize;
        this.tabStr = tabStr;
    }

    public List<SwitchThis> calculate() {
        List<SwitchThis> result = new ArrayList<>();
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();

        ModifiedChecker checker = new ModifiedChecker(this.fact);
        checker.simplify(this.modifnodes);

        Collection<Integer> unmodifCollection = ModifGeneratorVisitor.getIntCollection(this.modifnodes.getUnmodifiedNodes());

         for(Entry<Integer, ModifiedAttribute> entry : this.modifnodes.getModifiedAttributes().entrySet()) {
            Base base = this.fact.getRef(entry.getKey());

            if (Common.getIsNamedDeclaration(base) && HeaderGenerator.canCreateHeaderGenerator((NamedDeclaration) base)) {
                NamedDeclaration named = (NamedDeclaration) base;

                HeaderGenerator hgen = HeaderGenerator.createHeaderGenerator(named, getTabSize(), getTabStr());

                result.add(hgen.generateHeader());
            } else {
                this.modifnodes.markNodeAsModified(base.getId());
            }
        }

        for (ReplacedNode node : this.modifnodes.getReplacedNodes()) {
            Base from = this.fact.getRef(node.getNodeId());
            Base to = this.fact.getRef(node.getToId());

            if (Common.getIsPositioned(from)) {
                Positioned posed = (Positioned) from;
                Range rang = posed.getPosition();

                ModifGeneratorVisitor visitor = new ModifGeneratorVisitor(this.srcDirURI, unmodifCollection, getStartIndent(to));

                visitor.setTabSize(this.tabSize);
                visitor.setIndentType(getTabStr());

                visitor.setPrintIndentFirst(false);

                generator.run(visitor, to);

                String filePath = rang.getPath();
                Position begin = calculateBeginPosition(posed, rang);
                Position end = getPositionFromRange(rang, false);
                FilePart filePart = new FilePart(filePath, begin, end);
                result.add(new SwitchThis(filePart, visitor.getOut().toString()));
            } else {
                throw new IllegalArgumentException("The from should be Positioned node.");
            }
        }

        for (ModifiedNode node : this.modifnodes.getRemovedNodes()) {
            Base from = this.fact.getRef(node.getNodeId());

            if (Common.getIsPositioned(from)) {
                Positioned posed = (Positioned) from;
                Range rang = posed.getPosition();
                Base parent = from.getParent();

                if (rang.getPath() == null || "".equals(rang.getPath())) {
                    continue;
                }

                FilePart filePart = null;
                if (Common.getIsIf(parent) && ((If) parent).getFalseSubstatement() == from) {
                    If aktIf = (If) parent;

                    Position begin = getPositionFromRange(aktIf.getSubstatement().getPosition(), false);
                    Position end = getPositionFromRange(rang, false);

                    filePart = new FilePart(rang.getPath(), begin, end);
                } else {
                    Position begin = calculateBeginPosition(posed, rang);
                    String path = rang.getPath();

                    if (Common.getIsParameter(posed)) {
                        deleteSeparatorIfNeed(result, posed);
                    } else if (needDeleteNewLine(posed)) {
                        begin = deleteNewLine(begin, path);
                    }

                    Position end = getPositionFromRange(rang, false);

                    filePart = new FilePart(path, begin, end);
                }
                result.add(new SwitchThis(filePart, ""));
            } else {
                throw new IllegalArgumentException("The from should be Positioned node.");
            }
        }

        for (NewlyAddedNode node : this.modifnodes.getAddedNodes()) {
            Base aktBase = this.fact.getRef(node.getNodeId());

            if (Common.getIsCompilationUnit(aktBase)) {
                CompilationUnit cunit = (CompilationUnit) aktBase;
                SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();
                FilePart part = new FilePart(cunit.getPosition().getPath(), 1, 1, 1, 1);

                visitor.setIndentType(getTabStr());
                generator.run(visitor, cunit);

                result.add(new SwitchThis(part, visitor.getOut().toString()));
//                throw new UnsupportedOperationException("The new file creating is not supported yet.");
            } else {
                Positioned positioned = node.getNode();
                Range range = positioned.getPosition();
                NewPositionKind kind = node.getPositionKind();

                int startIndent = getStartIndent(positioned, kind, aktBase);

                ModifGeneratorVisitor visitor = new ModifGeneratorVisitor(this.srcDirURI, unmodifCollection, startIndent);

                visitor.setIndentType(getTabStr());
                visitor.setTabSize(this.tabSize);

                if (range.getPath() == null || "".equals(range.getPath())) {
                    continue;
                }

                int addToLast = Common.getIsPackageDeclaration(positioned) ? 1 : 0;

                FilePart aktPos = null;
                if (kind == NewPositionKind.AFTER) {
                    Position beginPos = new Position(range.getWideEndLine(), range.getWideEndCol() + addToLast);
                    Position endPos = new Position(range.getWideEndLine(), range.getWideEndCol() + addToLast);
                    aktPos = new FilePart(range.getPath(), beginPos, endPos);
                } else if (kind == NewPositionKind.BEFORE) {
                    Position beginPos = calculateBeginPosition(positioned, range);
                    Position endPos = calculateBeginPosition(positioned, range);
//                    Position beginPos = new Position(range.getWideLine(), range.getWideCol());
//                    Position endPos = new Position(range.getWideLine(), range.getWideCol());

                    aktPos = new FilePart(range.getPath(), beginPos, endPos);
                    visitor.setPrintIndentFirst(false);
                    visitor.setPrintIndentLast(true);
                } else if (kind == NewPositionKind.LAST) {
                    if (!getIsImplemented(positioned)) {
                        throw new UnsupportedOperationException("This type(" + positioned.getNodeKind().toString() + ") of LAST is not implemented yet. ");
                    }

                    Position beginPos = new Position(range.getWideEndLine(), range.getWideEndCol() - 1);
                    Position endPos = new Position(range.getWideEndLine(), range.getWideEndCol() - 1);

                    aktPos = new FilePart(range.getPath(), beginPos, endPos);
                    visitor.setPrintIndentFirst(false);
                    visitor.setPrintIndentLast(true);
                    visitor.setIsAddLast(true);
                } else {
                    throw new UnsupportedOperationException("The NewPositionKind.FIRST is not suppoerted yet.");
                }

                generator.run(visitor, aktBase);
                String string = createStringForAdd(positioned, kind, aktBase, visitor);

                result.add(new SwitchThis(aktPos, string));
            }
        }

        for (ModifiedNode node : this.modifnodes.getModifiedNodes()) {
            Base base = this.fact.getRef(node.getNodeId());

            if (!Common.getIsPositioned(base)) {
                continue;
            }

            ModifGeneratorVisitor visitor = new ModifGeneratorVisitor(this.srcDirURI, unmodifCollection, getStartIndent(base));
            Positioned posed = (Positioned) base;

            visitor.setIndentType(getTabStr());
            visitor.setTabSize(this.tabSize);
            visitor.setPrintIndentFirst(false);
            visitor.setRootBeginPosition(getPositionFromRange(posed.getPosition(), true));

            generator.run(visitor, posed);

            result.add(new SwitchThis(getFilePartFromRange(posed.getPosition()), visitor.getOut().toString()));
        }

        return result;
    }

    private Position deleteNewLine(Position begin, String path) {
        BackLetterIterator fiter = new BackLetterIterator(this.srcDirURI, path, begin, this.tabSize);
        Character next = null;

        while (fiter.hasNext()) {
            next = fiter.next();
            if (next != ' ' && next != '\t') {
                break;
            }
        }

        if (next == '\n') {
            return fiter.getPosition();
        } else {
            return begin;
        }
    }

    private boolean needDeleteNewLine(Positioned posed) {
        Range position = posed.getPosition();

        FrontLetterIterator fiter = new FrontLetterIterator(this.srcDirURI, position.getPath(), getPositionFromRange(position, false), this.tabSize);
        Character next = null;
        while (fiter.hasNext()) {
            next = fiter.next();
            if (next != ' ' && next != '\t') {
                break;
            }
        }

        return next == '\n';
    }

    private Position calculateBeginPosition(Positioned posed, Range rang) {
        Position begin = getPositionFromRange(rang, true);

        EdgeIterator<Comment> commentsIterator = posed.getCommentsIterator();
        while (commentsIterator.hasNext()) {
            begin = updateBeginPosition(begin, commentsIterator);
        }

        if (Common.getIsAnnotatedElement(posed)) {
            AnnotatedElement element = (AnnotatedElement) posed;
            EdgeIterator<Annotation> annotationsIterator = element.getAnnotationsIterator();
            while (annotationsIterator.hasNext()) {
                begin = updateBeginPosition(begin, annotationsIterator);
            }
        }

        return begin;
    }

    private Position updateBeginPosition(Position begin, EdgeIterator<? extends PositionedWithoutComment> commentsIterator) {
        PositionedWithoutComment next = commentsIterator.next();
        Range commPos = next.getPosition();
        Position cbegin = getPositionFromRange(commPos, true);
        if (cbegin.isLowerThan(begin)) {
            begin = cbegin;
        }

        return begin;
    }

    private int getStartIndent(Positioned positioned, NewPositionKind kind, Base aktBase) {
        int startIndent;

        if (Common.getIsComment(aktBase)) {
            startIndent = getStartIndent(positioned);

            if (kind == NewPositionKind.LAST) {
                startIndent++;
            }
        } else {
            startIndent = getStartIndent(aktBase);
        }

        return startIndent;
    }

    private String createStringForAdd(Positioned positioned, NewPositionKind kind, Base aktBase, ModifGeneratorVisitor visitor) {
        String string = visitor.getOut().toString();

        if (Common.getIsComment(aktBase)) {
            string += "\n";

            int indentsize = getStartIndent(positioned);
            for (int i = 0; i < indentsize; i++) {
                string += getTabStr();
            }
        }
        return string;
    }

    public String getTabStr() {
        return this.tabStr;
    }

    private boolean getIsImplemented(Positioned positioned) {
        return Common.getIsTypeDeclaration(positioned) || Common.getIsBlock(positioned) || Common.getIsSwitch(positioned);
    }

    private void deleteSeparatorIfNeed(List<SwitchThis> result, Positioned posed) {
        if (Common.getIsParameter(posed)) {
            Range rang = posed.getPosition();
            Position beginPos = new Position(rang.getWideLine(), rang.getWideCol());
            String path = rang.getPath();
            AbstractLetterIterator liter = new BackLetterIterator(this.srcDirURI, path, beginPos, this.tabSize);
            char aktChar = 0;

            while (liter.hasNext()) {
                aktChar = liter.next();
                if (aktChar == '(' || aktChar == ',') {
                    break;
                }
            }

            if (aktChar != '(' && aktChar != ',') {
                throw new IllegalArgumentException("The method position is invalid.");
            }

            if (aktChar == '(') {
                Position endPos = new Position(rang.getWideEndLine(), rang.getWideEndCol());
                liter = new FrontLetterIterator(this.srcDirURI, path, endPos, this.tabSize);
                aktChar = 0;

                while (liter.hasNext()) {
                    aktChar = liter.next();
                    if (aktChar == ')' || aktChar == ',') {
                        break;
                    }
                }

                if (aktChar == ')') {
                    return;
                }

                if (aktChar != ',') {
                    throw new IllegalArgumentException("The method position is invalid.");
                }
            }

            Position begin = liter.getBeginPosition();
            Position end = liter.getEndPosition();

            FilePart part = new FilePart(rang.getPath(), begin, end);
            result.add(new SwitchThis(part, ""));
        }
    }

    public static Position getPositionFromRange(Range range, boolean beginPos) {
        return new Position(beginPos ? range.getWideLine() : range.getWideEndLine(), beginPos ? range.getWideCol() : range.getWideEndCol());
    }

    public static FilePart getFilePartFromRange(Range pos) {
        return new FilePart(pos.getPath(), new Position(pos.getWideLine(), pos.getWideCol()), new Position(pos.getWideEndLine(), pos.getWideEndCol()));
    }

    public int getTabSize() {
        return this.tabSize;
    }

    public static int getStartIndent(Base to) {
        int result = 0;

        Base parent = to.getParent();
        while (parent != null && !Common.getIsPackage(parent)) {
            if (ModifGeneratorVisitor.needIncIndent(parent)) {
                result++;
            }

            parent = parent.getParent();
        }

        return result;
    }
}
