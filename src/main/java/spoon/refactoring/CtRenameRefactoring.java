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
