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
package spoon.processing;

import java.util.Collection;

import spoon.reflect.declaration.CtElement;

/**
 * The processing manager defines the API to process a program model of a given
 * {@link spoon.reflect.factory.Factory} with a set of processors. The program model has
 * been previously built using a {@link spoon.compiler.SpoonCompiler} - see
 * {@link spoon.compiler.SpoonCompiler#build()}. To use, add processors to
 * the manager, and then call the {@code process} method. Also, the method
 * {@link spoon.processing.Processor#processingDone()} is up called.
 *
 * @see spoon.compiler.Environment#getManager()
 */
public interface ProcessingManager extends FactoryAccessor {

	/**
	 * Adds a processor by instantiating its type (a class that must define an
	 * empty constructor).
	 *
	 * @see #getProcessors()
	 */
	void addProcessor(Class<? extends Processor<?>> type);

	/**
	 * Adds a processor.
	 *
	 * @see #getProcessors()
	 */
	boolean addProcessor(Processor<?> p);

	/**
	 * Adds a processor by instantiating its type (a class that must define an
	 * empty constructor and implement {@link Processor}).
	 *
	 * @param qualifiedName
	 *            the qualified name of the processor's type
	 * @see #getProcessors()
	 */
	void addProcessor(String qualifiedName);

	/**
	 * Gets the processors that have been added to the manager and that will be
	 * applied when invoking one of the {@code process} methods).
	 *
	 * @see #process(Collection)
	 */
	Collection<Processor<?>> getProcessors();

	/**
	 * Recursively processes a collection of {@link CtElement}s with this
	 * manager. All the processors added to this manager (see
	 * {@link #getProcessors()}) should be applied before the method returns
	 * (blocking implementation) or before another call to a
	 * <code>process</code> method (non-blocking implementation). Processors
	 * that have been applied are removed from the manager and
	 * {@link #getProcessors()} does not contain them anymore.
	 */
	void process(Collection<? extends CtElement> elements);

	/**
	 * Recursively processes a {@link CtElement} with this manager. All the
	 * processors added to this manager (see {@link #getProcessors()}) should
	 * be applied before the method returns (blocking implementation) or before
	 * another call to a <code>process</code> method (non-blocking
	 * implementation). Processors that have been applied are removed from the
	 * manager and {@link #getProcessors()} does not contain them anymore.
	 */
	void process(CtElement element);

}
