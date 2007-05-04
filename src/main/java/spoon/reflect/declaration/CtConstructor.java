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

package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;

/**
 * This element defines a constructor declaration.
 */
public interface CtConstructor<T> extends CtExecutable<T> {

	/**
	 * This operation is not allowed.
	 */
	void setSimpleName(String simpleName);

	/**
	 * Always returns "&lt;init&gt;".
	 */
	String getSimpleName();

	/**
	 * Returns the declaring type of this constructor (always the same as the
	 * constructor's type).
	 */
	CtType<T> getDeclaringType();

	/**
	 * This method has not effect for the constructor since its type is always
	 * the declaring type.
	 */
	void setType(CtTypeReference<T> type);

}