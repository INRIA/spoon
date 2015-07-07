package spoon.test.jar;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;

public class JarTest {

	@Test
	public void testJar() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("./src/test/resources/sourceJar/test.jar"));
		compiler.build();
	}

}
