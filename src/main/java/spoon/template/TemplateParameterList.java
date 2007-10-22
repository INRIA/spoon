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

import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtSimpleType;

/**
 * This interface defines a list of template parameters that is used to model a
 * list of statements.
 * 
 * @param <R>
 *            the type of the returned expression if the last statement is a
 *            return statement
 */
public interface TemplateParameterList<R> extends TemplateParameter<R> {

	/**
	 * See {@link TemplateParameter#S()}.
	 */
	R S();

	/**
	 * See {@link TemplateParameter#getSubstitution(CtSimpleType)}.
	 */
	CtStatementList<R> getSubstitution(CtSimpleType<?> targetType);
}
