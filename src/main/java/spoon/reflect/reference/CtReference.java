/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.reference;

import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Root;

/**
 * This is the root inferface for program element references. References can
 * point to program element reified in the metamodel or not. In the latter case,
 * introspection methods fall back on runtime reflection (<code>java.lang.reflect</code>)
 * to access the program information, as long as available in the classpath.
 * 
 * @see spoon.reflect.declaration.CtElement
 */
@Root
public interface CtReference extends FactoryAccessor, Comparable<CtReference> {

	/**
	 * Gets the simple name of referenced element.
	 */
	String getSimpleName();

	/**
	 * Sets the name of referenced element.
	 */
	void setSimpleName(String simpleName);

	/**
	 * Tries to get the declaration that corresponds to the referenced element.
	 * 
	 * @return referenced element or null if element does not exist
	 */
	CtElement getDeclaration();

	/**
	 * Accepts a visitor
	 */
	void accept(CtVisitor visitor);

}
