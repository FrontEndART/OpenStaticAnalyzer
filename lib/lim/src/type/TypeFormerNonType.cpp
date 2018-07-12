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

#include "lim/inc/lim.h"
#include "lim/inc/Common.h"
#include "common/inc/WriteMessage.h"

#include "lim/inc/messages.h"
#include <algorithm>
#include <string.h>
#include "common/inc/math/common.h"


namespace columbus { namespace lim { namespace asg {

typedef boost::crc_32_type  Crc_type;

namespace type { 
  TypeFormerNonType::TypeFormerNonType(NodeId _id, Factory *_factory) :
    TypeFormer(_id, _factory)
  {
  }

  TypeFormerNonType::~TypeFormerNonType() {
  }

  void TypeFormerNonType::clone(const TypeFormerNonType& other, bool tryOnVirtualParent) {
    type::TypeFormer::clone(other, false);

  }

  void TypeFormerNonType::prepareDelete(bool tryOnVirtualParent){
    type::TypeFormer::prepareDelete(false);
  }

  NodeKind TypeFormerNonType::getNodeKind() const {
    return ndkTypeFormerNonType;
  }

  bool TypeFormerNonType::setEdge(EdgeKind edgeKind, NodeId edgeEnd, void* acValue, bool tryOnVirtualParent) {
    if (type::TypeFormer::setEdge(edgeKind, edgeEnd, acValue, false)) {
      return true;
    }
    return false;
  }

  bool TypeFormerNonType::removeEdge(EdgeKind edgeKind, NodeId edgeEnd, bool tryOnVirtualParent) {
    if (type::TypeFormer::removeEdge(edgeKind, edgeEnd, false)) {
      return true;
    }
    return false;
  }

  void TypeFormerNonType::accept(Visitor &visitor) const {
    visitor.visit(*this);
  }

  void TypeFormerNonType::acceptEnd(Visitor &visitor) const {
    visitor.visitEnd(*this);
  }

  double TypeFormerNonType::getSimilarity(const base::Base& base){
    if(base.getNodeKind() == getNodeKind()) {
      return 1.0;
    } else {
      return 0.0;
    }
  }

  void TypeFormerNonType::swapStringTable(RefDistributorStrTable& newStrTable, std::map<Key,Key>& oldAndNewStrKeyMap ){
    std::map<Key,Key>::iterator foundKeyId;

  }

  NodeHashType TypeFormerNonType::getHash(std::set<NodeId>& travNodes) const {
    if (hashOk) return nodeHashCache;
    common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_BEGIN,this->getId()); 
    if (travNodes.count(getId())>0) {
      common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_SKIP);
      return 0;
    }
    travNodes.insert(getId());
    Crc_type resultHash;
    resultHash.process_bytes( "type::TypeFormerNonType", strlen("type::TypeFormerNonType"));
    common::WriteMsg::write(CMSG_GET_THE_NODE_HASH_OF_NODE_END,resultHash.checksum()); 
    nodeHashCache = resultHash.checksum();
    hashOk = true;
    return nodeHashCache;
  }

  void TypeFormerNonType::save(io::BinaryIO &binIo,bool withVirtualBase  /*= true*/) const {
    TypeFormer::save(binIo,false);

  }

  void TypeFormerNonType::load(io::BinaryIO &binIo, bool withVirtualBase /*= true*/) {
    TypeFormer::load(binIo,false);

  }


}


}}}
