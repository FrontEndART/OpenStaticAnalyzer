package coderepair.repair.utils;

import columbus.java.asg.Common;
import columbus.java.asg.Factory;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.SimpleTypeExpression;
import columbus.java.asg.expr.TypeExpression;
import columbus.java.asg.type.ClassType;
import columbus.java.asg.type.Type;

public class TypeExpressionExtractor {
    private TypeExpressionExtractor(){}

    public static TypeExpression createTypeExpressionFromType(Type refType, Factory fact) {
        TypeExpression retType = null;
        if (Common.getIsClassType(refType)) {
            retType = (SimpleTypeExpression) fact.createNode(NodeKind.ndkSimpleTypeExpression);
            ((SimpleTypeExpression)retType).setName(((ClassType)refType).getRefersTo().getName());
        } else {
            //TODO implementing other type form
        }
        return retType;
    }
}
