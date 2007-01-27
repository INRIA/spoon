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

package spoon.template;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtSimpleType;

/**
 * This interface defines a typed template parameter. It is parameterized by
 * <code>T</code>, the type of the template parameter, which can be retrieved
 * by the {@link #S()} method. For more details on how to use template
 * parameters, see {@link Template}.
 */
public interface TemplateParameter<T> {

	/**
	 * Gets the type of the template parameter. This methods has no runtime
	 * meaning (should return a <code>null</code> reference) but is used as a
	 * marker in a template code. When generating a template code, each
	 * invocation of this method will be substituted with the result of the
	 * {@link #getSubstitution(CtSimpleType)} method.
	 */
	T S();

	/**
	 * Returns the code which must be substituted to this template parameter,
	 * depending on its value.
	 * 
	 * @param targetType
	 *            the type that defines the context of the substitution (for
	 *            reference redirection).
	 */
	CtCodeElement getSubstitution(CtSimpleType<?> targetType);
}
