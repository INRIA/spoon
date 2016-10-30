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
package spoon.reflect.code;

import spoon.reflect.visitor.Filter;
import spoon.template.TemplateParameter;

/**
 * This code element represents a block of code, that is to say a list of
 * statements enclosed in curly brackets.
 *
 * Example: <pre>
 *  { // <-- block start
 *   System.out.println("foo");
 *  }
 *	</pre>
 *
 * When the context calls for a return
 * value, the block should contain a return statement as a lastly reachable
 * statement. The returned type if any is given by R.
 */
public interface CtBlock<R> extends CtStatement, CtStatementList, TemplateParameter<R> {

	/**
	 * Inserts the given statement at the beginning of the block.
	 */
	<T extends CtBlock<R>> T insertBegin(CtStatement statement);

	/**
	 * Inserts the given statement list at the beginning of the block.
	 */
	<T extends CtBlock<R>> T insertBegin(CtStatementList statements);

	/**
	 * Inserts the given statement at the end of the block.
	 */
	<T extends CtBlock<R>> T insertEnd(CtStatement statement);

	/**
	 * Inserts the given statements at the end of the block.
	 */
	<T extends CtBlock<R>> T insertEnd(CtStatementList statements);

	/**
	 * Inserts the given statement before a set of insertion points given by a
	 * filter.
	 */
	<T extends CtBlock<R>> T insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatement statement);

	/**
	 * Inserts the given statement list before a set of insertion points given
	 * by a filter.
	 */
	<T extends CtBlock<R>> T insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatementList statements);

	/**
	 * Inserts the given statement after a set of insertion points given by a
	 * filter.
	 */
	<T extends CtBlock<R>> T insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatement statement);

	/**
	 * Inserts the given statement list after a set of insertion points given by
	 * a filter.
	 */
	<T extends CtBlock<R>> T insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatementList statements);

	/**
	 * Gets the ith statement of this block.
	 */
	<T extends CtStatement> T getStatement(int i);

	/**
	 * Gets the last statement of this block.
	 */
	<T extends CtStatement> T getLastStatement();

	/**
	 * Replaces this element by another one.
	 */
	<T extends R> void replace(CtBlock<T> element);

	@Override
	CtBlock<R> clone();
}
