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

import spoon.template.TemplateParameterList;

/**
 * This code element represents a list of statements. It is not a valid Java
 * program element and is never used directly in the meta-model, on contrary to
 * a {@link spoon.reflect.code.CtBlock}. However, it is used as a container of
 * statements during code manipulation and for defining multiple-statement
 * template parameters.
 * 
 * @param <R>
 *            the type of the returned expression if the last statement is a
 *            return statement
 * @see spoon.template.TemplateParameterList
 * @see spoon.reflect.code.CtBlock
 * @see spoon.reflect.code.CtBlock#insertAfter(Filter, CtStatementList)
 * @see spoon.reflect.code.CtBlock#insertBefore(Filter, CtStatementList)
 */
public interface CtStatementList<R> extends CtCodeElement,
		TemplateParameterList<R> {

	/**
	 * Returns the statement list.
	 */
	List<CtStatement> getStatements();

	/**
	 * Sets the statement list.
	 */
	void setStatements(List<CtStatement> statements);
}
