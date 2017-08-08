/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.visitor.Filter;
import spoon.support.DerivedProperty;

import java.util.List;

import static spoon.reflect.path.CtRole.STATEMENT;

/**
 * This code element represents a list of statements. It is not a valid Java
 * program element and is never used directly, on contrary to
 * a {@link spoon.reflect.code.CtBlock}.
 */
public interface CtStatementList extends CtCodeElement, Iterable<CtStatement> {
	/**
	 * Returns the statement list.
	 */
	@PropertyGetter(role = STATEMENT)
	List<CtStatement> getStatements();

	/**
	 * Sets the statement list.
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T setStatements(List<CtStatement> statements);

	/**
	 * Adds a statement at the end of the list.
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T addStatement(CtStatement statement);

	/**
	 * Inserts the given statement at a specific position in the list of statements
	 * Shifts the statement currently at that position (if any) and any subsequent statements to the right (adds one to their indices).
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T addStatement(int index, CtStatement statement);

	/**
	 * Inserts the given statement at the beginning of the block.
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T insertBegin(CtStatement statement);

	/**
	 * Inserts the given statement list at the beginning of the block.
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T insertBegin(CtStatementList statements);

	/**
	 * Inserts the given statement at the end of the block.
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T insertEnd(CtStatement statement);

	/**
	 * Inserts the given statements at the end of the block.
	 */
	@PropertySetter(role = STATEMENT)
	<T extends CtStatementList> T insertEnd(CtStatementList statements);

	/**
	 * Inserts the given statement before a set of insertion points given by a
	 * filter.
	 */
	@DerivedProperty
	<T extends CtStatementList> T insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatement statement);

	/**
	 * Inserts the given statement list before a set of insertion points given
	 * by a filter.
	 */
	@DerivedProperty
	<T extends CtStatementList> T insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatementList statements);

	/**
	 * Inserts the given statement after a set of insertion points given by a
	 * filter.
	 */
	@DerivedProperty
	<T extends CtStatementList> T insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatement statement);

	/**
	 * Inserts the given statement list after a set of insertion points given by
	 * a filter.
	 */
	@DerivedProperty
	<T extends CtStatementList> T insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatementList statements);

	/**
	 * Gets the ith statement of this block.
	 */
	@DerivedProperty
	<T extends CtStatement> T getStatement(int i);

	/**
	 * Gets the last statement of this block.
	 */
	@DerivedProperty
	<T extends CtStatement> T getLastStatement();

	/**
	 * Removes a statement.
	 */
	@PropertySetter(role = STATEMENT)
	void removeStatement(CtStatement statement);

	@Override
	CtStatementList clone();
}
