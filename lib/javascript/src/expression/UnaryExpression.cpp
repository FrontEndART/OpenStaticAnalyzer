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

#include "javascript/inc/javascript.h"
#include "javascript/inc/Common.h"
#include "common/inc/WriteMessage.h"

#include "javascript/inc/messages.h"
#include <algorithm>
#include <string.h>
#include "common/inc/math/common.h"


namespace columbus { namespace javascript { namespace asg {

typedef boost::crc_32_type  Crc_type;

namespace expression { 
  UnaryExpression::UnaryExpression(NodeId _id, Factory *_factory) :
         Positioned(_id, _factory),
    Expression(_id, _factory),
    m_prefix(false),
    m_operator(unoMinus),
    m_hasArgument(0)
  {
  }

  UnaryExpression::~UnaryExpression() {
  }

  void UnaryExpression::prepareDelete(bool tryOnVirtualParent){
    removeArgument();
    if (tryOnVirtualParent) {
      base::Positioned::prepareDelete(false);
    }
    expression::Expression::prepareDelete(false);
  }

  NodeKind UnaryExpression::getNodeKind() const {
    return ndkUnaryExpression;
  }

  UnaryOperator UnaryExpression::getOperator() const {
    return m_operator;
  }

  bool UnaryExpression::getPrefix() const {
    return m_prefix;
  }

  void UnaryExpression::setOperator(UnaryOperator _operator) {
    m_operator = _operator;
  }

  void UnaryExpression::setPrefix(bool _prefix) {
    m_prefix = _prefix;
  }

  expression::Expression* UnaryExpression::getArgument() const {
    expression::Expression *_node = NULL;
    if (m_hasArgument != 0)
      _node = dynamic_cast<expression::Expression*>(factory->getPointer(m_hasArgument));
    if ( (_node == NULL) || factory->getIsFiltered(_node))
      return NULL;

    return _node;
  }

  bool UnaryExpression::setEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent) {
    switch (edgeKind) {
      case edkUnaryExpression_HasArgument:
        setArgument(edgeEnd);
        return true;
      default:
        break;
    }
    if (tryOnVirtualParent) {
      if (base::Positioned::setEdge(edgeKind, edgeEnd, false)) {
        return true;
      }
    }
    if (expression::Expression::setEdge(edgeKind, edgeEnd, false)) {
      return true;
    }
    return false;
  }

  bool UnaryExpression::removeEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent) {
    switch (edgeKind) {
      case edkUnaryExpression_HasArgument:
        removeArgument();
        return true;
      default:
        break;
    }
    if (tryOnVirtualParent) {
      if (base::Positioned::removeEdge(edgeKind, edgeEnd, false)) {
        return true;
      }
    }
    if (expression::Expression::removeEdge(edgeKind, edgeEnd, false)) {
      return true;
    }
    return false;
  }

  void UnaryExpression::setArgument(NodeId _id) {
    expression::Expression *_node = NULL;
    if (_id) {
      if (!factory->getExist(_id))
        throw JavascriptException(COLUMBUS_LOCATION, CMSG_EX_THE_END_POINT_OF_THE_EDGE_DOES_NOT_EXIST);

      _node = dynamic_cast<expression::Expression*> (factory->getPointer(_id));
      if ( _node == NULL) {
        throw JavascriptException(COLUMBUS_LOCATION, CMSG_EX_INVALID_NODE_KIND);
      }
      if (&(_node->getFactory()) != this->factory)
        throw JavascriptException(COLUMBUS_LOCATION, CMSG_EX_THE_FACTORY_OF_NODES_DOES_NOT_MATCH );

      if (m_hasArgument) {
        removeParentEdge(m_hasArgument);
        if (factory->getExistsReverseEdges())
          factory->reverseEdges->removeEdge(m_hasArgument, m_id, edkUnaryExpression_HasArgument);
      }
      m_hasArgument = _node->getId();
      if (m_hasArgument != 0)
        setParentEdge(factory->getPointer(m_hasArgument), edkUnaryExpression_HasArgument);
      if (factory->getExistsReverseEdges())
        factory->reverseEdges->insertEdge(m_hasArgument, this->getId(), edkUnaryExpression_HasArgument);
    } else {
      if (m_hasArgument) {
        throw JavascriptException(COLUMBUS_LOCATION, CMSG_EX_CAN_T_SET_EDGE_TO_NULL);
      }
    }
  }

  void UnaryExpression::setArgument(expression::Expression *_node) {
    if (_node == NULL)
      throw JavascriptException(COLUMBUS_LOCATION, CMSG_EX_CAN_T_SET_EDGE_TO_NULL);

    setArgument(_node->getId());
  }

  void UnaryExpression::removeArgument() {
      if (m_hasArgument) {
        removeParentEdge(m_hasArgument);
        if (factory->getExistsReverseEdges())
          factory->reverseEdges->removeEdge(m_hasArgument, m_id, edkUnaryExpression_HasArgument);
      }
      m_hasArgument = 0;
  }

  void UnaryExpression::accept(Visitor &visitor) const {
    visitor.visit(*this);
  }

  void UnaryExpression::acceptEnd(Visitor &visitor) const {
    visitor.visitEnd(*this);
  }

  double UnaryExpression::getSimilarity(const base::Base& base){
    if(base.getNodeKind() == getNodeKind()) {
      const UnaryExpression& node = dynamic_cast<const UnaryExpression&>(base);
      double matchAttrs = 0;
      if(node.getOperator() == getOperator()) ++matchAttrs;
      if(node.getPrefix() == getPrefix()) ++matchAttrs;
      return matchAttrs / (2 / (1 - Common::SimilarityMinimum)) + Common::SimilarityMinimum;
    } else {
      return 0.0;
    }
  }

  void UnaryExpression::swapStringTable(RefDistributorStrTable& newStrTable, std::map<Key,Key>& oldAndNewStrKeyMap ){
    std::map<Key,Key>::iterator foundKeyId;

  }

  NodeHashType UnaryExpression::getHash(std::set<NodeId>& travNodes) const {
    if (hashOk) return nodeHashCache;
    common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_BEGIN,this->getId()); 
    if (travNodes.count(getId())>0) {
      common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_SKIP);
      return 0;
    }
    travNodes.insert(getId());
    Crc_type resultHash;
    resultHash.process_bytes( "expression::UnaryExpression", strlen("expression::UnaryExpression"));
    common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_END,resultHash.checksum()); 
    nodeHashCache = resultHash.checksum();
    hashOk = true;
    return nodeHashCache;
  }

  void UnaryExpression::save(io::BinaryIO &binIo,bool withVirtualBase  /*= true*/) const {
    if (withVirtualBase)
      Positioned::save(binIo,false);

    Expression::save(binIo,false);

    unsigned char boolValues = 0;
    boolValues <<= 1;
    if (m_prefix) 
      boolValues |= 1;
    binIo.writeUByte1(boolValues);
    binIo.writeUByte1(m_operator);

    binIo.writeUInt4(m_hasArgument);

  }

  void UnaryExpression::load(io::BinaryIO &binIo, bool withVirtualBase /*= true*/) {
    if (withVirtualBase)
      Positioned::load(binIo, false);

    Expression::load(binIo,false);

    unsigned char boolValues = binIo.readUByte1();
    m_prefix = boolValues & 1;
    boolValues >>= 1;
    m_operator = (UnaryOperator)binIo.readUByte1();

    m_hasArgument =  binIo.readUInt4();
    if (m_hasArgument != 0)
      setParentEdge(factory->getPointer(m_hasArgument),edkUnaryExpression_HasArgument);

  }


}


}}}
