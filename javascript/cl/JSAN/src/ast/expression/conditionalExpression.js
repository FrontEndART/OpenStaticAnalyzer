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

var globals = require('../../globals');
var factory = globals.getFactory();

module.exports = function (node, parent, firstVisit) {
    if (firstVisit) {
        if (globals.getWrapperOfNode(node) !== undefined) {
            return;
        }
        var conditionalExpression = factory.createConditionalExpressionWrapper();
        globals.setPositionInfo(node, conditionalExpression);
        return conditionalExpression;
    } else {
        var conditionalExpressionWrapper = globals.getWrapperOfNode(node);

        if (node.alternate != null) {
            var alternateWrapper = globals.getWrapperOfNode(node.alternate);
            try {
                conditionalExpressionWrapper.setAlternate(alternateWrapper);
            } catch (e) {
                console.error("CONDITIONALEXPRESSION - Could not set alternate! Reason of the error: " + e + "\n");
            }
        }

        if (node.test != null) {
            var testWrapper = globals.getWrapperOfNode(node.test);
            try {
                conditionalExpressionWrapper.setTest(testWrapper);
            } catch (e) {
                console.error("CONDITIONALEXPRESSION - Could not set test! Reason of the error: " + e + "\n");
            }
        }

        if (node.consequent != null) {
            var consequentWrapper = globals.getWrapperOfNode(node.consequent);
            try {
                conditionalExpressionWrapper.setConsequent(consequentWrapper);
            } catch (e) {
                console.error("CONDITIONALEXPRESSION - Could not set consequent! Reason of the error: " + e + "\n");
            }
        }

    }
}