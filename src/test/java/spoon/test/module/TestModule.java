package spoon.test.module;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestModule {

	@Test
	public void testSimpleModule() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/module/com.greetings");
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.buildModel();

		assertEquals(3, launcher.getModel().getAllModules().size());
		CtModule moduleGreetings = launcher.getFactory().Module().getOrCreate("com.greetings");

		assertEquals("com.greetings", moduleGreetings.getSimpleName());


		Set<CtModuleRequirement> requiredModules = moduleGreetings.getRequiredModules();
		assertEquals(1, requiredModules.size());

		CtModuleRequirement moduleRequirement = requiredModules.iterator().next();
		assertEquals("java.logging", moduleRequirement.getSimpleName());
	}
}
