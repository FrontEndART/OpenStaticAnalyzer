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

#include "../../inc/Calculate/Calculate.h"
#include "../../inc/Conditions/FormulaCondition.h"
#include "../../inc/Conditions/CreateCondition.h"
#include "../../inc/GlobalsAndConstans.h"

using namespace std;
using namespace columbus::graph;
using namespace columbus::lim::asg;
using namespace columbus::lim::asg::base;
using namespace columbus::lim::patterns::conditions;

namespace columbus { namespace lim { namespace patterns { namespace calculate {

    CodeBlock::CodeBlock() {};
    CodeBlock::CodeBlock(vector<Code*> &_codes) {
        for (auto &_code : _codes) {
            this->codes.push_back(unique_ptr<Code>(move(_code)));
        }
    };
    CodeBlock::~CodeBlock() {};
    void CodeBlock::addCodeToBlock(Code* _code) {
        this->codes.push_back(unique_ptr<Code>(move(_code)));
    }
    bool CodeBlock::runCode(Graph &inGraph, const Base &base, stringAnyLValue &scopeVariables) {
        for (auto &_code : codes) {
            _code->runCode(inGraph, base, scopeVariables);
        }
        return true;
    };

}}}}