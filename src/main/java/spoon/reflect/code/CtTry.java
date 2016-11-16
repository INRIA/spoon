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

import spoon.template.TemplateParameter;

import java.util.List;

/**
 * This code element defines a <code>try</code> statement.
 *
 * Example:
 * <pre>
 *     try {
 *     	System.out.println("foo");
 *     } catch (Exception ignore) {}
 * </pre>
 */
public interface CtTry extends CtStatement, TemplateParameter<Void>, CtBodyHolder {

	/**
	 * Gets the <i>catchers</i> of this <code>try</code>.
	 */
	List<CtCatch> getCatchers();

	/**
	 * Sets the <i>catchers</i> of this <code>try</code>.
	 */
	<T extends CtTry> T setCatchers(List<CtCatch> catchers);

	/**
	 * Adds a catch block.
	 */
	<T extends CtTry> T addCatcher(CtCatch catcher);

	/**
	 * Removes a catch block.
	 */
	boolean removeCatcher(CtCatch catcher);

	/**
	 * Gets the try body.
	 */
	@Override
	CtBlock<?> getBody();

	/**
	 * Gets the <i>finalizer</i> block of this <code>try</code> (
	 * <code>finally</code> part).
	 */
	CtBlock<?> getFinalizer();

	/**
	 * Sets the <i>finalizer</i> block of this <code>try</code> (
	 * <code>finally</code> part).
	 */
	<T extends CtTry> T setFinalizer(CtBlock<?> finalizer);

	@Override
	CtTry clone();
}
