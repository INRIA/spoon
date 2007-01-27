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

import java.util.List;

/**
 * This code element defines a <code>switch</code> statement.
 * 
 * @param <S>
 *            the type of the selector expression (it would be better to be able
 *            to define an upper bound, but it is not possible because of Java's
 *            type hierachy, especially since the enums that make things even
 *            worse!)
 */
public interface CtSwitch<S> extends CtStatement {
	/**
	 * Gets the selector. The type of the Expression must be <code>char</code>,
	 * <code>byte</code>, <code>short</code>, <code>int</code>,
	 * <code>Character</code>, <code>Byte</code>, <code>Short</code>,
	 * <code>Integer</code>, or an <code>enum</code> type
	 */
	CtExpression<S> getSelector();

	/**
	 * Sets the selector. The type of the Expression must be <code>char</code>,
	 * <code>byte</code>, <code>short</code>, <code>int</code>,
	 * <code>Character</code>, <code>Byte</code>, <code>Short</code>,
	 * <code>Integer</code>, or an <code>enum</code> type
	 */
	void setSelector(CtExpression<S> selector);

	/**
	 * Gets the list of cases defined for this switch.
	 */
	List<CtCase<? super S>> getCases();

	/**
	 * Sets the list of cases defined for this switch.
	 */
	void setCases(List<CtCase<? super S>> cases);
}
