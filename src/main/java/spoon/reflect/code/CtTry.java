/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.template.TemplateParameter;

import java.util.List;

import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.CATCH;
import static spoon.reflect.path.CtRole.FINALIZER;

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
	@PropertyGetter(role = CATCH)
	List<CtCatch> getCatchers();

	/**
	 * Sets the <i>catchers</i> of this <code>try</code>.
	 */
	@PropertySetter(role = CATCH)
	<T extends CtTry> T setCatchers(List<CtCatch> catchers);

	/**
	 * Adds a catch block.
	 */
	@PropertySetter(role = CATCH)
	<T extends CtTry> T addCatcher(CtCatch catcher);

	/**
	 * Removes a catch block.
	 */
	@PropertySetter(role = CATCH)
	boolean removeCatcher(CtCatch catcher);

	/**
	 * Gets the try body.
	 */
	@Override
	@PropertyGetter(role = BODY)
	CtBlock<?> getBody();

	/**
	 * Gets the <i>finalizer</i> block of this <code>try</code> (
	 * <code>finally</code> part).
	 */
	@PropertyGetter(role = FINALIZER)
	CtBlock<?> getFinalizer();

	/**
	 * Sets the <i>finalizer</i> block of this <code>try</code> (
	 * <code>finally</code> part).
	 */
	@PropertySetter(role = FINALIZER)
	<T extends CtTry> T setFinalizer(CtBlock<?> finalizer);

	@Override
	CtTry clone();
}
