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

#include "python/inc/python.h"
#include "python/inc/Common.h"
#include "common/inc/WriteMessage.h"

#include "python/inc/messages.h"
#include <algorithm>
#include <string.h>
#include "common/inc/math/common.h"


namespace columbus { namespace python { namespace asg {

typedef boost::crc_32_type  Crc_type;

namespace expression { 
  GeneratorExpression::GeneratorExpression(NodeId _id, Factory *_factory) :
    Expression(_id, _factory),
    m_hasExpression(0),
    hasGeneratorContainer()
  {
  }

  GeneratorExpression::~GeneratorExpression() {
  }

  void GeneratorExpression::prepareDelete(bool tryOnVirtualParent){
    removeExpression();
    while (!hasGeneratorContainer.empty()) {
      const NodeId id = *hasGeneratorContainer.begin();
      removeParentEdge(id);
      if (factory->getExistsReverseEdges())
        factory->reverseEdges->removeEdge(id, this->getId(), edkGeneratorExpression_HasGenerator);
      hasGeneratorContainer.pop_front();
    }
    expression::Expression::prepareDelete(false);
  }

  NodeKind GeneratorExpression::getNodeKind() const {
    return ndkGeneratorExpression;
  }

  expression::Expression* GeneratorExpression::getExpression() const {
    expression::Expression *_node = NULL;
    if (m_hasExpression != 0)
      _node = dynamic_cast<expression::Expression*>(factory->getPointer(m_hasExpression));
    if ( (_node == NULL) || factory->getIsFiltered(_node))
      return NULL;

    return _node;
  }

  ListIterator<expression::Generator> GeneratorExpression::getGeneratorListIteratorBegin() const {
    return ListIterator<expression::Generator>(&hasGeneratorContainer, factory, true);
  }

  ListIterator<expression::Generator> GeneratorExpression::getGeneratorListIteratorEnd() const {
    return ListIterator<expression::Generator>(&hasGeneratorContainer, factory, false);
  }

  bool GeneratorExpression::getGeneratorIsEmpty() const {
    return getGeneratorListIteratorBegin() == getGeneratorListIteratorEnd();
  }

  unsigned int GeneratorExpression::getGeneratorSize() const {
    unsigned int size = 0;
    ListIterator<expression::Generator> endIt = getGeneratorListIteratorEnd();
    for (ListIterator<expression::Generator> it = getGeneratorListIteratorBegin(); it != endIt; ++it) {
      ++size;
    }
    return size;
  }

  bool GeneratorExpression::setEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent) {
    switch (edgeKind) {
      case edkGeneratorExpression_HasExpression:
        setExpression(edgeEnd);
        return true;
      case edkGeneratorExpression_HasGenerator:
        addGenerator(edgeEnd);
        return true;
      default:
        break;
    }
    if (expression::Expression::setEdge(edgeKind, edgeEnd, false)) {
      return true;
    }
    return false;
  }

  bool GeneratorExpression::removeEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent) {
    switch (edgeKind) {
      case edkGeneratorExpression_HasExpression:
        removeExpression();
        return true;
      case edkGeneratorExpression_HasGenerator:
        removeGenerator(edgeEnd);
        return true;
      default:
        break;
    }
    if (expression::Expression::removeEdge(edgeKind, edgeEnd, false)) {
      return true;
    }
    return false;
  }

  void GeneratorExpression::setExpression(NodeId _id) {
    expression::Expression *_node = NULL;
    if (_id) {
      if (!factory->getExist(_id))
        throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_END_POINT_OF_THE_EDGE_DOES_NOT_EXIST);

      _node = dynamic_cast<expression::Expression*> (factory->getPointer(_id));
      if ( _node == NULL) {
        throw PythonException(COLUMBUS_LOCATION, CMSG_EX_INVALID_NODE_KIND);
      }
      if (&(_node->getFactory()) != this->factory)
        throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_FACTORY_OF_NODES_DOES_NOT_MATCH );

      if (m_hasExpression) {
        removeParentEdge(m_hasExpression);
        if (factory->getExistsReverseEdges())
          factory->reverseEdges->removeEdge(m_hasExpression, m_id, edkGeneratorExpression_HasExpression);
      }
      m_hasExpression = _node->getId();
      if (m_hasExpression != 0)
        setParentEdge(factory->getPointer(m_hasExpression), edkGeneratorExpression_HasExpression);
      if (factory->getExistsReverseEdges())
        factory->reverseEdges->insertEdge(m_hasExpression, this->getId(), edkGeneratorExpression_HasExpression);
    } else {
      if (m_hasExpression) {
        throw PythonException(COLUMBUS_LOCATION, CMSG_EX_CAN_T_SET_EDGE_TO_NULL);
      }
    }
  }

  void GeneratorExpression::setExpression(expression::Expression *_node) {
    if (_node == NULL)
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_CAN_T_SET_EDGE_TO_NULL);

    setExpression(_node->getId());
  }

  void GeneratorExpression::removeExpression() {
      if (m_hasExpression) {
        removeParentEdge(m_hasExpression);
        if (factory->getExistsReverseEdges())
          factory->reverseEdges->removeEdge(m_hasExpression, m_id, edkGeneratorExpression_HasExpression);
      }
      m_hasExpression = 0;
  }

  void GeneratorExpression::addGenerator(const expression::Generator *_node) {
    if (_node == NULL)
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_NODE_IS_NULL);

    if (&(_node->getFactory()) != this->factory)
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_FACTORY_OF_NODES_DOES_NOT_MATCH);

    if (!((_node->getNodeKind() == ndkGenerator) ))
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_INVALID_NODE_KIND);

    hasGeneratorContainer.push_back(_node->getId());
    setParentEdge(_node,edkGeneratorExpression_HasGenerator);

    if (factory->reverseEdges)
      factory->reverseEdges->insertEdge(_node, this, edkGeneratorExpression_HasGenerator);
  }

  void GeneratorExpression::addGenerator(NodeId _id) {
    const expression::Generator *node = dynamic_cast<expression::Generator*>(factory->getPointer(_id));
    if (node == NULL)
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_INVALID_NODE_KIND);
    addGenerator( node );
  }

  void GeneratorExpression::removeGenerator(NodeId id) {
    if (!factory->getExist(id))
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_END_POINT_OF_THE_EDGE_DOES_NOT_EXIST);

    ListIterator<expression::Generator>::Container::iterator it = find(hasGeneratorContainer.begin(), hasGeneratorContainer.end(), id);

    if (it == hasGeneratorContainer.end())
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_END_POINT_OF_THE_EDGE_DOES_NOT_EXIST);

    hasGeneratorContainer.erase(it);

    removeParentEdge(id);

    if (factory->getExistsReverseEdges())
      factory->reverseEdges->removeEdge(id, this->getId(), edkGeneratorExpression_HasGenerator);
  }

  void GeneratorExpression::removeGenerator(expression::Generator *_node) {
    if (_node == NULL)
      throw PythonException(COLUMBUS_LOCATION, CMSG_EX_THE_EDGE_IS_NULL);

    removeGenerator(_node->getId());
  }

  void GeneratorExpression::accept(Visitor &visitor) const {
    visitor.visit(*this);
  }

  void GeneratorExpression::acceptEnd(Visitor &visitor) const {
    visitor.visitEnd(*this);
  }

  double GeneratorExpression::getSimilarity(const base::Base& base){
    if(base.getNodeKind() == getNodeKind()) {
      return 1.0;
    } else {
      return 0.0;
    }
  }

  void GeneratorExpression::swapStringTable(RefDistributorStrTable& newStrTable, std::map<Key,Key>& oldAndNewStrKeyMap ){
    std::map<Key,Key>::iterator foundKeyId;

  }

  NodeHashType GeneratorExpression::getHash(std::set<NodeId>& travNodes) const {
    if (hashOk) return nodeHashCache;
    common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_BEGIN,this->getId()); 
    if (travNodes.count(getId())>0) {
      common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_SKIP);
      return 0;
    }
    travNodes.insert(getId());
    Crc_type resultHash;
    resultHash.process_bytes( "expression::GeneratorExpression", strlen("expression::GeneratorExpression"));
    common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_END,resultHash.checksum()); 
    nodeHashCache = resultHash.checksum();
    hashOk = true;
    return nodeHashCache;
  }

  void GeneratorExpression::save(io::BinaryIO &binIo,bool withVirtualBase  /*= true*/) const {
    Expression::save(binIo,false);

    binIo.writeUInt4(m_hasExpression);


    for (ListIterator<expression::Generator>::Container::const_iterator it = hasGeneratorContainer.begin(); it != hasGeneratorContainer.end(); ++it) {
      binIo.writeUInt4(*it);
    }
    binIo.writeUInt4(0); // This is the end sign
  }

  void GeneratorExpression::load(io::BinaryIO &binIo, bool withVirtualBase /*= true*/) {
    Expression::load(binIo,false);

    m_hasExpression =  binIo.readUInt4();
    if (m_hasExpression != 0)
      setParentEdge(factory->getPointer(m_hasExpression),edkGeneratorExpression_HasExpression);

    NodeId _id;

    _id = binIo.readUInt4();
    while (_id) {
      hasGeneratorContainer.push_back(_id);
      setParentEdge(factory->getPointer(_id),edkGeneratorExpression_HasGenerator);
      _id = binIo.readUInt4();
    }
  }


}


}}}
