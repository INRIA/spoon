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
