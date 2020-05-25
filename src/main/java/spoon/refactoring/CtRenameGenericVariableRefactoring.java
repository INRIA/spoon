/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.VariableReferenceFunction;

/**
 * Spoon model that can refactor any type of variable (method parameters, local variables, field variables.
 * Provides no variable rename checking, so renaming variables to a name that already exists is possible, be wary
 * of creating incorrect code with this.
 * This class can be used as a tool for investigating code obfuscation. Useful for research purposes.
 * It has the same interface as the local variable rename:
 * <pre>
 * new CtRenameGenericVariableRefactoring().setTarget(myVar).setNewName("myNewName").refactor();
 * </pre>
 */
public class CtRenameGenericVariableRefactoring extends AbstractRenameRefactoring<CtVariable> {

	public CtRenameGenericVariableRefactoring() {
		super(javaIdentifierRE);
	}

	@Override
	protected void refactorNoCheck() {
		getTarget().map(new VariableReferenceFunction()).forEach(new CtConsumer<CtReference>() {
			@Override
			public void accept(CtReference t) {
				t.setSimpleName(newName);
			}
		});
		target.setSimpleName(newName);
	}
}
