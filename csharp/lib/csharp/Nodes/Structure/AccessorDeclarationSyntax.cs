/*
 *  This file is part of OpenStaticAnalyzer.
 *
 *  Copyright (c) 2004-2018 Department of Software Engineering - University of Szeged
 *
 *  Licensed under Version 1.2 of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence in the LICENSE file or at:
 *
 *  https://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 */

using System;
using System.Text;
using System.Diagnostics;
using System.Collections.Generic;

namespace Columbus.Csharp.Asg.Nodes.Structure {

    /// <summary>
    /// AccessorDeclarationSyntax class, which represents the structure::AccessorDeclarationSyntax node.
    /// </summary>
    [DebuggerDisplay("{DemangledName}")]
#if (CSHARP_INTERNAL)
    internal
#else
    public
#endif
    class AccessorDeclarationSyntax : Columbus.Csharp.Asg.Nodes.Base.Positioned {

        /// <summary>The Key of the `identifier`.</summary>
        protected uint m_identifier;


        // ---------- Edges ----------

        /// <summary>Container stores the id of the nodes the AttributeLists edge points to.</summary>
        protected LinkedList<uint> AttributeListsContainer;

        /// <summary>The id of the node the Body edge points to.</summary>
        protected uint m_Body;

        /// <summary>
        /// Constructor, only factory can instantiates nodes.
        /// </summary>
        /// <param name="nodeId">[in] The id of the node.</param>
        /// <param name="factory">[in] Poiter to the Factory the node belongs to.</param>
        public AccessorDeclarationSyntax(uint nodeId, Factory factory) : base(nodeId, factory) {
            m_identifier = 0;
            AttributeListsContainer = new LinkedList<uint>();
            m_Body = 0;
        }

        /// <summary>
        /// Gives back the NodeKind of the node.
        /// </summary>
        /// <returns>Returns with the NodeKind.</returns>
        public override Types.NodeKind NodeKind {
            get { return Types.NodeKind.ndkAccessorDeclarationSyntax; }
        }


        // ---------- Attribute propert(y)(ies) ----------

        /// <summary>
        /// Gives back or sets the identifier of the node.
        /// </summary>
        public string Identifier
        {
            get
            {
                return fact.StringTable.get(m_identifier);
            }
            set
            {
                m_identifier = fact.StringTable.set( value );
            }
        }

        /// <summary>
        /// Gives back or sets the Key of identifier of the node.
        /// </summary>
        public uint IdentifierKey
        {
            get
            {
                return m_identifier;
            }
            set
            {
               m_identifier = value;
            }
        }


        // ---------- Reflection getter function ----------

        /// <summary>
        /// Gives back the begin iterator for the edge specified
        /// </summary>
        /// <param name="edge">[in] The multiple edge for which the iterator is requested</param>
        public override ListIterator<Columbus.Csharp.Asg.Nodes.Base.Base> GetListIteratorBegin(string edge) {
            switch(edge) {
                case "AttributeLists":
                    return AttributeListsListIteratorBegin.Convert<Columbus.Csharp.Asg.Nodes.Base.Base>();
                default:
                    return base.GetListIteratorBegin(edge);
            }
        }


        // ---------- Reflection add function ----------

        /// <summary>
        /// Adds item as a new edge
        /// </summary>
        public override void add(string edge, uint nodeId) {
            switch(edge) {
                case "AttributeLists":
                    addAttributeLists(nodeId);
                    return;
                default:
                    base.add(edge, nodeId);
                    return;
            }
        }


        // ---------- Edge getter function(s) ----------

        /// <summary>
        /// Gives back iterator for the AttributeLists edges.
        /// </summary>
        /// <returns>Returns an iterator for the AttributeLists edges.</returns>
        public ListIterator<Columbus.Csharp.Asg.Nodes.Structure.AttributeListSyntax> AttributeListsListIteratorBegin {
            get {
                return new ListIterator<Columbus.Csharp.Asg.Nodes.Structure.AttributeListSyntax>(AttributeListsContainer, fact, true);
            }
        }

        /// <summary>
        /// Gives back iterator for the AttributeLists edges.
        /// </summary>
        /// <returns>Returns an iterator for the AttributeLists edges.</returns>
        public ListIterator<Columbus.Csharp.Asg.Nodes.Structure.AttributeListSyntax> AttributeListsListIteratorEnd {
            get {
                return new ListIterator<Columbus.Csharp.Asg.Nodes.Structure.AttributeListSyntax>(AttributeListsContainer, fact, false);
            }
        }

        /// <summary>
        /// Tells whether the node has AttributeLists edges or not.
        /// </summary>
        /// <returns>Returns true if the node has at least one AttributeLists edge.</returns>
        public bool AttributeListsIsEmpty {
            get { return AttributeListsContainer.Count == 0; }
        }

        /// <summary>
        /// Gives back how many AttributeLists edges the node has.
        /// </summary>
        /// <returns>Returns with the number of AttributeLists edges.</returns>
        public uint AttributeListsSize {
            get { return (uint)(AttributeListsContainer.Count); }
        }

        /// <summary>
        /// Gives back the id of the node the Body edge points to.
        /// </summary>
        /// <returns>Returns the end point of the m_Body edge.</returns>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the edge id is invalid.</exception>
        public uint getBody() {
            if (fact.getIsFiltered(m_Body))
                return 1;
            else
                return m_Body;
        }


        // ---------- Edge setter function(s) ----------

        /// <summary>
        /// Adds a new AttributeLists edge to the node and inserts it after the other ones.
        /// </summary>
        /// <param name="node">[in] The end point of the new AttributeLists edge.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if there is something wrong with the given node.</exception>
        public void addAttributeLists(Columbus.Csharp.Asg.Nodes.Structure.AttributeListSyntax node) {
            addAttributeLists(node.Id);
        }

        /// <summary>
        /// Adds a new AttributeLists edge to the node and inserts it after the other ones.
        /// </summary>
        /// <param name="nodeId">[in] The end point of the new AttributeLists edge.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the given nodeId is invalid.</exception>
        public void addAttributeLists(uint nodeId) {
            if (!fact.getExist(nodeId))
                throw new CsharpException("Columbus.Csharp.Asg.Nodes.Structure.AccessorDeclarationSyntax.addAttributeLists(NodeId)", "The end point of the edge does not exist.");
            Columbus.Csharp.Asg.Nodes.Base.Base node = fact.getRef(nodeId);
            if (Common.getIsBaseClassKind(node.NodeKind, Types.NodeKind.ndkAttributeListSyntax)) {
                AttributeListsContainer.AddLast(nodeId);
            } else {
                throw new CsharpException("Columbus.Csharp.Asg.Nodes.Structure.AccessorDeclarationSyntax.setAttributeLists(NodeId)", "Invalid NodeKind (" + node.NodeKind.ToString() + ").");
            }
           setParentEdge(nodeId, (uint)Types.EdgeKind.edkAccessorDeclarationSyntax_AttributeLists);
            if (fact.ReverseEdges != null)
              fact.ReverseEdges.insertEdge(nodeId, this.Id, Types.EdgeKind.edkAccessorDeclarationSyntax_AttributeLists);
        }

        /// <summary>
        /// Sets the Body edge.
        /// </summary>
        /// <param name="nodeId">[in] The new end point of the Body edge or 0. With 0 the edge can be deleted.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the nodeId is invalid.</exception>
        public void setBody(uint nodeId) {
            if (nodeId != 0) {
                if (!fact.getExist(nodeId))
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Structure.AccessorDeclarationSyntax.setBody(NodeId)", "The end point of the edge does not exist.");

                Columbus.Csharp.Asg.Nodes.Base.Base node = fact.getRef(nodeId);
                if (Common.getIsBaseClassKind(node.NodeKind, Types.NodeKind.ndkBlockSyntax)) {
                    if (m_Body != 0) {
                        removeParentEdge(m_Body);
                    }
                    m_Body = nodeId;
                    setParentEdge(m_Body, (uint)Types.EdgeKind.edkAccessorDeclarationSyntax_Body);
                } else {
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Structure.AccessorDeclarationSyntax.setBody(NodeId)", "Invalid NodeKind (" + node.NodeKind.ToString() + ").");
                }
            } else {
                if (m_Body != 0) {
                    removeParentEdge(m_Body);
                }
                m_Body = 0;
            }
        }

        /// <summary>
        /// Sets the Body edge.
        /// </summary>
        /// <param name="node">[in] The new end point of the Body edge.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if there's something wrong with the given node.</exception>
        public void setBody(Columbus.Csharp.Asg.Nodes.Statement.BlockSyntax node) {
            if (m_Body != 0) {
                removeParentEdge(m_Body);
            }
            m_Body = node.Id;
            setParentEdge(m_Body, (uint)Types.EdgeKind.edkAccessorDeclarationSyntax_Body);
        }


        // ---------- Accept functions for Visitor ----------

        /// <summary>
        /// It calls the appropriate visit method of the given visitor.
        /// </summary>
        /// <param name="visitor">[in] The used visitor.</param>
        public override void accept(Visitors.Visitor visitor) {
            visitor.visit(this);
        }

        /// <summary>
        /// It calls the appropriate visitEnd method of the given visitor.
        /// </summary>
        /// <param name="visitor">[in] The used visitor.</param>
        public override void acceptEnd(Visitors.Visitor visitor) {
            visitor.visitEnd(this);
        }

        /// <summary>
        /// Saves the node.
        /// </summary>
        /// <param name="io">[in] The node is written into io.</param>
        /// <exception cref="Columbus.ColumbusIOException">Throws ColumbusIOException if there is something wrong with the file.</exception>
        public override void save(IO io) {
            base.save(io);

            fact.StringTable.setType(m_identifier, StrTable.StrType.strToSave);
            io.writeUInt4(m_identifier);

            io.writeUInt4(m_Body);


            foreach (var AttributeLists in AttributeListsContainer) {
              io.writeUInt4(AttributeLists);
            }
            io.writeUInt4(0); // This is the end sign
        }

        /// <summary>
        /// Loads the node.
        /// </summary>
        /// <param name="io">[in] The node is read from io.</param>
        /// <exception cref="Columbus.ColumbusIOException">Throws ColumbusIOException if there is something wrong with the file.</exception>
        public override void load(IO binIo) {
            base.load(binIo);

            m_identifier = binIo.readUInt4();

            m_Body =  binIo.readUInt4();
            if (m_Body != 0)
              setParentEdge(m_Body,(uint)Types.EdgeKind.edkAccessorDeclarationSyntax_Body);

            uint _id;

            _id = binIo.readUInt4();
            while (_id != 0) {
              AttributeListsContainer.AddLast(_id);
              setParentEdge(_id, (uint)Types.EdgeKind.edkAccessorDeclarationSyntax_AttributeLists);
              _id = binIo.readUInt4();
            }
        }

    }

}
