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
        var arrayExpression = factory.createArrayExpressionWrapper();
        globals.setPositionInfo(node, arrayExpression);
        return arrayExpression;
    } else {
        var arrayExpressionWrapper = globals.getWrapperOfNode(node);
        if (node.elements != null) {
            for (var i = 0; i < node.elements.length; i++) {
                if (node.elements[i] != null) {
                    var elementsWrapper = globals.getWrapperOfNode(node.elements[i]);
                    try {
                        arrayExpressionWrapper.addElements(elementsWrapper);
                    } catch (e) {
                        console.error("ARRAYEXPRESSION - Could not add element! Reason of the error: " + e + "\n");
                    }
                }
            }
        }
    }
}