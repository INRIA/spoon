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
package spoon.test.main;

import org.junit.jupiter.api.Test;
import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {

	@Test
	public void testGenericContract() throws IOException {
		// contract: all generic contracts hold on a subset of classes
		Launcher launcher;
		CtPackage rootPackage;

		launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(11);

		// there are still some bugs with comments
		launcher.getEnvironment().setCommentEnabled(false);

		try (Stream<Path> stream = Files.walk(Paths.get("src/test/java"))) {
			stream.filter(path -> path.toAbsolutePath().toString().contains("testclasses")
							&& Files.isRegularFile(path) // only Java files, not directory
					)

					// by using testclasses, we find a lot of bugs
					// I propose to put them under the carpet first (aka carpet debugging)
					// in order to make progress on this important blocking first refactoring

					// bug 1: those two classes together trigger a bug somewhere in inner class
					.filter(path -> !filePathContains(path, "fieldaccesses/testclasses/Tacos")) // carpet debugging
					.filter(path -> !filePathContains(path, "reference/testclasses/Stream"))

					// bug 2: remove the filter to trigger it
					.filter(path -> !filePathContains(path, "MethodeWithNonAccessibleTypeArgument"))

					.map(Path::toString)
					.forEach(launcher::addInputResource);
		}

		launcher.buildModel();

		rootPackage = launcher.getFactory().Package().getRootPackage();

		// we verify all the contracts
		new ContractVerifier(rootPackage).verify();
	}
	
	private boolean filePathContains(Path path, String substring) {
		//normalize path separators to linux, to simplify searching for substring
		return path.toFile().getAbsolutePath().replace('\\', '/').contains(substring);
	}


	@Test
	public void testTest() {
		// the tests should be spoonable
		Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "src/test/java",
				"-o", "target/spooned",
				"--disable-comments",
				"--compliance", "8",
				"--level", "OFF"
		});


		new ContractVerifier(launcher.getFactory().Package().getRootPackage()).checkGenericContracts();

		// contract: all test classes, as well as template/processors/etc used in tests have "test" in their fully qualified name
		// if one analyzes src/main/java and src/test/java at the same time
		// this helps a lot to easily automatically differentiate app classes and test classes
		for (CtType t : launcher.getFactory().getModel().getAllTypes()) {
			if ("spoon.metamodel".equals(t.getPackage().getQualifiedName())
					|| t.getPackage().getQualifiedName().startsWith("spoon.generating")
					|| t.getPackage().getQualifiedName().startsWith("spoon.support.util.compilation")) {
				//Meta model classes doesn't have to follow test class naming conventions
				continue;
			}
			assertTrue(t.getQualifiedName().matches("(?i:.*test.*)"), t.getQualifiedName() + " is not clearly a test class, it should contain 'test' either in its package name or class name");
		}
	}

	@Test
	public void testResourcesCopiedInTargetDirectory() {
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		spoon.Launcher.main(new String[] {
				"-i", "src/test/resources/no-copy-resources/",
				"-o", "target/spooned-with-resources",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath, "--compile" });

		assertTrue(new File("src/test/resources/no-copy-resources/package.html").exists());
		assertTrue(new File("target/spooned-with-resources/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/package.html").exists());
		assertTrue(new File("target/spooned-with-resources/fr/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/inria/package.html").exists());
		assertTrue(new File("target/spooned-with-resources/fr/inria/package.html").exists());
	}

	@Test
	public void testResourcesNotCopiedInTargetDirectory() {
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		spoon.Launcher.main(new String[] {
				"-i", "src/test/resources/no-copy-resources",
				"-o", "target/spooned-without-resources",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath, "--compile",
				"-r" });

		assertTrue(new File("src/test/resources/no-copy-resources/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/fr/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/inria/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/fr/inria/package.html").exists());
	}
}
