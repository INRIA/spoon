package spoon.test.properties;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PropertiesTest {

	@Test
	public void testNonExistingDirectory() throws Exception {
		File tempFile = File.createTempFile("SPOON", "SPOON");
		tempFile.delete();

		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/properties/testclasses/Sample.java"
						));
		compiler.build();

		compiler.instantiateAndProcess(Arrays.asList(SimpleProcessor.class.getName()));
		assertEquals(0, factory.getEnvironment().getErrorCount());
	}

}
