/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * This class represents a template parameter that defines a void block
 * statement directly expressed in Java (no returns).
 *
 *
 * <p>
 * To define a new block template parameter, you must subclass this class and
 * implement the {@link #block()} method, which actually defines the Java block.
 * It corresponds to a {@link spoon.reflect.code.CtBlock}.
 */
public abstract class BlockTemplate extends AbstractTemplate<CtBlock<?>> {

	/**
	 * Returns the block.
	 */
	public static CtBlock<?> getBlock(CtClass<? extends BlockTemplate> p) {
		CtBlock<?> b = p.getMethod("block").getBody();
		return b;
	}

	/**
	 * Creates a new block template parameter.
	 */
	public BlockTemplate() {
	}

	public CtBlock<?> apply(CtType<?> targetType) {
		CtClass<? extends BlockTemplate> c;
		c = targetType.getFactory().Class().get(this.getClass());
		return Substitution.substitute(targetType, this, getBlock(c));
	}

	public Void S() {
		return null;
	}

	/**
	 * This method must be implemented to define the template block.
	 */
	public abstract void block() throws Throwable;
}
