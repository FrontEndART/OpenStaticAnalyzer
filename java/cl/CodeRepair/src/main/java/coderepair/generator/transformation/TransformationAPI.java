package coderepair.generator.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import columbus.java.asg.support.GeneratedSupport;
import coderepair.communication.exceptions.RepairAlgIllegalArgumentException;
import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Factory;
import columbus.java.asg.Range;
import columbus.java.asg.algorithms.AlgorithmPreorder;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Named;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.base.PositionedWithoutComment;
import columbus.java.asg.enums.AccessibilityKind;
import columbus.java.asg.enums.AssignmentOperatorKind;
import columbus.java.asg.enums.InfixOperatorKind;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.enums.PostfixOperatorKind;
import columbus.java.asg.enums.PrefixOperatorKind;
import columbus.java.asg.enums.PrimitiveTypeKind;
import columbus.java.asg.expr.ArrayTypeExpression;
import columbus.java.asg.expr.Assignment;
import columbus.java.asg.expr.BooleanLiteral;
import columbus.java.asg.expr.Conditional;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.FieldAccess;
import columbus.java.asg.expr.Identifier;
import columbus.java.asg.expr.InfixExpression;
import columbus.java.asg.expr.MethodInvocation;
import columbus.java.asg.expr.NewClass;
import columbus.java.asg.expr.ParenthesizedExpression;
import columbus.java.asg.expr.PostfixExpression;
import columbus.java.asg.expr.PrefixExpression;
import columbus.java.asg.expr.PrimitiveTypeExpression;
import columbus.java.asg.expr.QualifiedTypeExpression;
import columbus.java.asg.expr.SimpleTypeExpression;
import columbus.java.asg.expr.This;
import columbus.java.asg.expr.TypeApplyExpression;
import columbus.java.asg.expr.TypeExpression;
import columbus.java.asg.expr.TypeUnionExpression;
import columbus.java.asg.expr.WildcardExpression;
import columbus.java.asg.statm.Block;
import columbus.java.asg.statm.ExpressionStatement;
import columbus.java.asg.statm.Handler;
import columbus.java.asg.statm.If;
import columbus.java.asg.statm.Return;
import columbus.java.asg.statm.Statement;
import columbus.java.asg.statm.Throw;
import columbus.java.asg.statm.Try;
import columbus.java.asg.struc.ClassDeclaration;
import columbus.java.asg.struc.CompilationUnit;
import columbus.java.asg.struc.Import;
import columbus.java.asg.struc.InitializerBlock;
import columbus.java.asg.struc.Member;
import columbus.java.asg.struc.Method;
import columbus.java.asg.struc.MethodDeclaration;
import columbus.java.asg.struc.NormalMethod;
import columbus.java.asg.struc.Package;
import columbus.java.asg.struc.PackageDeclaration;
import columbus.java.asg.struc.Parameter;
import columbus.java.asg.struc.Scope;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.struc.Variable;
import columbus.java.asg.struc.VariableDeclaration;
import columbus.java.asg.support.DeepCopyAlgorithm;
import columbus.java.asg.type.ArrayType;
import columbus.java.asg.type.ClassType;
import columbus.java.asg.type.MethodType;
import columbus.java.asg.type.PackageType;
import columbus.java.asg.type.ParameterizedType;
import columbus.java.asg.type.Type;
import columbus.java.asg.type.UnionType;
import columbus.java.asg.type.WildcardType;
import columbus.java.asg.visitors.Visitor;
import columbus.java.asg.visitors.VisitorAbstractNodes;
import coderepair.generator.transformation.ModifiedNodes.NewPositionKind;

/**
 * The collection of helper methods to manipulate the java ASG easier.
 */
public class TransformationAPI {

    private Map<String, Integer> nameToIdMap = null;
    private Factory factory;
    private AlgorithmPreorder algorithmPreorder;

    public TransformationAPI(Factory factory) {
        super();
        this.factory = factory;
    }

    public void reload(Factory factory) {
        this.factory = factory;
        if (this.algorithmPreorder == null) {
            this.algorithmPreorder = new AlgorithmPreorder();
        }
        this.nameToIdMap = new TreeMap<String, Integer>();
        this.algorithmPreorder.run(factory, new VisitorAbstractNodes() {

            @Override
            public void visit(TypeDeclaration node, boolean callVirtualBase) {
                super.visit(node, callVirtualBase);
                TransformationAPI.this.nameToIdMap.put(node.getBinaryName(), node.getId());
            }

        });
    }

    /**
     * Gets the method for the given class by the given name.<br>
     * <br>
     * <i>This gives back the last overridden method in the type hierarchy.</i>
     * 
     * @param classDeclaration the class whose method we are looking for.
     * @param name the name of the method we are looking for.
     * @return the found method or <code>null</code> if there is none.
     */
    public static MethodDeclaration getMethodForClass(ClassDeclaration classDeclaration, String name) {
        MethodDeclaration ret = null;
        ClassDeclaration clazz = classDeclaration;

        outer: do {
            EdgeIterator<Member> memberIterator = clazz.getMembersIterator();
            while (memberIterator.hasNext()) {
                Member next = memberIterator.next();
                if (next instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) next;
                    if (name.equals(((MethodDeclaration) next).getName())) {
                        ret = method;
                        break outer;
                    }
                }
            }

            TypeExpression superClass = clazz.getSuperClass();
            if (superClass != null) {
                TypeDeclaration typeDecl = TransformationAPI.getTypeDeclForType(superClass.getType());
                if (typeDecl instanceof ClassDeclaration) {
                    clazz = (ClassDeclaration) typeDecl;
                } else {
                    throw new RepairAlgIllegalArgumentException("The type of the SuperClass is not a class.");
                }
            } else {
                clazz = null;
            }
        } while (ret == null && clazz != null);

        return ret;
    }

    /**
     * Checks the "return" type of the expression against the given qualified name
     * 
     * @param expression the expression which type should be checked
     * @param qualifiedName the name type should be (e.g.: <code>java.lang.String</code>)
     * @return true if the type's qualified name equals the given name
     */
    public static boolean checkExpressionTypeByQualifiedName(Expression expression, String qualifiedName) {
        final Type type = expression.getType(); // only classtype, paramtype allowed
        TypeDeclaration typeDeclaration = getTypeDeclForType(type);
        return typeDeclaration.getBinaryName().equals(qualifiedName);
    }

    /**
     * Returns the {@link TypeDeclaration} for the given {@link Type}.<br>
     * <br>
     * <i>This methods helps to deal with the difference between {@link ParameterizedType} and {@link ClassType}.</i>
     * 
     * @param type
     * @return
     */
    public static TypeDeclaration getTypeDeclForType(Type type) {
        TypeDeclaration typeDeclaration = null;
        if (type instanceof ParameterizedType) { // if it's a paramtype we need
                                                 // the rawtype
            ParameterizedType parameterizedType = (ParameterizedType) type;
            type = parameterizedType.getRawType();
        }
        if (type instanceof ClassType) { // which type does it represent?
            ClassType classType = (ClassType) type;
            typeDeclaration = classType.getRefersTo();
        }  else {
            throw new RepairAlgIllegalArgumentException("Expression must be a ClassType (or ParameterizedType)!");
        }
        return typeDeclaration;
    }

    /**
     * Returns the {@link ClassDeclaration} for the give unique name (e.g.: <code>java.lang.String</code>).
     * 
     * @param uniqueName
     * @return
     */
    public ClassDeclaration getClassByUniqueName(String uniqueName) {
        if (this.nameToIdMap == null) {
            reload(this.factory);
        }
        final Integer id = this.nameToIdMap.get(uniqueName);
        if (id == null) {
            throw new RepairAlgIllegalArgumentException("This uniquename (" + uniqueName + ") cannot be found in the factory");
        }
        return (ClassDeclaration) this.factory.getRef(id.intValue());
    }

    /**
     * Determines whether a node is after another node.
     * 
     * @param node1 if this node comes second, the method return true.
     * @param node2 if this node comes second, the method returns false.
     * @return if node1 is after (the end of) node2
     */
    public static boolean isNodeAfterAnother(PositionedWithoutComment node1, PositionedWithoutComment node2) {
        if (node1.getPosition().getWideEndLine() > node2.getPosition().getWideEndLine()) {
            return true;
        } else if (node1.getPosition().getWideEndLine() == node2.getPosition().getWideEndLine()) {
            if (node1.getPosition().getWideEndCol() > node2.getPosition().getWideEndCol()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Swaps two node in the ASG.
     * 
     * @param from The node we will swap.
     * @param to The node we will swap to.
     */
    public static void swapStatements(Base from, Base to) {
        GeneratedSupport.swapStatements(from, to);
    }

    /**
     * Remove the node from the tree.
     * 
     * @param node The node we want to be removed.
     */
    public static void removeStatement(Base node) {
        GeneratedSupport.removeStatement(node);
    }

    /**
     * Adds a node after another node in the ASG.
     * 
     * @param from The reference node.
     * @param to The node we should add after the other.
     */
    public static void addAfterStatement(Base from, Base to) {
        GeneratedSupport.addAfterStatement(from, to);
    }

    /**
     * Adds a node before another node in the ASG.
     * 
     * @param from The reference node.
     * @param to The node we should add before the other.
     */
    public static void addBeforeStatement(Base from, Base to) {
        GeneratedSupport.addBeforeStatement(from, to);
    }

    /**
     * Creates and returns a MethodType object from NormalMethod.
     * 
     * @param meth The method we use to create the MethodType.
     * @return The MethodType object.
     */
    public static MethodType createMethodType(NormalMethod meth) {
        Factory fact = meth.getFactory();
        List<Integer> paramlist = new ArrayList<Integer>();
        EdgeIterator<Parameter> paramIter = meth.getParametersIterator();
        while (paramIter.hasNext()) {
            Parameter param = paramIter.next();
            paramlist.add(createTypeFromTypeExpression(param.getType()).getId());
        }

        List<Integer> thrownlist = new ArrayList<Integer>();
        EdgeIterator<TypeExpression> thrownIter = meth.getThrownExceptionsIterator();
        while (thrownIter.hasNext()) {
            TypeExpression texpr = thrownIter.next();
            thrownlist.add(createTypeFromTypeExpression(texpr).getId());
        }

        TypeExpression returnType = meth.getReturnType();
        Type type;
        if (returnType == null) {
            type = fact.createVoidType();
        } else {
            type = createTypeFromTypeExpression(meth.getReturnType());
        }

        return fact.createMethodType(type.getId(), paramlist, thrownlist);
    }

    /**
     * Creates a Type from a TypeExpression node.
     * 
     * @param node The given TypeExpression node.
     * @return The Type of the TypeExpression.
     */
    public static Type createTypeFromTypeExpression(TypeExpression node) {
        if (node == null) {
            throw new RepairAlgIllegalArgumentException("The type expression is null.");
        }

        if (node.getType() != null) {
            return node.getType();
        }

        Factory fact = node.getFactory();
        Type result = null;
        if (Common.getIsArrayTypeExpression(node)) {
            ArrayTypeExpression etype = (ArrayTypeExpression) node;
            result = fact.createArrayType(1, createTypeFromTypeExpression(etype.getComponentType()).getId());
        } else if (Common.getIsExternalTypeExpression(node)) {
            result = fact.createVoidType();
        } else if (Common.getIsPrimitiveTypeExpression(node)) {
            PrimitiveTypeExpression etype = (PrimitiveTypeExpression) node;
            result = getPrimitiveTypeFromExpressionType(fact, etype);
        } else if (Common.getIsQualifiedTypeExpression(node)) {
            QualifiedTypeExpression etype = (QualifiedTypeExpression) node;
            result = createTypeFromTypeExpression(etype.getSimpleType());
        } else if (Common.getIsSimpleTypeExpression(node)) {
            SimpleTypeExpression etype = (SimpleTypeExpression) node;
            result = etype.getType();
        } else if (Common.getIsTypeApplyExpression(node)) {
            TypeApplyExpression etype = (TypeApplyExpression) node;
            List<Integer> paramTypeList = new ArrayList<Integer>();
            EdgeIterator<TypeExpression> iter = etype.getTypeArgumentsIterator();
            while (iter.hasNext()) {
                paramTypeList.add(Integer.valueOf(createTypeFromTypeExpression(iter.next()).getId()));
            }
            result = createParameterizedType(createTypeFromTypeExpression(etype.getRawType()), paramTypeList);
        } else if (Common.getIsTypeUnionExpression(node)) {
            TypeUnionExpression etype = (TypeUnionExpression) node;
            List<Integer> altertypes = new ArrayList<Integer>();
            EdgeIterator<TypeExpression> iter = etype.getAlternativesIterator();
            while (iter.hasNext()) {
                altertypes.add(Integer.valueOf(createTypeFromTypeExpression(iter.next()).getId()));
            }
            result = fact.createUnionType(altertypes);
        } else if (Common.getIsWildcardExpression(node)) {
            WildcardExpression etype = (WildcardExpression) node;
            result = createWildcardTypeFromKind(etype);
        } else {
            throw new UnsupportedOperationException("This type of TypeExpression(id" + node.getId() + ") is not supported yet.");
        }

        return result;
    }

    /**
     * Creates a Type from a WildcardExpression.
     * 
     * @param etype The WildcardExpression.
     * @return The Type from a WildCardExpression.
     */
    private static Type createWildcardTypeFromKind(WildcardExpression etype) {
        Factory fact = etype.getFactory();

        switch (etype.getKind()) {
            case tbkExtends:
                return fact.createUpperBoundedWildcardType(createTypeFromTypeExpression(etype.getBound()).getId());
            case tbkSuper:
                return fact.createLowerBoundedWildcardType(createTypeFromTypeExpression(etype.getBound()).getId());
            case tbkWildcard:
                return fact.createUnboundedWildcardType(createTypeFromTypeExpression(etype.getBound()).getId());
        }

        throw new RepairAlgIllegalArgumentException("The type(id" + etype.getId() + ") has invalid kind.");
    }

    /**
     * Creates and returns a Type from PrimitiveTypeExpression.
     * 
     * @param fact The factory belongs to the TypeExpression.
     * @param etype The given PrimitiveTypeExpression.
     * @return The Type of the PrimitiveTypeExpression.
     */
    private static Type getPrimitiveTypeFromExpressionType(Factory fact, PrimitiveTypeExpression etype) {
        Type result = null;
        switch (etype.getKind()) {
            case ptkBoolean:
                result = fact.createBooleanType();
                break;
            case ptkByte:
                result = fact.createByteType();
                break;
            case ptkChar:
                result = fact.createCharType();
                break;
            case ptkDouble:
                result = fact.createDoubleType();
                break;
            case ptkFloat:
                result = fact.createFloatType();
                break;
            case ptkInt:
                result = fact.createIntType();
                break;
            case ptkLong:
                result = fact.createLongType();
                break;
            case ptkShort:
                result = fact.createShortType();
                break;
            case ptkVoid:
                result = fact.createByteType();
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Returns a NormalMethod from the tree.
     * 
     * @param path The path of the returned NormalMethod.
     * @param fact The NormalMethod's factory.
     * @param paramtype The parameter type list of the returned method.
     * @return The founded NormalMethod.
     */
    public static NormalMethod getNormalMethod(String path, Factory fact, List<Type> paramtype) {
        return getNormalMethod(tokenizeStringOnDot(path), fact, paramtype);
    }

    /**
     * Returns a NormalMethod from the tree.
     * 
     * @param pathlist The path list of the returned NormalMethod.
     * @param fact The NormalMethod's factory.
     * @param paramtype The parameter type list of the returned method.
     * @return The founded NormalMethod.
     */
    public static NormalMethod getNormalMethod(List<String> pathlist, Factory fact, List<Type> paramtype) {
        if (pathlist.isEmpty()) {
            throw new RepairAlgIllegalArgumentException("The list cannot be empty.");
        }

        TypeDeclaration td = getTypeDeclaration(pathlist.subList(0, pathlist.size() - 1), fact);

        String methodname = pathlist.get(pathlist.size() - 1);

        return getNormalMethod(td, methodname, paramtype);
    }

    /**
     * Returns a NormalMethod from the tree.
     * 
     * @param td The type declaration of the returned NormalMethod.
     * @param methodname The name of the method.
     * @param paramtype The parameter type list of the returned method.
     * @return The founded NormalMethod.
     */
    public static NormalMethod getNormalMethod(TypeDeclaration td, String methodname, List<Type> paramtype) {
        if (td != null) {
            EdgeIterator<Member> iter = td.getMembersIterator();

            while (iter.hasNext()) {
                Member member = iter.next();

                if (Common.getIsNormalMethod(member)) {
                    NormalMethod method = (NormalMethod) member;

                    if (method.getName().equals(methodname) && canApplyTheseParameters(method, paramtype)) {
                        return method;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns true if the method can call by these type of parameters.
     * 
     * @param method The method we checking.
     * @param paramtype The parameter type list we use.
     * @return True if the method is callable.
     */
    private static boolean canApplyTheseParameters(NormalMethod method, List<Type> paramtype) {
        if (method.getParametersSize() != paramtype.size()) {
            return false;
        }

        EdgeIterator<Parameter> paramiter = method.getParametersIterator();
        Iterator<Type> typeiter = paramtype.iterator();

        while (typeiter.hasNext()) {
            Parameter param = paramiter.next();
            if (param == null) {
                return false;
            }

            TypeExpression texpr = param.getType();
            if (texpr == null) {
                return false;
            }

            if (texpr.getType() == null) {
                return false;
            }

            if (!canApplyParameter(typeiter, texpr)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if the type iterator equals with these type of parameters.
     * 
     * @param typeiter The TypeIterator we analyze.
     * @param texpr The type expression we checking.
     * @return True if acceptable.
     */
    private static boolean canApplyParameter(Iterator<Type> typeiter, TypeExpression texpr) {
        Type partype = texpr.getType();
        Type type = typeiter.next();

        if (partype.getNodeKind() != type.getNodeKind() && !Common.getIsNullType(type)) {
            return false;
        }

        if (Common.getIsPrimitiveType(type) || Common.getIsPrimitiveType(partype)) {
            if (partype != type) {
                return false;
            }
        } else if ((Common.getIsNullType(type) || canGetTypeDeclarationFrom(type)) && canGetTypeDeclarationFrom(partype)) {
            if (Common.getIsNullType(type)) {
                Set<TypeDeclaration> partdset = getTypeDeclarationsFromType(partype);
                Set<TypeDeclaration> typetdset = getTypeDeclarationsFromType(type);

                if (partdset.size() != typetdset.size() && partdset.size() != 1) {
                    TypeDeclaration partd = partdset.iterator().next();
                    TypeDeclaration typetd = typetdset.iterator().next();

                    if (!isHierarchicalParent(partd, typetd)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Returns True if the given type is UnionType, ParameterizedType, ClassType or ArrayType.
     * 
     * @param exprType The analyzed type.
     * @return True if the given type is UnionType, ParameterizedType, ClassType or ArrayType.
     */
    private static boolean canGetTypeDeclarationFrom(Type exprType) {
        return Common.getIsUnionType(exprType) || Common.getIsParameterizedType(exprType) || Common.getIsClassType(exprType) || Common.getIsArrayType(exprType);
    }

    /**
     * Returns the variable declaration by the given path.
     * 
     * @param path The path of the variable.
     * @param fact The factory of the variable.
     * @return The variable declaration by the given path.
     */
    public static VariableDeclaration getVariableDeclaration(String path, Factory fact) {
        return getVariableDeclaration(tokenizeStringOnDot(path), fact);
    }

    /**
     * Returns the variable declaration by the given path.
     * 
     * @param pathlist The path of the variable.
     * @param fact The factory of the variable.
     * @return The variable declaration by the given path.
     */
    public static VariableDeclaration getVariableDeclaration(List<String> pathlist, Factory fact) {
        if (pathlist.isEmpty()) {
            throw new RepairAlgIllegalArgumentException("The list cannot be empty.");
        }

        TypeDeclaration td = getTypeDeclaration(pathlist.subList(0, pathlist.size() - 1), fact);

        if (td != null) {
            EdgeIterator<Member> iter = td.getMembersIterator();
            String varName = pathlist.get(pathlist.size() - 1);

            while (iter.hasNext()) {
                Member member = iter.next();

                if (Common.getIsVariableDeclaration(member)) {
                    VariableDeclaration variable = (VariableDeclaration) member;

                    if (variable.getName().equals(varName)) {
                        return variable;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns the package by the given path.
     * 
     * @param pack The path of the Package.
     * @param fact The factory of the Package.
     * @param createIfNotExists If true, the Package will be created if not exists.
     * @return The package by the given path.
     */
    public static Package getPackage(String pack, Factory fact, boolean createIfNotExists) {
        return getPackage(tokenizeStringOnDot(pack), fact, createIfNotExists);
    }

    /**
     * Returns the package by the given path.
     * 
     * @param pathlist The path of the Package.
     * @param fact The factory of the Package.
     * @param createIfNotExists If true, the Package will be created if not exists.
     * @return The package by the given path.
     */
    public static Package getPackage(List<String> pathlist, Factory fact, boolean createIfNotExists) {
        return getPackage(pathlist, fact, fact.getRoot(), createIfNotExists);
    }

    /**
     * Returns the package by the given path and the root package.
     * 
     * @param pathlist The path of the Package.
     * @param fact The factory of the Package.
     * @param root Tha root of the Package tree.
     * @param createIfNotExists If true, the Package will be created if not exists.
     * @return The package by the given path.
     */
    public static Package getPackage(List<String> pathlist, Factory fact, Package root, boolean createIfNotExists) {
        if (pathlist.isEmpty()) {
            return root;
        }

        EdgeIterator<Member> iter = root.getMembersIterator();
        String aktName = pathlist.get(0);
        boolean lastElement = pathlist.size() == 1;

        while (iter.hasNext()) {
            Member aktMember = iter.next();

            if (Common.getIsNamed(aktMember)) {
                Named named = (Named) aktMember;
                String name = named.getName();

                if (aktName.equals(name)) {
                    if (Common.getIsPackage(named)) {
                        Package pack = (Package) named;
                        if (lastElement) {
                            return (Package) named;
                        } else {
                            return getPackage(pathlist.subList(1, pathlist.size()), fact, pack, createIfNotExists);
                        }
                    } else {
                        throw new RepairAlgIllegalArgumentException("The element (" + name + ") is not a Package.");
                    }
                }
            }
        }

        if (createIfNotExists) {
            Package newPack = (Package) fact.createNode(NodeKind.ndkPackage);

            root.addMembers(newPack);

            newPack.setName(aktName);
            newPack.setQualifiedName(createQualifiedNameForPackage(newPack));

            if (lastElement) {
                return newPack;
            } else {
                return getPackage(pathlist.subList(1, pathlist.size()), fact, newPack, createIfNotExists);
            }
        } else {
            return null;
        }
    }

    /**
     * Creates and returns a QualifiedName for the given Package.
     * 
     * @param newPack The Package.
     * @return The qualifiedName for the given Package.
     */
    public static String createQualifiedNameForPackage(Package newPack) {
        return createAttributeForPackage(newPack, new PackageAttributeCreator<String>() {

            @Override
            public String ifNullOrRoot() {
                return "";
            }

            @Override
            public String ifNullOrRootParent(Package pack) {
                return pack.getName();
            }

            @Override
            public String ifPackageWithPackageParent(Package pack, Package parent) {
                return createQualifiedNameForPackage(parent) + "." + pack.getName();
            }

        });
    }

    /**
     * Creates a T typed object for the given Package.
     * 
     * @param newPack The Package.
     * @param pac The creator object.
     * @return A T typed object for the given Package.
     */
    private static <T> T createAttributeForPackage(Package newPack, PackageAttributeCreator<T> pac) {
        if (newPack == null || newPack == newPack.getFactory().getRoot()) {
            return pac.ifNullOrRoot();
        } else {
            Factory fact = newPack.getFactory();
            Base parent = newPack.getParent();
            if (parent == null || parent == fact.getRoot()) {
                return pac.ifNullOrRootParent(newPack);
            } else if (Common.getIsPackage(parent)) {
                return pac.ifPackageWithPackageParent(newPack, (Package) parent);
            } else {
                throw new RepairAlgIllegalArgumentException("The package's parent is not a package.");
            }
        }
    }

    /**
     * Creates and returns a PackageDeclaration from the given Package.
     * 
     * @param pckg The package.
     * @return A created PackageDeclaration from the given Package.
     */
    public static PackageDeclaration createPackageDeclarationFromPackage(Package pckg) {
        final Factory fact = pckg.getFactory();
        Expression expression = createFQNFromPackage(pckg);

        if (expression == null) {
            return null;
        }

        PackageDeclaration pdecl = (PackageDeclaration) fact.createNode(NodeKind.ndkPackageDeclaration);

        pdecl.setPackageName(expression);
        pdecl.setRefersTo(pckg);

        return pdecl;
    }

    /**
     * Creates and returns a fully qualified name Expression from Package.
     * 
     * @param pckg The given Package.
     * @return A fully qualified name Expression from Package.
     */
    public static Expression createFQNFromPackage(Package pckg) {
        final Factory fact = pckg.getFactory();
        return createAttributeForPackage(pckg, new PackageAttributeCreator<Expression>() {

            @Override
            public Expression ifNullOrRoot() {
                return null;
            }

            @Override
            public Expression ifNullOrRootParent(Package pack) {
                Identifier identif = createIdentifierForPackage(fact, pack);

                return identif;
            }

            @Override
            public Expression ifPackageWithPackageParent(Package pack, Package parent) {
                Identifier identif = createIdentifierForPackage(fact, pack);
                FieldAccess access = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);

                access.setRightOperand(identif);
                access.setType(identif.getType());
                access.setLeftOperand(createFQNFromPackage(parent));

                return access;
            }

            private Identifier createIdentifierForPackage(final Factory fact, Package pack) {
                Identifier identif = (Identifier) fact.createNode(NodeKind.ndkIdentifier);

                identif.setName(pack.getName());
                identif.setRefersTo(pack);
                identif.setType(TransformationAPI.createPackageType(pack));

                return identif;
            }

        });
    }

    /**
     * The attribute creator for createAttributeForTypeDeclaration function.
     * 
     * @param <T> The return type of the operations.
     */
    private static interface TypeDeclarationAttributeCreator<T> {

        public T ifNull();

        public T ifNullOrRootParent(TypeDeclaration pack);

        public T ifTypeDeclarationWithTypeDeclarationParent(TypeDeclaration td, TypeDeclaration parent);

        public T ifTypeDeclarationWithPackageParent(TypeDeclaration td, Package parent);
    }

    /**
     * Creates and returns a T type attribute from TypeDeclaration.
     * 
     * @param typedecl The given TypeDeclaration.
     * @param pac The TypeDeclarationAttributeCreator object.
     * @return A T type attribute from TypeDeclaration.
     */
    private static <T> T createAttributeForTypeDeclaration(TypeDeclaration typedecl, TypeDeclarationAttributeCreator<T> pac) {
        if (typedecl == null) {
            return pac.ifNull();
        } else {
            Factory fact = typedecl.getFactory();
            Base parent = typedecl.getParent();
            if (parent == null || parent == fact.getRoot()) {
                return pac.ifNullOrRootParent(typedecl);
            } else if (Common.getIsPackage(parent)) {
                return pac.ifTypeDeclarationWithPackageParent(typedecl, (Package) parent);
            } else if (Common.getIsTypeDeclaration(parent)) {
                return pac.ifTypeDeclarationWithTypeDeclarationParent(typedecl, (TypeDeclaration) parent);
            } else {
                throw new RepairAlgIllegalArgumentException("The package's parent is not a package.");
            }
        }
    }

    /**
     * Creates a QualifiedName Expression from TypeDeclaration.
     * 
     * @param clazz The TypeDeclaration.
     * @return A QualifiedName Expression from TypeDeclaration.
     */
    public static Expression createFQNFromTypeDeclaration(TypeDeclaration clazz) {
        final Factory fact = clazz.getFactory();
        return createAttributeForTypeDeclaration(clazz, new TypeDeclarationAttributeCreator<Expression>() {

            @Override
            public Expression ifNull() {
                return null;
            }

            @Override
            public Expression ifNullOrRootParent(TypeDeclaration td) {
                Identifier identif = createIdentifierForTypeDeclaration(fact, td);

                return identif;
            }

            @Override
            public Expression ifTypeDeclarationWithTypeDeclarationParent(TypeDeclaration td, TypeDeclaration parent) {
                Identifier identif = createIdentifierForTypeDeclaration(fact, td);
                FieldAccess access = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);

                access.setRightOperand(identif);
                access.setType(identif.getType());
                access.setLeftOperand(createFQNFromTypeDeclaration(parent));

                return access;
            }

            @Override
            public Expression ifTypeDeclarationWithPackageParent(TypeDeclaration td, Package parent) {
                Identifier identif = createIdentifierForTypeDeclaration(fact, td);
                FieldAccess access = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);

                access.setRightOperand(identif);
                access.setType(identif.getType());
                access.setLeftOperand(createFQNFromPackage(parent));

                return access;
            }

            private Identifier createIdentifierForTypeDeclaration(final Factory fact, TypeDeclaration clazz) {
                Identifier identif = (Identifier) fact.createNode(NodeKind.ndkIdentifier);

                identif.setName(clazz.getName());
                identif.setRefersTo(clazz);
                identif.setType(TransformationAPI.createClassType(clazz));

                return identif;
            }
        });
    }

    /**
     * A package attribute creator for createAttributeForPackage operation.
     * 
     * @param <T> The return type of the operations.
     */
    private static interface PackageAttributeCreator<T> {

        public T ifNullOrRoot();

        public T ifNullOrRootParent(Package pack);

        public T ifPackageWithPackageParent(Package pack, Package parent);
    }

    /**
     * Creates and returns an Import object from the given TypeDeclaration.
     * 
     * @param superClass The given TypeDeclaration.
     * @return An Import object from the given TypeDeclaration.
     */
    public static Import createImportFromTypeDeclaration(TypeDeclaration superClass) {
        Expression expression = createFQNFromTypeDeclaration(superClass);

        if (expression == null) {
            return null;
        }

        Factory fact = superClass.getFactory();
        Import imp = (Import) fact.createNode(NodeKind.ndkImport);

        imp.setIsStatic(superClass.getIsStatic());
        imp.setTarget(expression);

        return imp;
    }

    /**
     * Returns the TypeDeclaration of the given path.
     * 
     * @param path The path of the TypeDeclaration.
     * @param fact The factory of the TypeDeclaration.
     * @return The TypeDeclaration of the given path.
     */
    public static TypeDeclaration getTypeDeclaration(String path, Factory fact) {
        return getTypeDeclaration(path, fact, fact.getRoot());
    }

    /**
     * Returns the TypeDeclaration of the given path.
     * 
     * @param path The path of the TypeDeclaration.
     * @param fact The factory of the TypeDeclaration.
     * @param root The root of the searching.
     * @return The TypeDeclaration of the given path.
     */
    public static TypeDeclaration getTypeDeclaration(String path, Factory fact, Scope root) {
        return getTypeDeclaration(tokenizeStringOnDot(path), fact, root);
    }

    private static TypeDeclaration getTypeDeclaration(List<String> tokenizeStringOnDot, Factory fact, Scope root) {
        return getTypeDeclaration(tokenizeStringOnDot, fact, root, true);
    }

    /**
     * Tokenize a String by the dot separators.
     * 
     * @param path The path separated by dots.
     * @return The List of the dot-separated Strings.
     */
    private static List<String> tokenizeStringOnDot(String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, ".");
        List<String> pathlist = new ArrayList<String>();

        while (tokenizer.hasMoreTokens()) {
            pathlist.add(tokenizer.nextToken());
        }
        return pathlist;
    }

    /**
     * Returns the TypeDeclaration of the given path.
     * 
     * @param pathlist The path of the TypeDeclaration.
     * @param fact The factory of the TypeDeclaration.
     * @return The TypeDeclaration of the given path.
     */
    public static TypeDeclaration getTypeDeclaration(List<String> pathlist, Factory fact) {
        return getTypeDeclaration(pathlist, fact, fact.getRoot());
    }

    public static TypeDeclaration getTypeDeclaration(String path, Factory fact, boolean createIfNotExists) {
        return getTypeDeclaration(tokenizeStringOnDot(path), fact, fact.getRoot(), createIfNotExists);
    }

    /**
     * Returns the TypeDeclaration of the given path.
     * 
     * @param pathlist The path of the TypeDeclaration.
     * @param fact The factory of the TypeDeclaration.
     * @param root The root of the searching.
     * @return The TypeDeclaration of the given path.
     */
    public static TypeDeclaration getTypeDeclaration(List<String> pathlist, Factory fact, Scope root, boolean createIfNotExists) {
        if (pathlist.isEmpty()) {
            throw new RepairAlgIllegalArgumentException("The list cannot be empty.");
        }

        Package container = getPackage(pathlist.subList(0, pathlist.size() - 1), fact, createIfNotExists);
        List<String> classPath = new LinkedList<String>();
        StringTokenizer tokenizer = new StringTokenizer(pathlist.get(pathlist.size() - 1), "$");

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            classPath.add(token);
        }

        return getContainedTypeDeclaration(classPath, fact, container, createIfNotExists);
    }

    private static TypeDeclaration getContainedTypeDeclaration(List<String> pathlist, Factory fact, Scope container, boolean createIfNotExists) {

        EdgeIterator<Member> iter = container.getMembersIterator();
        String aktName = pathlist.get(0);

        while (iter.hasNext()) {
            Member aktMember = iter.next();

            if (Common.getIsNamed(aktMember)) {
                Named named = (Named) aktMember;
                String name = named.getName();

                if (aktName.equals(name)) {
                    if (pathlist.size() == 1) {
                        if (Common.getIsTypeDeclaration(named)) {
                            return (TypeDeclaration) named;
                        } else {
                            throw new RepairAlgIllegalArgumentException("The element (" + name + ") is not a TypeDeclaration.");
                        }
                    } else {
                        if (Common.getIsTypeDeclaration(named)) {
                            Scope scope = (Scope) named;
                            return getContainedTypeDeclaration(pathlist.subList(1, pathlist.size()), fact, scope, createIfNotExists);
                        } else {
                            throw new RepairAlgIllegalArgumentException("The element (" + name + ") is not a TypeDeclaration.");
                        }
                    }
                }
            }
        }

        if (createIfNotExists) {
            StringBuilder path = new StringBuilder();
            if (container != fact.getRoot()) {
                if (Common.getIsPackage(container)) {
                    Package pack = (Package) container;

                    path.append(pack.getQualifiedName()).append('.');
                } else if (Common.getIsTypeDeclaration(container)) {
                    TypeDeclaration decl = (TypeDeclaration) container;

                    path.append(decl.getBinaryName()).append('$');
                } else {
                    throw new RepairAlgIllegalArgumentException("The container is neither a Package nor a TypeDeclaration.");
                }
            }

            boolean first = true;
            for (String pathelem : pathlist) {
                if (!first) {
                    path.append('$');
                }
                path.append(pathelem);

                first = false;
            }

            return ClassBuilderAPI.buildType(path.toString(), fact);
        } else {
            return null;
        }
    }

    /**
     * Returns the NewClass list which calls the given Constructor.
     * 
     * @param meth The constructor.
     * @return The NewClass list which calls the given Constructor.
     */
    public static List<NewClass> getConstructorCalls(final NormalMethod meth) {
        final List<NewClass> nclasses = new LinkedList<NewClass>();
        Factory fact = meth.getFactory();

        AlgorithmPreorder ap = new AlgorithmPreorder();
        Visitor visitor = new Visitor() {

            @Override
            public void visit(NewClass node, boolean callVirtualBase) {
                super.visit(node, callVirtualBase);

                if (node.getConstructor() == meth) {
                    nclasses.add(node);
                }
            }
        };

        ap.run(fact, visitor, fact.getRoot());

        return nclasses;
    }

    /**
     * Returns all MethodInvocation which calls the given NormalMethod.
     * 
     * @param meth The given NormalMethod.
     * @return All MethodInvocation which calls the given NormalMethod.
     */
    public static List<MethodInvocation> getMethodCalls(final NormalMethod meth) {
        Factory fact = meth.getFactory();

        return getMethodCalls(meth, fact.getRoot());
    }

    /**
     * Returns all MethodInvocation which calls the given NormalMethod.
     * 
     * @param meth The given NormalMethod.
     * @return All MethodInvocation which calls the given NormalMethod.
     */
    public static List<MethodInvocation> getMethodCalls(final NormalMethod meth, Base startsFrom) {
        final List<MethodInvocation> result = new LinkedList<MethodInvocation>();
        Factory fact = meth.getFactory();

        AlgorithmPreorder ap = new AlgorithmPreorder();
        Visitor visitor = new Visitor() {

            @Override
            public void visit(MethodInvocation node, boolean callVirtualBase) {
                super.visit(node, callVirtualBase);

                if (node.getInvokes() == meth) {
                    result.add(node);
                }
            }
        };

        ap.run(fact, visitor, startsFrom);

        return result;
    }

    /**
     * Returns all identifier which refers to the given named object.
     * 
     * @param named The given Named object.
     * @return All identifier which refers to the given named object.
     */
    public static List<Identifier> getNonMethodNamedUses(Named named) {
        return getNonMethodNamedUses(named, named.getFactory().getRoot());
    }

    /**
     * Returns all identifier which refers to the given named object.
     * 
     * @param named The given Named object.
     * @param startFrom The root of the searching.
     * @return All identifier which refers to the given named object.
     */
    public static List<Identifier> getNonMethodNamedUses(Named named, Base startFrom) {
        AlgorithmPreorder ap = new AlgorithmPreorder();
        NamedUseVisitor visitor = new NamedUseVisitor(named);
        ap.run(named.getFactory(), visitor, startFrom);

        return visitor.getList();
    }

    /**
     * The visitor which finding all NamedUses.
     */
    private static class NamedUseVisitor extends Visitor {

        private Named iden;
        private List<Identifier> lst;

        public NamedUseVisitor(Named iden) {
            this.iden = iden;
            this.lst = new ArrayList<Identifier>();
        }

        @Override
        public void visit(Identifier node, boolean callVirtualBase) {
            super.visit(node, callVirtualBase);

            if (node.getRefersTo() == this.iden) {
                this.lst.add(node);
            }
        }

        public List<Identifier> getList() {
            return this.lst;
        }
    }

    public static TypeDeclaration getContainerTypeDeclaration(Base base) {
        Base aktParent = null;
        Base aktBase = base;

        while ((aktParent = aktBase.getParent()) != null) {
            if (Common.getIsTypeDeclaration(aktParent)) {
                return (TypeDeclaration) aktParent;
            }

            aktBase = aktParent;
        }

        return null;
    }

    /**
     * Returns the first NormalMethod parent of the given node.
     * 
     * @param base The reference node.
     * @return The first NormalMethod parent of the given node.
     */
    public static NormalMethod getContainerNormalMethod(Base base) {
        Base aktParent = null;
        Base aktBase = base;

        while ((aktParent = aktBase.getParent()) != null) {
            if (Common.getIsNormalMethod(aktParent)) {
                return (NormalMethod) aktParent;
            }

            aktBase = aktParent;
        }

        return null;
    }

    /**
     * Returns the first InitializerBlock parent of the given node.
     * 
     * @param base The reference node.
     * @return The first InitializerBlock parent of the given node.
     */
    public static InitializerBlock getContainerInitializerBlock(Base base) {
        Base aktParent = null;
        Base aktBase = base;

        while ((aktParent = aktBase.getParent()) != null) {
            if (Common.getIsInitializerBlock(aktParent)) {
                return (InitializerBlock) aktParent;
            }

            aktBase = aktParent;
        }

        return null;
    }

    /**
     * Deletes all subtree node from the factory.
     * 
     * @param subTree The root of the subtree.
     */
    public static void deleteSubTreeFromFactory(Base subTree) {
        VisitorAbstractNodes visitor = new VisitorAbstractNodes() {

            @Override
            public void visit(Base base, boolean visitSpecNodes) {
                super.visit(base, visitSpecNodes);
                Factory fact = base.getFactory();
                //System.out.println("delet node: " + base.getId());
                //fact.deleteNode(base.getId());
            }
        };

        AlgorithmPreorder ap = new AlgorithmPreorder();
        Factory fact = subTree.getFactory();
        ap.run(fact, visitor, subTree);
    }

    /**
     * Swaps the from node with a list of nodes.
     * 
     * @param from The original node.
     * @param toList The new nodes.
     */
    public static void swapStatement(Base from, List<Base> toList) {
        if (toList.isEmpty()) {
            GeneratedSupport.removeStatement(from);
        } else {
            Base aktBase = toList.get(0);
            GeneratedSupport.swapStatements(from, aktBase);
            toList.remove(0);
            addAfterStatement(aktBase, toList);
        }
    }

    /**
     * Adds the node List contained nodes after the reference node.
     * 
     * @param from The reference node.
     * @param toList The added nodes.
     */
    public static void addAfterStatement(Base from, List<Base> toList) {
        Iterator<Base> iter = toList.iterator();
        Base aktBase = from;
        while (iter.hasNext()) {
            Base aktTo = iter.next();

            GeneratedSupport.addAfterStatement(aktBase, aktTo);

            aktBase = aktTo;
        }
    }

    /**
     * Adds the node List contained nodes before the reference node.
     * 
     * @param from The reference node.
     * @param toList The added nodes.
     */
    public static void addBeforeStatement(Base from, List<Base> toList) {
        Iterator<Base> iter = toList.iterator();
        while (iter.hasNext()) {
            Base aktTo = iter.next();
            GeneratedSupport.addBeforeStatement(from, aktTo);
        }
    }

    /**
     * Creates classType for the given TypeDeclaration.
     * 
     * @param td The typedeclaration.
     * @return The created classType for the given TypeDeclaration.
     */
    public static ClassType createClassType(TypeDeclaration td) {
        Factory fact = td.getFactory();

        Type owner = getOwnerType(td.getParent());
        ClassType ctype = fact.createClassType(owner.getId(), td.getId());

        return ctype;
    }

    /**
     * Creates a ParameterizedType from the given type with TypeParemters.
     * 
     * @param type
     * @param paramTypeList
     * @return The created ParameterizedType from the given type with TypeParemters.
     */
    private static ParameterizedType createParameterizedType(Type type, List<Integer> paramTypeList) {
        Factory fact = type.getFactory();

        int owner = 0;
        if (Common.getIsClassType(type)) {
            ClassType ctype = (ClassType) type;
            owner = ctype.getOwner().getId();
        }

        return fact.createParameterizedType(owner, type.getId(), paramTypeList);
    }

    /**
     * Creates a Parameterized type from the TypeDeclaration and the parameterList.
     * 
     * @param td TypeDeclaration of the ParameterizedType's rawtype.
     * @param params The type parameters.
     * @return The created Parameterized type from the TypeDeclaration and the parameterList.
     */
    public static ParameterizedType createParameterizedType(TypeDeclaration td, List<Integer> params) {
        Factory fact = td.getFactory();

        Type owner = getOwnerType(td.getParent());
        // TODO: lehet nem csak ClassType lehet.
        ParameterizedType paramType = fact.createParameterizedType(owner.getId(), createClassType(td).getId(), params);

        return paramType;
    }

    /**
     * Creates a PackageType for the given Package.
     * 
     * @param pack The Package parameter.
     * @return The created PackageType for the given Package.
     */
    public static PackageType createPackageType(Package pack) {
        Factory fact = pack.getFactory();
        PackageType ptype = fact.createPackageType(pack.getId());

        return ptype;
    }

    /**
     * Creates a ClassType if the node is a TypeDeclaration, or creates a PackageType if the node is a Package.
     * 
     * @param parent The given node.
     * @return The created ClassType if the node is a TypeDeclaration, or the created a PackageType if the node is a Package.
     */
    private static Type getOwnerType(Base parent) {
        if (Common.getIsTypeDeclaration(parent)) {
            return createClassType((TypeDeclaration) parent);
        } else if (Common.getIsPackage(parent)) {
            return createPackageType((Package) parent);
        } else {
            throw new UnsupportedOperationException("This type is not supported yet.");
        }
    }

    /**
     * Returns the only type declaration the given Type node refers to.
     * 
     * @param type The given type.
     * @return The only type declaration the given Type node refers to.
     */
    public static TypeDeclaration getOnlyTypeDeclarationFromType(Type type) {
        Set<TypeDeclaration> result = getTypeDeclarationsFromType(type);

        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.iterator().next();
        } else {
            throw new RepairAlgIllegalArgumentException("The type declaration size is more than 1.");
        }
    }

    /**
     * Returns all type declaration the given Type node refers to.
     * 
     * @param exprType The given type.
     * @return All type declaration the given Type node refers to.
     */
    public static Set<TypeDeclaration> getTypeDeclarationsFromType(Type exprType) {
        Set<TypeDeclaration> result = new HashSet<TypeDeclaration>();

        getTypeDeclarationsFromType(result, exprType);

        return result;
    }

    /**
     * Fill the given set with all type declaration the given Type node refers to.
     * 
     * @param set The fillable set.
     * @param exprType The given type.
     */
    public static void getTypeDeclarationsFromType(Set<TypeDeclaration> set, Type exprType) {
        if (exprType == null || Common.getIsErrorType(exprType)) {
            return;
        } else if (Common.getIsUnionType(exprType)) {
            UnionType utype = (UnionType) exprType;
            EdgeIterator<Type> eiter = utype.getAlternativesIterator();

            while (eiter.hasNext()) {
                getTypeDeclarationsFromType(set, eiter.next());
            }
        } else if (Common.getIsParameterizedType(exprType)) {
            ParameterizedType ptype = (ParameterizedType) exprType;

            getTypeDeclarationsFromType(set, ptype.getRawType());
        } else if (Common.getIsClassType(exprType)) {
            ClassType ctype = (ClassType) exprType;

            set.add(ctype.getRefersTo());
        } else if (Common.getIsArrayType(exprType)) {
            ArrayType atype = (ArrayType) exprType;

            getTypeDeclarationsFromType(set, atype.getComponentType());
        } else if (Common.getIsWildcardType(exprType)) {
            WildcardType wtype = (WildcardType) exprType;

            getTypeDeclarationsFromType(set, wtype.getBound());
        } else {
            throw new RepairAlgIllegalArgumentException("The type(" + exprType.getId() + ") is not supported yet.");
        }
    }

    /**
     * Sorts the TypeDeclaration elements of the given list by hierarchical.
     * 
     * @param typedecs The TypeDeclaration list.
     * @param childsFirst If true, the child will go first.
     */
    public static void hierarchycalSort(List<TypeDeclaration> typedecs, boolean childsFirst) {
        int listSize = typedecs.size();
        for (int i = listSize - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                TypeDeclaration egyik = typedecs.get(j);
                TypeDeclaration masik = typedecs.get(j + 1);

                if (egyik != masik && ((childsFirst && isHierarchicalParent(egyik, masik)) || (!childsFirst && isHierarchicalParent(masik, egyik)))) {
                    typedecs.set(j, masik);
                    typedecs.set(j + 1, egyik);
                }
            }
        }
    }

    /**
     * Returns true if child extends from parent TypeDeclaration.
     * 
     * @param parent The hierarchical parent.
     * @param child The hierarchical child.
     * @return True if child extends from parent TypeDeclaration.
     */
    public static boolean isHierarchicalParent(TypeDeclaration parent, TypeDeclaration child) {
        if (parent == child) {
            return true;
        }

        boolean result = false;

        if (child.getSuperClass() != null) {
            TypeExpression texpr = child.getSuperClass();
            TypeDeclaration tdecl = getExtendsFromTypeExpression(texpr);

            result = isHierarchicalParent(parent, tdecl);
        }

        EdgeIterator<TypeExpression> iter = child.getSuperInterfacesIterator();
        while (!result && iter.hasNext()) {
            TypeExpression texpr = iter.next();
            TypeDeclaration tdecl = getExtendsFromTypeExpression(texpr);

            result = isHierarchicalParent(parent, tdecl);
        }

        return result;
    }

    /**
     * Returns the lowest common ancestor of the two TypeDeclaration.
     * 
     * @param one The first TypeDeclaration.
     * @param two The another TypeDeclaration.
     * @return The lowest common ancestor of the two TypeDeclaration.
     */
    public static TypeDeclaration getLowestCommonAncient(TypeDeclaration one, TypeDeclaration two) {
        if (isHierarchicalParent(one, two)) {
            return one;
        } else if (isHierarchicalParent(two, one)) {
            return two;
        }

        int oneDepth = getHierarchicalDepth(one);
        int twoDepth = getHierarchicalDepth(two);

        while (oneDepth > twoDepth && one != null) {
            one = getOnlyTypeDeclarationFromType(one.getSuperClass().getType());
            oneDepth--;
        }

        if (one == null) {
            return null;
        }

        while (twoDepth > oneDepth && two != null) {
            two = getOnlyTypeDeclarationFromType(two.getSuperClass().getType());
            twoDepth--;
        }

        if (two == null) {
            return null;
        }

        while (oneDepth > 0 && one != two) {
            one = getOnlyTypeDeclarationFromType(one.getSuperClass().getType());
            two = getOnlyTypeDeclarationFromType(two.getSuperClass().getType());
            oneDepth--;
        }

        return one == two ? one : null;
    }

    /**
     * Returns the depth of the TypeDeclaration in the hierarchical tree.
     * 
     * @param typeDecl The analyzed TypeDeclaration.
     * @return The depth of the TypeDeclaration in the hierarchical tree.
     */
    public static int getHierarchicalDepth(TypeDeclaration typeDecl) {
        int result = 0;

        TypeDeclaration aktTd = typeDecl;
        while (aktTd.getSuperClass() != null) {

            result++;
            aktTd = getOnlyTypeDeclarationFromType(aktTd.getSuperClass().getType());
        }

        return result;
    }

    /**
     * Returns the TypeDeclaration from TypeExpression.
     * 
     * @param texpr The given TypeExpression.
     * @return The TypeDeclaration from TypeExpression.
     */
    private static TypeDeclaration getExtendsFromTypeExpression(TypeExpression texpr) {
        Type type = texpr.getType();

        return getExtendsFromType(type);
    }

    /**
     * Returns the TypeDeclaration from Type.
     * 
     * @param type The given Type.
     * @return The TypeDeclaration from Type.
     */
    private static TypeDeclaration getExtendsFromType(Type type) {
        if (Common.getIsClassType(type)) {
            ClassType ctype = (ClassType) type;

            return ctype.getRefersTo();
        } else if (Common.getIsParameterizedType(type)) {
            ParameterizedType ptype = (ParameterizedType) type;

            return getExtendsFromType(ptype.getRawType());
        } else {
            // TODO: ha van valami olyan, ami még lehet, és elfogadott, akkor
            // beleírjuk, de perpill most nem tudok olyat.
            throw new UnsupportedOperationException("This type(id" + type.getId() + ") is not supported yet.");
        }
    }

    /**
     * Safety deletes a boolean expression from the ASG. Removes the If node if needs.
     * 
     * @param expr The removable expression.
     * @param exprValue The removable Expression boolean value.
     */
    public static void deleteBooleanExpression(Expression expr, boolean exprValue) {
        Type exprType = expr.getType();

        if (!Common.getIsBooleanType(exprType)) {
            throw new RepairAlgIllegalArgumentException("The given expression's type is not a BooleanType.");
        }

        Base parent = expr.getParent();

        if (Common.getIsInfixExpression(parent)) {
            InfixExpression infix = (InfixExpression) parent;

            if (infix.getOperator() == InfixOperatorKind.iokConditionalAnd || infix.getOperator() == InfixOperatorKind.iokConditionalOr) {
                if ((infix.getOperator() == InfixOperatorKind.iokConditionalAnd && !exprValue)
                        || (infix.getOperator() == InfixOperatorKind.iokConditionalOr && exprValue)) {
                    deleteBooleanExpression(infix, exprValue);
                } else {
                    Expression other = getOtherSide(expr, infix);

                    nullOtherSide(expr, infix);

                    GeneratedSupport.swapStatements(infix, other);
                }
            } else {
                swapExpressionWithBoolean(expr, exprValue);
            }
        } else if (Common.getIsPrefixExpression(parent)) {
            PrefixExpression unar = (PrefixExpression) parent;

            if (unar.getOperator() == PrefixOperatorKind.peokNot) {
                deleteBooleanExpression(unar, !exprValue);
            } else {
                swapExpressionWithBoolean(expr, exprValue);
            }
        } else if (Common.getIsIf(parent)) {
            If ifstatm = (If) parent;
            Statement statm = null;

            if (exprValue) {
                statm = ifstatm.getSubstatement();
            } else {
                statm = ifstatm.getFalseSubstatement();
            }

            if (statm != null) {
                GeneratedSupport.swapStatements(ifstatm, statm);
            } else {
                GeneratedSupport.removeStatement(ifstatm);
            }

        } else if (Common.getIsParenthesizedExpression(parent)) {
            ParenthesizedExpression parenther = (ParenthesizedExpression) parent;

            deleteBooleanExpression(parenther, exprValue);
        } else if (Common.getIsConditional(parent)) {
            Conditional cond = (Conditional) parent;
            Expression expression = exprValue ? cond.getTrueExpression() : cond.getFalseExpression();

            TransformationAPI.swapStatements(cond, expression);
        } else {
            swapExpressionWithBoolean(expr, exprValue);
        }
    }

    /**
     * Sets the InfixExpression's other side null.
     * 
     * @param expr The reference expression.
     * @param infix The infix expression.
     */
    private static void nullOtherSide(Expression expr, InfixExpression infix) {
        if (infix.getLeftOperand() == expr) {
            infix.setRightOperand(null);
        } else if (infix.getRightOperand() == expr) {
            infix.setLeftOperand(null);
        } else {
            throw new RepairAlgIllegalArgumentException("The infix expression(id" + infix.getId() + ") has no tree edge with expression(id" + expr.getId() + ").");
        }
    }

    /**
     * Returns the InfixExpression's other side.
     * 
     * @param expr The reference expression.
     * @param infix The infix expression.
     * @return The InfixExpression's other side.
     */
    private static Expression getOtherSide(Expression expr, InfixExpression infix) {
        if (infix.getLeftOperand() == expr) {
            return infix.getRightOperand();
        } else if (infix.getRightOperand() == expr) {
            return infix.getLeftOperand();
        } else {
            throw new RepairAlgIllegalArgumentException("The infix expression(id" + infix.getId() + ") has no tree edge with expression(id" + expr.getId() + ").");
        }
    }

    /**
     * Swaps the given Expression with a boolean value.
     * 
     * @param expr The swapped expression.
     * @param exprValue The BooleanLiteral's value.
     */
    private static void swapExpressionWithBoolean(Expression expr, boolean exprValue) {
        Factory fact = expr.getFactory();
        BooleanLiteral literal = (BooleanLiteral) fact.createNode(NodeKind.ndkBooleanLiteral);

        literal.setPosition(new Range(expr.getPosition().getStringTable()));
        literal.setBooleanValue(exprValue);

        GeneratedSupport.swapStatements(expr, literal);
    }

    /**
     * This method create Increase method for a variable.
     * 
     * @param fact Factory of the graph.
     * @param var Variable which we want to create the increase method for.
     * @param isPost post- or pre-increase
     * @return Method increase method
     */
    public static Method createPostOrPreIncrease(Factory fact, Variable var, boolean isPost) {
        ClassDeclaration varClass = (ClassDeclaration) var.getParent();
        Method method = (Method) fact.createNode(NodeKind.ndkMethod);
        method.setIsStatic(var.getIsStatic());
        TypeExpression expr = var.getType();
        method.setReturnType(expr);// set return type
        method.setAccessibility(AccessibilityKind.ackPublic); // set accessibility
        StringBuffer methodName = new StringBuffer(var.getName());
        methodName.setCharAt(0, ((methodName.toString()).toUpperCase()).charAt(0));
        if (isPost) {
            method.setName("postIncrease" + methodName);
        } else {
            method.setName("preIncrease" + methodName);
        }

        Block block = (Block) fact.createNode(NodeKind.ndkBlock);
        Return ret = (Return) fact.createNode(NodeKind.ndkReturn);

        PostfixExpression postExpr = (PostfixExpression) fact.createNode(NodeKind.ndkPostfixExpression);
        PrefixExpression preExpr = (PrefixExpression) fact.createNode(NodeKind.ndkPrefixExpression);
        postExpr.setOperator(PostfixOperatorKind.pookIncrement);
        preExpr.setOperator(PrefixOperatorKind.peokIncrement);

        FieldAccess fieldAcc = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);
        if (var.getIsStatic()) {
            Identifier fieldLeft = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
            fieldLeft.setName(varClass.getName());
            fieldLeft.setRefersTo(varClass);
            fieldAcc.setLeftOperand(fieldLeft);
        } else {
            This fieldLeft = (This) fact.createNode(NodeKind.ndkThis);
            fieldAcc.setLeftOperand(fieldLeft);
        }

        Identifier identRight = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
        identRight.setName(var.getName());
        fieldAcc.setRightOperand(identRight);

        if (isPost) {
            postExpr.setOperand(fieldAcc);
            ret.setExpression(postExpr);
        } else {
            preExpr.setOperand(fieldAcc);
            ret.setExpression(preExpr);
        }
        block.addStatements(ret);
        method.setBody(block);
        return method;
    }

    /**
     * This method create Decrease method for a variable.
     * 
     * @param fact Factory of the graph.
     * @param var Variable which we want to create the decrease method for.
     * @param isPost post- or pre-decrease
     * @return Method decrease method
     */
    public static Method createPostOrPreDecrease(Factory fact, Variable var, boolean isPost) {
        ClassDeclaration varClass = (ClassDeclaration) var.getParent();
        Method method = (Method) fact.createNode(NodeKind.ndkMethod);
        TypeExpression expr = var.getType();
        method.setIsStatic(var.getIsStatic());
        method.setReturnType(expr);// set return type
        method.setAccessibility(AccessibilityKind.ackPublic); // set accessibility
        StringBuffer methodName = new StringBuffer(var.getName());
        methodName.setCharAt(0, ((methodName.toString()).toUpperCase()).charAt(0));
        if (isPost) {
            method.setName("postDecrease" + methodName);
        } else {
            method.setName("preDecrease" + methodName);
        }

        Block block = (Block) fact.createNode(NodeKind.ndkBlock);
        Return ret = (Return) fact.createNode(NodeKind.ndkReturn);

        PostfixExpression postExpr = (PostfixExpression) fact.createNode(NodeKind.ndkPostfixExpression);
        PrefixExpression preExpr = (PrefixExpression) fact.createNode(NodeKind.ndkPrefixExpression);
        postExpr.setOperator(PostfixOperatorKind.pookDecrement);
        preExpr.setOperator(PrefixOperatorKind.peokDecrement);

        FieldAccess fieldAcc = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);
        if (var.getIsStatic()) {
            Identifier fieldLeft = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
            fieldLeft.setName(varClass.getName());
            fieldLeft.setRefersTo(varClass);
            fieldAcc.setLeftOperand(fieldLeft);
        } else {
            This fieldLeft = (This) fact.createNode(NodeKind.ndkThis);
            fieldAcc.setLeftOperand(fieldLeft);
        }

        Identifier identRight = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
        identRight.setName(var.getName());
        fieldAcc.setRightOperand(identRight);

        if (isPost) {
            postExpr.setOperand(fieldAcc);
            ret.setExpression(postExpr);
        } else {
            preExpr.setOperand(fieldAcc);
            ret.setExpression(preExpr);
        }
        block.addStatements(ret);
        method.setBody(block);
        return method;
    }

    /**
     * This method create Getter method for a variable.
     * 
     * @param fact Factory of the graph.
     * @param var Variable which we want to create the getter for.
     * @return Method getter method
     */
    public static Method createGetter(Factory fact, Variable var) {
        ClassDeclaration varClass = (ClassDeclaration) var.getParent();
        Method method = (Method) fact.createNode(NodeKind.ndkMethod);
        TypeExpression expr = var.getType();
        method.setReturnType(expr);// set return type
        method.setIsStatic(var.getIsStatic());
        method.setAccessibility(AccessibilityKind.ackPublic); // set accessibility
        StringBuffer methodName = new StringBuffer(var.getName());
        methodName.setCharAt(0, ((methodName.toString()).toUpperCase()).charAt(0));
        if ((var.getType().getType().getNodeKind()).equals(NodeKind.ndkBooleanType)) {
            method.setName("is" + methodName.toString()); // set method name
        } else {
            method.setName("get" + methodName.toString()); // set method name
        }
        Block block = (Block) fact.createNode(NodeKind.ndkBlock);
        Return ret = (Return) fact.createNode(NodeKind.ndkReturn);
        FieldAccess fieldA = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);
        Identifier fieldRight = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
        fieldRight.setName(var.getName());
        fieldRight.setRefersTo(var);

        if (var.getIsStatic()) {
            Identifier fieldLeft = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
            fieldLeft.setName(varClass.getName());
            fieldLeft.setRefersTo(varClass);
            fieldA.setLeftOperand(fieldLeft);
        } else {
            This fieldLeft = (This) fact.createNode(NodeKind.ndkThis);
            fieldA.setLeftOperand(fieldLeft);
        }

        fieldA.setRightOperand(fieldRight);
        ret.setExpression(fieldA);
        block.addStatements(ret);
        method.setBody(block);

        return method;
    }

    /**
     * This method create Setter method for a variable.
     * 
     * @param fact Factory of the graph.
     * @param var Variable which we want to create the setter for.
     * @return Method setter method
     */
    public static Method createSetter(Factory fact, Variable var) {
        ClassDeclaration varClass = (ClassDeclaration) var.getParent();
        Method method = (Method) fact.createNode(NodeKind.ndkMethod);
        PrimitiveTypeExpression expr = (PrimitiveTypeExpression) fact.createNode(NodeKind.ndkPrimitiveTypeExpression);
        expr.setKind(PrimitiveTypeKind.ptkVoid);
        method.setReturnType(expr);// set return type
        method.setAccessibility(AccessibilityKind.ackPublic); // set accessibility
        method.setIsStatic(var.getIsStatic());
        StringBuffer methodName = new StringBuffer(var.getName());
        methodName.setCharAt(0, ((methodName.toString()).toUpperCase()).charAt(0));
        method.setName("set" + methodName.toString()); // set method name
        Parameter par = (Parameter) fact.createNode(NodeKind.ndkParameter);
        par.setAccessibility(AccessibilityKind.ackNone);
        par.setType(var.getType());
        par.setName(var.getName());
        method.addParameters(par); // set params

        Block block = (Block) fact.createNode(NodeKind.ndkBlock);
        ExpressionStatement statement = (ExpressionStatement) fact.createNode(NodeKind.ndkExpressionStatement);
        Assignment assignment = (Assignment) fact.createNode(NodeKind.ndkAssignment);
        assignment.setOperator(AssignmentOperatorKind.askAssign);
        FieldAccess fieldAccess = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);

        if (var.getIsStatic()) {
            Identifier fieldLeft = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
            fieldLeft.setName(varClass.getName());
            fieldLeft.setRefersTo(varClass);
            fieldAccess.setLeftOperand(fieldLeft);
        } else {
            This thisExp = (This) fact.createNode(NodeKind.ndkThis);
            fieldAccess.setLeftOperand(thisExp);
        }

        Identifier identOfField = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
        identOfField.setName(var.getName());
        identOfField.setRefersTo(var.getId());
        fieldAccess.setRightOperand(identOfField);
        assignment.setLeftOperand(fieldAccess);
        Identifier identRight = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
        identRight.setName(var.getName());
        identRight.setRefersTo(par);
        assignment.setRightOperand(identRight);
        statement.setExpression(assignment);
        block.addStatements(statement);
        method.setBody(block);
        return method;
    }

    /**
     * This method transfers AssignmentOperatorKind to InfixOperatorKind.
     * 
     * @param aok which we want to transfer
     * @return InfixOperationKind equivalent to aok
     */
    public static InfixOperatorKind transferAokToIok(AssignmentOperatorKind aok) {
        if (aok == AssignmentOperatorKind.askPlusAssign) {
            return InfixOperatorKind.iokPlus;
        } else if (aok == AssignmentOperatorKind.askMinusAssign) {
            return InfixOperatorKind.iokMinus;
        } else if (aok == AssignmentOperatorKind.askTimesAssign) {
            return InfixOperatorKind.iokTimes;
        } else if (aok == AssignmentOperatorKind.askDivideAssign) {
            return InfixOperatorKind.iokDivide;
        } else if (aok == AssignmentOperatorKind.askRemainderAssign) {
            return InfixOperatorKind.iokRemainder;
        } else if (aok == AssignmentOperatorKind.askAndAssign) {
            return InfixOperatorKind.iokBitwiseAndLogicalAnd;
        } else if (aok == AssignmentOperatorKind.askOrAssign) {
            return InfixOperatorKind.iokBitwiseAndLogicalOr;
        } else if (aok == AssignmentOperatorKind.askXorAssign) {
            return InfixOperatorKind.iokBitwiseAndLogicalXor;
        } else if (aok == AssignmentOperatorKind.askLeftShiftAssign) {
            return InfixOperatorKind.iokLeftShift;
        } else if (aok == AssignmentOperatorKind.askSignedRightShiftAssign) {
            return InfixOperatorKind.iokSignedRightShift;
        } else {
            return InfixOperatorKind.iokUnsignedRightShift;
        }
    }

    /**
     * Returns all exceptions TypeDeclaration which will be not caught.
     * 
     * @param roots The subtree roots.
     * @param deleteChildOnExtends True if we need just the anchestor.
     * @return All exceptions TypeDeclaration which will be not caught.
     */
    public static Set<TypeDeclaration> getUncatchedExceptionTypesSet(final List<Base> roots, final boolean deleteChildOnExtends) {
        Set<TypeDeclaration> result = new HashSet<TypeDeclaration>();
        Iterator<Base> rootiter = roots.iterator();

        while (rootiter.hasNext()) {
            Base root = rootiter.next();

            getUncatchedExceptionTypes(result, root, deleteChildOnExtends, new HashSet<Integer>());
        }

        return result;
    }

    /**
     * Returns all exceptions TypeDeclaration which will be not caught.
     * 
     * @param types The result collection.
     */
    public static <CollType extends Collection<TypeDeclaration>> void getUncatchedExceptionTypes(final CollType types, final Base root,
            final boolean deleteChildOnExtends) {
        getUncatchedExceptionTypes(types, root, deleteChildOnExtends, new HashSet<Integer>());
    }

    /**
     * Returns all exceptions TypeDeclaration which will be not caught, and the thrower is not in the ignore set.
     * 
     * @param types The result collection.
     * @param root The analyzed subtree root.
     * @param deleteChildOnExtends True if we need just the ancestor.
     * @param ignoredMethodSet The ignored method's throwed exceptions will not collected.
     */
    public static <CollType extends Collection<TypeDeclaration>> void getUncatchedExceptionTypes(final CollType types, final Base root,
            final boolean deleteChildOnExtends, final Set<Integer> ignoredMethodSet) {

        AlgorithmPreorder ap = new AlgorithmPreorder();
        VisitorAbstractNodes visitor = new VisitorAbstractNodes() {

            private TypeDeclaration runtimeException = getTypeDeclaration("java.lang.RuntimeException", root.getFactory());

            @Override
            public void visit(NewClass node, boolean callVirtualBase) {
                super.visit(node, callVirtualBase);

                NormalMethod method = node.getConstructor();

                if (method == null || ignoredMethodSet.contains(method.getId())) {
                    return;
                }

                examineNormalMethod(node, method);
            }

            @Override
            public void visit(MethodInvocation node, boolean callVirtualBase) {
                super.visit(node, callVirtualBase);

                MethodDeclaration methDecl = node.getInvokes();

                if (methDecl == null || !Common.getIsNormalMethod(methDecl) || ignoredMethodSet.contains(methDecl.getId())) {
                    return;
                }

                NormalMethod method = (NormalMethod) methDecl;

                examineNormalMethod(node, method);
            }

            private void examineNormalMethod(Base node, NormalMethod method) {
                EdgeIterator<TypeExpression> iter = method.getThrownExceptionsIterator();
                while (iter.hasNext()) {
                    TypeExpression expr = iter.next();

                    examineExpression(node, expr);
                }
            }

            @Override
            public void visit(Throw node, boolean callVirtualBase) {
                super.visit(node, callVirtualBase);

                Expression expr = node.getExpression();

                examineExpression(node, expr);
            }

            private void examineExpression(Base node, Expression expr) {
                if (expr != null && expr.getType() != null) {
                    Set<TypeDeclaration> tdSet = getTypeDeclarationsFromType(expr.getType());

                    for (TypeDeclaration tdecl : tdSet) {
                        if (addable(node, tdecl)) {
                            addTypeDeclaration(tdecl);
                        }
                    }
                }
            }

            private void addTypeDeclaration(TypeDeclaration tdecl) {
                if (deleteChildOnExtends) {
                    Set<TypeDeclaration> removables = new HashSet<TypeDeclaration>();
                    boolean add = true;

                    for (TypeDeclaration storedDecl : types) {
                        if (tdecl == storedDecl) {
                            continue;
                        } else if (isHierarchicalParent(tdecl, storedDecl)) {
                            removables.add(storedDecl);
                        } else if (isHierarchicalParent(storedDecl, tdecl)) {
                            add = false;
                        }
                    }

                    types.removeAll(removables);

                    if (add) {
                        types.add(tdecl);
                    }
                } else {
                    types.add(tdecl);
                }
            }

            private boolean addable(Base node, TypeDeclaration tdecl) {
                return !extendsFromRuntimeException(tdecl) && getCatcher(node, tdecl, root) == null;
            }

            private boolean extendsFromRuntimeException(TypeDeclaration tdecl) {
                if (this.runtimeException == null) {
                    return false;
                }

                return isHierarchicalParent(this.runtimeException, tdecl);
            }
        };

        ap.run(root.getFactory(), visitor, root);
    }

    /**
     * Returns the Handler, which will catch the given Exception Type. If there's no acceptable handler, returns null.
     * 
     * @param refPoint The throw position.
     * @param tdecl The throwed Exception.
     * @param endPoint The end point of the search.
     * @return The Handler, which will catch the given Exception Type. If there's no acceptable handler, returns null.
     */
    public static Handler getCatcher(Base refPoint, TypeDeclaration tdecl, Base endPoint) {
        Base basePoint = refPoint;

        while (basePoint != endPoint && basePoint != null) {
            Base parent = basePoint.getParent();

            if (Common.getIsTry(parent)) {
                Try tryparent = (Try) parent;

                if (tryparent.getBlock() == basePoint) {
                    Handler catcher = getCatcher(tryparent, tdecl);

                    if (catcher != null) {
                        return catcher;
                    }
                }
            }

            basePoint = parent;
        }

        return null;
    }

    /**
     * Returns the Handler that catch the given Type of Exception in the given Try statement.
     * 
     * @param traj The Try statement.
     * @param tdecl The Exception TypeDeclaration.
     * @return The Handler that catch the given Type of Exception in the given Try statement.
     */
    public static Handler getCatcher(Try traj, TypeDeclaration tdecl) {
        EdgeIterator<Handler> iter = traj.getHandlersIterator();
        while (iter.hasNext()) {
            Handler hndlr = iter.next();

            if (isHandlerCatchIt(hndlr, tdecl)) {
                return hndlr;
            }
        }

        return null;
    }

    /**
     * Returns true if the given Handler will handle the exception TypeDeclaration.
     * 
     * @param hndlr The handler.
     * @param tdecl The TypeDeclaration.
     * @return True if the given Handler will handle the exception TypeDeclaration.
     */
    public static boolean isHandlerCatchIt(Handler hndlr, TypeDeclaration tdecl) {
        Parameter param = hndlr.getParameter();

        if (param == null) {
            return false;
        }

        TypeExpression texpr = param.getType();

        if (texpr == null) {
            return false;
        }

        Type type = texpr.getType();

        if (type == null) {
            return false;
        }

        Set<TypeDeclaration> decl = getTypeDeclarationsFromType(type);
        Iterator<TypeDeclaration> iter = decl.iterator();
        while (iter.hasNext()) {
            TypeDeclaration aktDecl = iter.next();

            if (isHierarchicalParent(aktDecl, tdecl)) {
                return true;
            }
        }

        return false;
    }

    /**
     * DeepCopy the subtree by giving the subtree's root node.
     * 
     * @param base The root of the subtree.
     * @return The root of the clone subtree.
     */
    public static Base deepCopySubtreeClonedRootResult(Base base) {
        Map<Integer, Integer> deepCopyMap = deepCopySubtree(base);
        Factory fact = base.getFactory();

        return fact.getRef(deepCopyMap.get(base.getId()));
    }

    /**
     * DeepCopy the subtree by giving the subtree's root node.
     * 
     * @param base The root of the subtree.
     * @return The cloned node -> clone node Map.
     */
    public static Map<Integer, Integer> deepCopySubtree(Base base) {
        if (base == null) {
            throw new RepairAlgIllegalArgumentException("The root should not be null.");
        }

        Factory fact = base.getFactory();
        DeepCopyAlgorithm dca = new DeepCopyAlgorithm(fact);

        return dca.runDeepCopy(base.getId());
    }

    /**
     * Returns true if the reference's CompilationUnit parent is imported the given TypeDeclaration.
     * 
     * @param tdecl The TypeDeclaration.
     * @param reference The reference node.
     * @return True if the reference's CompilationUnit parent is imported the given TypeDeclaration.
     */
    public static boolean isImportedTypeDeclaration(TypeDeclaration tdecl, Base reference) {
        String declFQN = tdecl.getBinaryName().replace('$', '.');
        return isImportedTypeDeclaration(declFQN, reference);
    }

    /**
     * Returns true if the reference's CompilationUnit parent is imported the given TypeDeclaration.
     * 
     * @param declFQN The fullyqualifiedname of the class.
     * @param reference The reference node.
     * @return True if the reference's CompilationUnit parent is imported the given TypeDeclaration.
     */
    public static boolean isImportedTypeDeclaration(String declFQN, Base reference) {
        TypeDeclaration outher = getOutherTypeDeclaration(reference);
        CompilationUnit compunit = outher.getIsInCompilationUnit();

        if (compunit == null) {
            return false;
        }

        EdgeIterator<Import> iter = compunit.getImportsIterator();
        while (iter.hasNext()) {
            Import next = iter.next();

            if (next.getIsStatic()) {
                continue;
            }

            String fqn = getFQNFromImport(next);
            if (fqn.endsWith("*")) {
                String fqnPacks = fqn.substring(0, fqn.lastIndexOf('.'));
                String declFQNPacks = declFQN.substring(0, declFQN.lastIndexOf('.'));

                if (fqnPacks.equals(declFQNPacks)) {
                    return true;
                }
            } else {
                if (fqn.equals(declFQN)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Adds an Import to the CompilationUnit of the reference.
     * 
     * @param nodes The ModifiedNodes object for refactoring.
     * @param reference The reference node.
     * @param tdecl The TypeDeclaration we import.
     */
    public static void addImportToTheCompilationUnit(ModifiedNodes nodes, Base reference, TypeDeclaration tdecl) {
        TypeDeclaration outher = getOutherTypeDeclaration(reference);
        CompilationUnit unit = outher.getIsInCompilationUnit();

        if (unit == null) {
            throw new RepairAlgIllegalArgumentException("The compilationUnit of the type declaration(id" + outher.getId() + ") should not be null");
        }

        NewPositionKind poskind;
        Positioned newPos;
        if (!unit.getImportsIsEmpty()) {
            newPos = unit.getImportsIterator().next();
            poskind = NewPositionKind.AFTER;
        } else if (unit.getPackageDeclaration() != null) {
            newPos = unit.getPackageDeclaration();
            poskind = NewPositionKind.AFTER;
        } else {
            newPos = outher;
            poskind = NewPositionKind.BEFORE;
        }

        Import imprt = TransformationAPI.createImportFromTypeDeclaration(tdecl);

        unit.addImports(imprt);

        if (nodes != null) {
            nodes.markNodeAsNew(imprt.getId(), poskind, newPos);
        }
    }

    /**
     * Adds an Import to the CompilationUnit of the reference.
     * 
     * @param nodes The ModifiedNodes object for refactoring.
     * @param reference The reference node.
     * @param tdecl The TypeDeclaration we import.
     */
    public static void addImportToTheCompilationUnit(ModifiedNodes nodes, Base reference, String tdecl) {
        TypeDeclaration outher = getOutherTypeDeclaration(reference);
        CompilationUnit unit = outher.getIsInCompilationUnit();

        if (unit == null) {
            throw new RepairAlgIllegalArgumentException("The compilationUnit of the type declaration(id" + outher.getId() + ") should not be null");
        }

        NewPositionKind poskind;
        Positioned newPos;
        if (!unit.getImportsIsEmpty()) {
            newPos = unit.getImportsIterator().next();
            poskind = NewPositionKind.BEFORE;
        } else if (unit.getPackageDeclaration() != null) {
            newPos = unit.getPackageDeclaration();
            poskind = NewPositionKind.AFTER;
        } else {
            newPos = outher;
            poskind = NewPositionKind.BEFORE;
        }

        Import imprt = TransformationAPI.createImportFromClassPath(tdecl, reference.getFactory());

        unit.addImports(imprt);

        if (nodes != null) {
            nodes.markNodeAsNew(imprt.getId(), poskind, newPos);
        }
    }

    /**
     * Returns an Import from the given classpath.
     * 
     * @param tdecl The ClassPath.
     * @param fact The Import's factory.
     * @return An Import from the given classpath.
     */
    public static Import createImportFromClassPath(String tdecl, Factory fact) {
        if (tdecl.indexOf('$') != -1) {
            tdecl = tdecl.replace('$', '.');
        }

        Expression expr = createExpressionFromString(tdecl, fact);
        Import imp = (Import) fact.createNode(NodeKind.ndkImport);

        imp.setTarget(expr);

        return imp;
    }

    /**
     * Creates an qualified name Expression from the given path string.
     * 
     * @param tdecl The classpath.
     * @param fact The factory of the Expression.
     * @return An qualified name Expression from the given path string.
     */
    private static Expression createExpressionFromString(String tdecl, Factory fact) {
        int dotIndex;
        Expression expr = null;
        while ((dotIndex = tdecl.indexOf('.')) != -1) {
            String iden = tdecl.substring(0, dotIndex);

            expr = getNextExpr(fact, expr, iden);

            tdecl = tdecl.substring(dotIndex + 1);
        }

        expr = getNextExpr(fact, expr, tdecl);
        return expr;
    }

    /**
     * Returns the new expression which concatenate the expr Expression and the String identifier.
     * 
     * @param fact The Factory.
     * @param expr The old Expression
     * @param iden The actual Identifier's name.
     * @return The new expression which concatenate the expr Expression and the String identifier.
     */
    private static Expression getNextExpr(Factory fact, Expression expr, String iden) {
        Identifier id = (Identifier) fact.createNode(NodeKind.ndkIdentifier);
        id.setName(iden);

        if (expr == null) {
            expr = id;
        } else {
            FieldAccess access = (FieldAccess) fact.createNode(NodeKind.ndkFieldAccess);

            access.setLeftOperand(expr);
            access.setRightOperand(id);

            expr = access;
        }
        return expr;
    }

    /**
     * Returns the fully qualified name from the import.
     * 
     * @param next The import.
     * @return The fully qualified name from the import.
     */
    public static String getFQNFromImport(Import next) {
        Expression target = next.getTarget();

        return getFQNFromExpression(target);
    }

    /**
     * Returns the fully qualified name from the expression.
     * 
     * @param target The Expression we use to create the qualified name.
     * @return The fully qualified name from the expression.
     */
    private static String getFQNFromExpression(Expression target) {
        if (Common.getIsIdentifier(target)) {
            Identifier iden = (Identifier) target;

            return iden.getName();
        } else if (Common.getIsFieldAccess(target)) {
            FieldAccess acc = (FieldAccess) target;
            StringBuilder builder = new StringBuilder();

            builder.append(getFQNFromExpression(acc.getLeftOperand()));
            builder.append(".");
            builder.append(getFQNFromExpression(acc.getRightOperand()));

            return builder.toString();
        } else {
            throw new RepairAlgIllegalArgumentException("The target is neither an Identifier nor a FieldAccess.");
        }
    }

    /**
     * Returns the outher container class of the reference node.
     * 
     * @param reference The reference node.
     * @return The outher container class of the reference node.
     */
    public static TypeDeclaration getOutherTypeDeclaration(Base reference) {
        TypeDeclaration outher = null;
        while (reference != null && !Common.getIsPackage(reference)) {
            if (Common.getIsTypeDeclaration(reference)) {
                outher = (TypeDeclaration) reference;
            }

            reference = reference.getParent();
        }

        if (outher == null) {
            throw new RepairAlgIllegalArgumentException("The reference node is not in the asg graph.");
        }
        return outher;
    }

    /**
     * Returns true if the given Expression node is used.
     * 
     * @param iden The given Expression.
     * @return True if the given Expression node is used.
     */
    public static boolean isUsed(Expression iden) {
        Base parent = iden.getParent();

        if (parent == null) {
            throw new RepairAlgIllegalArgumentException("The expression's parent should not be null.");
        }

        if (isSimpleUsed(iden, parent)) {
            return true;
        } else if (Common.getIsFieldAccess(parent)) {
            FieldAccess acc = (FieldAccess) parent;

            if (acc.getLeftOperand() == iden) {
                return true;
            } else if (acc.getRightOperand() == iden) {
                return isUsed(acc);
            } else {
                throw new RepairAlgIllegalArgumentException("The FieldAccess(id" + acc.getId() + ") parent is not contains the Expression(id" + iden.getId() + ").");
            }
        } else if (Common.getIsPostfixExpression(parent) || Common.getIsPrefixExpression(parent) || Common.getIsParenthesizedExpression(parent)) {
            return isUsed((Expression) parent);
        }

        return false;
    }

    /**
     * Returns true if the given Identifier's value is changed.
     * 
     * @param iden The given Identifier node.
     * @return True if the given Identifier's value is changed.
     */
    public static boolean isChanged(Identifier iden) {
        return isChanged((Expression) iden);
    }

    /**
     * Returns true if the given Expression's (Identifier of FieldAccess) value is changed.
     * 
     * @param iden The given Identifier or FieldAccess node.
     * @return True if the given Expression's (Identifier of FieldAccess) value is changed.
     */
    private static boolean isChanged(Expression iden) {
        Base parent = iden.getParent();

        if (parent == null) {
            throw new RepairAlgIllegalArgumentException("The expression's parent should not be null.");
        }

        if (getIsFieldAccessRightOperand(iden, parent)) {
            return isChanged((Expression) parent);
        } else if (getIsAssignmentLeftOperand(iden, parent) || getIsPostOrPrefOperator(iden, parent)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if the given identifier is changed, but uses it's previous value.
     * 
     * @param iden The given identifier we checks.
     * @return True if the given identifier is changed, but uses it's previous value.
     */
    public static boolean isSelfUseChanged(Identifier iden) {
        return isSelfUseChanged((Expression) iden);
    }

    /**
     * Returns true if the given expression is changed, but uses it's previous value.
     * 
     * @param iden The given expression we checks.
     * @return True if the given expression is changed, but uses it's previous value.
     */
    private static boolean isSelfUseChanged(Expression iden) {
        Base parent = iden.getParent();

        if (parent == null) {
            throw new RepairAlgIllegalArgumentException("The expression's parent should not be null.");
        }

        if (getIsFieldAccessRightOperand(iden, parent)) {
            return isSelfUseChanged((Expression) parent);
        } else if (getIsAssignmentLeftOperandWithNoAssignOperator(iden, parent) || getIsPostOrPrefOperator(iden, parent)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if the given Expression's parent is postfix operator or prefix operator with peokDecrement or peokIncrement PrefixOperatorKind.
     * 
     * @param iden The given Expression.
     * @param parent The Expression node's parent.
     * @return True if the given Expression's parent is postfix operator or prefix operator with peokDecrement or peokIncrement PrefixOperatorKind.
     */
    private static boolean getIsPostOrPrefOperator(Expression iden, Base parent) {
        if (Common.getIsPostfixExpression(parent)) {
            return true;
        } else if (Common.getIsPrefixExpression(parent)) {
            PrefixExpression pre = (PrefixExpression) parent;

            if (pre.getOperator() == PrefixOperatorKind.peokDecrement || pre.getOperator() == PrefixOperatorKind.peokIncrement) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the given Expression's parent is a field access, and the Expression node is the FieldAccess parent's right operand.
     * 
     * @param iden The Expression node.
     * @param parent The Expression node's parent.
     * @return True if the given Expression's parent is a field access, and the Expression node is the FieldAccess parent's right operand.
     */
    private static boolean getIsFieldAccessRightOperand(Expression iden, Base parent) {
        return Common.getIsFieldAccess(parent) && ((FieldAccess) parent).getRightOperand() == iden;
    }

    /**
     * Returns true if the given Expression's parent is an Assignment, and the Expression is the Assignment parent's left operand.
     * 
     * @param iden The given Expression.
     * @param parent The Expresion node's parent.
     * @return True if the given Expression's parent is an Assignment, and the Expression is the Assignment parent's left operand.
     */
    private static boolean getIsAssignmentLeftOperand(Expression iden, Base parent) {
        return Common.getIsAssignment(parent) && ((Assignment) parent).getLeftOperand() == iden;
    }

    /**
     * Returns true if the given Expression's parent and the relation between them is correct.
     * 
     * @param iden The Expression node.
     * @param parent The parent node.
     * @return True if the given Expression's parent and the relation between them is correct.
     */
    private static boolean isSimpleUsed(Expression iden, Base parent) {
        return Common.getIsConditional(parent) || Common.getIsInstanceOf(parent) || Common.getIsMethodInvocation(parent) || Common.getIsNewArray(parent)
                || Common.getIsNewClass(parent) || Common.getIsTypeCast(parent)
                || Common.getIsArrayAccess(parent) || Common.getIsInfixExpression(parent) || getIsAssignmentRightOperand(iden, parent)
                || getIsAssignmentLeftOperandWithNoAssignOperator(iden, parent)
                || getIsNotExpressionStatementStatement(parent);
    }

    /**
     * Returns true if the given Expression's parent node is an Assignment, the Expression is the Assignment parent's left operand, and the parent's
     * AssignmentOperatorKind is not askAssign.
     * 
     * @param iden The Expression node.
     * @param parent The Expression node's parent node.
     * @return True if the given Expression's parent node is an Assignment, the Expression is the Assignment parent's left operand, and the parent's
     *         AssignmentOperatorKind is not askAssign.
     */
    private static boolean getIsAssignmentLeftOperandWithNoAssignOperator(Expression iden, Base parent) {
        if (!Common.getIsAssignment(parent)) {
            return false;
        }

        Assignment ass = (Assignment) parent;
        if (ass.getLeftOperand() != iden) {
            return false;
        }

        return ass.getOperator() != AssignmentOperatorKind.askAssign;
    }

    /**
     * Returns true if the given Base node is Statement, but not ExpressionStatement.
     * 
     * @param parent The given node.
     * @return True if the given Base node is Statement, but not ExpressionStatement.
     */
    private static boolean getIsNotExpressionStatementStatement(Base parent) {
        return Common.getIsStatement(parent) && !Common.getIsExpressionStatement(parent);
    }

    /**
     * Returns true if the Expression's parent node is an Assignment, and the Expression is the Assignment typed parent node
     * 
     * @param iden The given Expression.
     * @param parent The Expression's parent.
     * @return True if the Expression's parent node is an Assignment, and the Expression is the Assignment typed parent node
     */
    private static boolean getIsAssignmentRightOperand(Expression iden, Base parent) {
        return Common.getIsAssignment(parent) && ((Assignment) parent).getRightOperand() == iden;
    }
}
