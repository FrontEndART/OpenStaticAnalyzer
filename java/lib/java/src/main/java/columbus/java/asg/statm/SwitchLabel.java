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

package columbus.java.asg.statm;

import columbus.java.asg.*;
import columbus.java.asg.base.Positioned;

/**
 * Interface SwitchLabel, which represents the {@link columbus.java.asg.statm.SwitchLabel SwitchLabel} node.
 * @columbus.node (missing)
 * @columbus.attr colonPosition (Range) : (missing)
 * @columbus.edge hasStatements ({@link columbus.java.asg.statm.Statement Statement}, multiple) : (missing)
 */
public interface SwitchLabel extends Positioned {

	/**
	 * Gives back the {@link columbus.java.asg.statm.SwitchLabel#attributeColonPosition colonPosition} of the node.
	 * @return Returns with the colonPosition.
	 */
	public Range getColonPosition();

	/**
	 * Sets the {@link columbus.java.asg.statm.SwitchLabel#attributeColonPosition colonPosition} of the node.
	 * @param value The new value of the colonPosition.
	 */
	public void setColonPosition(Range value);

	/**
	 * Gives back iterator for the {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edges.
	 * @return Returns an iterator for the hasStatements edges.
	 */
	public EdgeIterator<Statement> getStatementsIterator();

	/**
	 * Tells whether the node has {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edges or not.
	 * @return Returns true if the node doesn't have any hasStatements edge.
	 */
	public boolean getStatementsIsEmpty();

	/**
	 * Gives back how many {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edges the node has.
	 * @return Returns with the number of hasStatements edges.
	 */
	public int getStatementsSize();

	/**
	 * Adds a new {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edge to the node.
	 * @param id The end point of the new hasStatements edge.
	 */
	public void addStatements(int id);

	/**
	 * Adds a new {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edge to the node.
	 * @param node The end point of the new hasStatements edge.
	 */
	public void addStatements(Statement node);

	/**
	 * Adds a new {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edge to the node.
	 * @param node The end point of the new hasStatements edge.
	 * @param index The index of end point of the new hasStatements edge.
	 */
	public void addStatements(Statement node, int index);

	/**
	 * Adds a new {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edge to the node.
	 * @param node The end point of the new hasStatements edge.
	 * @param index The index of end point of the new hasStatements edge.
	 */
	public void setStatements(Statement node, int index);

	/**
	 * Remove the {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edge by id from the node.
	 * @param id The new end point of the hasStatements edge.
	 */
	public void removeStatements(int id);

	/**
	 * Remove the {@link columbus.java.asg.statm.SwitchLabel#edgeHasStatements hasStatements} edge from the node.
	 * @param node The new end point of the hasStatements edge.
	 */
	public void removeStatements(Statement node);

}

