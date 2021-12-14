package coderepair.generator.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import coderepair.generator.support.Modification.MType;
import columbus.java.asg.Common;
import columbus.java.asg.Factory;
import columbus.java.asg.Range;
import columbus.java.asg.algorithms.AlgorithmPreorder;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.base.PositionedWithoutComment;
import columbus.java.asg.visitors.VisitorAbstractNodes;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.ModifiedNodes.ModifiedAttribute;
import coderepair.generator.transformation.ModifiedNodes.ModifiedNode;
import coderepair.generator.transformation.ModifiedNodes.NewlyAddedNode;
import coderepair.generator.transformation.ModifiedNodes.ReplacedNode;

/**
 * Class for checks the modified Nodes and deletes the unnecessary ones.
 */
public class ModifiedChecker {

    private Factory fact;
    private Map<Integer, Modification> modifMap;

    /**
     * The constructor.
     * 
     * @param fact The factory.
     */
    public ModifiedChecker(Factory fact) {
        super();

        this.fact = fact;
        this.modifMap = new LinkedHashMap<Integer, Modification>();
    }

    /**
     * Simplifies the ModifiedNodes object's elements.
     * 
     * @param nodes The ModifiedNodes object.
     */
    public void simplify(ModifiedNodes nodes) {
        Collection<ReplacedNode> replaceds = nodes.getReplacedNodes();
        for (ReplacedNode node : replaceds) {
            this.addReplacedTo(node.getToId(), node);
            this.addReplacedFrom(node.getNodeId(), node);
        }

        Collection<ModifiedNode> removed = nodes.getRemovedNodes();
        for (ModifiedNode node : removed) {
            this.addRemoved(node.getNodeId(), node);
        }

        Collection<NewlyAddedNode> added = nodes.getAddedNodes();
        for (NewlyAddedNode node : added) {
            this.addAddedNode(node.getNodeId(), node);
        }

        Collection<ModifiedNode> modified = nodes.getModifiedNodes();
        for (ModifiedNode node : modified) {
            this.addModifiedNode(node.getNodeId(), node);
        }

        Set<Entry<Integer, ModifiedAttribute>> modifiedattr = nodes.getModifiedAttributes().entrySet();
        for (Entry<Integer, ModifiedAttribute> node : modifiedattr) {
            this.addModifiedAttr(node.getKey(), node.getValue());
        }

        Collection<ModifiedNode> unmodif = nodes.getUnmodifiedNodes();
        for (ModifiedNode node : unmodif) {
            this.addUnmodified(node.getNodeId(), node);
        }

        nodes.clear();

        for (Entry<Integer, Modification> modif : this.modifMap.entrySet()) {
            Integer id = modif.getKey();
            Modification mod = modif.getValue();

            if (mod instanceof ReplacedTo) {
                ReplacedTo rep = (ReplacedTo) mod;
                nodes.markNodeReplaced(rep.getModif().getNodeId(), rep.getModif().getToId());
            } else if (mod instanceof Removed) {
                nodes.markNodeAsDeleted(id);
            } else if (mod instanceof Added) {
                NewlyAddedNode add = ((Added) mod).getModif();
                nodes.markNodeAsNew(add.getNodeId(), add.getPositionKind(), add.getNode());
            } else if (mod instanceof Modified) {
                nodes.markNodeAsModified(id);
            } else if (mod instanceof ModifAttr) {
                ModifiedAttribute att = ((ModifAttr) mod).getModif();
                 nodes.markAttributeAsModified(id, att.getnNodeKinds());
            } else if (mod instanceof UnmodifModif) {
                nodes.markNodeAsUnchanged(id);
            }
        }
    }

    private void addUnmodified(Integer id, ModifiedNode modif) {
        UnmodifModif wrapmodif = new UnmodifModif(modif);

        if (!applyable(id, wrapmodif)) {
            return;
        }

        Set<MType> types = new HashSet<MType>();

        types.add(MType.MODIFIEDATTR);
        types.add(MType.MODIFIED);
        types.add(MType.ADDED);
        types.add(MType.REMOVED);
        types.add(MType.REPLACEDTO);

        Base base = this.fact.getRef(id);
        if (!Common.getIsPositionedWithoutComment(base)) {
            return;
        }
        PositionedWithoutComment posed = (PositionedWithoutComment) base;

        if (hasThisTypeOfChild(id, posed.getPosition(), types)) {
            return;
        }

        this.modifMap.put(id, wrapmodif);
    }

    private void addModifiedAttr(Integer id, ModifiedAttribute modif) {
        ModifAttr wrapmodif = new ModifAttr(modif);

        if (!applyable(id, wrapmodif)) {
            return;
        }

        Set<MType> types = getIgnoredParentSet();

        Base base = this.fact.getRef(id);
        if (!Common.getIsPositionedWithoutComment(base)) {
            return;
        }
        PositionedWithoutComment posed = (PositionedWithoutComment) base;

        if (hasThisTypeOfParent(id, posed.getPosition(), types)) {
            return;
        }

        types.clear();
        types.add(MType.UNMODIF);
        removeThisTypeFromParent(id, types);

//        if (this.modifMap.containsKey(id)) {
//            Modification modifed = this.modifMap.get(id);
//
//            if (modifed instanceof ModifAttr) {
//                ModifAttr modifAttribute = (ModifAttr) modifed;
//                
//                modifAttribute.getModif().getAttributeKinds();
//            }
//        }

        this.modifMap.put(id, wrapmodif);
    }

    private void addModifiedNode(Integer id, ModifiedNode modif) {
        Modification wrapmodif = new Modified(modif);

        if (!applyable(id, wrapmodif)) {
            return;
        }

        Set<MType> types = getIgnoredParentSet();

        Base base = this.fact.getRef(id);
        if (!Common.getIsPositionedWithoutComment(base)) {
            return;
        }
        PositionedWithoutComment posed = (PositionedWithoutComment) base;

        if (hasThisTypeOfParent(id, posed.getPosition(), types)) {
            return;
        }

        removeChildAndParent(id, posed.getPosition(), wrapmodif, types);
    }

    private void addAddedNode(Integer id, NewlyAddedNode modif) {
        Modification wrapmodif = new Added(modif);

        if (!applyable(id, wrapmodif)) {
            return;
        }

        Set<MType> types = getIgnoredParentSet();

        Positioned posed = modif.getNode();
        Range p = posed.getPosition();

        if (hasThisTypeOfParent(id, p, types)) {
            return;
        }

        removeChildAndParent(id, p, wrapmodif, types);
    }

    private void addRemoved(Integer id, ModifiedNode modif) {
        Modification wrapmodif = new Removed(modif);

        if (!applyable(id, wrapmodif)) {
            return;
        }

        Set<MType> types = getIgnoredParentSet();

        Base base = this.fact.getRef(id);
        if (!Common.getIsPositionedWithoutComment(base)) {
            return;
        }
        PositionedWithoutComment posed = (PositionedWithoutComment) base;

        if (hasThisTypeOfParent(id, posed.getPosition(), types)) {
            return;
        }

        removeChildAndParent(id, posed.getPosition(), wrapmodif, types);
    }

    private void addReplacedTo(Integer id, ReplacedNode modif) {
        Modification wrapmodif = new ReplacedTo(modif);

        if (!applyable(id, wrapmodif)) {
            return;
        }

        Set<MType> types = getIgnoredParentSet();

        Base base = this.fact.getRef(modif.getNodeId());
        if (!Common.getIsPositionedWithoutComment(base)) {
            return;
        }
        PositionedWithoutComment posed = (PositionedWithoutComment) base;

        if (hasThisTypeOfParent(id, posed.getPosition(), types)) {
            return;
        }

        removeChildAndParent(id, posed.getPosition(), wrapmodif, types);
    }

    private void addReplacedFrom(Integer id, ReplacedNode modif) {
        Modification wrapmodif = new ReplacedFrom(modif);

        Set<MType> types = new HashSet<Modification.MType>();

        types.clear();
        types.add(MType.MODIFIED);
        types.add(MType.MODIFIEDATTR);
        types.add(MType.ADDED);
        types.add(MType.REMOVED);
        types.add(MType.REPLACEDTO);
        Base base = this.fact.getRef(id);
        if (!Common.getIsPositionedWithoutComment(base)) {
            return;
        }
        PositionedWithoutComment posed = (PositionedWithoutComment) base;
        removeThisTypeFromChild(id, posed.getPosition(), types);

        this.modifMap.put(id, wrapmodif);
    }

    private Set<MType> getIgnoredParentSet() {
        Set<MType> types = new HashSet<MType>();

        types.add(MType.MODIFIED);
        types.add(MType.ADDED);
        types.add(MType.REMOVED);
        types.add(MType.REPLACEDTO);
        // types.add(MType.REPLACEDFROM);
        return types;
    }

    private void removeChildAndParent(Integer id, Range relative, Modification wrapmodif, Set<MType> types) {
        types.clear();
        types.add(MType.UNMODIF);
        removeThisTypeFromParent(id, types);

        types.clear();
        types.add(MType.MODIFIED);
        types.add(MType.MODIFIEDATTR);
        types.add(MType.ADDED);
        types.add(MType.REMOVED);
        types.add(MType.REPLACEDTO);
        removeThisTypeFromChild(id, relative, types);

        this.modifMap.put(id, wrapmodif);
    }

    private void removeThisTypeFromChild(Integer id, Range relative, Set<MType> types) {
        AlgorithmPreorder ap = new AlgorithmPreorder();

        ChildModifierVisitor visitor = new ChildModifierVisitor(types, this.modifMap);

        ap.run(this.fact, visitor, id);

        if (relative.getPath().equals("")) {
            throw new IllegalArgumentException("The relative path for id(" + id + ") should have path.");
        }

        removeModifMapRemovedReplaced(id, relative, types, true);
    }

    private void removeModifMapRemovedReplaced(Integer id, Range relative, Set<MType> types, boolean b) {
        Iterator<Entry<Integer, Modification>> iter = this.modifMap.entrySet().iterator();
        Set<Integer> intList = new HashSet<Integer>();

        while (iter.hasNext()) {
            Entry<Integer, Modification> entry = iter.next();

            Modification modif = entry.getValue();

            if (checkRemoveAndReplaced(relative, types, entry, modif, b)) {
                intList.add(id);
            }
        }

        for (Integer intt : intList) {
            this.modifMap.remove(intt);
        }
    }

    private void removeThisTypeFromParent(Integer id, Set<MType> types) {
        Set<Integer> removables = new HashSet<Integer>();

        Base parent = this.fact.getRef(id).getParent();
        while (parent != null) {
            if (this.modifMap.containsKey(parent.getId())) {
                Modification modif = this.modifMap.get(parent.getId());

                if (types.contains(modif.getType())) {
                    removables.add(parent.getId());
                }
            }

            parent = parent.getParent();
        }

        for (Integer aktId : removables) {
            this.modifMap.remove(aktId);
        }
    }

    private boolean hasThisTypeOfParent(Integer id, Range relative, Set<MType> types) {
        Base parent = this.fact.getRef(id).getParent();
        while (parent != null) {
            if (this.modifMap.containsKey(parent.getId())) {
                Modification modif = this.modifMap.get(parent.getId());

                if (types.contains(modif.getType())) {
                    return true;
                }
            }

            parent = parent.getParent();
        }

        if (relative.getPath().equals("")) {
            throw new IllegalArgumentException("The relative path for id(" + id + ") should have path.");
        }

        return checkModifMapRemovedReplaced(relative, types, false);
    }

    private boolean hasThisTypeOfChild(Integer id, Range relative, Set<MType> types) {
        AlgorithmPreorder ap = new AlgorithmPreorder();

        ChildTypeVisitor visitor = new ChildTypeVisitor(types, this.modifMap);

        ap.run(this.fact, visitor, id);

        if (visitor.getHas()) {
            return true;
        }

        return checkModifMapRemovedReplaced(relative, types, true);
    }

    private boolean checkModifMapRemovedReplaced(Range aktPos, Set<MType> types, boolean innerPos) {
        Iterator<Entry<Integer, Modification>> iter = this.modifMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, Modification> entry = iter.next();

            Modification modif = entry.getValue();

            if (checkRemoveAndReplaced(aktPos, types, entry, modif, innerPos)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkRemoveAndReplaced(Range aktualPos, Set<MType> types, Entry<Integer, Modification> entry, Modification modif, boolean innerPos) {
        if ((modif.getType() == MType.REMOVED && types.contains(MType.REMOVED))
                || (modif.getType() == MType.REPLACEDFROM && types.contains(MType.REPLACEDFROM))) {
            Base mapped = this.fact.getRef(entry.getKey());

            if (!Common.getIsPositionedWithoutComment(mapped)) {
                return false;
            }

            PositionedWithoutComment mappedPosed = (PositionedWithoutComment) mapped;

            if (mappedPosed.getPosition().getPath().equals("")) {
                throw new IllegalArgumentException("The path for id(" + mapped.getId() + ") should have path.");
            }

            if ((innerPos && isInnerPositioned(mappedPosed.getPosition(), aktualPos))
                    || (!innerPos && isOutherPositioned(mappedPosed.getPosition(), aktualPos))) {
                return true;
            }
        }

        return false;
    }

    private boolean isOutherPositioned(Range mapped, Range aktual) {
        return isInnerPositioned(aktual, mapped);
    }

    private boolean isInnerPositioned(Range mappedPos, Range aktualPos) {
        if (mappedPos.getPath() == null || mappedPos.getPath().equals("")) {
            throw new IllegalArgumentException("The mappedPos must not be empty path.");
        }

        if (aktualPos.getPath() == null || aktualPos.getPath().equals("")) {
            throw new IllegalArgumentException("The aktualPos must not be empty path.");
        }

        if (aktualPos.getPath().equals(mappedPos.getPath())
                && getIsAfter(aktualPos.getWideLine(), aktualPos.getWideCol(), mappedPos.getWideLine(), mappedPos.getWideCol()) &&
                getIsAfter(mappedPos.getWideEndLine(), mappedPos.getWideEndCol(), aktualPos.getWideEndLine(), aktualPos.getWideEndCol())) {
            return true;
        }

        return false;
    }

    private boolean getIsAfter(int line, int col, int anoLine, int anoCol) {
        return (line < anoLine) || (line == anoLine && col <= anoCol);
    }

    private boolean applyable(Integer id, Modification modif) {
        if (!this.modifMap.containsKey(id)) {
            return true;
        }

        Modification stored = this.modifMap.get(id);

        if (modif.isEqualType(stored)) {
            if (modif.getType().getIsEqualsProblem()) {
                throw new IllegalArgumentException("The modification " + modif.getType() + " is added more than one to node(" + id + ").");
            } else {
                return true;
            }
        }
        if (modif.isStronger(stored) || stored instanceof ReplacedTo) {
            return true;
        }

        return false;
    }
}

abstract class Modification {

    public enum MType {
        REPLACEDFROM(5, true),
        REPLACEDTO(5, true),
        REMOVED(4, false),
        ADDED(3, true),
        MODIFIED(2, false),
        MODIFIEDATTR(1, false),
        UNMODIF(0, false);

        private int rang;
        private boolean equalsProblem;

        MType(int rang, boolean equalsproblem) {
            this.rang = rang;
            this.equalsProblem = equalsproblem;
        }

        public boolean isStronger(MType another) {
            return this.rang > another.rang;
        }

        public boolean getIsEqualsProblem() {
            return this.equalsProblem;
        }
    }

    private MType type;

    public Modification(MType type) {
        super();

        this.type = type;
    }

    public MType getType() {
        return this.type;
    }

    public boolean isEqualType(Modification another) {
        return this.type == another.getType();
    }

    public boolean isStronger(Modification another) {
        return this.type.isStronger(another.getType());
    }
}

abstract class ModifNodeStorer extends Modification {

    private ModifiedNode modif;

    public ModifNodeStorer(MType type, ModifiedNode modif) {
        super(type);

        this.modif = modif;
    }

    public ModifiedNode getModif() {
        return this.modif;
    }
}

class UnmodifModif extends ModifNodeStorer {

    public UnmodifModif(ModifiedNode modif) {
        super(MType.UNMODIF, modif);
    }
}

class Modified extends ModifNodeStorer {

    public Modified(ModifiedNode modif) {
        super(MType.MODIFIED, modif);
    }
}

class Removed extends ModifNodeStorer {

    public Removed(ModifiedNode modif) {
        super(MType.REMOVED, modif);
    }
}

class ReplacedTo extends Modification {

    private ReplacedNode modif;

    public ReplacedTo(ReplacedNode modif) {
        super(MType.REPLACEDTO);

        this.modif = modif;
    }

    public ReplacedNode getModif() {
        return this.modif;
    }
}

class ReplacedFrom extends Modification {

    private ReplacedNode modif;

    public ReplacedFrom(ReplacedNode modif) {
        super(MType.REPLACEDFROM);

        this.modif = modif;
    }

    public ReplacedNode getModif() {
        return this.modif;
    }
}

class Added extends Modification {

    private NewlyAddedNode modif;

    public Added(NewlyAddedNode modif) {
        super(MType.ADDED);

        this.modif = modif;
    }

    public NewlyAddedNode getModif() {
        return this.modif;
    }
}

class ModifAttr extends Modification {

    private ModifiedAttribute modif;

    public ModifAttr(ModifiedAttribute modif) {
        super(MType.MODIFIEDATTR);

        this.modif = modif;
    }

    public ModifiedAttribute getModif() {
        return this.modif;
    }
}

class ChildTypeVisitor extends VisitorAbstractNodes {

    private Set<MType> types;
    private Map<Integer, Modification> modifMap;
    private boolean has;

    public ChildTypeVisitor(Set<MType> types, Map<Integer, Modification> modifMap) {
        super();

        this.types = types;
        this.modifMap = modifMap;
        this.has = false;
    }

    public boolean getHas() {
        return this.has;
    }

    @Override
    public void visit(Positioned node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);

        if (this.has || !this.modifMap.containsKey(node.getId())) {
            return;
        }

        Modification modif = this.modifMap.get(node.getId());

        if (this.types.contains(modif.getType())) {
            this.has = true;
        }
    }
}

class ChildModifierVisitor extends VisitorAbstractNodes {

    private Set<MType> types;
    private Map<Integer, Modification> modifMap;

    public ChildModifierVisitor(Set<MType> types, Map<Integer, Modification> modifMap) {
        super();

        this.types = types;
        this.modifMap = modifMap;
    }

    @Override
    public void visit(Positioned node, boolean callVirtualBase) {
        super.visit(node, callVirtualBase);

        if (!this.modifMap.containsKey(node.getId())) {
            return;
        }

        Modification modif = this.modifMap.get(node.getId());

        if (this.types.contains(modif.getType())) {
            this.modifMap.remove(node.getId());
        }
    }
}