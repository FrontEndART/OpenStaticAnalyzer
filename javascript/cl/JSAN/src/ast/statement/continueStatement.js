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
        var continueStatement = factory.createContinueStatementWrapper(factory);
        globals.setPositionInfo(node, continueStatement);
        return continueStatement;
    } else {
        var continueStatementWrapper = globals.getWrapperOfNode(node);
        if (node.label != null) {
            var labelWrapper = globals.getWrapperOfNode(node.label);
            if (node.label.type !== "Literal") {
                var labelWrapperFunctionString = "setLabel" + node.label.type;
            } else {
                var labelWrapperFunctionString = "setLabel" + globals.getLiteralType(node.label) + node.label.type;
            }
            try {
                continueStatementWrapper[labelWrapperFunctionString](labelWrapper);
            } catch (e) {
                console.error("CONTINUESTATEMENT - Function not exist: continueStatementWrapper." + labelWrapperFunctionString + "! Reason of the error: " + e + "\n");
            }
        }

    }
}