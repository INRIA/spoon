package spoon.test.module;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleExport;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.reference.CtModuleReference;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestModule {

	@Test
	public void testSimpleModule() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/module/com.greetings");
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.getEnvironment().setCommentEnabled(true);
		// does not compile in full classpath mode as it should be...
		// we got the following error: The package com.greetings.pkg does not exist or is empty
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		assertEquals(5, launcher.getModel().getAllModules().size());
		CtModule moduleGreetings = launcher.getFactory().Module().getOrCreate("com.greetings");

		assertEquals("com.greetings", moduleGreetings.getSimpleName());


		Set<CtModuleRequirement> requiredModules = moduleGreetings.getRequiredModules();
		assertEquals(1, requiredModules.size());

		CtModuleRequirement moduleRequirement = requiredModules.iterator().next();
		assertEquals("java.logging", moduleRequirement.getModuleReference().getSimpleName());
		assertTrue(moduleRequirement.getRequiresModifiers().contains(CtModuleRequirement.RequiresModifier.STATIC));

		Set<CtModuleExport> moduleExports = moduleGreetings.getExportedPackages();
		assertEquals(1, moduleExports.size());

		CtModuleExport moduleExport = moduleExports.iterator().next();
		assertEquals("com.greetings.pkg", moduleExport.getPackageReference().getQualifiedName());

		assertEquals(2, moduleExport.getTargetExport().size());

		for (CtModuleReference target : moduleExport.getTargetExport()) {
			if (!target.getSimpleName().equals("com.other.module") && !target.getSimpleName().equals("com.second.module")) {
				fail();
			}
		}
	}
}
