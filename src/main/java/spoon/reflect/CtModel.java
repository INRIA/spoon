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
package spoon.reflect;

import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/** represents a Java program, modeled by a set of compile-time (Ct) objects
 * where each objects is a program element (for instance, a CtClass represents a class).
 */
public interface CtModel extends Serializable {

	/** returns the root package */
	CtPackage getRootPackage();

	/** returns all top-level types of the model */
	Collection<CtType<?>> getAllTypes();

	/** returns all packages of the model */
	Collection<CtPackage> getAllPackages();

	/** process this model with the given processor */
	void processWith(Processor<?> processor);

	/** Returns all the model elements matching the filter. */
	<E extends CtElement> List<E> getElements(Filter<E> filter);

}
