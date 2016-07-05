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

import spoon.compiler.Environment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

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

	public Environment getEnvironment() {
		return getFactory().getEnvironment();
	}

	public final Factory getFactory() {
		return this.factory;
	}

	/**
	 * Invalid method in this context.
	 */
	public final Set<Class<? extends CtElement>> getProcessedElementTypes() {
		return null;
	}

	/**
	 * Invalid method in this context.
	 */
	public final TraversalStrategy getTraversalStrategy() {
		return TraversalStrategy.POST_ORDER;
	}

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
	public final boolean isToBeProcessed(CtElement candidate) {
		return false;
	}

	/**
	 * Does nothing in this context.
	 */
	public final void process(CtElement element) {
	}

	public void processingDone() {
		// do nothing by default
	}

	public final void setFactory(Factory factory) {
		this.factory = factory;
	}

	public final void initProperties(ProcessorProperties properties) {
		AbstractProcessor.initProperties(this, properties);
	}

	@Override
	public void interrupt() {
		throw new ProcessInterruption();
	}
}
