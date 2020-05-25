/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.regex.Pattern;

import spoon.SpoonException;
import spoon.reflect.declaration.CtNamedElement;

/**
 * abstract implementation of rename element refactoring
 *
 * @param <T> the type of target renamed element
 */
public abstract class AbstractRenameRefactoring<T extends CtNamedElement> implements CtRenameRefactoring<T> {
	public static final Pattern javaIdentifierRE = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");

	protected T target;
	protected String newName;
	protected Pattern newNameValidationRE;

	protected AbstractRenameRefactoring(Pattern newNameValidationRE) {
		this.newNameValidationRE = newNameValidationRE;
	}

	@Override
	public void refactor() {
		if (getTarget() == null) {
			throw new SpoonException("The target of refactoring is not defined");
		}
		if (getNewName() == null) {
			throw new SpoonException("The new name of refactoring is not defined");
		}
		detectIssues();
		refactorNoCheck();
	}

	protected abstract void refactorNoCheck();

	protected void detectIssues() {
		checkNewNameIsValid();
		detectNameConflicts();
	}

	/**
	 * client may implement this method to check whether {@link #newName} is valid
	 */
	protected void checkNewNameIsValid() {
	}

	/**
	 * client may implement this method to check whether {@link #newName}
	 * is in conflict with names of other model elements
	 */
	protected void detectNameConflicts() {
	}

	/**
	 * Helper method, which can be used by the child classes to check if name is an java identifier
	 * @param name the to be checked name
	 * @return true if name is valid java identifier
	 */
	protected boolean isJavaIdentifier(String name) {
		return javaIdentifierRE.matcher(name).matches();
	}

	@Override
	public T getTarget() {
		return target;
	}

	@Override
	public AbstractRenameRefactoring<T> setTarget(T target) {
		this.target = target;
		return this;
	}

	@Override
	public String getNewName() {
		return newName;
	}

	@Override
	public AbstractRenameRefactoring<T> setNewName(String newName) {
		if (newNameValidationRE != null && newNameValidationRE.matcher(newName).matches() == false) {
			throw new SpoonException("New name \"" + newName + "\" is not valid name");
		}
		this.newName = newName;
		return this;
	}
}
