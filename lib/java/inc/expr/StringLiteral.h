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

#ifndef _JAVA_StringLiteral_H_
#define _JAVA_StringLiteral_H_

#include "java/inc/java.h"

/**
* \file StringLiteral.h
* \brief Contains declaration of the expr::StringLiteral class.
* \brief The it get atributes from 
    * \brief base::Base
    * \brief base::Commentable
*/

namespace columbus { namespace java { namespace asg {
namespace expr {

  /**
  * \brief StringLiteral class, which represents the expr::StringLiteral node.
  * (missing)
  * 
  * Attributes:
  *   - value (String) : (missing)
  *   - formatString (String) : (missing)
  */
  class StringLiteral : public Literal {
    protected:
      /**
      * \internal
      * \brief Non-public constructor, only factory can instantiates nodes.
      * \param nodeId  [in] The id of the node.
      * \param factory [in] Poiter to the Factory the node belongs to.
      */
      StringLiteral(NodeId nodeId, Factory *factory);

      /**
      * \internal
      * \brief Non-public destructor, only factory can destroy nodes.
      */
      virtual ~StringLiteral();

    private:
      /**
      * \brief This function always throws a JavaException due to copying is not allowed!
      */
      StringLiteral & operator=(const StringLiteral&);

      /**
      * \brief This function always throws a JavaException due to copying is not allowed!
      */
      StringLiteral(const StringLiteral&);

    protected:
      /**
      * \internal
      * \brief Clone method.
      */
      virtual void clone(const StringLiteral& other, bool tryOnVirtualParent);

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
      * \brief Gives back the value of the node.
      * \return Returns with the value.
      */
      const std::string& getValue() const;

      /**
      * \brief Gives back the Key of value of the node.
      * \return Returns with the Key of the value.
      */
      Key getValueKey() const;

      /**
      * \brief Gives back the formatString of the node.
      * \return Returns with the formatString.
      */
      const std::string& getFormatString() const;

      /**
      * \brief Gives back the Key of formatString of the node.
      * \return Returns with the Key of the formatString.
      */
      Key getFormatStringKey() const;


      // ---------- Attribute setter function(s) ----------

      /**
      * \internal
      * \brief Sets the value of the node.
      * \param value [in] The new value of the value.
      */
      void setValue(const std::string& _value);

      /**
      * \internal
      * \brief Sets the formatString of the node.
      * \param formatString [in] The new value of the formatString.
      */
      void setFormatString(const std::string& _formatString);

      /**
      * \internal
      * \brief Sets the value of the node.
      * \param value [in] The new Key of the value.
      */
      void setValueKey(Key _value);

      /**
      * \internal
      * \brief Sets the formatString of the node.
      * \param formatString [in] The new Key of the formatString.
      */
      void setFormatStringKey(Key _formatString);

    protected:

      // ---------- Attribute(s) ----------

      /** \internal \brief The Key of the `value`. */
      Key m_value;

      /** \internal \brief The Key of the `formatString`. */
      Key m_formatString;

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

