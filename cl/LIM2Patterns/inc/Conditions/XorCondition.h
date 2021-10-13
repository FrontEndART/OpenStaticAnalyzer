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

#ifndef XOR_CONDITION_H
#define XOR_CONDITION_H

#include "MultiCondition.h"

namespace columbus { namespace lim { namespace patterns { namespace conditions {

    /**
    * \brief Class that represent the Condition named 'Xor' in our pattern
    */
    class XorCondition : public MultiCondition {
    public:

        /**
         * \brief Default Constructor
        */
        XorCondition();

        /**
        * \brief Copy Constructor
        * \param multiCond [in]
        */
        XorCondition(MultiCondition *multiCond);

        /**
        * \brief Refere to Condition::testCondition
        */
        virtual bool testCondition(graph::Graph &inGraph, const asg::base::Base& limNode);
    };

}}}}

#endif // !XOR_CONDITION_H
