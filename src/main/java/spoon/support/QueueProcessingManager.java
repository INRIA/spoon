/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.processing.ProcessInterruption;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.SpoonProgress;
import spoon.support.visitor.ProcessingVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This processing manager applies the processors one by one from the given root element.
 * for p : processors
 *   p.process(el)
 * Default processor in Spoon
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
		setFactory(factory);
	}

	@Override
	public void addProcessor(Class<? extends Processor<?>> type) {
		try {
			Processor<?> p = type.newInstance();
			addProcessor(p);
		} catch (Exception e) {
			throw new SpoonException("Unable to instantiate processor \"" + type.getName() + "\" - Your processor should have a constructor with no arguments", e);
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
			throw new SpoonException("Unable to load processor \"" + qualifiedName + "\" - Check your classpath.", e);
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

	@Override
	public void process(Collection<? extends CtElement> elements) {
		Processor<?> p;
		// copy so that one can reuse the processing manager
		// among different processing steps
		Queue<Processor<?>> processors = new LinkedList<>(getProcessors());
		if (factory.getEnvironment().getSpoonProgress() != null) {
			factory.getEnvironment().getSpoonProgress().start(SpoonProgress.Process.PROCESS);
		}
		int i = 0;
		while ((p = processors.poll()) != null) {
			try {
				getFactory().getEnvironment().reportProgressMessage(p.getClass().getName());
				current = p;
				p.init(); // load the properties
				p.process();
				for (CtElement e : new ArrayList<>(elements)) {
					getVisitor().setProcessor(p);
					getVisitor().scan(e);
				}
			} catch (ProcessInterruption ignore) {
			} finally {
				p.processingDone();
				if (factory.getEnvironment().getSpoonProgress() != null) {
					factory.getEnvironment().getSpoonProgress().step(SpoonProgress.Process.PROCESS, p.getClass().getName(), ++i, getProcessors().size());
				}
			}
		}
		if (factory.getEnvironment().getSpoonProgress() != null) {
			factory.getEnvironment().getSpoonProgress().end(SpoonProgress.Process.PROCESS);
		}
	}

	@Override
	public void process(CtElement element) {
		List<CtElement> l = new ArrayList<>();
		l.add(element);
		process(l);
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
		factory.getEnvironment().setManager(this);
	}

}
