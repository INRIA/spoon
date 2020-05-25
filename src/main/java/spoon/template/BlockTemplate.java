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
		return p.getMethod("block").getBody();
	}

	/**
	 * Creates a new block template parameter.
	 */
	public BlockTemplate() {
	}

	@Override
	public CtBlock<?> apply(CtType<?> targetType) {
		CtClass<? extends BlockTemplate> c = Substitution.getTemplateCtClass(targetType, this);
		return TemplateBuilder.createPattern(getBlock(c), this).setAddGeneratedBy(isAddGeneratedBy()).substituteSingle(targetType, CtBlock.class);
	}

	public Void S() {
		return null;
	}

	/**
	 * This method must be implemented to define the template block.
	 */
	public abstract void block() throws Throwable;
}
