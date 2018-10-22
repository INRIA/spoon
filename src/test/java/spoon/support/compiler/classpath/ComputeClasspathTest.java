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
package spoon.support.compiler.classpath;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.builder.ClasspathOptions;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ComputeClasspathTest {

	private static final String TEST_CLASSPATH =
			"./src/test/java/spoon/test/annotation/"
					+ File.pathSeparator
					+ "./src/test/java/spoon/test/api/"
					+ File.pathSeparator
					+ "./src/test/java/spoon/test/arrays/"
					+ File.pathSeparator
					+ "./src/test/java/spoon/test/casts/"
					+ File.pathSeparator;

	private JDTBasedSpoonCompiler compiler;
	private Class<? extends JDTBasedSpoonCompiler> compilerClass;

	private String[] systemClasspath;

	@Before
	public void setUp() {
		Launcher launcher = new Launcher() {

			public SpoonModelBuilder createCompiler(Factory factory) {
				return new JDTBasedSpoonCompiler(factory);
			}

		};
		launcher.getEnvironment().setLevel("OFF");

		this.compiler = (JDTBasedSpoonCompiler) launcher.createCompiler();
		this.compilerClass = compiler.getClass();

		this.systemClasspath = TEST_CLASSPATH.split(File.pathSeparator);
	}

	@Test
	public void testSourceClasspath() {
		final ClasspathOptions options = new ClasspathOptions().classpath(systemClasspath);
		assertEquals("-cp " + TEST_CLASSPATH, String.join(" ", options.build()));
	}
}
