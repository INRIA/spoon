/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect;

import spoon.SpoonModelBuilder;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtQueryable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/** represents a Java program, modeled by a set of compile-time (Ct) objects
 * where each object is a program element (for instance, a CtClass represents a class).
 */
public interface CtModel extends Serializable, CtQueryable {

	/** returns the root package of the unnamed module */
	CtPackage getRootPackage();

	/** returns all top-level types of the model */
	Collection<CtType<?>> getAllTypes();

	/** returns all packages of the model */
	Collection<CtPackage> getAllPackages();

	/**
	 * Returns the unnamed module.
	 */
	CtModule getUnnamedModule();

	/**
	 * returns all modules of the model
	 */
	Collection<CtModule> getAllModules();

	/** process this model with the given processor */
	void processWith(Processor<?> processor);

	/** Returns all the model elements matching the filter. */
	<E extends CtElement> List<E> getElements(Filter<E> filter);

	/**
	 * If true, the first build model has been finished.
	 * This value should be false at the beginning of {@link SpoonModelBuilder#build()} and true at this end.
	 */
	boolean isBuildModelFinished();

	/**
	 * Set to true to flag that a build model has been finished.
	 * By default, this method is called with a true value at the end of {@link SpoonModelBuilder#build()}
	 */
	<T extends CtModel> T setBuildModelIsFinished(boolean buildModelFinished);

}
