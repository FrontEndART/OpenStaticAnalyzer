package coderepair.repair.utils;

import coderepair.generator.transformation.ModifiedNodes;
import columbus.java.asg.Common;
import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Factory;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Comment;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.expr.Annotation;
import columbus.java.asg.expr.Expression;
import columbus.java.asg.expr.FieldAccess;
import columbus.java.asg.expr.Identifier;
import columbus.java.asg.struc.CompilationUnit;
import columbus.java.asg.struc.Import;
import columbus.java.asg.struc.PackageDeclaration;
import columbus.java.asg.struc.TypeDeclaration;

public class ImportChecker {
    public static void checkImport(Factory factory, Base node, String path, ModifiedNodes modifiedNodes) {
        TypeDeclaration td = null;
        Base parent = node;
        while (parent != null) {
            if (Common.getIsTypeDeclaration(parent)) {
                td =(TypeDeclaration) parent;
                break;
            }
            parent = parent.getParent();
        }
        if (td == null) return;
        CompilationUnit cu = td.getIsInCompilationUnit();
        if (cu==null) return;

        String str[] = path.split("\\.");
        EdgeIterator<Import> iterator = cu.getImportsIterator();

        boolean findImport = false;
        nextImport:
        while(iterator.hasNext()) {
            Import i = iterator.next();

            Expression target = i.getTarget();
            int counter = str.length - 1;
            if (target != null) {
                while (target instanceof FieldAccess && counter >= 0) {
                    Expression rs = ((FieldAccess) target).getRightOperand();
                    target = ((FieldAccess) target).getLeftOperand();
                    if (rs instanceof Identifier) {
                        if (!("*".equals(((Identifier) rs).getName()) || "*".equals(str[counter]) || str[counter].equals(((Identifier) rs).getName())))
                            continue nextImport;
                    }
                    counter--;

                }
                if (target instanceof Identifier && counter == 0 && str[counter].equals(((Identifier) target).getName())) {
                    findImport = true;
                }
            }
        }
        //We have to build and insert the missing import
        if (!findImport) {
            Import newImport = (Import) factory.createNode(NodeKind.ndkImport);
            int counter=str.length-1;
            FieldAccess lastFA = null;
            if (counter !=0) {
                while (counter > 0) {
                    FieldAccess newFieldAccess = (FieldAccess) factory.createNode(NodeKind.ndkFieldAccess);
                    if (lastFA == null) {
                        newImport.setTarget(newFieldAccess);
                    } else {
                        lastFA.setLeftOperand(newFieldAccess);
                        Identifier newIdentifier = (Identifier) factory.createNode(NodeKind.ndkIdentifier);
                        newIdentifier.setName(str[counter--]);
                        newFieldAccess.setRightOperand(newIdentifier);
                    }
                    lastFA = newFieldAccess;
                }
            }

            Identifier newIdentifier = (Identifier) factory.createNode(NodeKind.ndkIdentifier);
            newIdentifier.setName(str[counter]);
            if (lastFA == null) {
                newImport.setTarget(newIdentifier);
            } else {
                lastFA.setLeftOperand(newIdentifier);
            }
            cu.addImports(newImport, 0);
            //modifiedNodes.markNodeAsModified(cu.getId());
           PackageDeclaration pd = cu.getPackageDeclaration();
            if (pd != null) {
                modifiedNodes.markNodeAsNew(newImport.getId(), ModifiedNodes.NewPositionKind.AFTER, pd);
            } else

            if (!cu.getTypeDeclarationsIsEmpty()) {
                TypeDeclaration typeDeclaration = cu.getTypeDeclarationsIterator().next();

                if (!typeDeclaration.getAnnotationsIsEmpty()) {
                    Annotation firstAnnotation = typeDeclaration.getAnnotationsIterator().next();
                    if (typeDeclaration.getPosition().getLine()<firstAnnotation.getPosition().getLine()) {
                        modifiedNodes.markNodeAsNew(newImport.getId(), ModifiedNodes.NewPositionKind.BEFORE, typeDeclaration);
                    }else modifiedNodes.markNodeAsNew(newImport.getId(), ModifiedNodes.NewPositionKind.BEFORE, firstAnnotation);
                }
                else modifiedNodes.markNodeAsNew(newImport.getId(), ModifiedNodes.NewPositionKind.BEFORE, typeDeclaration);

            }
        }

    }
}
