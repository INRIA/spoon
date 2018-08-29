package spoon;

import org.junit.Assert;
import org.junit.Test;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LauncherTest {

	@Test
	public void testInitEnvironmentDefault() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[0]);
		launcher.processArguments();

		final Environment environment = launcher.getEnvironment();
		// specify the default values
		Assert.assertFalse(environment.isAutoImports());
		Assert.assertFalse(environment.isUsingTabulations());
		Assert.assertFalse(environment.isPreserveLineNumbers());
		assertEquals(4, environment.getTabulationSize());
		Assert.assertTrue(environment.isCopyResources());

		JavaOutputProcessor processor = (JavaOutputProcessor) environment.getDefaultFileGenerator();
		Assert.assertTrue(processor.getPrinter() instanceof DefaultJavaPrettyPrinter);

		// now assertions on the model builder
		final SpoonModelBuilder builder = launcher.getModelBuilder();
		assertEquals(new File("spooned").getCanonicalFile(), builder.getSourceOutputDirectory());
		assertEquals(0, builder.getInputSources().size());
		assertEquals("UTF-8", environment.getEncoding().displayName());
	}

	@Test
	public void testInitEnvironment() throws Exception {

		// Main class of Spoon who contain initEnvironment method.
		final Launcher launcher = new Launcher();
		launcher.setArgs("--tabs --tabsize 42 --compliance 5 --with-imports -r --lines -o spooned2 -i src/main/java --encoding UTF-16".split(" "));
		launcher.processArguments();

		final Environment environment = launcher.getEnvironment();

		// Verify if the environment is correct.
		Assert.assertTrue(environment.isAutoImports());
		Assert.assertTrue(environment.isUsingTabulations());
		Assert.assertTrue(environment.isPreserveLineNumbers());
		assertEquals(42, environment.getTabulationSize());
		assertEquals(5, environment.getComplianceLevel());
		Assert.assertFalse(environment.isCopyResources());

		final SpoonModelBuilder builder = launcher.getModelBuilder();
		assertEquals(new File("spooned2").getCanonicalFile(), builder.getSourceOutputDirectory());

		// the input directories
		List<File> inputSources = new ArrayList<>(builder.getInputSources());
		assertTrue(inputSources.get(0).getPath().replace('\\', '/').contains("src/main/java"));
		assertEquals("UTF-16", environment.getEncoding().displayName());
	}

	@Test
	public void testLauncherInEmptyWorkingDir() throws Exception {

		// Contract: Spoon can be launched in an empty folder as a working directory
		// See: https://github.com/INRIA/spoon/pull/1208
		// This test does not fail (it's not enough to change user.dir we should launch process inside that dir) but it explains the problem
		final Launcher launcher = new Launcher();
		Path path = Files.createTempDirectory("emptydir");

		String oldUserDir = System.getProperty("user.dir");
		System.setProperty("user.dir", path.toFile().getAbsolutePath());

		// path should exist, otherwise it would crash on a filenotfoundexception before showing the bug
		launcher.addInputResource(oldUserDir + "/src/test/java/spoon/LauncherTest.java");
		try {
			launcher.buildModel();
		} finally {
			System.setProperty("user.dir", oldUserDir);
		}
	}

	@Test
	public void testLLauncherBuildModelReturnAModel() {
		// contract: Launcher#buildModel should return a consistent CtModel
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/api/Foo.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		assertNotNull(model);

		assertEquals(2, model.getAllTypes().size());
	}
}
