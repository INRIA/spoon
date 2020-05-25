/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import org.apache.logging.log4j.Level;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.testing.utils.ProcessorUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * This class defines an abstract processor to be subclassed by the user for
 * defining new processors.
 */
public abstract class AbstractProcessor<E extends CtElement> implements Processor<E> {

	Factory factory;

	Set<Class<? extends CtElement>> processedElementTypes = new HashSet<>();

	/**
	 * Empty constructor only for all processors (invoked by Spoon).
	 */
	@SuppressWarnings("unchecked")
	public AbstractProcessor() {
		for (Method m : getClass().getMethods()) {
			if ("process".equals(m.getName()) && (m.getParameterTypes().length == 1)) {
				Class<?> c = m.getParameterTypes()[0];
				if (CtElement.class != c) {
					addProcessedElementType((Class<E>) c);
				}
			}
		}
		if (processedElementTypes.isEmpty()) {
			addProcessedElementType(CtElement.class);
		}
	}

	/**
	 * Adds a processed element type. This method is typically invoked in
	 * subclasses' constructors.
	 */
	protected void addProcessedElementType(Class<? extends CtElement> elementType) {
		processedElementTypes.add(elementType);
	}

	/**
	 * Clears the processed element types.
	 */
	protected void clearProcessedElementType() {
		processedElementTypes.clear();
	}

	@Override
	public Environment getEnvironment() {
		return getFactory().getEnvironment();
	}

	@Override
	public Factory getFactory() {
		return this.factory;
	}

	@Override
	public Set<Class<? extends CtElement>> getProcessedElementTypes() {
		return processedElementTypes;
	}

	/**
	 * Helper method to load the properties of the given processor (uses
	 * {@link Environment#getProcessorProperties(String)}).
	 */
	public ProcessorProperties loadProperties() {
		Processor<?> p = this;
		ProcessorProperties props = null;
		try {
			props = p.getFactory().getEnvironment().getProcessorProperties(p.getClass().getName());
		} catch (Exception e) {
			p.getFactory().getEnvironment().report(p, Level.ERROR,
					"unable to get properties for processor '" + p.getClass().getName() + "': " + e.getMessage());
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return props;
	}

	@Override
	public TraversalStrategy getTraversalStrategy() {
		return TraversalStrategy.POST_ORDER;
	}

	@Override
	public void init() {
		this.initProperties(loadProperties());
	}

	@Override
	public boolean isToBeProcessed(E candidate) {
		return true;
	}

	/**
	 * Helper method to initialize the properties of a given processor.
	 */
	@Override
	public void initProperties(ProcessorProperties properties) {
		ProcessorUtils.initProperties(this, properties);
	}

	/**
	 * The manual meta-model processing cannot be overridden (use
	 * {@link AbstractManualProcessor}) to do so.
	 */
	@Override
	public final void process() {
	}

	@Override
	public void processingDone() {
		// do nothing by default
	}

	/**
	 * Removes a processed element type.
	 */
	protected void removeProcessedElementType(Class<? extends CtElement> elementType) {
		processedElementTypes.remove(elementType);
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	@Override
	public void interrupt() {
		throw new ProcessInterruption();
	}
}
