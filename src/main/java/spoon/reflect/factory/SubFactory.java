/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.factory;

/**
 * This class is the superclass for all the sub-factories of
 * {@link spoon.reflect.factory.Factory}.
 */
public abstract class SubFactory {

	protected Factory factory;

	/**
	 * The sub-factory constructor takes an instance of the parent factory.
	 */
	public SubFactory(Factory factory) {
		super();
		this.factory = factory;
	}

	/**
	 * Generically sets the parent of a set of elements or lists of elements.
	 *
	 * @param parent
	 *            the parent
	 * @param elements
	 *            some {@link CtElement} or lists of {@link CtElement}
	 */
	//	protected void setParent(CtElement parent, Object... elements) {
	//		for (Object o : elements) {
	//			if (o instanceof CtElement) {
	//				((CtElement) o).setParent(parent);
	//			} else if (o instanceof Collection) {
	//				for (Object o2 : (Collection<?>) o) {
	//					((CtElement) o2).setParent(parent);
	//				}
	//			}
	//		}
	//	}

}
