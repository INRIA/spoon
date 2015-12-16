package spoon.test.main;

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
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.parent.ParentTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
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
				"--compile",
				"--compliance", "7",
				"--level", "OFF"
		});

		checkGenericContracts(launcher.getFactory().Package().getRootPackage());
	}

	public void checkGenericContracts(CtPackage pack) {
		// parent
		ParentTest.checkParentContract(pack);

		// assignments
		checkAssignmentContracts(pack);

		// scanners
		checkContractCtScanner(pack);
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
