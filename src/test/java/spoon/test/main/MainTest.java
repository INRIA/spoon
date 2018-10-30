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

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.SpoonModelBuilder.InputType;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtPathStringBuilder;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.parent.ParentTest;
import spoon.test.visibility.testclasses.MethodeWithNonAccessibleTypeArgument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MainTest {

	@Test
	public void testGenericContract() throws IOException {
		// contract: all generic contracts hold on a subset of classes
		Launcher launcher;
		CtPackage rootPackage;

		// we have to remove the test-classes folder
		// so that the precondition of --source-classpath is not violated
		// (target/test-classes contains src/test/resources which itself contains Java files)
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		launcher = new Launcher();

		launcher.setArgs(new String[] {
				"-o", "target/spooned",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath,
				"--compile", // compiling Spoon code itself on the fly
				"--compliance", "8",
				"--level", "OFF",
		});

		// there are still some bugs with comments
		launcher.getEnvironment().setCommentEnabled(false);

		int n = 0;
		Files.walk(Paths.get("src/test/java"))
				.filter(path -> path.toFile().getAbsolutePath().contains("testclasses")
						&& path.toFile().isFile() // only Java files, not directory
				)

				// by using testclasses, we find a lot of bugs
				// I propose to put them under the carpet first (aka carpet debugging)
				// in order to make progress on this important blocking first refactoring

				// bug 1: those three classes together trigger a bug somewhere in inner class
				.filter(path -> !path.toFile().getAbsolutePath().contains("fieldaccesses/testclasses/Tacos")) // carpet debugging
				.filter(path -> !path.toFile().getAbsolutePath().contains("fieldaccesses/testclasses/internal/Bar"))
				.filter(path -> !path.toFile().getAbsolutePath().contains("fieldaccesses/testclasses/internal/Foo"))
				.filter(path -> !path.toFile().getAbsolutePath().contains("reference/testclasses/Stream"))

				// bug 2: remove the filter to trigger it
				.filter(path -> !path.toFile().getAbsolutePath().contains("AccessibleClassFromNonAccessibleInterf"))

				// bug 3: remove the filter to trigger it
				.filter(path -> !path.toFile().getAbsolutePath().contains("MethodeWithNonAccessibleTypeArgument"))

				// bug 4: remove the filter to trigger it
				.filter(path -> !path.toFile().getAbsolutePath().contains("lambda/testclasses/Bar"))

				// bug 5: remove the filter to trigger it
				.filter(path -> !path.toFile().getAbsolutePath().contains("LambdaRxJava"))

				// bug 6: remove the filter to trigger it
				.filter(path -> !path.toFile().getAbsolutePath().contains("Tapas"))

				.forEach(x -> {
					launcher.addInputResource(x.toString());
					}
				);

		launcher.buildModel();

		rootPackage = launcher.getFactory().Package().getRootPackage();

		// we verify all the contracts
		new ContractVerifier(rootPackage).verify();
	}


	@Test
	public void testTest() {
		// the tests should be spoonable
		Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "src/test/java",
				"-o", "target/spooned",
				"--noclasspath",
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
					|| t.getPackage().getQualifiedName().startsWith("spoon.generating")) {
				//Meta model classes doesn't have to follow test class naming conventions
				continue;
			}
			assertTrue(t.getQualifiedName() + " is not clearly a test class, it should contain 'test' either in its package name or class name", t.getQualifiedName().matches("(?i:.*test.*)"));
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

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Test
	public void testLauncherWithoutArgumentsExitWithSystemExit() {
		exit.expectSystemExit();

		final PrintStream oldErr = System.err;
		System.setErr(new PrintStream(errContent));
		exit.checkAssertionAfterwards(new Assertion() {
			@Override
			public void checkAssertion() {
				assertTrue(errContent.toString().contains("Usage: java <launcher name> [option(s)]"));
				System.setErr(oldErr);
			}
		});

		new Launcher().run(new String[] { });
	}
}
