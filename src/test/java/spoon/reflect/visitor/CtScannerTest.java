/*
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

package spoon.reflect.visitor;

import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.generating.GeneratingTypeProcessor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.processors.CheckScannerProcessor;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CtScannerTest {
	@Test
	public void testScannerContract() throws Exception {
		// contract: CtScanner must call enter and exit methods in each visit methods.
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addProcessor(new CheckScannerProcessor());
		launcher.setSourceOutputDirectory("./target/trash");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.run();

		// All assertions are in the processor.
	}

	@Test
	public void testName2() throws Exception {
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		Launcher launcher = new Launcher();

		launcher.addInputResource("src/main/java");
		launcher.setSourceOutputDirectory("target/spooned");
		launcher.getFactory().getEnvironment().setSourceClasspath(systemClassPath.split(File.pathSeparator));
		launcher.addProcessor(new MyProcessor());
		launcher.run();

	}

	class MyProcessor extends AbstractProcessor<CtElement> {
		@Override
		public void process(CtElement element) {
			//ReplacementVisitor.replace(element, getFactory().Core().clone(element));
		}
	}

	@Test
	@Ignore
	public void testName() throws Exception {
		class RegexFilter implements Filter<CtType<?>> {
			private final Pattern regex;

			private RegexFilter(String regex) {
				if (regex == null) {
					throw new IllegalArgumentException();
				}
				this.regex = Pattern.compile(regex);
			}

			public boolean matches(CtType<?> element) {
				Matcher m = regex.matcher(element.getQualifiedName());
				return m.matches();
			}

			public Class<CtElement> getType() {
				return CtElement.class;
			}
		}

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setGenerateJavadoc(true);
		launcher.getEnvironment().useTabulations(true);
		launcher.setSourceOutputDirectory("./target/generated/");
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// Utils.
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.addInputResource("./src/main/java/spoon/generating/replace/");
		launcher.addProcessor(new GeneratingTypeProcessor());
		launcher.setOutputFilter(new RegexFilter("spoon.support.visitor.replace.*"));
		launcher.run();
	}
}
