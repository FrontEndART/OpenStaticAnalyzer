package coderepair.generator.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import columbus.java.asg.Factory;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.enums.NodeKind;

/**
 * This class stores the nodes which were modified during the refactoring.
 */
public class ModifiedNodes {

    private final List<Map<Integer, ? extends AbstractModified>> all;
    private final Map<Integer, ModifiedNode> unmodifiedNodes;
    private final Map<Integer, NewlyAddedNode> addedNodes;
    private final Map<Integer, ModifiedNode> modifiedNodes;
    private final Map<Integer, ModifiedNode> removedNodes;
    private final Map<Integer, ReplacedNode> replacedNodes;
    private final Map<Integer, ModifiedAttribute> modifiedAttributes;

    private Factory factory;

    public ModifiedNodes() {
        super();
        this.unmodifiedNodes = new LinkedHashMap<Integer, ModifiedNodes.ModifiedNode>();
        this.addedNodes = new LinkedHashMap<Integer, ModifiedNodes.NewlyAddedNode>();
        this.modifiedNodes = new LinkedHashMap<Integer, ModifiedNodes.ModifiedNode>();
        this.removedNodes = new LinkedHashMap<Integer, ModifiedNodes.ModifiedNode>();
        this.replacedNodes = new LinkedHashMap<Integer, ModifiedNodes.ReplacedNode>();
        this.modifiedAttributes = new LinkedHashMap<Integer, ModifiedNodes.ModifiedAttribute>();

        this.all = new ArrayList<Map<Integer, ? extends AbstractModified>>();
        this.all.add(this.unmodifiedNodes);
        this.all.add(this.addedNodes);
        this.all.add(this.modifiedNodes);
        this.all.add(this.removedNodes);
        this.all.add(this.replacedNodes);
        this.all.add(this.modifiedAttributes);
    }

    public Factory getFactory() {
        return this.factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    /**
     * Mark a nodes attribute as modified.<br>
     * <br>

     * @param id the id of the node which attribute has been modified
     * @param nodeKinds the nodes modified attributes (can be more than one)
     */
    public void markAttributeAsModified(int id, NodeKind... nodeKinds) {
        this.modifiedAttributes.put(id, new ModifiedAttribute(id, ModificationType.MODIFIED, nodeKinds));
    }

    /**
     * Mark a node as unchanged.<br>
     * Nodes (and the subtree below it) marked like this will not be generated again, the original source code will be used instead.<br>
     * (By default, every node is assumed unchanged. This class is for those cases when you want to stop the source code generator at some point under a
     * recursively generated node.)
     * 
     * @param id the id of the node which we mark as unchanged
     * @param recursionKind specifies the scope of the marking. If the whole subtree should be marked as unchanged, use {@link RecursionKind#RECURSIVE}.
     */
    public void markNodeAsUnchanged(Integer id, RecursionKind recursionKind) {
        this.unmodifiedNodes.put(id, new ModifiedNode(id, ModificationType.UNCHANGED, recursionKind == RecursionKind.RECURSIVE));
    }

    /**
     * It's the same as {@link ModifiedNodes#markNodeAsUnchanged(Integer, RecursionKind)}, except recursionkind is {@link RecursionKind#RECURSIVE} by default.
     */
    public void markNodeAsUnchanged(Integer id) {
        this.unmodifiedNodes.put(id, new ModifiedNode(id, ModificationType.UNCHANGED, true));
    }

    /**
     * Mark a node as newly added.<br>
     * Nodes (and the subtree below it) marked like this will be generated, and inserted on the position you provide.<br>
     * <br>
     * ({@link NewPositionKind} is a property to define where to add the new node. {@link NewPositionKind#FIRST} and {@link NewPositionKind#LAST} indicate that
     * the new element is positioned on the beginning or the ending of the given node. {@link NewPositionKind#BEFORE} and {@link NewPositionKind#AFTER} indicate
     * it has to be inserted before or after the given node's position.)
     * 
     * @param id
     * @param recursionKind
     * @param newPositionKind
     * @param node
     */
    public void markNodeAsNew(Integer id, RecursionKind recursionKind, NewPositionKind newPositionKind, Positioned node) {
        this.addedNodes.put(id, new NewlyAddedNode(id, ModificationType.ADDED, recursionKind == RecursionKind.RECURSIVE, newPositionKind, node));
    }

    /**
     * It's the same as {@link ModifiedNodes#markNodeAsNew(Integer, RecursionKind, NewPositionKind, Positioned)}, except recursionkind is {@link RecursionKind#RECURSIVE} by default.
     */
    public void markNodeAsNew(Integer id, NewPositionKind newPositionKind, Positioned node) {
        this.addedNodes.put(id, new NewlyAddedNode(id, ModificationType.ADDED, true, newPositionKind, node));
    }

    /**
     * Mark a node as modified.<br>
     * Nodes (and the subtree below it) marked like this will be generated again on their original position.<br>
     * (This method covers the case when a node's edge is modified, e.g.: when a new member is added to the class.)
     * 
     * @param id the id of the node which we mark as modified
     * @param recursionKind specifies the scope of the marking. If the whole subtree should be marked as modified, use {@link RecursionKind#RECURSIVE}.
     */
    public void markNodeAsModified(Integer id, RecursionKind recursionKind) {
        this.modifiedNodes.put(id, new ModifiedNode(id, ModificationType.MODIFIED, recursionKind == RecursionKind.RECURSIVE));
    }

    /**
     * It's the same as {@link ModifiedNodes#markNodeAsModified(Integer, RecursionKind)}, except recursionkind is {@link RecursionKind#RECURSIVE} by default.
     */
    public void markNodeAsModified(Integer id) {
        this.modifiedNodes.put(id, new ModifiedNode(id, ModificationType.MODIFIED, true));
    }

    /**
     * Mark a node as deleted.<br>
     * Nodes (and the subtree below it) marked like this will be removed from the source code.
     * 
     * @param id the id of the node which we mark as deleted
     * @param recursionKind specifies the scope of the marking. If the whole subtree should be marked as deleted, use {@link RecursionKind#RECURSIVE}.
     */
    public void markNodeAsDeleted(Integer id, RecursionKind recursionKind) {
        this.removedNodes.put(id, new ModifiedNode(id, ModificationType.REMOVED, recursionKind == RecursionKind.RECURSIVE));
    }

    /**
     * It's the same as {@link ModifiedNodes#markNodeAsDeleted(Integer, RecursionKind)}, except recursionkind is {@link RecursionKind#RECURSIVE} by default.
     */
    public void markNodeAsDeleted(Integer id) {
        this.removedNodes.put(id, new ModifiedNode(id, ModificationType.REMOVED, true));
    }

    /**
     * Mark a node as replaced.<br>
     * Nodes (and the subtree below it) marked like this will be generated again and the new code will replace the original one.
     * 
     * @param fromId the id of the original node
     * @param toId the id of the node the original will be replaced by
     * @param recursionKind specifies the scope of the marking. If the whole subtree should be marked as replaced, use {@link RecursionKind#RECURSIVE}.
     */
    public void markNodeReplaced(Integer fromId, Integer toId, RecursionKind recursionKind) {
        this.replacedNodes.put(fromId, new ReplacedNode(fromId, toId, ModificationType.REPLACED, recursionKind == RecursionKind.RECURSIVE));
    }

    /**
     * It's the same as {@link ModifiedNodes#markNodeReplaced(Integer, Integer, RecursionKind)}, except recursionkind is {@link RecursionKind#RECURSIVE} by
     * default.
     */
    public void markNodeReplaced(Integer fromId, Integer toId) {
        this.replacedNodes.put(fromId, new ReplacedNode(fromId, toId, ModificationType.REPLACED, true));
    }

    public Map<Integer, ModifiedAttribute> getModifiedAttributes() {
        return this.modifiedAttributes;
    }

    public Collection<ModifiedNode> getUnmodifiedNodes() {
        return this.unmodifiedNodes.values();
    }

    public Collection<NewlyAddedNode> getAddedNodes() {
        return this.addedNodes.values();
    }

    public Collection<ModifiedNode> getModifiedNodes() {
        return this.modifiedNodes.values();
    }

    public Collection<ModifiedNode> getRemovedNodes() {
        return this.removedNodes.values();
    }

    public Collection<ReplacedNode> getReplacedNodes() {
        return this.replacedNodes.values();
    }

    /**
     * Clears all the previous markings.
     */
    public void clear() {
        for (Map<Integer, ? extends AbstractModified> map : this.all) {
            map.clear();
        }
    }

    /**
     * Returns <tt>true</tt> if there are no nodes in this ModifiedNodes instance.
     * 
     * @return <tt>true</tt> if there are no nodes in this ModifiedNodes instance
     */
    public boolean isEmpty() {
        for (Map<Integer, ? extends AbstractModified> map : this.all) {
            if (!map.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // ---------------- inner classes ----------------

    public enum RecursionKind {
        RECURSIVE, NON_RECURSIVE
    }

    public enum ModificationType {
        UNCHANGED, ADDED, MODIFIED, REPLACED, REMOVED
    }

    public enum NewPositionKind {
        FIRST, LAST, BEFORE, AFTER
    }

    private abstract class AbstractModified {

        private final Integer nodeId;
        private final ModificationType type;

        private AbstractModified(Integer nodeId, ModificationType type) {
            super();
            this.nodeId = nodeId;
            this.type = type;
        }

        public Integer getNodeId() {
            return this.nodeId;
        }

        public ModificationType getType() {
            return this.type;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.nodeId == null) ? 0 : this.nodeId.hashCode());
            result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            AbstractModified other = (AbstractModified) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.nodeId == null) {
                if (other.nodeId != null) {
                    return false;
                }
            } else if (!this.nodeId.equals(other.nodeId)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            return true;
        }

        private ModifiedNodes getOuterType() {
            return ModifiedNodes.this;
        }

        @Override
        public String toString() {
            return "AbstractModified [nodeId=" + this.nodeId + ", type=" + this.type + "]";
        }

    }

    public class ModifiedAttribute extends AbstractModified {

        private final NodeKind[] nodeKinds;

        public ModifiedAttribute(Integer nodeId, ModificationType type, NodeKind[] nodeKinds) {
            super(nodeId, type);
            this.nodeKinds = nodeKinds;
        }

        public NodeKind[] getnNodeKinds() {
            return this.nodeKinds;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.nodeKinds == null) ? 0 : this.nodeKinds.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ModifiedAttribute other = (ModifiedAttribute) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.nodeKinds != other.nodeKinds) {
                return false;
            }
            return true;
        }

        private ModifiedNodes getOuterType() {
            return ModifiedNodes.this;
        }

        @Override
        public String toString() {
            return "ModifiedAttribute [" + ", toString()=" + super.toString() + "]";
        }

    }

    public class ModifiedNode extends AbstractModified {

        private final Boolean recursive;

        public ModifiedNode(Integer nodeId, ModificationType type, Boolean recursive) {
            super(nodeId, type);
            this.recursive = recursive;
        }

        public Boolean getRecursive() {
            return this.recursive;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.recursive == null) ? 0 : this.recursive.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ModifiedNode other = (ModifiedNode) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.recursive == null) {
                if (other.recursive != null) {
                    return false;
                }
            } else if (!this.recursive.equals(other.recursive)) {
                return false;
            }
            return true;
        }

        private ModifiedNodes getOuterType() {
            return ModifiedNodes.this;
        }

        @Override
        public String toString() {
            return "ModifiedNode [recursive=" + this.recursive + ", toString()=" + super.toString() + "]";
        }

    }

    public class NewlyAddedNode extends ModifiedNode {

        private final NewPositionKind positionKind;
        private final Positioned node;

        public NewlyAddedNode(Integer nodeId, ModificationType type, Boolean recursive, NewPositionKind positionKind, Positioned node) {
            super(nodeId, type, recursive);
            this.positionKind = positionKind;
            this.node = node;
        }

        public NewPositionKind getPositionKind() {
            return this.positionKind;
        }

        public Positioned getNode() {
            return this.node;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.node == null) ? 0 : this.node.hashCode());
            result = prime * result + ((this.positionKind == null) ? 0 : this.positionKind.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            NewlyAddedNode other = (NewlyAddedNode) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.node == null) {
                if (other.node != null) {
                    return false;
                }
            } else if (!this.node.equals(other.node)) {
                return false;
            }
            if (this.positionKind != other.positionKind) {
                return false;
            }
            return true;
        }

        private ModifiedNodes getOuterType() {
            return ModifiedNodes.this;
        }

        @Override
        public String toString() {
            return "NewlyAddedNode [positionKind=" + this.positionKind + ", node=" + this.node + ", toString()=" + super.toString() + "]";
        }

    }

    public class ReplacedNode extends ModifiedNode {

        private final Integer toId;

        public ReplacedNode(Integer fromId, Integer toId, ModificationType type, Boolean recursive) {
            super(fromId, type, recursive);
            this.toId = toId;
        }

        public Integer getToId() {
            return this.toId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.toId == null) ? 0 : this.toId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ReplacedNode other = (ReplacedNode) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.toId == null) {
                if (other.toId != null) {
                    return false;
                }
            } else if (!this.toId.equals(other.toId)) {
                return false;
            }
            return true;
        }

        private ModifiedNodes getOuterType() {
            return ModifiedNodes.this;
        }

    }

}
