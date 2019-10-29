/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.Collection;
import java.util.regex.Pattern;

import spoon.SpoonException;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.reflect.visitor.filter.LocalVariableReferenceFunction;
import spoon.reflect.visitor.filter.LocalVariableScopeFunction;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;
import spoon.reflect.visitor.filter.SiblingsFunction;
import spoon.reflect.visitor.filter.SiblingsFunction.Mode;
import spoon.reflect.visitor.filter.VariableReferenceFunction;
import spoon.support.reflect.declaration.CtParameterImpl;

/**
 * Spoon model that can refactor any type of variable (method parameters, local variables, field variables.
 * Also provides no variable rename checking, so renaming variables to a name that already exists is possible.
 * This class can be used as a tool for investigating code obfuscation.
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