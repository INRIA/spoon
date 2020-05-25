/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import spoon.reflect.declaration.CtNamedElement;

/**
 * The kind of refactoring, which renames a `target` element
 * to the `newName`<br>
 * Usage:<br>
 * <pre>
 * CtVariable someVariable = ...
 * new SomeRenameRefactoring().setTarget(someVariable).setNewName("mutchBetterName").refactor();
 * </pre>
 */
public interface CtRenameRefactoring<T extends CtNamedElement> extends CtRefactoring {
	/**
	 * @return target model element, which has to be refactored.
	 */
	T getTarget();
	/**
	 * @param target the model element, which has to be refactored.
	 * @return this to support fluent API
	 */
	CtRenameRefactoring<T> setTarget(T target);

	/**
	 * @return the required name of the `target` model element
	 */
	String getNewName();
	/**
	 * @param newName the required name of the `target` model element
	 * @return this to support fluent API
	 */
	CtRenameRefactoring<T> setNewName(String newName);
}
