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
const conversions = require('../conversions');

module.exports = function (node, parent, firstVisit) {
    if (firstVisit) {
        if (globals.getWrapperOfNode(node) !== undefined) {
            return;
        }
        var methodDefinition = factory.createMethodDefinitionWrapper();
        globals.setPositionInfo(node, methodDefinition);
        methodDefinition.setKind(conversions.convertMethodDefinitionKind(node.kind));
        methodDefinition.setComputed(node.computed);
        methodDefinition.setStatic(node.static);
        return methodDefinition;
    } else {
        var methodDefinitionWrapper = globals.getWrapperOfNode(node);

        if (node.key != null) {
            var keyWrapper = globals.getWrapperOfNode(node.key);
            try {
                methodDefinitionWrapper.setKey(keyWrapper);
            } catch (e) {
                console.error("METHODDEFINITION - Could not set key! Reason of the error: " + e + "\n");
            }
        }

        if (node.value != null) {
            var valueWrapper = globals.getWrapperOfNode(node.value);
            try {
                methodDefinitionWrapper.setValue(valueWrapper);
            } catch (e) {
                console.error("METHODDEFINITION - Could not set value! Reason of the error: " + e + "\n");
            }
        }

    }
}