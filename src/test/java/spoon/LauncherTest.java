package spoon;

import org.junit.Assert;
import org.junit.Test;

import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
		assertEquals(new File("spooned"), builder.getSourceOutputDirectory());
		assertEquals(0, builder.getInputSources().size());
		assertEquals("UTF-8", builder.getEncoding());
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
		assertEquals(new File("spooned2"), builder.getSourceOutputDirectory());

		// the input directories
		List<File> inputSources = new ArrayList<>(builder.getInputSources());
		assertTrue(inputSources.get(0).getPath().replace('\\', '/').contains("src/main/java"));
		assertEquals("UTF-16", builder.getEncoding());

	}

}