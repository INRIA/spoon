package spoon.test.module;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleExport;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtModuleProvidedService;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestModule {

	@Test
	public void testCompleteModuleInfoContentNoClasspath() {
		// contract: all information of the module-info should be available through the model
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/module/module-info.java");
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		assertEquals(6, launcher.getModel().getAllModules().size());
		CtModule moduleGreetings = launcher.getFactory().Module().getOrCreate("com.greetings");

		assertEquals("com.greetings", moduleGreetings.getSimpleName());

		List<CtModuleRequirement> requiredModules = moduleGreetings.getRequiredModules();
		assertEquals(1, requiredModules.size());

		CtModuleRequirement moduleRequirement = requiredModules.get(0);
		assertEquals("java.logging", moduleRequirement.getModuleReference().getSimpleName());
		assertTrue(moduleRequirement.getRequiresModifiers().contains(CtModuleRequirement.RequiresModifier.TRANSITIVE));

		List<CtModuleExport> moduleExports = moduleGreetings.getExportedPackages();
		assertEquals(1, moduleExports.size());

		assertEquals("com.greetings.pkg", moduleExports.get(0).getPackageReference().getQualifiedName());

		assertEquals(2, moduleExports.get(0).getTargetExport().size());

		for (CtModuleReference target : moduleExports.get(0).getTargetExport()) {
			if (!target.getSimpleName().equals("com.other.module") && !target.getSimpleName().equals("com.second.module")) {
				fail();
			}
		}

		List<CtModuleExport> moduleOpened = moduleGreetings.getOpenedPackages();
		assertEquals(2, moduleOpened.size());

		CtModuleExport openedFirst = moduleOpened.get(0);
		CtModuleExport openedSecond = moduleOpened.get(1);

		assertEquals("com.greetings.otherpkg", openedFirst.getPackageReference().getSimpleName());
		assertTrue(openedFirst.getTargetExport().isEmpty());

		assertEquals("com.greetings.openpkg", openedSecond.getPackageReference().getSimpleName());
		assertEquals(1, openedSecond.getTargetExport().size());
		assertEquals("com.third.module", openedSecond.getTargetExport().iterator().next().getSimpleName());

		List<CtTypeReference> consumedService = moduleGreetings.getConsumedServices();
		assertEquals(1, consumedService.size());
		assertEquals("com.greetings.pkg.ConsumedService", consumedService.get(0).getQualifiedName());

		List<CtModuleProvidedService> providedServices = moduleGreetings.getProvidedServices();
		assertEquals(2, providedServices.size());

		CtModuleProvidedService providedService1 = providedServices.get(0);
		CtModuleProvidedService providedService2 = providedServices.get(1);

		assertEquals("com.greetings.pkg.ConsumedService", providedService1.getProvidingType().getQualifiedName());
		assertEquals(2, providedService1.getUsedTypes().size());
		assertEquals("com.greetings.pkg.ProvidedClass1", providedService1.getUsedTypes().get(0).getQualifiedName());
		assertEquals("com.greetings.otherpkg.ProvidedClass2", providedService1.getUsedTypes().get(1).getQualifiedName());

		assertEquals("java.logging.Service", providedService2.getProvidingType().getQualifiedName());
		assertEquals(1, providedService2.getUsedTypes().size());
		assertEquals("com.greetings.logging.Logger", providedService2.getUsedTypes().get(0).getQualifiedName());
	}
}
