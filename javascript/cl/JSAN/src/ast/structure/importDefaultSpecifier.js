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
        var importDefaultSpecifier = factory.createImportDefaultSpecifierWrapper();
        globals.setPositionInfo(node, importDefaultSpecifier);
        return importDefaultSpecifier;
    } else {
        var importDefaultSpecifierWrapper = globals.getWrapperOfNode(node);

        if (node.local != null) {
            var localWrapper = globals.getWrapperOfNode(node.local);
            try {
                importDefaultSpecifierWrapper.setLocal(localWrapper);
            } catch (e) {
                console.error("IMPORTDEFAULTSPECIFIER - Could not set local! Reason of the error: " + e + "\n");
            }
        }

    }
}