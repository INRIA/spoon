package spoon.test.main;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import spoon.Launcher;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.parent.ParentTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MainTest {

	@Test
	public void testMain() throws Exception {

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

		Launcher launcher = new Launcher();

		launcher.run(new String[] {
				"-i", "src/main/java",
				"-o", "target/spooned",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath,
				"--compile", // compiling Spoon code itself on the fly
				"--compliance", "7",
				"--level", "OFF"
		});

		checkGenericContracts(launcher.getFactory().Package().getRootPackage());

		checkShadow(launcher.getFactory().Package().getRootPackage());

		checkParentConsistency(launcher.getFactory().Package().getRootPackage());
	}

	public void checkGenericContracts(CtPackage pack) {
		// clone
		checkEqualityBetweenOriginalAndClone(pack);

		// parent
		ParentTest.checkParentContract(pack);

		// assignments
		checkAssignmentContracts(pack);

		// scanners
		checkContractCtScanner(pack);

		// type parameter reference.
		checkBoundAndUnboundTypeReference(pack);
	}

	private void checkBoundAndUnboundTypeReference(CtPackage pack) {
		new CtScanner() {
			@Override
			public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
				CtTypeParameter declaration = ref.getDeclaration();
				if (declaration != null) {
					assertEquals(ref.getSimpleName(), declaration.getSimpleName());
				}
				super.visitCtTypeParameterReference(ref);
			}
		}.scan(pack);
	}

	private void checkEqualityBetweenOriginalAndClone(CtPackage pack) {
		class ActualCounterScanner extends CtBiScannerDefault {
			@Override
			public boolean biScan(CtElement element, CtElement other) {
				if (element == null) {
					if (other != null) {
						Assert.fail("element can't be null if other isn't null.");
					}
				} else if (other == null) {
					Assert.fail("other can't be null if element isn't null.");
				} else {
					assertEquals(element, other);
					assertFalse(element == other);
				}
				return super.biScan(element, other);
			}
		}
		final ActualCounterScanner actual = new ActualCounterScanner();
		actual.biScan(pack, pack.clone());
	}

	private void checkShadow(CtPackage pack) {
		new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null && CtShadowable.class.isAssignableFrom(element.getClass())) {
					assertFalse(((CtShadowable) element).isShadow());
				}
				super.scan(element);
			}

			@Override
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				assertNotNull(reference);
				if (CtTypeReference.NULL_TYPE_NAME.equals(reference.getSimpleName()) || "?".equals(reference.getSimpleName())) {
					super.visitCtTypeReference(reference);
					return;
				}
				final CtType<T> typeDeclaration = reference.getTypeDeclaration();
				assertNotNull(typeDeclaration);
				assertEquals(reference.getSimpleName(), typeDeclaration.getSimpleName());
				assertEquals(reference.getQualifiedName(), typeDeclaration.getQualifiedName());
				assertEquals(reference, typeDeclaration.getReference());
				if (reference.getDeclaration() == null) {
					assertTrue(typeDeclaration.isShadow());
				}
				super.visitCtTypeReference(reference);
			}

			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
				assertNotNull(reference);
				if (isLanguageExecutable(reference)) {
					super.visitCtExecutableReference(reference);
					return;
				}
				final CtExecutable<T> executableDeclaration = reference.getExecutableDeclaration();
				assertNotNull(executableDeclaration);
				assertEquals(reference.getSimpleName(), executableDeclaration.getSimpleName());

				// when a generic type is used in a parameter and return type, the shadow type doesn't have these information.
				boolean hasGeneric = false;
				for (int i = 0; i < reference.getParameters().size(); i++) {
					if (reference.getParameters().get(i) instanceof CtTypeParameterReference) {
						hasGeneric = true;
						continue;
					}
					if (reference.getParameters().get(i) instanceof CtArrayTypeReference && ((CtArrayTypeReference) reference.getParameters().get(i)).getComponentType() instanceof CtTypeParameterReference) {
						hasGeneric = true;
						continue;
					}
					assertEquals(reference.getParameters().get(i), executableDeclaration.getParameters().get(i).getType());
				}
				if (!hasGeneric) {
					assertEquals(reference, executableDeclaration.getReference());
				}

				if (reference.getDeclaration() == null && CtShadowable.class.isAssignableFrom(executableDeclaration.getClass())) {
					assertTrue(((CtShadowable) executableDeclaration).isShadow());
				}

				super.visitCtExecutableReference(reference);
			}

			private <T> boolean isLanguageExecutable(CtExecutableReference<T> reference) {
				return "values".equals(reference.getSimpleName());
			}

			@Override
			public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
				assertNotNull(reference);
				if (isLanguageField(reference) || isDeclaredInSuperClass(reference)) {
					super.visitCtFieldReference(reference);
					return;
				}
				final CtField<T> fieldDeclaration = reference.getFieldDeclaration();
				assertNotNull(fieldDeclaration);
				assertEquals(reference.getSimpleName(), fieldDeclaration.getSimpleName());
				assertEquals(reference.getType(), fieldDeclaration.getType());
				assertEquals(reference, fieldDeclaration.getReference());

				if (reference.getDeclaration() == null) {
					assertTrue(fieldDeclaration.isShadow());
				}
				super.visitCtFieldReference(reference);
			}

			private <T> boolean isLanguageField(CtFieldReference<T> reference) {
				return "class".equals(reference.getSimpleName()) || "length".equals(reference.getSimpleName());
			}

			private <T> boolean isDeclaredInSuperClass(CtFieldReference<T> reference) {
				final CtType<?> typeDeclaration = reference.getDeclaringType().getTypeDeclaration();
				return typeDeclaration != null && typeDeclaration.getField(reference.getSimpleName()) == null;
			}
		}.visitCtPackage(pack);
	}

	@Test
	public void test() throws Exception {
		final Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		spoon.addInputResource("./src/test/java/spoon/test/main/testclasses");
		spoon.addInputResource("./src/main/java/spoon/template/Parameter.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.run();

		checkShadow(spoon.getFactory().Package().getRootPackage());

		checkParentConsistency(spoon.getFactory().Package().getRootPackage());
	}

	private void checkContractCtScanner(CtPackage pack) {
		class Counter {
			int scan, enter, exit = 0;
		}

		final Counter counter = new Counter();

		new CtScanner() {

			@Override
			public void scan(CtElement element) {
				if (element != null) {
					counter.scan++;
				}
				super.scan(element);
			}

			@Override
			public void enter(CtElement element) {
				counter.enter++;
				super.enter(element);
			}

			@Override
			public void exit(CtElement element) {
				counter.exit++;
				super.exit(element);
			}

		}.visitCtPackage(pack);

		assertTrue(counter.enter == counter.exit);
		// there is one scan less, because we start with visit
		assertTrue(counter.enter == counter.scan + 1);
	}

	public static void checkAssignmentContracts(CtElement pack) {
		for (CtAssignment assign : pack.getElements(new TypeFilter<CtAssignment>(
				CtAssignment.class))) {
			CtExpression assigned = assign.getAssigned();
			if (!(assigned instanceof CtFieldWrite
					|| assigned instanceof CtVariableWrite || assigned instanceof CtArrayWrite)) {
				throw new AssertionError("AssignmentContract error:" + assign.getPosition()+"\n"+assign.toString()+"\nAssigned is "+assigned.getClass());
			}
		}

	}

	private void checkParentConsistency(CtPackage pack) {
		final Set<CtElement> inconsistentParents = new HashSet<>();
		new CtScanner() {
			private Deque<CtElement> previous = new ArrayDeque();
			@Override
			protected void enter(CtElement e) {
				if (e != null) {
					if (!previous.isEmpty()) {
						try {
							if (e.getParent() != previous.getLast()) {
								inconsistentParents.add(e);
							}
						} catch (ParentNotInitializedException ignore) {
							inconsistentParents.add(e);
						}
					}
					previous.add(e);
				}
				super.enter(e);
			}

			@Override
			protected void exit(CtElement e) {
				if (e == null) {
					return;
				}
				if (e.equals(previous.getLast())) {
					previous.removeLast();
				} else {
					throw new RuntimeException("Inconsistent stack");
				}
				super.exit(e);
			}
		}.visitCtPackage(pack);
		assertEquals("All parents have to be consistent", 0, inconsistentParents.size());
	}

	@Test
	public void testTest() throws Exception {
		// the tests should be spoonable
		Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "src/test/java",
				"-o", "target/spooned",
				"--noclasspath",
				"--compliance", "8",
				"--level", "OFF"
		});

		checkGenericContracts(launcher.getFactory().Package().getRootPackage());
	}

	@Test
	public void testResourcesCopiedInTargetDirectory() throws Exception {
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
	public void testResourcesNotCopiedInTargetDirectory() throws Exception {
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
	public void testLauncherWithoutArgumentsExitWithSystemExit() throws Exception {
		exit.expectSystemExit();

		final PrintStream oldErr = System.err;
		System.setErr(new PrintStream(errContent));
		exit.checkAssertionAfterwards(new Assertion() {
			@Override
			public void checkAssertion() throws Exception {
				assertTrue(errContent.toString().contains("Usage: java <launcher name> [option(s)]"));
				System.setErr(oldErr);
			}
		});

		new Launcher().run(new String[] { });
	}

}
