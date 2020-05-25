/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

/**
 * This class represents a template parameter that defines a block statement
 * directly expressed in Java (must return an expression of type <code>R</code>
 * ).
 *
 * <p>
 * To define a new block template parameter, you must subclass this class and
 * implement the {@link #block()} method, which actually defines the Java block.
 * It corresponds to a {@link spoon.reflect.code.CtBlock}.
 */
public abstract class TypedBlockTemplateParameter<R> implements TemplateParameter<R> {

	/**
	 * Creates a new block template parameter.
	 */
	public TypedBlockTemplateParameter() {
	}

	/**
	 * This method must be implemented to define the template block.
	 */
	public abstract R block();

	@SuppressWarnings("unchecked")
	public CtBlock<R> getSubstitution(CtType<?> targetType) {
		CtClass<?> c;
		c = targetType.getFactory().Class().get(this.getClass());
		if (c == null) {
			c = targetType.getFactory().Class().get(this.getClass());
		}
		CtMethod m = c.getMethod("block");
		if (this instanceof Template) {
			return Substitution.substitute(targetType, (Template<?>) this, m.getBody());
		}
		return m.getBody().clone();
	}

	@Override
	public R S() {
		return null;
	}
}
