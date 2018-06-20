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
package spoon.testing.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Level;
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
							p.getFactory().getEnvironment().report(p, Level.WARN,
									"No value found for property '" + f.getName() + "' in processor " + p.getClass().getName());
						}
					}
				}
			}
		}
	}
}
