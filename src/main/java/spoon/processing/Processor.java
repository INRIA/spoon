/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import spoon.compiler.Environment;
import spoon.reflect.declaration.CtElement;

import java.util.Set;

/**
 * This interface defines a generic code processor. To define a new processor,
 * the user should subclass {@link spoon.processing.AbstractProcessor}, the
 * abstract default implementation of this interface.
 *
 * If a processor contains fields annotated with @{@link Property}, they can be set using a {@link ProcessorProperties}
 */
public interface Processor<E extends CtElement> extends FactoryAccessor {

	/**
	 * Gets the model's traversal strategy for this processor (default is
	 * {@link TraversalStrategy#POST_ORDER}). Programmers should override this
	 * method to return another strategy if needed.
	 */
	TraversalStrategy getTraversalStrategy();

	/**
	 * Gets the environment of this processor.
	 */
	Environment getEnvironment();

	/**
	 * Tells if this element is to be processed (returns <code>true</code> in
	 * the default implementation).
	 *
	 * @param candidate
	 * 		the candidate
	 * @return true if the candidate is to be processed by the
	 * {@link #process(CtElement)}
	 */
	boolean isToBeProcessed(E candidate);

	/**
	 * A callback method upcalled by the meta-model scanner to perform a
	 * dedicated job on the currently scanned element. The way Spoon upcalls
	 * this method depends on the processed element types (
	 * {@link #getProcessedElementTypes()}), the traversal strategy (
	 * {@link #getTraversalStrategy()}), and the used processing manager (
	 * {@link Environment#getManager()}. Also, this method is upcalled only if
	 * the method {@link #isToBeProcessed(CtElement)} returns true for a given
	 * scanned element. In order to manually scan the meta-model, one can define
	 * the {@link #process()} method instead.
	 *
	 * @param element
	 * 		the element that is currently being scanned
	 */
	void process(E element);

	/**
	 * A callback method upcalled by the manager so that this processor can
	 * manually implement a processing job. On contrary to
	 * {@link #process(CtElement)}, this method does not rely on a built-in
	 * meta-model scanner and has to implement its own traversal strategy on the
	 * meta-model, which is stored in the factory (
	 * {@link FactoryAccessor#getFactory}). Note that if a processor implements
	 * both process methods, this one is upcalled first. This method does
	 * nothing in default implementations (
	 * {@link spoon.processing.AbstractProcessor}).
	 */
	void process();

	/**
	 * Do the processing job for a given element. This method is upcalled on an
	 * element if the method {@link #isToBeProcessed(CtElement)} returns true.
	 *
	 * @param element
	 *            the element that holds the processed annotations
	 */

	/**
	 * Gets all the element types than need to be processed.
	 */
	Set<Class<? extends CtElement>> getProcessedElementTypes();

	/**
	 * This method is upcalled by the {@link ProcessingManager} when this
	 * processor has finished a full processing round on the program's model. It
	 * is convenient to override this method to tune the application's strategy
	 * of a set of processors, for instance by dynamically adding processors to
	 * the processing manager when a processing round ends (see
	 * {@link ProcessingManager#addProcessor(Class)}). Does nothing by default.
	 */
	void processingDone();

	/**
	 * This method is upcalled to initialize the processor before each
	 * processing round. It is convenient to override this method rather than
	 * using a default constructor to initialize the processor, since the
	 * factory is not initialized at construction time. When overriding, do not
	 * forget to call super.init() first so that all the initializations
	 * performed by superclasses are also applied.
	 */
	void init();

	/**
	 * Initializes the properties defined by this processor by using the
	 * environment.
	 *
	 * @see Environment#getProcessorProperties(String)
	 */
	void initProperties(ProcessorProperties properties);

	/**
	 * Interrupts the processing of this processor but changes on your AST are kept
	 * and the invocation of this method doesn't interrupt the processing of all
	 * processors specified in the {@link ProcessingManager}.
	 */
	void interrupt();
}
