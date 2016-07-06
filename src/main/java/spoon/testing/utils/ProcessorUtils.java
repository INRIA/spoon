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
package spoon.testing.utils;

import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.util.Collection;

public final class ProcessorUtils {
	private ProcessorUtils() {
		throw new AssertionError();
	}

	public static void process(Factory factory, Collection<Processor<?>> processors) {
		final JDTBasedSpoonCompiler compiler = (JDTBasedSpoonCompiler) new Launcher().createCompiler(factory);
		compiler.process(processors);
	}
}
