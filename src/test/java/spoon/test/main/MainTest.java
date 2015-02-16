package spoon.test.main;

import java.io.File;

import org.junit.Test;

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

		spoon.Launcher.main(new String[] { "-i", "src/main/java", "-o",
				"target/spooned", "--source-classpath",
				systemClassPath, "--compile" });
		// we should have no exception
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
				"--source-classpath", systemClassPath, "--compile",
				"-r"});

		assertTrue(new File("src/test/resources/no-copy-resources/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/fr/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/inria/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/fr/inria/package.html").exists());
	}

}
