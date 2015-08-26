package spoon.test.jar;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.VirtualFile;

import java.io.File;
import java.util.Arrays;

public class JarTest {

	@Test
	public void testJar() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("./src/test/resources/sourceJar/test.jar"));
		Assert.assertTrue(compiler.build());
	}

	@Test
	public void testFile() throws Exception {
		Launcher launcher = new Launcher();

		SpoonCompiler compiler = launcher.createCompiler(
				launcher.getFactory(),
				Arrays.asList(
						SpoonResourceHelper.createFile(new File("./src/test/resources/spoon/test/api/Foo.java"))));
		Assert.assertTrue(compiler.build());

		Assert.assertNotNull(launcher.getFactory().Type().get("Foo"));
	}

	@Test
	public void testResource() throws Exception {
		Launcher launcher = new Launcher();

		String foo = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("spoon/test/api/Foo.java"));

		SpoonCompiler compiler = launcher.createCompiler(
				launcher.getFactory(),
				Arrays.asList(
						new VirtualFile(foo, "Foo.java")
				));
		Assert.assertTrue(compiler.build());

		Assert.assertNotNull(launcher.getFactory().Type().get("Foo"));
	}

}
