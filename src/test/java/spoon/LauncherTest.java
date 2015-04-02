package spoon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

public class LauncherTest extends TestCase {

	@Test
	public void testInitEnvironmentDefault() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[0]);
		launcher.processArguments();

		final Environment environment = launcher.getEnvironment();
		// specify the default values
		Assert.assertFalse(environment.isVerbose());
		Assert.assertFalse(environment.isAutoImports());
		Assert.assertFalse(environment.isUsingTabulations());
		Assert.assertFalse(environment.isPreserveLineNumbers());
		Assert.assertEquals(4, environment.getTabulationSize());
		Assert.assertFalse(environment.isDebug());
		Assert.assertTrue(environment.isCopyResources());

		JavaOutputProcessor processor = (JavaOutputProcessor) environment.getDefaultFileGenerator();
		Assert.assertTrue(processor.getPrinter() instanceof DefaultJavaPrettyPrinter);
		
		// now assertions on the model builder
		final SpoonModelBuilder builder = launcher.getModelBuilder();
		assertEquals(new File("spooned"), builder.getOutputDirectory());
		assertEquals(0, builder.getInputSources().size());
		assertEquals("UTF-8", builder.getEncoding());
	}
	
	@Test
	public void testInitEnvironment() throws Exception {

		// Main class of Spoon who contain initEnvironment method.
		final Launcher launcher = new Launcher();
		launcher.setArgs("--tabs --tabsize 42 --compliance 5 --verbose --with-imports -r --lines -o spooned2 -i src/main/java --encoding UTF-16".split(" "));
		launcher.processArguments();

		final Environment environment = launcher.getEnvironment();
		
		// Verify if the environment is correct.
		Assert.assertTrue(environment.isVerbose());
		Assert.assertTrue(environment.isAutoImports());
		Assert.assertTrue(environment.isUsingTabulations());
		Assert.assertTrue(environment.isPreserveLineNumbers());
		Assert.assertEquals(42, environment.getTabulationSize());
		Assert.assertEquals(5, environment.getComplianceLevel());
		Assert.assertFalse(environment.isCopyResources());
		 
		final SpoonModelBuilder builder = launcher.getModelBuilder();
		assertEquals(new File("spooned2"), builder.getOutputDirectory());
		
		// the input directories
		List<File> inputSources = new ArrayList<>(builder.getInputSources());
		assertEquals(new File("src/main/java").toURI(), inputSources.get(0).toURI());
		assertEquals("UTF-16", builder.getEncoding());

	}

}