/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.visitor;

import java.util.ArrayList;
import java.util.Collection;

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

	private boolean canBeProcessed(Processor<?> p, CtElement e) {
		if (!factory.getEnvironment().isProcessingStopped()
				&& p.getProcessedElementTypes() != null) {
			for (Object o : p.getProcessedElementTypes()) {
				if (((Class<?>) o).isAssignableFrom(e.getClass())) {
					return true;
				}
			}
		}
		return false;
	}

	public Processor<?> getProcessor() {
		return processor;
	}

	@Override
	public void scan(Collection<? extends CtElement> elements) {
		if ((elements != null)) {
			for (CtElement e : new ArrayList<CtElement>(elements)) {
				scan(e);
			}
		}
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
				&& canBeProcessed(p, e)) {
			if (p.isToBeProcessed(e)) {
				p.process(e);
			}
		}
		super.scan(e);
		if (p.getTraversalStrategy() == TraversalStrategy.POST_ORDER
				&& canBeProcessed(p, e)) {
			if (p.isToBeProcessed(e)) {
				p.process(e);
			}
		}
	}

	public void setProcessor(Processor<?> processor) {
		this.processor = processor;
	}
}
