package coderepair.generator.support;

import coderepair.generator.algorithm.AlgorithmSrcGenerator;
import coderepair.generator.support.ModifiedNodesPrinter.SwitchThis;
import coderepair.generator.visitor.ModifGeneratorVisitor;
import coderepair.generator.visitor.SrcGeneratorVisitor;

import coderepair.generator.fileeditor.FileEditorUtil.FilePart;
import coderepair.generator.fileeditor.FileEditorUtil.Position;

import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Range;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Comment;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.expr.Annotation;
import columbus.java.asg.expr.TypeExpression;
import columbus.java.asg.statm.Block;
import columbus.java.asg.struc.AnnotatedElement;
import columbus.java.asg.struc.GenericDeclaration;
import columbus.java.asg.struc.Member;
import columbus.java.asg.struc.NamedDeclaration;
import columbus.java.asg.struc.NormalMethod;
import columbus.java.asg.struc.Parameter;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.struc.TypeParameter;

/**
 * Class for generates the header of classes, interfaces, enums and methods.
 */
public abstract class HeaderGenerator {

    private int tabSize;
    private String tabStr;

    /**
     * Constructor.
     * 
     * @param tabSize The tabsize.
     * @param tabStr The tab String.
     */
    public HeaderGenerator(int tabSize, String tabStr) {
        super();

        this.tabSize = tabSize;
        this.tabStr = tabStr;
    }

    /**
     * Returns the tab String.
     * 
     * @return The tab String.
     */
    public String getTabStr() {
        return this.tabStr;
    }

    /**
     * Generates header for the
     * 
     * @return The generated header.
     */
    public SwitchThis generateHeader() {

        Range position = getPosition();
        String path = position.getPath();
        Position beginPos = ModifiedNodesPrinter.getPositionFromRange(position, true);
        Position endPos = getEndPosition();

        String newStr = generateAnnotateAndHeader();

        return new SwitchThis(new FilePart(path, beginPos, endPos), newStr);
    }

    private String generateAnnotateAndHeader() {
        StringBuilder builder = new StringBuilder();

        builder.append(generateAnnotations());
        builder.append(generateNewStr());

        return builder.toString();
    }

    private String generateAnnotations() {
        AnnotatedElement annotatedElem = getAnnotatedElement();
        EdgeIterator<Annotation> iter = annotatedElem.getAnnotationsIterator();
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();
        StringBuilder builder = new StringBuilder();

        while (iter.hasNext()) {
            Annotation annot = iter.next();
            ModifGeneratorVisitor visitor = new ModifGeneratorVisitor(ModifiedNodesPrinter.getStartIndent(annotatedElem));

            visitor.setIndentType(getTabStr());
            visitor.setTabSize(getTabSize());
            visitor.setPrintIndentFirst(false);
            visitor.setPrintIndentLast(true);

            generator.run(visitor, annot);

            builder.append(visitor.getOut().toString());
        }

        return builder.toString();
    }

    protected void generateGenerics(StringBuilder builder, Base base) {
        if (!Common.getIsGenericDeclaration(base)) {
            return;
        }

        GenericDeclaration generic = (GenericDeclaration) base;

        if (generic.getTypeParametersIsEmpty()) {
            return;
        }

        builder.append("<");

        generateTypeParameters(builder, generic);

        builder.append(">");
    }

    private void generateTypeParameters(StringBuilder builder, GenericDeclaration generic) {
        EdgeIterator<TypeParameter> tparam = generic.getTypeParametersIterator();
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();
        boolean first = true;

        while (tparam.hasNext()) {
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }

            TypeParameter parameter = tparam.next();
            SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();

            visitor.setIndentType(getTabStr());

            generator.run(visitor, parameter);
            builder.append(visitor.getOut().toString());
        }
    }

    protected abstract Range getPosition();

    protected abstract Position getEndPosition();

    protected abstract String generateNewStr();

    protected abstract AnnotatedElement getAnnotatedElement();

    protected int getTabSize() {
        return this.tabSize;
    }

    /**
     * Creates header generator for the given named declaration.
     * 
     * @param declaration The named declaration.
     * @param tabSize The tab size.
     * @param tabStr The tab string.
     * @return The created header generator.
     */
    public static HeaderGenerator createHeaderGenerator(NamedDeclaration declaration, int tabSize, String tabStr) {
        if (!canCreateHeaderGenerator(declaration)) {
            throw new UnsupportedOperationException("This type of headerGenerator is not implemented yet.");
        }

        if (Common.getIsNormalMethod(declaration)) {
            return new MethodHeaderGenerator((NormalMethod) declaration, tabSize, tabStr);
        } else if (Common.getIsTypeDeclaration(declaration)) {
            return new TypeDeclarationGenerator((TypeDeclaration) declaration, tabSize, tabStr);
        } else {
            throw new IllegalArgumentException("The NamedDeclaration is not a TypeDeclaration nor a NormalMethod.");
        }
    }

    /**
     * Returns true if the NamedDeclaration is acceptable by the createHeaderGenerator method.
     * 
     * @param declaration The declaration.
     * @return True if the NamedDeclaration is acceptable by the createHeaderGenerator method.
     */
    public static boolean canCreateHeaderGenerator(NamedDeclaration declaration) {
        return Common.getIsNormalMethod(declaration) || Common.getIsTypeDeclaration(declaration);
    }
}

//TypeDeclarationGenerator
class TypeDeclarationGenerator extends HeaderGenerator {

    private TypeDeclaration typeDecl;

    public TypeDeclarationGenerator(TypeDeclaration td, int tabSize, String tabStr) {
        super(tabSize, tabStr);
        this.typeDecl = td;
    }

    @Override
    protected Range getPosition() {
        return this.typeDecl.getPosition();
    }

    @Override
    protected Position getEndPosition() {
        EdgeIterator<Member> members = this.typeDecl.getMembersIterator();
        Position endPos = null;
        while (/* endPos == null && */members.hasNext()) {// The member list is not ordered yet.
            Member aktMember = members.next();

            if (Common.getIsPositioned(aktMember)) {
                Range position = ((Positioned) aktMember).getPosition();
                endPos = getLowerPosition(endPos, position);
            }

            if (!aktMember.getCommentsIsEmpty()) {
                EdgeIterator<Comment> commentIter = aktMember.getCommentsIterator();
                while (commentIter.hasNext()) {
                    Comment comm = commentIter.next();
                    Range position = comm.getPosition();

                    endPos = getLowerPosition(endPos, position);
                }
            }

            if (!aktMember.getAnnotationsIsEmpty()) {
                EdgeIterator<Annotation> annotIter = aktMember.getAnnotationsIterator();
                while (annotIter.hasNext()) {
                    Annotation annot = annotIter.next();
                    Range position = annot.getPosition();

                    endPos = getLowerPosition(endPos, position);
                }
            }
        }

        if (endPos == null) {
            Range position = this.typeDecl.getPosition();
            endPos = new Position(position.getWideEndLine(), position.getWideEndCol() - 1);
        }

        return endPos;
    }

    private Position getLowerPosition(Position endPos, Range position) {
        if (!getIsEmptyPosition(position)) {
            Position aktPos = ModifiedNodesPrinter.getPositionFromRange(position, true);
            if (endPos == null || endPos.isLowerThan(aktPos)) {
                return aktPos;
            }
        }
        return endPos;
    }

    private boolean getIsEmptyPosition(Range position) {
        return position == null || position.getPath() == null || "".equals(position.getPath());
    }

    @Override
    protected String generateNewStr() {
        StringBuilder builder = new StringBuilder();

        builder.append(SrcGeneratorVisitor.generateModifiers(this.typeDecl));
        builder.append(getKeyWord());
        builder.append(" ");
        builder.append(this.typeDecl.getName());
        builder.append(generateExtends());
        builder.append(generateImplements());
        builder.append(" {\n");
        if (hasNotGeneratedMethod()) {
            builder.append(printIndent());
        } else {
            builder.append(printPreIndent());
        }

        return builder.toString();
    }

    private boolean hasNotGeneratedMethod() {
        EdgeIterator<Member> memberIt = this.typeDecl.getMembersIterator();

        while (memberIt.hasNext()) {
            Member mem = memberIt.next();

            if (Common.getIsPositioned(mem)) {
                Positioned posed = (Positioned) mem;

                if (!posed.getIsCompilerGenerated() && !posed.getIsToolGenerated()) {
                    return true;
                }
            }
        }

        return false;
    }

    private String printPreIndent() {
        int indentSize = ModifiedNodesPrinter.getStartIndent(this.typeDecl);

        return printIndent(indentSize);
    }

    private String printIndent() {
        int indentSize = ModifiedNodesPrinter.getStartIndent(this.typeDecl) + 1;

        return printIndent(indentSize);
    }

    private String printIndent(int indentSize) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indentSize; i++) {
            builder.append(getTabStr());
        }

        return builder.toString();
    }

    private String generateImplements() {
        StringBuilder builder = new StringBuilder();

        builder.append(" implements ");
        boolean hasOne = false;

        EdgeIterator<TypeExpression> iter = this.typeDecl.getSuperInterfacesIterator();
        while (iter.hasNext()) {
            TypeExpression texp = iter.next();

            if (texp == null || texp.getIsCompilerGenerated()) {
                continue;
            } else if (!hasOne) {
                hasOne = true;
            } else {
                builder.append(", ");
            }

            builder.append(generateTypeExpression(texp));
        }

        if (!hasOne) {
            return "";
        }

        return builder.toString();
    }

    private String generateTypeExpression(TypeExpression texp) {
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();
        SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();

        visitor.setIndentType(getTabStr());

        generator.run(visitor, texp);

        return visitor.getOut().toString();
    }

    private String generateExtends() {
        StringBuilder builder = new StringBuilder();

        builder.append(" extends ");
        TypeExpression texp = this.typeDecl.getSuperClass();

        if (texp == null || texp.getIsCompilerGenerated()) {
            return "";
        }

        builder.append(generateTypeExpression(texp));

        return builder.toString();
    }

    private String getKeyWord() {
        if (Common.getIsAnnotationType(this.typeDecl)) {
            return "@interface";
        } else if (Common.getIsClassDeclaration(this.typeDecl)) {
            return "class";
        } else if (Common.getIsEnum(this.typeDecl)) {
            return "enum";
        } else if (Common.getIsInterfaceDeclaration(this.typeDecl)) {
            return "interface";
        } else {
            throw new IllegalArgumentException("The typeDeclaration is not a regular type declaration.");
        }
    }

    @Override
    protected AnnotatedElement getAnnotatedElement() {
        return this.typeDecl;
    }
}

//MethodHeaderGenerator
class MethodHeaderGenerator extends HeaderGenerator {

    private NormalMethod meth;

    public MethodHeaderGenerator(NormalMethod method, int tabSize, String tabStr) {
        super(tabSize, tabStr);
        this.meth = method;
    }

    @Override
    protected Range getPosition() {
        return this.meth.getPosition();
    }

    @Override
    protected String generateNewStr() {
        StringBuilder builder = new StringBuilder();

        builder.append(SrcGeneratorVisitor.generateModifiers(this.meth));
        if (this.meth.getReturnType() != null) {
            builder.append(generateMethodtype(this.meth.getReturnType()));
            builder.append(" ");
        }

        builder.append(this.meth.getName());
        generateGenerics(builder);
        generateParameters(builder);
        generateThrow(builder);

        return builder.toString();
    }

    private void generateThrow(StringBuilder builder) {
        if (this.meth.getThrownExceptionsIsEmpty()) {
            return;
        }

        builder.append(" throws ");
        EdgeIterator<TypeExpression> iter = this.meth.getThrownExceptionsIterator();
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();
        boolean first = true;
        while (iter.hasNext()) {
            TypeExpression expr = iter.next();
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }

            SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();

            visitor.setIndentType(getTabStr());

            generator.run(visitor, expr);

            builder.append(visitor.getOut().toString());
        }
    }

    private String generateMethodtype(TypeExpression returnType) {
        SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();

        visitor.setIndentType(getTabStr());

        generator.run(visitor, returnType);

        return visitor.getOut().toString();
    }

    @Override
    protected Position getEndPosition() {
        Block blck = this.meth.getBody();

        if (blck == null) {
            Range position = this.meth.getPosition();
            return new Position(position.getWideEndLine(), position.getWideEndCol() - 1);
        }

        return ModifiedNodesPrinter.getPositionFromRange(blck.getPosition(), true);
    }

    private void generateGenerics(StringBuilder builder) {
        generateGenerics(builder, this.meth);
    }

    private void generateParameters(StringBuilder builder) {
        builder.append("(");

        EdgeIterator<Parameter> params = this.meth.getParametersIterator();
        AlgorithmSrcGenerator generator = new AlgorithmSrcGenerator();
        boolean first = true;

        while (params.hasNext()) {
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }

            Parameter param = params.next();
            SrcGeneratorVisitor visitor = new SrcGeneratorVisitor();

            visitor.setIndentType(getTabStr());

            generator.run(visitor, param);

            builder.append(visitor.getOut().toString());
        }

        builder.append(")");
    }

    @Override
    protected AnnotatedElement getAnnotatedElement() {
        return this.meth;
    }
}
