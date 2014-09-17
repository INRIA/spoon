package spoon.test.main;

import java.io.File;

import org.junit.Test;

public class MainTest {

	@Test
	public void testMain() throws Exception {

		// we have to remove the test-classes folder
		// so that the precondition of --source-classpath is not violated
		// (target/test-classes contains src/test/resources which itself contains Java files)
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator))
		{
			if (!classpathEntry.contains("test-classes"))
			{
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

}
