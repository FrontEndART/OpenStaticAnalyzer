package coderepair.generator.transformation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coderepair.communication.exceptions.RepairAlgIllegalArgumentException;
import columbus.java.asg.Common;
import columbus.java.asg.Factory;
import columbus.java.asg.enums.AccessibilityKind;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.ExternalTypeExpression;
import columbus.java.asg.expr.MarkerAnnotation;
import columbus.java.asg.struc.AnnotatedElement;
import columbus.java.asg.struc.GenericDeclaration;
import columbus.java.asg.struc.MethodDeclaration;
import columbus.java.asg.struc.NamedDeclaration;
import columbus.java.asg.struc.NormalMethod;
import columbus.java.asg.struc.Parameter;
import columbus.java.asg.struc.Scope;
import columbus.java.asg.struc.TypeDeclaration;
import columbus.java.asg.struc.TypeParameter;
import columbus.java.asg.struc.Variable;
import columbus.java.asg.type.ArrayType;
import columbus.java.asg.type.MethodType;

public class ClassBuilderAPI {

    public static TypeDeclaration buildType(String path, Factory fact) {
        Scope container = null;
        int firstDollar = path.indexOf('$');
        int lastDot = path.lastIndexOf('.');
        if (firstDollar == -1 && lastDot ==-1) {
            container = TransformationAPI.getPackage(path, fact, true);
        }
        else if (firstDollar == -1 && lastDot > 0) {
            container = TransformationAPI.getPackage(path.substring(0, lastDot), fact, true);
        } else {
            int lastDollar = path.lastIndexOf('$');

            container = TransformationAPI.getTypeDeclaration(path.substring(0, lastDollar), fact, true);
        }

        try {
            Class<?> clazz = Class.forName(path);

            TypeDeclaration createdClass = createConcreateTypeDeclaration(clazz, fact, container);

            Logger logger = LoggerFactory.getLogger(ClassBuilderAPI.class);
            if (createdClass == null) {
                logger.error("Can't find or load the referred Class/Interface {}.", path);
                return null;
            } else {
                logger.debug("Class {} loaded, id: {}.", path, createdClass.getId());
                return createdClass;
            }
        } catch (ClassNotFoundException e) {
            Logger logger = LoggerFactory.getLogger(ClassBuilderAPI.class);

            logger.error("Can't find or load the referred Class/Interface {}. Cause: {}", path, e.getMessage());

            return null;
        }
    }

    private static TypeDeclaration createConcreateTypeDeclaration(Class<?> clazz, Factory fact, Scope packObj) {
        NodeKind ndk = getNodeKind(clazz);

        TypeDeclaration tdecl = (TypeDeclaration) fact.createNode(ndk);
        packObj.addMembers(tdecl);

        int modifiers = clazz.getModifiers();

        tdecl.setName(clazz.getSimpleName());
        tdecl.setBinaryName(clazz.getName());

        fillModifiers(tdecl, modifiers);

        setExtends(clazz, tdecl);
        setImplements(clazz, tdecl);

        setTypeParameters(clazz.getTypeParameters(), tdecl);
        fillAnnotatedElement(clazz, tdecl);

        addFields(clazz, tdecl);
        addConstructors(clazz, tdecl);
        addMethods(clazz, tdecl);

        return tdecl;
    }

    private static NodeKind getNodeKind(Class<?> clazz) {
        boolean generic = clazz.getTypeParameters().length != 0;

        NodeKind ndk = null;
        if (clazz.isAnnotation()) {
            ndk = NodeKind.ndkAnnotationType;
        } else if (clazz.isEnum()) {
            ndk = NodeKind.ndkEnum;
        } else if (clazz.isInterface()) {
            ndk = generic ? NodeKind.ndkInterfaceGeneric : NodeKind.ndkInterface;
        } else {
            ndk = generic ? NodeKind.ndkClassGeneric : NodeKind.ndkClass;
        }
        return ndk;
    }

    private static void addMethods(Class<?> clazz, TypeDeclaration tdecl) {
        Method[] methods = clazz.getDeclaredMethods();
        Factory fact = tdecl.getFactory();
        for (Method method : methods) {
            TypeVariable<Method>[] typeParams = method.getTypeParameters();
            boolean generic = typeParams.length != 0;

            NodeKind ndk = null;
            if (generic) {
                ndk = NodeKind.ndkMethodGeneric;
            } else {
                ndk = NodeKind.ndkMethod;
            }

            NormalMethod methodObj = (NormalMethod) fact.createNode(ndk);

            methodObj.setName(method.getName());

            setReturnType(method, methodObj);
            setTypeParameters(typeParams, methodObj);
            fillModifiers(methodObj, method.getModifiers());
            fillAnnotatedElement(method, methodObj);
            fillParameters(method, methodObj);

            MethodType methType = TransformationAPI.createMethodType(methodObj);
            methodObj.setMethodType(methType);

            tdecl.addMembers(methodObj);
        }
    }

    private static void setReturnType(Method method, NormalMethod methodObj) {
        Class<?> returnType = method.getReturnType();

        Factory fact = methodObj.getFactory();

        methodObj.setReturnType(createExternalTypeExpression(returnType, fact));
    }

    private static ExternalTypeExpression createExternalTypeExpression(Class<?> returnType, Factory fact) {
        columbus.java.asg.type.Type type = getTypeFromClass(returnType, fact);
        ExternalTypeExpression etypee = (ExternalTypeExpression) fact.createNode(NodeKind.ndkExternalTypeExpression);

        etypee.setType(type);

        return etypee;
    }

    private static ExternalTypeExpression createParameterizedTypeExpression(ParameterizedType parametrizedType, Factory fact) {
        columbus.java.asg.type.Type type = ClassBuilderAPI.getTypeFromParameterizedType(parametrizedType, fact);
        ExternalTypeExpression etypee = (ExternalTypeExpression) fact.createNode(NodeKind.ndkExternalTypeExpression);

        etypee.setType(type);

        return etypee;
    }


    private static columbus.java.asg.type.Type getTypeFromType(Type typeToHandle, Factory fact) {
        if (typeToHandle instanceof Class<?>) {
            return getTypeFromClass((Class<?>) typeToHandle, fact);
        } else if (typeToHandle instanceof ParameterizedType) {
            return getTypeFromParameterizedType((ParameterizedType) typeToHandle, fact);
        } else if (typeToHandle instanceof WildcardType) {
            return getTypeFromWildcardType((WildcardType) typeToHandle, fact);
        } else if (typeToHandle instanceof GenericArrayType) {
            return getTypeFromGenericArrayType((GenericArrayType) typeToHandle, fact);
        } else if (typeToHandle instanceof TypeVariable<?>) {
            return getTypeFromTypeVariable((TypeVariable<?>) typeToHandle, fact);
        } else {
            throw new RepairAlgIllegalArgumentException("This type of reflection Type is not handled yet. Typename is " + typeToHandle.getClass().getName());
        }
    }

    private static boolean isObjectType(Type type) {
        if (!(type instanceof Class<?>)) {
            return false;
        }

        Class<?> classType = (Class<?>) type;

        return classType.getName().equals("java.lang.Object");
    }

    private static columbus.java.asg.type.Type getTypeFromTypeVariable(TypeVariable<?> typeToHandle, Factory fact) {
        Type[] bounds = typeToHandle.getBounds();
        if (bounds.length == 0 || (bounds.length == 1 && isObjectType(bounds[0]))) {
            return getTypeFromClass(Object.class, fact);
        } else {
            return getTypeFromType(bounds[0], fact);
        }
    }

    private static columbus.java.asg.type.Type getTypeFromGenericArrayType(GenericArrayType typeToHandle, Factory fact) {
        columbus.java.asg.type.Type compType = getTypeFromType(typeToHandle.getGenericComponentType(), fact);

        return fact.createArrayType(1, compType.getId());
    }

    private static columbus.java.asg.type.Type getTypeFromWildcardType(WildcardType typeToHandle, Factory fact) {
        Type[] lowerBounds = typeToHandle.getLowerBounds();
        Type[] upperBounds = typeToHandle.getUpperBounds();

        columbus.java.asg.type.Type result = null;
        if (upperBounds.length != 0) {
            Type type = upperBounds[0];

            columbus.java.asg.type.Type typeFromType = getTypeFromType(type, fact);

            result = fact.createUpperBoundedWildcardType(typeFromType.getId());
        } else if (lowerBounds.length != 0) {
            Type type = lowerBounds[0];

            columbus.java.asg.type.Type typeFromType = getTypeFromType(type, fact);

            result = fact.createLowerBoundedWildcardType(typeFromType.getId());
        } else {
            result = fact.createUnboundedWildcardType(0);
        }

        return result;
    }

    private static columbus.java.asg.type.Type getTypeFromParameterizedType(ParameterizedType typeToHandle, Factory fact) {
        Type owType = typeToHandle.getOwnerType();
        int ownerTypeId = owType == null ? 0 : getTypeFromType(owType, fact).getId();
        columbus.java.asg.type.Type rawType = getTypeFromType(typeToHandle.getRawType(), fact);
        List<Integer> typeParams = new LinkedList<>();
        /*Type[] ata = typeToHandle.getActualTypeArguments();
        for (Type type : ata) { //TODO recursive method calls
            typeParams.add(getTypeFromType(type, fact).getId());
        }*/

        return fact.createParameterizedType(ownerTypeId, rawType.getId(), typeParams);
    }

    private static columbus.java.asg.type.Type getTypeFromClass(Class<?> typeToHandle, Factory fact) {
        columbus.java.asg.type.Type result = null;
        if (typeToHandle.isArray()) {
            columbus.java.asg.type.Type componentType = getTypeFromClass(typeToHandle.getComponentType(), fact);
            result = fact.createArrayType(1, componentType.getId());
        } else if (typeToHandle.isPrimitive()) {
            if (int.class.equals(typeToHandle)) {
                result = fact.createIntType();
            } else if (boolean.class.equals(typeToHandle)) {
                result = fact.createBooleanType();
            } else if (byte.class.equals(typeToHandle)) {
                result = fact.createByteType();
            } else if (char.class.equals(typeToHandle)) {
                result = fact.createCharType();
            } else if (double.class.equals(typeToHandle)) {
                result = fact.createDoubleType();
            } else if (float.class.equals(typeToHandle)) {
                result = fact.createFloatType();
            } else if (long.class.equals(typeToHandle)) {
                result = fact.createLongType();
            } else if (short.class.equals(typeToHandle)) {
                result = fact.createShortType();
            } else if (void.class.equals(typeToHandle)) {
                result = fact.createVoidType();
            } else {
                throw new RepairAlgIllegalArgumentException("Unhandled primitive type: " + typeToHandle.getName());
            }
        } else {
            TypeDeclaration tdecl = TransformationAPI.getTypeDeclaration(typeToHandle.getName(), fact, true);
            //TypeVariable<?>[] tparams = typeToHandle.getTypeParameters();//TODO causes Enum<T extends Enum<T>> recursion.

            //if (tparams.length != 0) {
            //    List<Integer> tparamList = new LinkedList<Integer>();
            //    for (TypeVariable<?> tparam : tparams) {
           //         columbus.java.asg.type.Type typeFromTV = getTypeFromTypeVariable(tparam, fact);

            //        tparamList.add(typeFromTV.getId());
            //    }
            //    result = TransformationAPI.createParameterizedType(tdecl, tparamList);
           // } else {
            result = TransformationAPI.createClassType(tdecl);
           // }
        }

        return result;
    }

    private static void addConstructors(Class<?> clazz, TypeDeclaration tdecl) {
        Constructor<?>[] fields = clazz.getDeclaredConstructors();
        Factory fact = tdecl.getFactory();
        for (Constructor<?> field : fields) {
            NormalMethod method = (NormalMethod) fact.createNode(NodeKind.ndkMethod);

            method.setName(field.getName());

            fillModifiers(method, field.getModifiers());
            fillAnnotatedElement(field, method);
            fillParameters(field, method);

            MethodType methType = TransformationAPI.createMethodType(method);
            method.setMethodType(methType);

            tdecl.addMembers(method);
        }
    }

    private static void fillParameters(Method field, NormalMethod method) {
        fillParameters(method, field.getParameterTypes());
    }

    private static void fillParameters(Constructor<?> field, NormalMethod method) {
        fillParameters(method, field.getParameterTypes());
    }

    private static void fillParameters(NormalMethod method, Class<?>[] params) {
        Factory fact = method.getFactory();
        int index = 0;

        for (Class<?> param : params) {
            Parameter paramObj = (Parameter) fact.createNode(NodeKind.ndkParameter);

            paramObj.setName("arg" + index++);
            paramObj.setType(createExternalTypeExpression(param, fact));

            method.addParameters(paramObj);
        }
    }

    private static void addFields(Class<?> clazz, TypeDeclaration tdecl) {
        Field[] fields = clazz.getDeclaredFields();
        Factory fact = tdecl.getFactory();
        for (Field field : fields) {
            Variable var = (Variable) fact.createNode(NodeKind.ndkVariable);

            var.setName(field.getName());
            var.setType(createExternalTypeExpression(field.getType(), fact));

            fillModifiers(var, field.getModifiers());

            tdecl.addMembers(var);
        }
    }

    private static void setTypeParameters(TypeVariable<?>[] typeParameters, NamedDeclaration tdecl) {
        if (typeParameters.length > 0) {
            if (Common.getIsGenericDeclaration(tdecl)) {
                Factory fact = tdecl.getFactory();
                GenericDeclaration gdecl = (GenericDeclaration) tdecl;

                for (TypeVariable<?> type : typeParameters) {
                    TypeParameter tparam = (TypeParameter) fact.createNode(NodeKind.ndkTypeParameter);

                    tparam.setName(type.getName());

                    for (Type bound : type.getBounds()) {
                        if (bound instanceof Class<?>) {
                            Class<?> boundClass = (Class<?>) bound;

                            if (!"java.lang.Object".equals(boundClass.getName())) {
                                tparam.addBounds(createExternalTypeExpression(boundClass, fact));
                            }
                        }
                        else if (bound instanceof ParameterizedType) {
                            //System.out.println("Bouund:" + bound.getTypeName() + " " + bound.getClass());
                            tparam.addBounds(createParameterizedTypeExpression((ParameterizedType)bound, fact));
                        } else {
                            throw new RepairAlgIllegalArgumentException("This type of bound (" + bound.getClass().getName() + ") is not implemented yet.");
                        }
                    }

                    gdecl.addTypeParameters(tparam);
                }
            } else {
                throw new RepairAlgIllegalArgumentException("The given NamedDeclaration is not a GenericDeclaration.");
            }
        }
    }

    private static void setImplements(Class<?> clazz, TypeDeclaration tdecl) {
        Class<?>[] interfaces = clazz.getInterfaces();
        Factory fact = tdecl.getFactory();

        for (Class<?> interf : interfaces) {
            tdecl.addSuperInterfaces(createExternalTypeExpression(interf, fact));
        }
    }

    private static void setExtends(Class<?> clazz, TypeDeclaration tdecl) {
        Class<?> superclass = clazz.getSuperclass();

        if (superclass == null) {
            return;
        }

        Factory fact = tdecl.getFactory();

        tdecl.setSuperClass(createExternalTypeExpression(superclass, fact));
    }

//    private static ExternalTypeExpression createExternalTypeExpression(TypeDeclaration ext) {
//        Factory fact = ext.getFactory();
//        ExternalTypeExpression ste = (ExternalTypeExpression) fact.createNode(NodeKind.ndkExternalTypeExpression);
//
//        ste.setType(TransformationAPI.createClassType(ext));
//
//        return ste;
//    }

    private static void fillAnnotatedElement(java.lang.reflect.AnnotatedElement clazz, AnnotatedElement element) {
        Annotation[] annots = clazz.getDeclaredAnnotations();
        Factory fact = element.getFactory();
        for (Annotation annot : annots) {
            Class<? extends Annotation> type = annot.annotationType();
            columbus.java.asg.expr.MarkerAnnotation mannot = (MarkerAnnotation) fact.createNode(NodeKind.ndkMarkerAnnotation);

            mannot.setAnnotationName(createExternalTypeExpression(type, fact));
            mannot.setType(getTypeFromClass(type, fact));

            element.addAnnotations(mannot);
        }
    }

    private static void fillModifiers(Variable tdecl, int modifiers) {
        fillModifiers((NamedDeclaration) tdecl, modifiers);

        tdecl.setIsVolatile(Modifier.isVolatile(modifiers));
        tdecl.setIsTransient(Modifier.isTransient(modifiers));
    }

    private static void fillModifiers(TypeDeclaration tdecl, int modifiers) {
        fillModifiers((NamedDeclaration) tdecl, modifiers);

        tdecl.setIsAbstract(Modifier.isAbstract(modifiers));
        tdecl.setIsStrictfp(Modifier.isStrict(modifiers));
    }

    private static void fillModifiers(NormalMethod method, int modifiers) {
        fillModifiers((MethodDeclaration) method, modifiers);

        method.setIsNative(Modifier.isNative(modifiers));
        method.setIsSynchronized(Modifier.isSynchronized(modifiers));
    }

    private static void fillModifiers(MethodDeclaration method, int modifiers) {
        fillModifiers((NamedDeclaration) method, modifiers);

        method.setIsAbstract(Modifier.isAbstract(modifiers));
        method.setIsStrictfp(Modifier.isStrict(modifiers));
    }

    private static void fillModifiers(NamedDeclaration named, int modifiers) {
        AccessibilityKind ack = null;
        if (Modifier.isPrivate(modifiers)) {
            ack = AccessibilityKind.ackPrivate;
        } else if (Modifier.isProtected(modifiers)) {
            ack = AccessibilityKind.ackProtected;
        } else if (Modifier.isPublic(modifiers)) {
            ack = AccessibilityKind.ackPublic;
        } else {
            ack = AccessibilityKind.ackNone;
        }

        named.setAccessibility(ack);
        named.setIsFinal(Modifier.isFinal(modifiers));
        named.setIsStatic(Modifier.isStatic(modifiers));
        named.setIsToolGenerated(true);
    }
}
