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

package columbus.java.asg;

import columbus.java.asg.base.Base;
import columbus.logger.LoggerHandler;

/**
 * Class for storing and iterating edges.
 * @param <T> The base type of the nodes in this edge.
 */
public class EdgeList<T extends Base> {

	@SuppressWarnings("unused")
	private static final LoggerHandler logger = new LoggerHandler(EdgeList.class, Constant.LoggerPropertyFile);
	@SuppressWarnings("rawtypes")
	private static final EdgeList EMPTY_LIST = new EdgeList(null);

	@SuppressWarnings("unchecked")
	public static final <T extends Base> EdgeList<T> emptyList() {
		return (EdgeList<T>)EMPTY_LIST;
	}

	private Factory factory;

	private int[] array;

	private int realSize;

	public EdgeList(Factory factory) {
		this.factory = factory;
	}

	public void add(T node) {
		add(node.getId());
	}

	public void add(T node, int index) {
		add(node.getId(), index);
	}

	public void add(int value) {
		add(value, realSize);
	}

	public void add(int value, int index) {
		if (array == null)
			array = new int[2];
		if (index > realSize) {
			throw new ArrayIndexOutOfBoundsException(realSize);
		} else if (array.length <= realSize) {
			resize();
		}
		realSize++;
		System.arraycopy(array, index, array, index + 1, realSize - index - 1);
		array[index] = value;
	}

	public int remove(int index) {
			if (array == null) {
				throw new NullPointerException("EdgeList not initialized");
			}
			else if (index < 0 || index >=realSize){
				throw new ArrayIndexOutOfBoundsException(index);
			}
			int tmp = array[index];
			System.arraycopy(array, index + 1, array, index, realSize - index - 1);
			realSize--;
			return tmp;
	}

	public boolean remove(T node) {
		if (array == null) {
			return false;
		}
		int id = node.getId();
		int index = 0;
		while (index <realSize && array[index] != id) {
				index++;
		}
		if (index < realSize) {
			remove(index);
			return true;
		}
		return false;
	}

	public int set(T node, int index) {
		return set(node.getId(), index);
	}
	public int set(int value, int index) {
		if (array == null) {
			throw new NullPointerException("EdgeList not initialized!");
		}
		else if (index < 0 || index >= realSize) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		else {
			int tmp = array[index];
			array[index] = value;
			return tmp;
		}
	}
	private void resize() {
		int oldCapacity = array.length;
		int newCapacity = (oldCapacity * 3) / 2 + 1;
		int oldData[] = array;
		array = new int[newCapacity];
		System.arraycopy(oldData, 0, array, 0, realSize);
	}

	public int size() {
		return realSize;
	}

	public boolean isEmpty() {
		return realSize == 0;
	}

	public void trimToSize() {
		int oldCapacity = array.length;
		if (realSize < oldCapacity) {
			int oldData[] = array;
			array = new int[realSize];
			System.arraycopy(oldData, 0, array, 0, realSize);
		}
	}

	public EdgeIterator<T> iterator() {
		return new Itr();
	}

	private class Itr implements EdgeIterator<T> {

	private final LoggerHandler logger = new LoggerHandler(Itr.class, Constant.LoggerPropertyFile);
		protected int i;

		public boolean hasNext() {
			if (array == null)
				return false;
			int j = i;
			while (j < realSize && (!factory.getExist(array[j]) || factory.getIsFiltered(array[j]))) {
				++j;
			}
			return j < realSize;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			if (array == null)
				throw new JavaException(logger.formatMessage("ex.java.xxx.Next_element_does_not_exist") );
			while (i < realSize && (!factory.getExist(array[i]) || factory.getIsFiltered(array[i]))) {
				++i;
			}
			if (i >= realSize)
				throw new JavaException(logger.formatMessage("ex.java.xxx.Next_element_does_not_exist"));
			return (T)factory.getRef(array[i++]);
		}
	public void add(T value) {
		EdgeList.this.add(value, i);
	}
	public void set(T value) {
		EdgeList.this.set(value, i - 1);
	}
	public void remove() {
		// don't need to remove anything because of the getExist method.
	}
	}

}

