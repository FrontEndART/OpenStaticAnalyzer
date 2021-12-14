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

package columbus.java.asg.type;

import columbus.java.asg.*;

/**
 * Interface IntersectionType, which represents the {@link columbus.java.asg.type.IntersectionType IntersectionType} node.
 * @columbus.node (missing)
 * @columbus.edge bounds ({@link columbus.java.asg.type.Type Type}, multiple) : (missing)
 */
public interface IntersectionType extends Type {

	/**
	 * Gives back iterator for the {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edges.
	 * @return Returns an iterator for the bounds edges.
	 */
	public EdgeIterator<Type> getBoundsIterator();

	/**
	 * Tells whether the node has {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edges or not.
	 * @return Returns true if the node doesn't have any bounds edge.
	 */
	public boolean getBoundsIsEmpty();

	/**
	 * Gives back how many {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edges the node has.
	 * @return Returns with the number of bounds edges.
	 */
	public int getBoundsSize();

	/**
	 * Adds a new {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edge to the node.
	 * @param id The end point of the new bounds edge.
	 */
	public void addBounds(int id);

	/**
	 * Adds a new {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edge to the node.
	 * @param node The end point of the new bounds edge.
	 */
	public void addBounds(Type node);

	/**
	 * Adds a new {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edge to the node.
	 * @param node The end point of the new bounds edge.
	 * @param index The index of end point of the new bounds edge.
	 */
	public void addBounds(Type node, int index);

	/**
	 * Adds a new {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edge to the node.
	 * @param node The end point of the new bounds edge.
	 * @param index The index of end point of the new bounds edge.
	 */
	public void setBounds(Type node, int index);

	/**
	 * Remove the {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edge by id from the node.
	 * @param id The new end point of the bounds edge.
	 */
	public void removeBounds(int id);

	/**
	 * Remove the {@link columbus.java.asg.type.IntersectionType#edgeBounds bounds} edge from the node.
	 * @param node The new end point of the bounds edge.
	 */
	public void removeBounds(Type node);

}

