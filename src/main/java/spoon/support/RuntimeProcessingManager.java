/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;

import org.apache.logging.log4j.Level;
import spoon.processing.ProcessInterruption;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.support.visitor.ProcessingVisitor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This processing manager implements a blocking processing policy that consists
 * of applying the processors in a FIFO order until no processors remain to be
 * applied.
 */
public class RuntimeProcessingManager implements ProcessingManager {
	Processor<?> current;

	Factory factory;

	List<Processor<?>> processors;

	ProcessingVisitor visitor;

	/**
	 * Creates a new processing manager that maintains a queue of processors to
	 * be applied to a given factory.
	 *
	 * @param factory
	 * 		the factory on which the processing applies (contains the
	 * 		meta-model)
	 */
	public RuntimeProcessingManager(Factory factory) {
		setFactory(factory);
	}

	@Override
	public void addProcessor(Class<? extends Processor<?>> type) {
		try {
			Processor<?> p = type.newInstance();
			p.setFactory(factory);
			p.init();
			addProcessor(p);
		} catch (Exception e) {
			factory.getEnvironment().report(null, Level.ERROR, "Unable to instantiate processor \"" + type.getName() + "\" - Your processor should have a constructor with no arguments");
		}
	}

	@Override
	public boolean addProcessor(Processor<?> p) {
		p.setFactory(getFactory());
		return getProcessors().add(p);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addProcessor(String qualifiedName) {
		try {
			addProcessor((Class<? extends Processor<?>>) getFactory().getEnvironment().getInputClassLoader().loadClass(qualifiedName));
		} catch (ClassNotFoundException e) {
			factory.getEnvironment().report(null, Level.ERROR, "Unable to load processor \"" + qualifiedName + "\" - Check your classpath.");
		}
	}

	public Processor<?> getCurrentProcessor() {
		return current;
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public List<Processor<?>> getProcessors() {
		if (processors == null) {
			processors = new LinkedList<>();
		}
		return processors;
	}

	private ProcessingVisitor getVisitor() {
		if (visitor == null) {
			visitor = new ProcessingVisitor(getFactory());
		}
		return visitor;
	}

	@Override
	public void process(Collection<? extends CtElement> elements) {
		for (Processor<?> p : getProcessors()) {
			current = p;
			process(elements, p);
		}
	}

	/**
	 * Recursively processes elements and their children with a given processor.
	 */
	public void process(Collection<? extends CtElement> elements, Processor<?> processor) {
		try {
			getFactory().getEnvironment().debugMessage("processing with '" + processor.getClass().getName() + "'...");
			current = processor;
			for (CtElement e : elements) {
				process(e, processor);
			}
		} catch (ProcessInterruption ignored) {
		}
	}

	@Override
	public void process(CtElement element) {
		for (Processor<?> p : getProcessors()) {
			current = p;
			process(element, p);
		}
	}

	/**
	 * Recursively processes an element and its children with a given processor.
	 */
	public void process(CtElement element, Processor<?> processor) {
		getFactory().getEnvironment().debugMessage(
				"processing '" + ((element instanceof CtNamedElement) ? ((CtNamedElement) element).getSimpleName() : element.toString()) + "' with '" + processor.getClass().getName() + "'...");
		processor.init();
		getVisitor().setProcessor(processor);
		getVisitor().scan(element);
		processor.processingDone();
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
		factory.getEnvironment().setManager(this);
	}


}
