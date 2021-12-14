package columbus.java.asg.support;

import java.util.List;

import columbus.java.asg.base.*;
import columbus.java.asg.enums.*;
import columbus.java.asg.expr.*;
import columbus.java.asg.statm.*;
import columbus.java.asg.struc.*;
import columbus.java.asg.struc.Class;
import columbus.java.asg.struc.Enum;
import columbus.java.asg.struc.Package;
import columbus.java.asg.type.*;

import columbus.java.asg.EdgeIterator;
import columbus.java.asg.Common;

/**
 * Structure for helper refactoring methods.
 */
public class GeneratedSupport {

  private enum AddingType {
    AT_SET,
    AT_ADDBEFORE,
    AT_ADDAFTER,
    AT_REMOVE
  }

  public static <T> int getListIndex(EdgeIterator<T> iter, T element) {
    int index = 0;
    while(iter.hasNext()){
      T aktElem = iter.next();
      if(aktElem == element)
        return index;
      ++index;
    }
    return -1;
  }

  public static <T> boolean isInList(EdgeIterator<T> iter, T element) {
    while(iter.hasNext()){
      T aktElem = iter.next();
      if(aktElem == element)
        return true;
    }
    return false;
  }

  public static void swapStatements(Base from, Base to) {
    universalSetAddRemove(from, to, AddingType.AT_SET);
  }

  public static void removeStatement(Base from) {
    universalSetAddRemove(from, null, AddingType.AT_REMOVE);
  }

  public static void addAfterStatement(Base from, Base to) {
    universalSetAddRemove(from, to, AddingType.AT_ADDAFTER);
  }

  public static void addBeforeStatement(Base from, Base to) {
    universalSetAddRemove(from, to, AddingType.AT_ADDBEFORE);
  }

  public static void universalSetAddRemove(Base from, Base to, AddingType atype) {
    Base parent = from.getParent();
    if(Common.getIsAnnotation(parent)){
      Annotation element = (Annotation) parent;
      if(Common.getIsTypeExpression(from) && element.getAnnotationName() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setAnnotationName(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setAnnotationName(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsArrayTypeExpression(parent)){
      ArrayTypeExpression element = (ArrayTypeExpression) parent;
      if(Common.getIsTypeExpression(from) && element.getComponentType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setComponentType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setComponentType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsBinary(parent)){
      Binary element = (Binary) parent;
      if(Common.getIsExpression(from) && element.getLeftOperand() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          //element.setLeftOperand(null);
           element.removeLeftOperand();
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeLeftOperand();
          element.setLeftOperand(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && element.getRightOperand() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          //element.setRightOperand(null);
		  element.removeRightOperand();
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeRightOperand();
          element.setRightOperand(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsClassLiteral(parent)){
      ClassLiteral element = (ClassLiteral) parent;
      if(Common.getIsTypeExpression(from) && element.getComponentType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setComponentType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setComponentType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsConditional(parent)){
      Conditional element = (Conditional) parent;
      if(Common.getIsExpression(from) && element.getCondition() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setCondition(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setCondition(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && element.getTrueExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setTrueExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setTrueExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && element.getFalseExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setFalseExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setFalseExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsErroneous(parent)){
      Erroneous element = (Erroneous) parent;
      if(Common.getIsPositioned(from) && isInList(element.getErrorsIterator(), (Positioned) from)){
        if(atype == AddingType.AT_REMOVE){
          Positioned aktFrom = (Positioned) from;
          element.removeErrors(aktFrom);
          return;
        } else {
          if(!Common.getIsPositioned(to))
            throw new IllegalArgumentException("The node to is not an Positioned");

          Positioned aktFrom = (Positioned) from;
          Positioned aktTo = (Positioned) to;

          int index = getListIndex(element.getErrorsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setErrors(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addErrors(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addErrors(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsErroneousTypeExpression(parent)){
      ErroneousTypeExpression element = (ErroneousTypeExpression) parent;
      if(Common.getIsPositioned(from) && isInList(element.getErrorsIterator(), (Positioned) from)){
        if(atype == AddingType.AT_REMOVE){
          Positioned aktFrom = (Positioned) from;
          element.removeErrors(aktFrom);
          return;
        } else {
          if(!Common.getIsPositioned(to))
            throw new IllegalArgumentException("The node to is not an Positioned");

          Positioned aktFrom = (Positioned) from;
          Positioned aktTo = (Positioned) to;

          int index = getListIndex(element.getErrorsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setErrors(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addErrors(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addErrors(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsInstanceOf(parent)){
      InstanceOf element = (InstanceOf) parent;
      if(Common.getIsTypeExpression(from) && element.getTypeOperand() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setTypeOperand(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setTypeOperand(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsMethodInvocation(parent)){
      MethodInvocation element = (MethodInvocation) parent;
      if(Common.getIsTypeExpression(from) && isInList(element.getTypeArgumentsIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeTypeArguments(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getTypeArgumentsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setTypeArguments(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addTypeArguments(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addTypeArguments(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsExpression(from) && isInList(element.getArgumentsIterator(), (Expression) from)){
        if(atype == AddingType.AT_REMOVE){
          Expression aktFrom = (Expression) from;
          element.removeArguments(aktFrom);
          return;
        } else {
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktFrom = (Expression) from;
          Expression aktTo = (Expression) to;

          int index = getListIndex(element.getArgumentsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setArguments(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addArguments(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addArguments(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsNewArray(parent)){
      NewArray element = (NewArray) parent;
      if(Common.getIsTypeExpression(from) && element.getComponentType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setComponentType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setComponentType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && isInList(element.getDimensionsIterator(), (Expression) from)){
        if(atype == AddingType.AT_REMOVE){
          Expression aktFrom = (Expression) from;
          element.removeDimensions(aktFrom);
          return;
        } else {
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktFrom = (Expression) from;
          Expression aktTo = (Expression) to;

          int index = getListIndex(element.getDimensionsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setDimensions(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addDimensions(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addDimensions(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsExpression(from) && isInList(element.getInitializersIterator(), (Expression) from)){
        if(atype == AddingType.AT_REMOVE){
          Expression aktFrom = (Expression) from;
          element.removeInitializers(aktFrom);
          return;
        } else {
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktFrom = (Expression) from;
          Expression aktTo = (Expression) to;

          int index = getListIndex(element.getInitializersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setInitializers(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addInitializers(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addInitializers(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsNewClass(parent)){
      NewClass element = (NewClass) parent;
      if(Common.getIsExpression(from) && element.getEnclosingExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setEnclosingExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setEnclosingExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsTypeExpression(from) && isInList(element.getTypeArgumentsIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeTypeArguments(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getTypeArgumentsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setTypeArguments(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addTypeArguments(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addTypeArguments(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsTypeExpression(from) && element.getTypeName() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setTypeName(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setTypeName(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && isInList(element.getArgumentsIterator(), (Expression) from)){
        if(atype == AddingType.AT_REMOVE){
          Expression aktFrom = (Expression) from;
          element.removeArguments(aktFrom);
          return;
        } else {
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktFrom = (Expression) from;
          Expression aktTo = (Expression) to;

          int index = getListIndex(element.getArgumentsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setArguments(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addArguments(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addArguments(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsAnonymousClass(from) && element.getAnonymousClass() == (AnonymousClass) from){
        if(atype == AddingType.AT_REMOVE){
          element.setAnonymousClass(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsAnonymousClass(to))
            throw new IllegalArgumentException("The node to is not an AnonymousClass");

          AnonymousClass aktTo = (AnonymousClass) to;

          element.setAnonymousClass(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsNormalAnnotation(parent)){
      NormalAnnotation element = (NormalAnnotation) parent;
      if(Common.getIsExpression(from) && isInList(element.getArgumentsIterator(), (Expression) from)){
        if(atype == AddingType.AT_REMOVE){
          Expression aktFrom = (Expression) from;
          element.removeArguments(aktFrom);
          return;
        } else {
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktFrom = (Expression) from;
          Expression aktTo = (Expression) to;

          int index = getListIndex(element.getArgumentsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setArguments(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addArguments(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addArguments(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsQualifiedTypeExpression(parent)){
      QualifiedTypeExpression element = (QualifiedTypeExpression) parent;
      if(Common.getIsTypeExpression(from) && element.getQualifierType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setQualifierType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setQualifierType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsSimpleTypeExpression(from) && element.getSimpleType() == (SimpleTypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setSimpleType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsSimpleTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an SimpleTypeExpression");

          SimpleTypeExpression aktTo = (SimpleTypeExpression) to;

          element.setSimpleType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsSingleElementAnnotation(parent)){
      SingleElementAnnotation element = (SingleElementAnnotation) parent;
      if(Common.getIsExpression(from) && element.getArgument() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setArgument(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setArgument(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsTypeApplyExpression(parent)){
      TypeApplyExpression element = (TypeApplyExpression) parent;
      if(Common.getIsTypeExpression(from) && element.getRawType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setRawType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setRawType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsTypeExpression(from) && isInList(element.getTypeArgumentsIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeTypeArguments(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getTypeArgumentsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setTypeArguments(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addTypeArguments(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addTypeArguments(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsTypeCast(parent)){
      TypeCast element = (TypeCast) parent;
      if(Common.getIsTypeExpression(from) && element.getTypeOperand() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setTypeOperand(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setTypeOperand(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsTypeUnionExpression(parent)){
      TypeUnionExpression element = (TypeUnionExpression) parent;
      if(Common.getIsTypeExpression(from) && isInList(element.getAlternativesIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeAlternatives(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getAlternativesIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setAlternatives(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addAlternatives(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addAlternatives(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsUnary(parent)){
      Unary element = (Unary) parent;
      if(Common.getIsExpression(from) && element.getOperand() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setOperand(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeOperand();
          element.setOperand(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsWildcardExpression(parent)){
      WildcardExpression element = (WildcardExpression) parent;
      if(Common.getIsTypeExpression(from) && element.getBound() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setBound(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setBound(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsAssert(parent)){
      Assert element = (Assert) parent;
      if(Common.getIsExpression(from) && element.getCondition() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setCondition(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setCondition(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && element.getDetail() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setDetail(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setDetail(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsBasicFor(parent)){
      BasicFor element = (BasicFor) parent;
      if(Common.getIsStatement(from) && isInList(element.getInitializersIterator(), (Statement) from)){
        if(atype == AddingType.AT_REMOVE){
          Statement aktFrom = (Statement) from;
          element.removeInitializers(aktFrom);
          return;
        } else {
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktFrom = (Statement) from;
          Statement aktTo = (Statement) to;

          int index = getListIndex(element.getInitializersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setInitializers(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addInitializers(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addInitializers(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsExpression(from) && element.getCondition() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setCondition(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setCondition(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsStatement(from) && isInList(element.getUpdatesIterator(), (Statement) from)){
        if(atype == AddingType.AT_REMOVE){
          Statement aktFrom = (Statement) from;
          element.removeUpdates(aktFrom);
          return;
        } else {
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktFrom = (Statement) from;
          Statement aktTo = (Statement) to;

          int index = getListIndex(element.getUpdatesIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setUpdates(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addUpdates(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addUpdates(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsBlock(parent)){
      Block element = (Block) parent;
      if(Common.getIsStatement(from) && isInList(element.getStatementsIterator(), (Statement) from)){
        if(atype == AddingType.AT_REMOVE){
          Statement aktFrom = (Statement) from;
          element.removeStatements(aktFrom);
          return;
        } else {
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktFrom = (Statement) from;
          Statement aktTo = (Statement) to;

          int index = getListIndex(element.getStatementsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setStatements(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addStatements(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addStatements(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsCase(parent)){
      Case element = (Case) parent;
      if(Common.getIsExpression(from) && element.getExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsDo(parent)){
      Do element = (Do) parent;
      if(Common.getIsExpression(from) && element.getCondition() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setCondition(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setCondition(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsEnhancedFor(parent)){
      EnhancedFor element = (EnhancedFor) parent;
      if(Common.getIsParameter(from) && element.getParameter() == (Parameter) from){
        if(atype == AddingType.AT_REMOVE){
          element.setParameter(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsParameter(to))
            throw new IllegalArgumentException("The node to is not an Parameter");

          Parameter aktTo = (Parameter) to;

          element.setParameter(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsExpression(from) && element.getExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsExpressionStatement(parent)){
      ExpressionStatement element = (ExpressionStatement) parent;
      if(Common.getIsExpression(from) && element.getExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeExpression();
          element.setExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsHandler(parent)){
      Handler element = (Handler) parent;
      if(Common.getIsParameter(from) && element.getParameter() == (Parameter) from){
        if(atype == AddingType.AT_REMOVE){
          element.setParameter(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsParameter(to))
            throw new IllegalArgumentException("The node to is not an Parameter");

          Parameter aktTo = (Parameter) to;
          
          element.setParameter(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsBlock(from) && element.getBlock() == (Block) from){
        if(atype == AddingType.AT_REMOVE){
          element.setBlock(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsBlock(to))
            throw new IllegalArgumentException("The node to is not an Block");

          Block aktTo = (Block) to;
          element.removeBlock();
          element.setBlock(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsIf(parent)){
      If element = (If) parent;
      if(Common.getIsStatement(from) && element.getSubstatement() == (Statement) from){
        if(atype == AddingType.AT_REMOVE){
          element.setSubstatement(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktTo = (Statement) to;
          element.removeSubstatement();
          element.setSubstatement(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsStatement(from) && element.getFalseSubstatement() == (Statement) from){
        if(atype == AddingType.AT_REMOVE){
          element.setFalseSubstatement(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktTo = (Statement) to;
          element.removeFalseSubstatement();
          element.setFalseSubstatement(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsIteration(parent)){
      Iteration element = (Iteration) parent;
      if(Common.getIsStatement(from) && element.getSubstatement() == (Statement) from){
        if(atype == AddingType.AT_REMOVE){
          element.setSubstatement(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktTo = (Statement) to;
          element.removeSubstatement();
          element.setSubstatement(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsLabeledStatement(parent)){
      LabeledStatement element = (LabeledStatement) parent;
      if(Common.getIsStatement(from) && element.getStatement() == (Statement) from){
        if(atype == AddingType.AT_REMOVE){
          element.setStatement(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktTo = (Statement) to;
          element.removeStatement();
          element.setStatement(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsReturn(parent)){
      Return element = (Return) parent;
      if(Common.getIsExpression(from) && element.getExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeExpression();
          element.setExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsSelection(parent)){
      Selection element = (Selection) parent;
      if(Common.getIsExpression(from) && element.getCondition() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setCondition(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeCondition();
          element.setCondition(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsSwitch(parent)){
      Switch element = (Switch) parent;
      if(Common.getIsSwitchLabel(from) && isInList(element.getCasesIterator(), (SwitchLabel) from)){
        if(atype == AddingType.AT_REMOVE){
          SwitchLabel aktFrom = (SwitchLabel) from;
          element.removeCases(aktFrom);
          return;
        } else {
          if(!Common.getIsSwitchLabel(to))
            throw new IllegalArgumentException("The node to is not an SwitchLabel");

          SwitchLabel aktFrom = (SwitchLabel) from;
          SwitchLabel aktTo = (SwitchLabel) to;

          int index = getListIndex(element.getCasesIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
			element.removeCases(index);
            element.setCases(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addCases(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addCases(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsSwitchLabel(parent)){
      SwitchLabel element = (SwitchLabel) parent;
      if(Common.getIsStatement(from) && isInList(element.getStatementsIterator(), (Statement) from)){
        if(atype == AddingType.AT_REMOVE){
          Statement aktFrom = (Statement) from;
          element.removeStatements(aktFrom);
          return;
        } else {
          if(!Common.getIsStatement(to))
            throw new IllegalArgumentException("The node to is not an Statement");

          Statement aktFrom = (Statement) from;
          Statement aktTo = (Statement) to;

          int index = getListIndex(element.getStatementsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setStatements(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addStatements(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addStatements(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsSynchronized(parent)){
      Synchronized element = (Synchronized) parent;
      if(Common.getIsExpression(from) && element.getLock() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setLock(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeLock();
          element.setLock(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsBlock(from) && element.getBlock() == (Block) from){
        if(atype == AddingType.AT_REMOVE){
          element.setBlock(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsBlock(to))
            throw new IllegalArgumentException("The node to is not an Block");

          Block aktTo = (Block) to;
          element.removeBlock();
          element.setBlock(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsThrow(parent)){
      Throw element = (Throw) parent;
      if(Common.getIsExpression(from) && element.getExpression() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setExpression(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeExpression();
          element.setExpression(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsTry(parent)){
      Try element = (Try) parent;
      if(Common.getIsVariable(from) && isInList(element.getResourcesIterator(), (Variable) from)){
        if(atype == AddingType.AT_REMOVE){
          Variable aktFrom = (Variable) from;
          element.removeResources(aktFrom);
          return;
        } else {
          if(!Common.getIsVariable(to))
            throw new IllegalArgumentException("The node to is not an Variable");

          Variable aktFrom = (Variable) from;
          Variable aktTo = (Variable) to;

          int index = getListIndex(element.getResourcesIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setResources(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addResources(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addResources(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsBlock(from) && element.getBlock() == (Block) from){
        if(atype == AddingType.AT_REMOVE){
          element.setBlock(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsBlock(to))
            throw new IllegalArgumentException("The node to is not an Block");

          Block aktTo = (Block) to;

          element.setBlock(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsHandler(from) && isInList(element.getHandlersIterator(), (Handler) from)){
        if(atype == AddingType.AT_REMOVE){
          Handler aktFrom = (Handler) from;
          element.removeHandlers(aktFrom);
          return;
        } else {
          if(!Common.getIsHandler(to))
            throw new IllegalArgumentException("The node to is not an Handler");

          Handler aktFrom = (Handler) from;
          Handler aktTo = (Handler) to;

          int index = getListIndex(element.getHandlersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setHandlers(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addHandlers(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addHandlers(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsBlock(from) && element.getFinallyBlock() == (Block) from){
        if(atype == AddingType.AT_REMOVE){
          element.setFinallyBlock(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsBlock(to))
            throw new IllegalArgumentException("The node to is not an Block");

          Block aktTo = (Block) to;

          element.setFinallyBlock(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsWhile(parent)){
      While element = (While) parent;
      if(Common.getIsExpression(from) && element.getCondition() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setCondition(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setCondition(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsAnnotatedElement(parent)){
      AnnotatedElement element = (AnnotatedElement) parent;
      if(Common.getIsAnnotation(from) && isInList(element.getAnnotationsIterator(), (Annotation) from)){
        if(atype == AddingType.AT_REMOVE){
          Annotation aktFrom = (Annotation) from;
          element.removeAnnotations(aktFrom);
          return;
        } else {
          if(!Common.getIsAnnotation(to))
            throw new IllegalArgumentException("The node to is not an Annotation");

          Annotation aktFrom = (Annotation) from;
          Annotation aktTo = (Annotation) to;

          int index = getListIndex(element.getAnnotationsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setAnnotations(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addAnnotations(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addAnnotations(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsAnnotationTypeElement(parent)){
      AnnotationTypeElement element = (AnnotationTypeElement) parent;
      if(Common.getIsExpression(from) && element.getDefaultValue() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setDefaultValue(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setDefaultValue(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsCompilationUnit(parent)){
      CompilationUnit element = (CompilationUnit) parent;
      if(Common.getIsPackageDeclaration(from) && element.getPackageDeclaration() == (PackageDeclaration) from){
        if(atype == AddingType.AT_REMOVE){
          element.setPackageDeclaration(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsPackageDeclaration(to))
            throw new IllegalArgumentException("The node to is not an PackageDeclaration");

          PackageDeclaration aktTo = (PackageDeclaration) to;

          element.setPackageDeclaration(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsImport(from) && isInList(element.getImportsIterator(), (Import) from)){
        if(atype == AddingType.AT_REMOVE){
          Import aktFrom = (Import) from;
          element.removeImports(aktFrom);
          return;
        } else {
          if(!Common.getIsImport(to))
            throw new IllegalArgumentException("The node to is not an Import");

          Import aktFrom = (Import) from;
          Import aktTo = (Import) to;

          int index = getListIndex(element.getImportsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setImports(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addImports(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addImports(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsPositioned(from) && isInList(element.getOthersIterator(), (Positioned) from)){
        if(atype == AddingType.AT_REMOVE){
          Positioned aktFrom = (Positioned) from;
          element.removeOthers(aktFrom);
          return;
        } else {
          if(!Common.getIsPositioned(to))
            throw new IllegalArgumentException("The node to is not an Positioned");

          Positioned aktFrom = (Positioned) from;
          Positioned aktTo = (Positioned) to;

          int index = getListIndex(element.getOthersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setOthers(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addOthers(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addOthers(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsEnumConstant(parent)){
      EnumConstant element = (EnumConstant) parent;
      if(Common.getIsNewClass(from) && element.getNewClass() == (NewClass) from){
        if(atype == AddingType.AT_REMOVE){
          element.setNewClass(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsNewClass(to))
            throw new IllegalArgumentException("The node to is not an NewClass");

          NewClass aktTo = (NewClass) to;

          element.setNewClass(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsGenericDeclaration(parent)){
      GenericDeclaration element = (GenericDeclaration) parent;
      if(Common.getIsTypeParameter(from) && isInList(element.getTypeParametersIterator(), (TypeParameter) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeParameter aktFrom = (TypeParameter) from;
          element.removeTypeParameters(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeParameter(to))
            throw new IllegalArgumentException("The node to is not an TypeParameter");

          TypeParameter aktFrom = (TypeParameter) from;
          TypeParameter aktTo = (TypeParameter) to;

          int index = getListIndex(element.getTypeParametersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setTypeParameters(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addTypeParameters(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addTypeParameters(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsImport(parent)){
      Import element = (Import) parent;
      if(Common.getIsExpression(from) && element.getTarget() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setTarget(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setTarget(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsInitializerBlock(parent)){
      InitializerBlock element = (InitializerBlock) parent;
      if(Common.getIsBlock(from) && element.getBody() == (Block) from){
        if(atype == AddingType.AT_REMOVE){
          element.setBody(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsBlock(to))
            throw new IllegalArgumentException("The node to is not an Block");

          Block aktTo = (Block) to;

          element.setBody(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsMethodDeclaration(parent)){
      MethodDeclaration element = (MethodDeclaration) parent;
      if(Common.getIsTypeExpression(from) && element.getReturnType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setReturnType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setReturnType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsNormalMethod(parent)){
      NormalMethod element = (NormalMethod) parent;
      if(Common.getIsParameter(from) && isInList(element.getParametersIterator(), (Parameter) from)){
        if(atype == AddingType.AT_REMOVE){
          Parameter aktFrom = (Parameter) from;
          element.removeParameters(aktFrom);
          return;
        } else {
          if(!Common.getIsParameter(to))
            throw new IllegalArgumentException("The node to is not an Parameter");

          Parameter aktFrom = (Parameter) from;
          Parameter aktTo = (Parameter) to;

          int index = getListIndex(element.getParametersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setParameters(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addParameters(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addParameters(aktTo, index+1);
          }
          return;
        }
      }
      if(Common.getIsBlock(from) && element.getBody() == (Block) from){
        if(atype == AddingType.AT_REMOVE){
          element.setBody(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsBlock(to))
            throw new IllegalArgumentException("The node to is not an Block");

          Block aktTo = (Block) to;

          element.setBody(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsTypeExpression(from) && isInList(element.getThrownExceptionsIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeThrownExceptions(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getThrownExceptionsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setThrownExceptions(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addThrownExceptions(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addThrownExceptions(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsPackage(parent)){
      Package element = (Package) parent;
      if(Common.getIsCompilationUnit(from) && isInList(element.getCompilationUnitsIterator(), (CompilationUnit) from)){
        if(atype == AddingType.AT_REMOVE){
          CompilationUnit aktFrom = (CompilationUnit) from;
          element.removeCompilationUnits(aktFrom);
          return;
        } else {
          if(!Common.getIsCompilationUnit(to))
            throw new IllegalArgumentException("The node to is not an CompilationUnit");

          CompilationUnit aktFrom = (CompilationUnit) from;
          CompilationUnit aktTo = (CompilationUnit) to;

          int index = getListIndex(element.getCompilationUnitsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setCompilationUnits(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addCompilationUnits(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addCompilationUnits(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsPackageDeclaration(parent)){
      PackageDeclaration element = (PackageDeclaration) parent;
      if(Common.getIsExpression(from) && element.getPackageName() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setPackageName(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;

          element.setPackageName(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsScope(parent)){
      Scope element = (Scope) parent;
      if(Common.getIsMember(from) && isInList(element.getMembersIterator(), (Member) from)){
        if(atype == AddingType.AT_REMOVE){
          Member aktFrom = (Member) from;
          element.removeMembers(aktFrom);
          return;
        } else {
          if(!Common.getIsMember(to))
            throw new IllegalArgumentException("The node to is not an Member");

          Member aktFrom = (Member) from;
          Member aktTo = (Member) to;

          int index = getListIndex(element.getMembersIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setMembers(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addMembers(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addMembers(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsTypeDeclaration(parent)){
      TypeDeclaration element = (TypeDeclaration) parent;
      if(Common.getIsTypeExpression(from) && element.getSuperClass() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setSuperClass(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setSuperClass(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
      if(Common.getIsTypeExpression(from) && isInList(element.getSuperInterfacesIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeSuperInterfaces(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getSuperInterfacesIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setSuperInterfaces(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addSuperInterfaces(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addSuperInterfaces(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsTypeParameter(parent)){
      TypeParameter element = (TypeParameter) parent;
      if(Common.getIsTypeExpression(from) && isInList(element.getBoundsIterator(), (TypeExpression) from)){
        if(atype == AddingType.AT_REMOVE){
          TypeExpression aktFrom = (TypeExpression) from;
          element.removeBounds(aktFrom);
          return;
        } else {
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktFrom = (TypeExpression) from;
          TypeExpression aktTo = (TypeExpression) to;

          int index = getListIndex(element.getBoundsIterator(), aktFrom);
          if(atype == AddingType.AT_SET){
            element.setBounds(aktTo, index);
          } else if(atype == AddingType.AT_ADDBEFORE){
            element.addBounds(aktTo, index);
          } else {//AddingType.AT_ADDAFTER
            element.addBounds(aktTo, index+1);
          }
          return;
        }
      }
    }
    if(Common.getIsVariable(parent)){
      Variable element = (Variable) parent;
      if(Common.getIsExpression(from) && element.getInitialValue() == (Expression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setInitialValue(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsExpression(to))
            throw new IllegalArgumentException("The node to is not an Expression");

          Expression aktTo = (Expression) to;
          element.removeInitialValue();
          element.setInitialValue(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    if(Common.getIsVariableDeclaration(parent)){
      VariableDeclaration element = (VariableDeclaration) parent;
      if(Common.getIsTypeExpression(from) && element.getType() == (TypeExpression) from){
        if(atype == AddingType.AT_REMOVE){
          element.setType(null);
          return;
        } else if(atype == AddingType.AT_SET){
          if(!Common.getIsTypeExpression(to))
            throw new IllegalArgumentException("The node to is not an TypeExpression");

          TypeExpression aktTo = (TypeExpression) to;

          element.setType(aktTo);
          return;
        } else {
          throw new IllegalArgumentException("The addAfter/Before not supported when mult==1.");
        }
      }
    }
    throw new IllegalArgumentException("The from node is not in the asg.");
  }

}
