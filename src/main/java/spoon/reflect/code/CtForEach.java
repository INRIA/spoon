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

package spoon.reflect.code;

/**
 * This code element defines a <code>foreach</code> loop (enhanced
 * <code>for</code>).
 */

public interface CtForEach extends CtLoop {
	/**
	 * Gets the iterated expression (an iterable of an array).
	 */
	CtExpression<?> getExpression();

	/**
	 * Gets the variable that references the currently iterated element.
	 */
	CtLocalVariable<?> getVariable();

	/**
	 * Sets the iterated expression (an iterable of an array).
	 */
	void setExpression(CtExpression<?> expression);

	/**
	 * Sets the variable that references the currently iterated element.
	 */
	void setVariable(CtLocalVariable<?> variable);
}
