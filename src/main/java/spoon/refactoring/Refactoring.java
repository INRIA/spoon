/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

import spoon.SpoonException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Contains all methods to refactor code elements in the AST.
 */
public final class Refactoring {
	private Refactoring() { }

	/**
	 * Changes name of a type element.
	 *
	 * @param type
	 * 		Type in the AST.
	 * @param name
	 * 		New name of the element.
	 */
	public static void changeTypeName(final CtType<?> type, String name) {

		final String typeQFN = type.getQualifiedName();
		final List<CtTypeReference<?>> references = Query.getElements(type.getFactory(), new TypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				String refFQN = reference.getQualifiedName();
				return typeQFN.equals(refFQN);
			}
		});

		type.setSimpleName(name);
		for (CtTypeReference<?> reference : references) {
			reference.setSimpleName(name);
		}
	}

	/**
	 * Changes name of a method, propagates the change in the executable references of the model.
	 */
	public static void changeMethodName(final CtMethod<?> method, String newName) {

		final List<CtExecutableReference<?>> references = Query.getElements(method.getFactory(), new TypeFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference<?> reference) {
				return reference.getDeclaration() == method;
			}
		});

		method.setSimpleName(newName);

		for (CtExecutableReference<?> reference : references) {
			reference.setSimpleName(newName);
		}
	}

	/** See doc in {@link CtMethod#copyMethod()} */
	public static CtMethod<?> copyMethod(final CtMethod<?> method) {
		CtMethod<?> clone = method.clone();
		String tentativeTypeName = method.getSimpleName() + "Copy";
		CtType parent = method.getParent(CtType.class);
		while (!parent.getMethodsByName(tentativeTypeName).isEmpty()) {
			tentativeTypeName += "X";
		}
		final String cloneMethodName = tentativeTypeName;
		clone.setSimpleName(cloneMethodName);
		parent.addMethod(clone);
		new CtScanner() {
			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
				CtExecutable<T> declaration = reference.getDeclaration();
				if (declaration == null) {
					return;
				}
				if (declaration == method) {
					reference.setSimpleName(cloneMethodName);
				}
				if (reference.getDeclaration() != clone) {
					throw new SpoonException("post condition broken " + reference);
				}
				super.visitCtExecutableReference(reference);

			}
		}.scan(clone);
		return clone;
	}


	/** See doc in {@link CtType#copyType()} */
	public static CtType<?> copyType(final CtType<?> type) {
		CtType<?> clone = type.clone();
		StringBuilder tentativeTypeName = new StringBuilder(type.getSimpleName() + "Copy");
		while (type.getFactory().Type().get(type.getPackage().getQualifiedName() + "." + tentativeTypeName) != null) {
			tentativeTypeName.append("X");
		}
		final String cloneTypeName = tentativeTypeName.toString();
		clone.setSimpleName(cloneTypeName);
		type.getPackage().addType(clone);
		new CtScanner() {
			@Override
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				if (reference.getDeclaration() == null) {
					return;
				}
				if (reference.getDeclaration() == type) {
					reference.setSimpleName(cloneTypeName);
				}
				if (reference.getDeclaration() != clone) {
					throw new SpoonException("post condition broken " + reference);
				}
				super.visitCtTypeReference(reference);
			}

			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
				CtExecutable<T> declaration = reference.getDeclaration();
				if (declaration == null) {
					return;
				}
				if (declaration.hasParent(type)) {
					reference.getDeclaringType().setSimpleName(cloneTypeName);
				}
				if (!reference.getDeclaration().hasParent(clone)) {
					throw new SpoonException("post condition broken " + reference);
				}
				super.visitCtExecutableReference(reference);

			}

			@Override
			public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
				CtField<T> declaration = reference.getDeclaration();
				if (declaration == null) {
					return;
				}
				if (declaration.hasParent(type)) {
					reference.getDeclaringType().setSimpleName(cloneTypeName);
				}
				if (reference.getDeclaration() == null || !reference.getDeclaration().hasParent(clone)) {
					throw new SpoonException("post condition broken " + reference);
				}
				super.visitCtFieldReference(reference);
			}

		}.scan(clone);
		return clone;
	}

	/**
	 * Changes name of a {@link CtLocalVariable}.
	 *
	 * @param localVariable
	 * 		to be renamed {@link CtLocalVariable} in the AST.
	 * @param newName
	 * 		New name of the element.
	 * @throws RefactoringException when rename to newName would cause model inconsistency, like ambiguity, shadowing of other variables, etc.
	 */
	public static void changeLocalVariableName(CtLocalVariable<?> localVariable, String newName) throws RefactoringException {
		new CtRenameLocalVariableRefactoring().setTarget(localVariable).setNewName(newName).refactor();
	}
}
