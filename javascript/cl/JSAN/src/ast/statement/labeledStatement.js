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
        var labeledStatement = factory.createLabeledStatementWrapper(factory);
        globals.setPositionInfo(node, labeledStatement);
        return labeledStatement;
    } else {
        var labeledStatementWrapper = globals.getWrapperOfNode(node);


        if (node.label != null) {
            var labelWrapper = globals.getWrapperOfNode(node.label);
            if (node.label.type !== "Literal") {
                var labelWrapperFunctionString = "setLabel" + node.label.type;
            } else {
                var labelWrapperFunctionString = "setLabel" + globals.getLiteralType(node.label) + node.label.type;
            }
            try {
                labeledStatementWrapper[labelWrapperFunctionString](labelWrapper);
            } catch (e) {
                console.error("LABELEDSTATEMENT - Function not exist: labeledStatementWrapper." + labelWrapperFunctionString + "! Reason of the error: " + e + "\n");
            }
        }

        if (node.body != null) {
            var bodyWrapper = globals.getWrapperOfNode(node.body);
            if (node.body.type !== "Literal") {
                var bodyWrapperFunctionString = "setBody" + node.body.type;
            } else {
                var bodyWrapperFunctionString = "setBody" + globals.getLiteralType(node.body) + node.body.type;
            }
            try {
                labeledStatementWrapper[bodyWrapperFunctionString](bodyWrapper);
            } catch (e) {
                console.error("LABELEDSTATEMENT - Function not exist: labeledStatementWrapper." + bodyWrapperFunctionString + "! Reason of the error: " + e + "\n");
            }
        }

    }
}