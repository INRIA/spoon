/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Find local variables catch, parameters, fields, super fields
 * @author tdurieux
 */
public class AccessibleVariablesFinder {

	private CtElement expression;

	public AccessibleVariablesFinder(CtElement expression) {
		this.expression = expression;
	}

	public List<CtVariable> find() {
		if (expression.isParentInitialized()) {
			return getVariable(expression.getParent());
		}
		return Collections.emptyList();
	}

	private List<CtVariable> getVariable(final CtElement parent) {
		final List<CtVariable> variables = new ArrayList<>();
		if (parent == null) {
			return variables;
		}
		class VariableScanner extends CtInheritanceScanner {
			@Override
			public void visitCtStatementList(CtStatementList e) {
				for (int i = 0; i < e.getStatements().size(); i++) {
					CtStatement ctStatement = e.getStatements().get(i);

					if (expression.getPosition().isValidPosition() && ctStatement.getPosition().isValidPosition()
							&& ctStatement.getPosition().getSourceStart() > expression.getPosition().getSourceEnd()) {
						break;
					}

					if (ctStatement instanceof CtVariable) {
						variables.add((CtVariable) ctStatement);
					}
				}
				super.visitCtStatementList(e);
			}

			@Override
			public <T> void scanCtType(CtType<T> type) {
				List<CtField<?>> fields = type.getFields();
				for (CtField<?> ctField : fields) {
					if (ctField.hasModifier(ModifierKind.PUBLIC) || ctField.hasModifier(ModifierKind.PROTECTED)) {
						variables.add(ctField);
					} else if (ctField.hasModifier(ModifierKind.PRIVATE)) {
						if (expression.hasParent(type)) {
							variables.add(ctField);
						}
					} else if (expression.getParent(CtPackage.class).equals(type.getParent(CtPackage.class))) {
						// default visibility
						variables.add(ctField);
					}
				}
				CtTypeReference<?> superclass = type.getSuperclass();
				if (superclass != null) {
					variables.addAll(getVariable(superclass.getTypeDeclaration()));
				}
				Set<CtTypeReference<?>> superInterfaces = type.getSuperInterfaces();
				for (CtTypeReference<?> typeReference : superInterfaces) {
					variables.addAll(getVariable(typeReference.getTypeDeclaration()));
				}
				super.scanCtType(type);
			}

			@Override
			public void visitCtTryWithResource(CtTryWithResource e) {
				variables.addAll(e.getResources());
				super.visitCtTryWithResource(e);
			}

			@Override
			public void scanCtExecutable(CtExecutable e) {
				variables.addAll(e.getParameters());
				super.scanCtExecutable(e);
			}

			@Override
			public void visitCtFor(CtFor e) {
				for (CtStatement ctStatement : e.getForInit()) {
					this.scan(ctStatement);
				}
				super.visitCtFor(e);
			}

			@Override
			public void visitCtForEach(CtForEach e) {
				variables.add(e.getVariable());
				super.visitCtForEach(e);
			}

			@Override
			public void visitCtMethod(CtMethod e) {
				this.scan(e.getBody());
				super.visitCtMethod(e);
			}

			@Override
			public void visitCtLocalVariable(CtLocalVariable e) {
				variables.add(e);
				super.visitCtLocalVariable(e);
			}

			@Override
			public void visitCtCatch(CtCatch e) {
				variables.add(e.getParameter());

				super.visitCtCatch(e);
			}
		}

		new VariableScanner().scan(parent);

		return variables;
	}
}
