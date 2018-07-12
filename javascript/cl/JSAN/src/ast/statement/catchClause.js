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
        var catchClause = factory.createCatchClauseWrapper(factory);
        globals.setPositionInfo(node, catchClause);
        return catchClause;
    } else {
        var catchClauseWrapper = globals.getWrapperOfNode(node);

        if (node.body != null) {
            var bodyWrapper = globals.getWrapperOfNode(node.body);
            if (node.body.type !== "Literal") {
                var bodyWrapperFunctionString = "setBody" + node.body.type;
            } else {
                var bodyWrapperFunctionString = "setBody" + globals.getLiteralType(node.body) + node.body.type;
            }
            try {
                catchClauseWrapper[bodyWrapperFunctionString](bodyWrapper);
            } catch (e) {
                console.error("CATCHCLAUSE - Function not exist: catchClauseWrapper." + bodyWrapperFunctionString + "! Reason of the error: " + e + "\n");
            }
        }

        if (node.param != null) {
            var paramWrapper = globals.getWrapperOfNode(node.param);
            if (node.param.type !== "Literal") {
                var paramWrapperFunctionString = "setParam" + node.param.type;
            } else {
                var paramWrapperFunctionString = "setParam" + globals.getLiteralType(node.param) + node.param.type;
            }
            try {
                catchClauseWrapper[paramWrapperFunctionString](paramWrapper);
            } catch (e) {
                console.error("CATCHCLAUSE - Function not exist: catchClauseWrapper." + paramWrapperFunctionString + "! Reason of the error: " + e + "\n");
            }
        }

    }
}