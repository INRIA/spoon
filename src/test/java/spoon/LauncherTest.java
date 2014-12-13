package spoon;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.visitor.FragmentDrivenJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;

public class LauncherTest extends TestCase {

	@Test
	public void testInitEnvironment() throws Exception {
		// Arguments given in command line but hardcoded for the test.
		File properties = new File(".");
		int complianceLevel = 5;
		boolean verbose = true;
		boolean debug = true;
		boolean autoImports = true;
		int tabulationSize = 42;
		boolean useTabulations = true;
		boolean useSourceCodeFragments = true;
		boolean preserveLineNumbers = true;

		// Main class of Spoon who contain initEnvironment method.
		final Launcher launcher = new Launcher();

		// The attribute factory isn't public in Launcher class so we create a
		// new one and specify his implementation as type to have an access to
		// getXmlRootFolder method.
		final StandardEnvironment environment = new StandardEnvironment();

		launcher.initEnvironment(environment, complianceLevel, verbose,
				debug, properties, autoImports, tabulationSize, useTabulations,
				useSourceCodeFragments, preserveLineNumbers, new File("target/spooned"));

		// Verify if the environment is correct.
		Assert.assertEquals(properties, environment.getXmlRootFolder());
		Assert.assertTrue(environment.isVerbose());
		Assert.assertTrue(environment.isDebug());
		Assert.assertTrue(environment.isAutoImports());
		Assert.assertTrue(environment.isUsingTabulations());
		Assert.assertTrue(environment.isUsingSourceCodeFragments());
		Assert.assertTrue(environment.isPreserveLineNumbers());
		Assert.assertEquals(tabulationSize, environment.getTabulationSize());

		// Check if the processor of the output is a FragmentDrivenJavaPrettyPrinter
		//
		JavaOutputProcessor processor = (JavaOutputProcessor) environment
				.getDefaultFileGenerator();
		Assert.assertTrue(processor
				.getPrinter() instanceof FragmentDrivenJavaPrettyPrinter);
	}
}