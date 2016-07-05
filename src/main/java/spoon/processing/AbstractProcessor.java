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

import org.apache.log4j.Level;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.util.RtHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
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
		super();
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

	public Environment getEnvironment() {
		return getFactory().getEnvironment();
	}

	public Factory getFactory() {
		return this.factory;
	}

	public Set<Class<? extends CtElement>> getProcessedElementTypes() {
		return processedElementTypes;
	}

	/**
	 * Helper method to load the properties of the given processor (uses
	 * {@link Environment#getProcessorProperties(String)}).
	 */
	public static ProcessorProperties loadProperties(Processor<?> p) {
		ProcessorProperties props = null;
		try {
			props = p.getFactory().getEnvironment().getProcessorProperties(p.getClass().getName());
		} catch (FileNotFoundException e) {
			p.getFactory().getEnvironment()
					.debugMessage("property file not found for processor '" + p.getClass().getName() + "'");
		} catch (IOException e) {
			p.getFactory().getEnvironment().report(p, Level.ERROR,
					"wrong properties file format for processor '" + p.getClass().getName() + "'");
			Launcher.LOGGER.error(e.getMessage(), e);
		} catch (Exception e) {
			p.getFactory().getEnvironment().report(p, Level.ERROR,
					"unable to get properties for processor '" + p.getClass().getName() + "': " + e.getMessage());
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return props;
	}

	public TraversalStrategy getTraversalStrategy() {
		return TraversalStrategy.POST_ORDER;
	}

	public void init() {
		// loadProperties();
	}

	public final void initProperties(ProcessorProperties properties) {
		initProperties(this, properties);
	}

	public boolean isToBeProcessed(E candidate) {
		return true;
	}

	/**
	 * Helper method to initialize the properties of a given processor.
	 */
	public static void initProperties(Processor<?> p, ProcessorProperties properties) {
		if (properties != null) {
			for (Field f : RtHelper.getAllFields(p.getClass())) {
				if (f.isAnnotationPresent(Property.class)) {
					Object obj = properties.get(f.getType(), f.getName());
					if (obj != null) {
						f.setAccessible(true);
						try {
							f.set(p, obj);
						} catch (Exception e) {
							Launcher.LOGGER.error(e.getMessage(), e);
						}
					} else {
						p.getFactory().getEnvironment().report(p, Level.WARN,
								"No value found for property '" + f.getName() + "' in processor " + p.getClass()
										.getName());
					}
				}
			}
		}
	}

	/**
	 * The manual meta-model processing cannot be overridden (use
	 * {@link AbstractManualProcessor}) to do so.
	 */
	public final void process() {
	}

	public void processingDone() {
		// do nothing by default
	}

	/**
	 * Removes a processed element type.
	 */
	protected void removeProcessedElementType(Class<? extends CtElement> elementType) {
		processedElementTypes.remove(elementType);
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	@Override
	public void interrupt() {
		throw new ProcessInterruption();
	}
}
