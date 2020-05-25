/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import spoon.processing.Processor;
import spoon.processing.TraversalStrategy;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;

/**
 * This visitor implements the code processing engine.
 */
public class ProcessingVisitor extends CtScanner {

	Factory factory;

	Processor<?> processor;

	/**
	 * The constructor.
	 */
	public ProcessingVisitor(Factory factory) {
		this.factory = factory;
	}

	private boolean canBeProcessed(CtElement e) {
		if (!factory.getEnvironment().isProcessingStopped()
				&& processor.getProcessedElementTypes() != null) {
			for (Object o : processor.getProcessedElementTypes()) {
				if (!((Class<?>) o).isAssignableFrom(e.getClass())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public Processor<?> getProcessor() {
		return processor;
	}

	/**
	 * Applies the processing to the given element. To apply the processing,
	 * this method upcalls, for all the registered processor in, the
	 * {@link Processor#process(CtElement)} method if
	 * {@link Processor#isToBeProcessed(CtElement)} returns true.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void scan(CtElement e) {
		if (e == null) {
			return;
		}
		Processor<CtElement> p = (Processor<CtElement>) processor;
		if (p.getTraversalStrategy() == TraversalStrategy.PRE_ORDER
				&& canBeProcessed(e)) {
			if (p.isToBeProcessed(e)) {
				p.process(e);
			}
		}
		super.scan(e);
		if (p.getTraversalStrategy() == TraversalStrategy.POST_ORDER
				&& canBeProcessed(e)) {
			if (p.isToBeProcessed(e)) {
				p.process(e);
			}
		}
	}

	public void setProcessor(Processor<?> processor) {
		this.processor = processor;
	}
}
