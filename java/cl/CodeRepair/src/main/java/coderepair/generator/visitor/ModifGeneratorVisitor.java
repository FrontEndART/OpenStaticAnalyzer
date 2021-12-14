package coderepair.generator.visitor;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import coderepair.generator.support.ModifiedNodesPrinter;

import coderepair.generator.fileeditor.FileEditorUtil;
import com.google.gdata.util.common.base.CharEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coderepair.communication.exceptions.RepairAlgIllegalArgumentException;
import columbus.java.asg.Common;
import columbus.java.asg.Range;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.expr.ErroneousTypeExpression;
import columbus.java.asg.expr.ExternalTypeExpression;
import coderepair.generator.transformation.ModifiedNodes.ModifiedNode;

/**
 * Class for generate code by using the ModifiedNodes elements.
 */
public class ModifGeneratorVisitor extends SrcGeneratorVisitor {

    private static final Logger logger = LoggerFactory.getLogger(ModifGeneratorVisitor.class);
    public static final int TABSIZE = 4;

    private Set<Integer> unmodifNodes;
    private int tabSize;
    private URI srcDirURI;

    public ModifGeneratorVisitor(URI srcDirURI) {
        super();
        this.srcDirURI = srcDirURI;
        this.unmodifNodes = new HashSet<Integer>();
        this.tabSize = TABSIZE;
    }

    public ModifGeneratorVisitor(int startIndent) {
        this(null);

        this.indent += startIndent;
    }

    public ModifGeneratorVisitor(URI srcDirURI, Collection<Integer> unmodif, int startIndent) {
        this(startIndent);
        this.srcDirURI = srcDirURI;
        this.unmodifNodes.addAll(unmodif);
    }

    public ModifGeneratorVisitor(URI srcDirURI, Integer[] unmodif, int startIndent) {
        this(srcDirURI, getWideCollectionFromArray(unmodif), startIndent);
    }

    private static Collection<Integer> getWideCollectionFromArray(Integer[] unmodif) {
        Collection<Integer> result = new HashSet<Integer>();

        for (int i = 0; i < unmodif.length; ++i) {
            result.add(unmodif[i]);
        }

        return result;
    }

    public void setTabSize(int newsize) {
        this.tabSize = newsize;
    }

    public int getTabSize() {
        return this.tabSize;
    }

    @Override
    protected void printSemicolon(Base node) {
        if (!isUnmodified(node)) {
            super.printSemicolon(node);
        }
    }

    private boolean isUnmodified(Base node) {
        return this.unmodifNodes.contains(node.getId());
    }

    @Override
    public void visit(Base node, boolean callVirtualBase) {
        if (isNotPrintableAnnotation(node)) {
            incPrintBlocker();
        }

        super.visit(node, callVirtualBase);

        if (isUnmodified(node)) {
            if (isPrintBlockerOff()) {
                if (Common.getIsPositioned(node)) {
                    Positioned posed = (Positioned) node;
                    printPositionedBySource(posed);
                }
            }
            incPrintBlocker();
        }
    }

    @Override
    public void visitEnd(Base node, boolean callVirtualBase) {
        if (isUnmodified(node)) {
            decPrintBlocker();
        }
        super.visitEnd(node, callVirtualBase);
        if (isNotPrintableAnnotation(node)) {
            decPrintBlocker();
        }
    }

    // -- PrimitiveTypeExpression
    @Override
    public void visit(ErroneousTypeExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);

        logger.debug("ErroneousTypeExpression detected.");
        Range position = node.getPosition();
        if ("".equals(position.getPath())) {
            logger.warn("The ErroneousTypeExpression(id{}) has no acceptable position.", String.valueOf(node.getId()));
            return;
        }

        printPositionedBySource(position);
    }

    // -- PrimitiveTypeExpression
    @Override
    public void visit(ExternalTypeExpression node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);

        Range position = node.getPosition();
        if ("".equals(position.getPath())) {
            return;
        }

        logger.debug("Printable ExternalTypeExpression detected.");

        printPositionedBySource(position);
    }

    private void printPositionedBySource(Positioned posed) {
        Range position = posed.getPosition();
        printPositionedBySource(position);
    }

    private void printPositionedBySource(Range position) {
        if ("".equals(position.getPath())) {
            throw new RepairAlgIllegalArgumentException("The ");
        }

        URI resolved = this.srcDirURI.resolve(CharEscapers.uriPathEscaper().escape(position.getPath().replace("\\", "/")));
        printMultiLine(FileEditorUtil.getFileSlice(resolved, ModifiedNodesPrinter.getFilePartFromRange(position), this.tabSize));
    }

    private boolean isNotPrintableAnnotation(Base node) {
        return Common.getIsAnnotation(node) && isUnmodified(node.getParent());
    }

    public static Collection<Integer> getIntCollection(Collection<ModifiedNode> modif) {
        Collection<Integer> result = new HashSet<Integer>();

        Iterator<ModifiedNode> iter = modif.iterator();
        while (iter.hasNext()) {
            ModifiedNode node = iter.next();
            result.add(node.getNodeId());
        }

        return result;
    }
}
