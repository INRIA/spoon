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
