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
package spoon.support;

import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessInterruption;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.visitor.ProcessingVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This processing manager implements a blocking processing policy that consists
 * of applying the processors in a FIFO order until no processors remain to be
 * applied.The processors will be removed from the manager once applied.
 */
public class QueueProcessingManager implements ProcessingManager {
	Processor<?> current;

	Factory factory;

	Queue<Processor<?>> processors;

	ProcessingVisitor visitor;

	/**
	 * Creates a new processing manager that maintains a queue of processors to
	 * be applied to a given factory.
	 *
	 * @param factory
	 * 		the factory on which the processing applies (contains the
	 * 		meta-model)
	 */
	public QueueProcessingManager(Factory factory) {
		super();
		setFactory(factory);
	}

	public void addProcessor(Class<? extends Processor<?>> type) {
		try {
			Processor<?> p = type.newInstance();
			addProcessor(p);
		} catch (Exception e) {
			throw new SpoonException("Unable to instantiate processor \"" + type.getName() + "\" - Your processor should have a constructor with no arguments", e);
		}
	}

	public boolean addProcessor(Processor<?> p) {
		p.setFactory(getFactory());
		return getProcessors().add(p);
	}

	@SuppressWarnings("unchecked")
	public void addProcessor(String qualifiedName) {
		try {
			addProcessor((Class<? extends Processor<?>>) getFactory().getEnvironment().getClassLoader().loadClass(qualifiedName));
		} catch (ClassNotFoundException e) {
			throw new SpoonException("Unable to load processor \"" + qualifiedName + "\" - Check your classpath.", e);
		}
	}

	public Processor<?> getCurrentProcessor() {
		return current;
	}

	public Factory getFactory() {
		return factory;
	}

	public Queue<Processor<?>> getProcessors() {
		if (processors == null) {
			processors = new LinkedList<>();
		}
		return processors;
	}

	protected ProcessingVisitor getVisitor() {
		if (visitor == null) {
			visitor = new ProcessingVisitor(getFactory());
		}
		return visitor;
	}

	public void process(Collection<? extends CtElement> elements) {
		Processor<?> p;
		while ((p = getProcessors().poll()) != null) {
			try {
				getFactory().getEnvironment().reportProgressMessage(p.getClass().getName());
				current = p;
				p.initProperties(AbstractProcessor.loadProperties(p));
				p.init();
				p.process();
				for (CtElement e : new ArrayList<>(elements)) {
					process(e, p);
				}
			} catch (ProcessInterruption ignore) {
			} finally {
				p.processingDone();
			}
		}
	}

	public void process(CtElement element) {
		Processor<?> p;
		while ((p = getProcessors().poll()) != null) {
			try {
				current = p;
				p.init();
				p.process();
				process(element, p);
			} catch (ProcessInterruption ignore) {
			} finally {
				p.processingDone();
			}
		}
	}

	protected void process(CtElement element, Processor<?> processor) {
		getVisitor().setProcessor(processor);
		getVisitor().scan(element);
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
		factory.getEnvironment().setManager(this);
	}



}
