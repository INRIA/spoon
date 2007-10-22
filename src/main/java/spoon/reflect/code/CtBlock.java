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

import spoon.reflect.visitor.Filter;
import spoon.template.TemplateParameter;

/**
 * This code element represents a block of code, that is to say a list of
 * statements enclosed in curly brackets. When the context calls for a return
 * value, the block should contain a return statement as a lastly reachable
 * statement. The returned type if any is given by <code>R</code>.
 */
public interface CtBlock<R> extends CtStatement, TemplateParameter<R> {

	/**
	 * Inserts the given statement at the begining of the block.
	 */
	void insertBegin(CtStatement statement);

	/**
	 * Inserts the given statement list at the begining of the block.
	 */
	void insertBegin(CtStatementList<?> statements);

	/**
	 * Inserts the given statement at the end of the block.
	 */
	void insertEnd(CtStatement statement);

	/**
	 * Inserts the given statements at the end of the block.
	 */
	void insertEnd(CtStatementList<?> statements);

	/**
	 * Inserts the given statement before a set of insertion points given by a
	 * filter.
	 */
	void insertBefore(Filter<? extends CtStatement> insertionPoints,
			CtStatement statement);

	/**
	 * Inserts the given statement list before a set of insertion points given
	 * by a filter.
	 */
	void insertBefore(Filter<? extends CtStatement> insertionPoints,
			CtStatementList<?> statements);

	/**
	 * Inserts the given statement after a set of insertion points given by a
	 * filter.
	 */
	void insertAfter(Filter<? extends CtStatement> insertionPoints,
			CtStatement statement);

	/**
	 * Inserts the given statement list after a set of insertion points given by
	 * a filter.
	 */
	void insertAfter(Filter<? extends CtStatement> insertionPoints,
			CtStatementList<?> statements);

	/**
	 * Returns the statements enclosed by this block.
	 */
	List<CtStatement> getStatements();

	/**
	 * Sets the statements enclosed by this block.
	 */
	void setStatements(List<CtStatement> statements);

}
