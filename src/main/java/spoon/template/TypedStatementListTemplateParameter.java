/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * This class represents a template parameter that defines a statement list
 * directly expressed in Java (the statement list ends with a return statement
 * returning a expression of type <code>R</code>).
 *
 * <p>
 * To define a new statement list template parameter, you must subclass this
 * class and implement the {@link #statements()} method, which actually defines
 * the Java statements. It corresponds to a
 * {@link spoon.reflect.code.CtStatementList}.
 */
public abstract class TypedStatementListTemplateParameter<R> implements TemplateParameter<R> {

	/**
	 * Creates a new statement list template parameter.
	 */
	public TypedStatementListTemplateParameter() {
	}

	public CtStatementList getSubstitution(CtType<?> targetType) {
		CtClass<?> c;
		CtBlock<?> b;
		c = targetType.getFactory().Class().get(this.getClass());
		if (c == null) {
			c = targetType.getFactory().Class().get(this.getClass());
		}
		CtStatementList l = targetType.getFactory().Core().createStatementList();
		if (this instanceof Template) {
			b = Substitution.substitute(targetType, (Template<?>) this, c.getMethod("statements").getBody());
		} else {
			b = c.getMethod("statements").getBody().clone();
		}
		l.setStatements(b.getStatements());
		return l;
	}

	@Override
	public R S() {
		return null;
	}

	/**
	 * This method must be implemented to define the template statement list.
	 */
	public abstract R statements();
}
