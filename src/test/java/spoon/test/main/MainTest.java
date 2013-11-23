package spoon.test.main;

import org.junit.Test;

public class MainTest {

	@Test
	public void testMain() throws Exception {
		spoon.Launcher.main(new String[] {
				"-i", "src/main/java", 
				"-o", "target/spooned",
				"--source-classpath", System.getProperty("java.class.path"),
				"--compile"
		});
		// we should have no exception
	}

}
