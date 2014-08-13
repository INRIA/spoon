package spoon.test.main;

import org.junit.Test;

public class MainTest {

	@Test
	public void testMain() throws Exception {
		
		
		String systemClassPath = System.getProperty("java.class.path");

		// we have to remove the test-classes folder
		// so that the precondition of --source-classpath is not violated
		// (target/test-classes contains src/test/resources which itself contains Java files)
		systemClassPath = systemClassPath.replaceAll("(:|^)[^:]*test-classes:", "$1");
		
		spoon.Launcher.main(new String[] { "-i", "src/main/java", "-o",
				"target/spooned", "--source-classpath",
				systemClassPath, "--compile" });
		// we should have no exception
	}

}
