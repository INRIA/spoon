package spoon.test.module;

import org.junit.Test;
import spoon.Launcher;

import static org.junit.Assert.assertEquals;

public class TestModule {

	@Test
	public void testSimpleModule() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/module/com.greetings");
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());
	}
}
