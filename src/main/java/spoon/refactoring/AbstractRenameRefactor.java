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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import spoon.SpoonException;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;

public abstract class AbstractRenameRefactor<T extends CtNamedElement> implements Refactor {
	public static final Pattern javaIdentifierRE = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");

	protected T target;
	protected String newName;
	protected Pattern newNameValidationRE;

	protected AbstractRenameRefactor(Pattern newNameValidationRE) {
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
		List<Issue> issues = getIssues();
		if (issues.isEmpty() == false) {
			throw new SpoonException("Refactoring cannot be processed. There are issues: " + issues.toString());
		}
		refactorNoCheck();
	}

	protected void refactorNoCheck() {
		forEachReference(new CtConsumer<CtReference>() {
			@Override
			public void accept(CtReference t) {
				t.setSimpleName(AbstractRenameRefactor.this.newName);
			}
		});
		target.setSimpleName(newName);
	}

	protected abstract void forEachReference(CtConsumer<CtReference> consumer);

	@Override
	public List<Issue> getIssues() {
		List<Issue> issues = new ArrayList<>();
		detectIssues(issues);
		return issues;
	}

	protected void detectIssues(List<Issue> issues) {
		checkNewNameIsValid(issues);
		detectNameConflicts(issues);
	}

	/**
	 * checks whether {@link #newName} is valid java identifier
	 * @param issues
	 */
	protected void checkNewNameIsValid(List<Issue> issues) {
	}

	protected void detectNameConflicts(List<Issue> issues) {
	}


	protected boolean isJavaIdentifier(String name) {
		return javaIdentifierRE.matcher(name).matches();
	}

	public T getTarget() {
		return target;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		if (newNameValidationRE != null && newNameValidationRE.matcher(newName).matches() == false) {
			throw new SpoonException("New name \"" + newName + "\" is not valid name");
		}
		this.newName = newName;
	}
}
