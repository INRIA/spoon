/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.util.List;

/**
 * This class represents a template parameter that defines a statement list
 * directly expressed in Java (no returns).
 *
 * <p>
 * To define a new statement list template parameter, you must subclass this
 * class and implement the {@link #statement()} method, which actually defines
 * the Java statements. It corresponds to a
 * {@link spoon.reflect.code.CtStatementList}.
 */
public abstract class StatementTemplate extends AbstractTemplate<CtStatement> {

	/**
	 * Creates a new statement list template parameter.
	 */
	public StatementTemplate() {
	}

	@Override
	public CtStatement apply(CtType<?> targetType) {
		CtClass<?> c = Substitution.getTemplateCtClass(targetType, this);
		// we substitute the first statement of method statement
		CtStatement patternModel = c.getMethod("statement").getBody().getStatements().get(0);
		List<CtStatement> statements = TemplateBuilder.createPattern(patternModel, this)
				.setAddGeneratedBy(isAddGeneratedBy())
				.substituteList(c.getFactory(), targetType, CtStatement.class);
		if (statements.size() != 1) {
			throw new IllegalStateException();
		}
		return statements.get(0);
	}

	public Void S() {
		return null;
	}

	/**
	 * This method must be implemented to define the template statement list.
	 */
	public abstract void statement() throws Throwable;
}
