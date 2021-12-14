package columbus.java.asg.support;

import java.util.HashMap;
import java.util.Map;

import columbus.java.asg.Factory;
import columbus.java.asg.base.Base;
import columbus.java.asg.visitors.VisitorAbstractNodes;

public class CloneVisitor extends VisitorAbstractNodes {
  Map<Integer, Integer> idMap;
  Factory fact;

  public CloneVisitor(){
    this.idMap = new HashMap<Integer, Integer>();
  }

  private Factory getFactory(Base node){
    if(fact == null)
      fact = node.getFactory();

    return fact;
  }

  public void visit(Base node, boolean vmi){
    Base aktClone = getFactory(node).createNode(node.getNodeKind());
    idMap.put(node.getId(), aktClone.getId());
  }

  public Map<Integer, Integer> getIdMap(){
    return idMap;
  }
}
