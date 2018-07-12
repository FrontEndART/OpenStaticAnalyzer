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

#ifndef _JAVA_Do_H_
#define _JAVA_Do_H_

#include "java/inc/java.h"

/**
* \file Do.h
* \brief Contains declaration of the statm::Do class.
* \brief The it get atributes from 
    * \brief base::Base
    * \brief base::Commentable
*/

namespace columbus { namespace java { namespace asg {
namespace statm {

  /**
  * \brief Do class, which represents the statm::Do node.
  * (missing)
  * 
  * Attributes:
  *   - whilePosition (Range) : (missing)
  * 
  * Edges:
  *   - hasCondition (expr::Expression, single) : (missing)
  */
  class Do : public Iteration {
    protected:
      /**
      * \internal
      * \brief Non-public constructor, only factory can instantiates nodes.
      * \param nodeId  [in] The id of the node.
      * \param factory [in] Poiter to the Factory the node belongs to.
      */
      Do(NodeId nodeId, Factory *factory);

      /**
      * \internal
      * \brief Non-public destructor, only factory can destroy nodes.
      */
      virtual ~Do();

    private:
      /**
      * \brief This function always throws a JavaException due to copying is not allowed!
      */
      Do & operator=(const Do&);

      /**
      * \brief This function always throws a JavaException due to copying is not allowed!
      */
      Do(const Do&);

    protected:
      /**
      * \internal
      * \brief Clone method.
      */
      virtual void clone(const Do& other, bool tryOnVirtualParent);

    public:
      /**
      * \brief Gives back the NodeKind of the node.
      * \return Returns with the NodeKind.
      */
      virtual NodeKind getNodeKind() const;

      /**
      * \brief Delete all edge.
      */
      virtual void prepareDelete(bool tryOnVirtualParent);


      // ---------- Attribute getter function(s) ----------

      /**
      * \brief Gives back the whilePosition of the node.
      * \return Returns with the whilePosition.
      */
      virtual const Range getWhilePosition() const;

      /**
      * \brief Compare two node by whilePosition attribute of the node.
      * \return Returns with the result.
      */
      virtual int compareByWhilePosition(const Do& other) const;


      // ---------- Attribute setter function(s) ----------

      /**
      * \internal
      * \brief Sets the whilePosition of the node.
      * \param whilePosition [in] The new value of the whilePosition.
      */
      virtual void setWhilePosition(const Range& _whilePosition);

    protected:

      // ---------- Attribute(s) ----------

      /** \internal \brief todo (unknown). */
      MultiRange m_whilePosition;

    protected:
      /**
      * \brief Set or add the edge by edge kind
      * \param edgeKind           [in] The kind of the edge.
      * \param edgeEnd            [in] The id of node which is on the end of the edge.
      * \param tryOnVirtualParent [in] This is help for the traversal.
      * \return Return true if setting was success.
      */
      virtual bool setEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent);

    protected:
      /**
      * \brief Remove the edge by edge kind
      * \param edgeKind           [in] The kind of the edge.
      * \param edgeEnd            [in] The id of node which is on the end of the edge.
      * \param tryOnVirtualParent [in] This is help for the traversal.
      * \return Return true if removing was success.
      */
      virtual bool removeEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent);

    public:

      // ---------- Edge getter function(s) ----------

      /**
      * \brief Gives back the pointer of the node the hasCondition edge points to.
      * \return Returns the end point of the hasCondition edge.
      */
      expr::Expression* getCondition() const;


      // ---------- Edge setter function(s) ----------

      /**
      * \brief Sets the hasCondition edge.
      * \param id [in] The new end point of the hasCondition edge.
      */
      void setCondition(NodeId id);

      /**
      * \brief Sets the hasCondition edge.
      * \param node [in] The new end point of the hasCondition edge.
      */
      void setCondition(expr::Expression *node);

      /**
      * \brief remove the hasCondition edge.
      */
      void removeCondition();

    protected:

      // ---------- Edges ----------

      /** \internal \brief The id of the node the hasCondition edge points to. */
      NodeId m_hasCondition;

    public:

      // ---------- Accept fundtions for Visitor ----------

      /**
      * \brief It calls the appropriate visit method of the given visitor.
      * \param visitor [in] The used visitor.
      */
      virtual void accept(Visitor& visitor) const;

      /**
      * \brief It calls the appropriate visitEnd method of the given visitor.
      * \param visitor [in] The used visitor.
      */
      virtual void acceptEnd(Visitor& visitor) const;

      /**
      * \internal
      * \brief Calculate node similarity.
      * \param nodeIf [in] The other node.
      */
      virtual double getSimilarity(const base::Base& node);

      /**
      * \internal
      * \brief Calculate node hash.
      */
      virtual NodeHashType getHash(std::set<NodeId>&  node) const ;

    protected:
      /**
      * \internal
      * \brief It is swap the string table ids to the other string table.
      * \param newStrTable [in] The new table
      * \param oldAndNewStrKeyMap [in] The map for fast serch.
      */
      virtual void swapStringTable(RefDistributorStrTable& newStrTable, std::map<Key,Key>& oldAndNewStrKeyMap );

      /**
      * \internal
      * \brief Saves the node.
      * \param io [in] The node is written into io.
      */
      virtual void save(io::BinaryIO &io, bool withVirtualBase = true) const;

      /**
      * \internal
      * \brief Loads the node.
      * \param io [in] The node is read from io.
      */
      virtual void load(io::BinaryIO &io, bool withVirtualBase = true);

      /**
      * \internal
      * \brief Sorts some edges and attributes of the node.
      */
      virtual void sort(bool withVirtualBase = true);


      friend class java::asg::Factory;
      friend class java::asg::VisitorSave;
  };

} 


}}}
#endif

