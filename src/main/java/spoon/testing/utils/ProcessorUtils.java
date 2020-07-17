/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.testing.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.processing.Property;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.util.RtHelper;

import java.lang.reflect.Field;
import java.util.Collection;

public final class ProcessorUtils {
	private static final ObjectMapper converter = new ObjectMapper();

	private ProcessorUtils() {
		throw new AssertionError();
	}

	public static void process(Factory factory, Collection<Processor<?>> processors) {
		final JDTBasedSpoonCompiler compiler = (JDTBasedSpoonCompiler) new Launcher().createCompiler(factory);
		compiler.process(processors);
	}

	/** sets the fields of processor "p" given as parameter according to the properties */
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
							throw new SpoonException(e);
						}
					} else {
						obj = properties.get(String.class, f.getName());
						if (obj != null) {
							try {
								obj = converter.readValue((String) obj, f.getType());
								f.setAccessible(true);
								f.set(p, obj);
							} catch (Exception e) {
								throw new SpoonException("Error while assigning the value to " + f.getName(), e);
							}
						} else {
								if (f.getAnnotation(Property.class).notNullable()) {
										throw new SpoonException("No value found for property '" + f.getName()
																							+ "' in processor " + p.getClass().getName());
							}
						}
					}
				}
			}
		}
	}
}
