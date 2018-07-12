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

namespace Columbus.Csharp.Asg.Nodes.Statement {

    /// <summary>
    /// UsingStatementSyntax class, which represents the statement::UsingStatementSyntax node.
    /// </summary>
    [DebuggerDisplay("{DemangledName}")]
#if (CSHARP_INTERNAL)
    internal
#else
    public
#endif
    class UsingStatementSyntax : StatementSyntax {


        // ---------- Edges ----------

        /// <summary>The id of the node the Declaration edge points to.</summary>
        protected uint m_Declaration;

        /// <summary>The id of the node the Expression edge points to.</summary>
        protected uint m_Expression;

        /// <summary>The id of the node the Statement edge points to.</summary>
        protected uint m_Statement;

        /// <summary>
        /// Constructor, only factory can instantiates nodes.
        /// </summary>
        /// <param name="nodeId">[in] The id of the node.</param>
        /// <param name="factory">[in] Poiter to the Factory the node belongs to.</param>
        public UsingStatementSyntax(uint nodeId, Factory factory) : base(nodeId, factory) {
            m_Declaration = 0;
            m_Expression = 0;
            m_Statement = 0;
        }

        /// <summary>
        /// Gives back the NodeKind of the node.
        /// </summary>
        /// <returns>Returns with the NodeKind.</returns>
        public override Types.NodeKind NodeKind {
            get { return Types.NodeKind.ndkUsingStatementSyntax; }
        }


        // ---------- Reflection getter function ----------

        /// <summary>
        /// Gives back the begin iterator for the edge specified
        /// </summary>
        /// <param name="edge">[in] The multiple edge for which the iterator is requested</param>
        public override ListIterator<Columbus.Csharp.Asg.Nodes.Base.Base> GetListIteratorBegin(string edge) {
            switch(edge) {
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
                default:
                    base.add(edge, nodeId);
                    return;
            }
        }


        // ---------- Edge getter function(s) ----------

        /// <summary>
        /// Gives back the id of the node the Declaration edge points to.
        /// </summary>
        /// <returns>Returns the end point of the m_Declaration edge.</returns>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the edge id is invalid.</exception>
        public uint getDeclaration() {
            if (fact.getIsFiltered(m_Declaration))
                return 1;
            else
                return m_Declaration;
        }

        /// <summary>
        /// Gives back the id of the node the Expression edge points to.
        /// </summary>
        /// <returns>Returns the end point of the m_Expression edge.</returns>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the edge id is invalid.</exception>
        public uint getExpression() {
            if (fact.getIsFiltered(m_Expression))
                return 1;
            else
                return m_Expression;
        }

        /// <summary>
        /// Gives back the id of the node the Statement edge points to.
        /// </summary>
        /// <returns>Returns the end point of the m_Statement edge.</returns>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the edge id is invalid.</exception>
        public uint getStatement() {
            if (fact.getIsFiltered(m_Statement))
                return 1;
            else
                return m_Statement;
        }


        // ---------- Edge setter function(s) ----------

        /// <summary>
        /// Sets the Declaration edge.
        /// </summary>
        /// <param name="nodeId">[in] The new end point of the Declaration edge or 0. With 0 the edge can be deleted.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the nodeId is invalid.</exception>
        public void setDeclaration(uint nodeId) {
            if (nodeId != 0) {
                if (!fact.getExist(nodeId))
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Statement.UsingStatementSyntax.setDeclaration(NodeId)", "The end point of the edge does not exist.");

                Columbus.Csharp.Asg.Nodes.Base.Base node = fact.getRef(nodeId);
                if (Common.getIsBaseClassKind(node.NodeKind, Types.NodeKind.ndkVariableDeclarationSyntax)) {
                    if (m_Declaration != 0) {
                        removeParentEdge(m_Declaration);
                    }
                    m_Declaration = nodeId;
                    setParentEdge(m_Declaration, (uint)Types.EdgeKind.edkUsingStatementSyntax_Declaration);
                } else {
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Statement.UsingStatementSyntax.setDeclaration(NodeId)", "Invalid NodeKind (" + node.NodeKind.ToString() + ").");
                }
            } else {
                if (m_Declaration != 0) {
                    removeParentEdge(m_Declaration);
                }
                m_Declaration = 0;
            }
        }

        /// <summary>
        /// Sets the Declaration edge.
        /// </summary>
        /// <param name="node">[in] The new end point of the Declaration edge.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if there's something wrong with the given node.</exception>
        public void setDeclaration(Columbus.Csharp.Asg.Nodes.Structure.VariableDeclarationSyntax node) {
            if (m_Declaration != 0) {
                removeParentEdge(m_Declaration);
            }
            m_Declaration = node.Id;
            setParentEdge(m_Declaration, (uint)Types.EdgeKind.edkUsingStatementSyntax_Declaration);
        }

        /// <summary>
        /// Sets the Expression edge.
        /// </summary>
        /// <param name="nodeId">[in] The new end point of the Expression edge or 0. With 0 the edge can be deleted.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the nodeId is invalid.</exception>
        public void setExpression(uint nodeId) {
            if (nodeId != 0) {
                if (!fact.getExist(nodeId))
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Statement.UsingStatementSyntax.setExpression(NodeId)", "The end point of the edge does not exist.");

                Columbus.Csharp.Asg.Nodes.Base.Base node = fact.getRef(nodeId);
                if (Common.getIsBaseClassKind(node.NodeKind, Types.NodeKind.ndkExpressionSyntax)) {
                    if (m_Expression != 0) {
                        removeParentEdge(m_Expression);
                    }
                    m_Expression = nodeId;
                    setParentEdge(m_Expression, (uint)Types.EdgeKind.edkUsingStatementSyntax_Expression);
                } else {
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Statement.UsingStatementSyntax.setExpression(NodeId)", "Invalid NodeKind (" + node.NodeKind.ToString() + ").");
                }
            } else {
                if (m_Expression != 0) {
                    removeParentEdge(m_Expression);
                }
                m_Expression = 0;
            }
        }

        /// <summary>
        /// Sets the Expression edge.
        /// </summary>
        /// <param name="node">[in] The new end point of the Expression edge.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if there's something wrong with the given node.</exception>
        public void setExpression(Columbus.Csharp.Asg.Nodes.Expression.ExpressionSyntax node) {
            if (m_Expression != 0) {
                removeParentEdge(m_Expression);
            }
            m_Expression = node.Id;
            setParentEdge(m_Expression, (uint)Types.EdgeKind.edkUsingStatementSyntax_Expression);
        }

        /// <summary>
        /// Sets the Statement edge.
        /// </summary>
        /// <param name="nodeId">[in] The new end point of the Statement edge or 0. With 0 the edge can be deleted.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if the nodeId is invalid.</exception>
        public void setStatement(uint nodeId) {
            if (nodeId != 0) {
                if (!fact.getExist(nodeId))
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Statement.UsingStatementSyntax.setStatement(NodeId)", "The end point of the edge does not exist.");

                Columbus.Csharp.Asg.Nodes.Base.Base node = fact.getRef(nodeId);
                if (Common.getIsBaseClassKind(node.NodeKind, Types.NodeKind.ndkStatementSyntax)) {
                    if (m_Statement != 0) {
                        removeParentEdge(m_Statement);
                    }
                    m_Statement = nodeId;
                    setParentEdge(m_Statement, (uint)Types.EdgeKind.edkUsingStatementSyntax_Statement);
                } else {
                    throw new CsharpException("Columbus.Csharp.Asg.Nodes.Statement.UsingStatementSyntax.setStatement(NodeId)", "Invalid NodeKind (" + node.NodeKind.ToString() + ").");
                }
            } else {
                if (m_Statement != 0) {
                    removeParentEdge(m_Statement);
                }
                m_Statement = 0;
            }
        }

        /// <summary>
        /// Sets the Statement edge.
        /// </summary>
        /// <param name="node">[in] The new end point of the Statement edge.</param>
        /// <exception cref="Columbus.Csharp.Asg.CsharpException">Throws CsharpException if there's something wrong with the given node.</exception>
        public void setStatement(Columbus.Csharp.Asg.Nodes.Statement.StatementSyntax node) {
            if (m_Statement != 0) {
                removeParentEdge(m_Statement);
            }
            m_Statement = node.Id;
            setParentEdge(m_Statement, (uint)Types.EdgeKind.edkUsingStatementSyntax_Statement);
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

            io.writeUInt4(m_Declaration);
            io.writeUInt4(m_Expression);
            io.writeUInt4(m_Statement);

        }

        /// <summary>
        /// Loads the node.
        /// </summary>
        /// <param name="io">[in] The node is read from io.</param>
        /// <exception cref="Columbus.ColumbusIOException">Throws ColumbusIOException if there is something wrong with the file.</exception>
        public override void load(IO binIo) {
            base.load(binIo);

            m_Declaration =  binIo.readUInt4();
            if (m_Declaration != 0)
              setParentEdge(m_Declaration,(uint)Types.EdgeKind.edkUsingStatementSyntax_Declaration);

            m_Expression =  binIo.readUInt4();
            if (m_Expression != 0)
              setParentEdge(m_Expression,(uint)Types.EdgeKind.edkUsingStatementSyntax_Expression);

            m_Statement =  binIo.readUInt4();
            if (m_Statement != 0)
              setParentEdge(m_Statement,(uint)Types.EdgeKind.edkUsingStatementSyntax_Statement);

        }

    }

}
