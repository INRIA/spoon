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
import spoon.reflect.factory.Factory;
import spoon.testing.utils.ProcessorUtils;

import java.util.Set;

/**
 * This class defines an abstract processor to be subclassed by the user for
 * defining new manual processors. A manual processor should override the init
 * method (called once) and scan the meta-model manually.
 */
public abstract class AbstractManualProcessor implements Processor<CtElement> {

	Factory factory;

	/**
	 * Empty constructor only for all processors (invoked by Spoon).
	 */
	public AbstractManualProcessor() {
	}

	/**
	 * Invalid method in this context.
	 */
	protected void addProcessedElementType(
			Class<? extends CtElement> elementType) {
	}

	@Override
	public Environment getEnvironment() {
		return getFactory().getEnvironment();
	}

	@Override
	public final Factory getFactory() {
		return this.factory;
	}

	/**
	 * Invalid method in this context.
	 */
	@Override
	public final Set<Class<? extends CtElement>> getProcessedElementTypes() {
		return null;
	}

	/**
	 * Invalid method in this context.
	 */
	@Override
	public final TraversalStrategy getTraversalStrategy() {
		return TraversalStrategy.POST_ORDER;
	}

	@Override
	public void init() {
	}

	/**
	 * Invalid method in this context.
	 */
	public final boolean isPrivileged() {
		return false;
	}

	/**
	 * Always returns false in this context.
	 */
	@Override
	public final boolean isToBeProcessed(CtElement candidate) {
		return false;
	}

	/**
	 * Does nothing in this context.
	 */
	@Override
	public final void process(CtElement element) {
	}

	@Override
	public void processingDone() {
		// do nothing by default
	}

	@Override
	public final void setFactory(Factory factory) {
		this.factory = factory;
	}

	@Override
	public final void initProperties(ProcessorProperties properties) {
		ProcessorUtils.initProperties(this, properties);
	}

	@Override
	public void interrupt() {
		throw new ProcessInterruption();
	}
}
