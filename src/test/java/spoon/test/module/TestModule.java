package spoon.test.module;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtModuleReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestModule {
	private static final String MODULE_RESOURCES_PATH = "./src/test/resources/spoon/test/module";

	@BeforeClass
	public static void setUp() throws IOException {
		File directory = new File(MODULE_RESOURCES_PATH);
		Files.walk(directory.toPath()).forEach(path -> {
			if (path.toFile().getName().equals("module-info-tpl.java")) {
				try {
					Files.copy(path, new File(path.getParent().toFile(), "module-info.java").toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@AfterClass
	public static void tearDown() throws IOException {
		File directory = new File(MODULE_RESOURCES_PATH);
		Files.walk(directory.toPath()).forEach(path -> {
			if (path.toFile().getName().equals("module-info.java")) {
				try {
					Files.delete(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Test
	public void testCompleteModuleInfoContentNoClasspath() {
		// contract: all information of the module-info should be available through the model
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/module/simple-module/module-info.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());

		CtModule unnamedModule = launcher.getFactory().Module().getOrCreate(CtModule.TOP_LEVEL_MODULE_NAME);
		assertSame(unnamedModule, launcher.getModel().getUnnamedModule());
		CtModule moduleGreetings = launcher.getFactory().Module().getOrCreate("com.greetings");

		assertEquals("com.greetings", moduleGreetings.getSimpleName());

		assertEquals(7, moduleGreetings.getModuleDirectives().size());

		List<CtModuleRequirement> requiredModules = moduleGreetings.getRequiredModules();
		assertEquals(1, requiredModules.size());

		CtModuleRequirement moduleRequirement = requiredModules.get(0);
		assertEquals("java.logging", moduleRequirement.getModuleReference().getSimpleName());
		assertTrue(moduleRequirement.getRequiresModifiers().contains(CtModuleRequirement.RequiresModifier.TRANSITIVE));

		List<CtPackageExport> moduleExports = moduleGreetings.getExportedPackages();
		assertEquals(1, moduleExports.size());

		assertEquals("com.greetings.pkg", moduleExports.get(0).getPackageReference().getQualifiedName());

		assertEquals(2, moduleExports.get(0).getTargetExport().size());

		for (CtModuleReference target : moduleExports.get(0).getTargetExport()) {
			if (!target.getSimpleName().equals("com.other.module") && !target.getSimpleName().equals("com.second.module")) {
				fail();
			}
		}

		List<CtPackageExport> moduleOpened = moduleGreetings.getOpenedPackages();
		assertEquals(2, moduleOpened.size());

		CtPackageExport openedFirst = moduleOpened.get(0);
		CtPackageExport openedSecond = moduleOpened.get(1);

		assertEquals("com.greetings.otherpkg", openedFirst.getPackageReference().getSimpleName());
		assertTrue(openedFirst.getTargetExport().isEmpty());

		assertEquals("com.greetings.openpkg", openedSecond.getPackageReference().getSimpleName());
		assertEquals(1, openedSecond.getTargetExport().size());
		assertEquals("com.third.module", openedSecond.getTargetExport().iterator().next().getSimpleName());

		List<CtUsedService> consumedService = moduleGreetings.getUsedServices();
		assertEquals(1, consumedService.size());
		assertEquals("com.greetings.pkg.ConsumedService", consumedService.get(0).getServiceType().getQualifiedName());

		List<CtProvidedService> providedServices = moduleGreetings.getProvidedServices();
		assertEquals(2, providedServices.size());

		CtProvidedService providedService1 = providedServices.get(0);
		CtProvidedService providedService2 = providedServices.get(1);

		assertEquals("com.greetings.pkg.ConsumedService", providedService1.getServiceType().getQualifiedName());
		assertEquals(2, providedService1.getImplementationTypes().size());
		assertEquals("com.greetings.pkg.ProvidedClass1", providedService1.getImplementationTypes().get(0).getQualifiedName());
		assertEquals("com.greetings.otherpkg.ProvidedClass2", providedService1.getImplementationTypes().get(1).getQualifiedName());

		assertEquals("java.logging.Service", providedService2.getServiceType().getQualifiedName());
		assertEquals(1, providedService2.getImplementationTypes().size());
		assertEquals("com.greetings.logging.Logger", providedService2.getImplementationTypes().get(0).getQualifiedName());
	}

	@Test
	public void testModuleInfoShouldBeCorrectlyPrettyPrinted() throws IOException {
		// contract: module-info with complete information should be correctly pretty printed

		File input = new File("./src/test/resources/spoon/test/module/simple-module/module-info.java");
		File output = new File("./target/spoon-module");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.setSourceOutputDirectory(output.getPath());
		launcher.addInputResource(input.getPath());
		launcher.run();

		assertEquals(2, launcher.getModel().getAllModules().size());

		assertEquals(1, Files.list(output.toPath()).count());
		File fileOuput = new File(output, "com.greetings/module-info.java");
		List<String> originalLines = Files.readAllLines(input.toPath());
		List<String> createdLines = Files.readAllLines(fileOuput.toPath());

		assertEquals(originalLines.size(), createdLines.size());

		for (int i = 0; i < originalLines.size(); i++) {
			assertEquals(originalLines.get(i), createdLines.get(i));
		}
	}

	@Test
	public void testModuleInfoWithComments() {
		// contract: documentation on module-info elements should be managed

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.addInputResource(MODULE_RESOURCES_PATH+"/module-with-comments/module-info.java");
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());
		CtModule module = launcher.getFactory().Module().getModule("anothermodule");
		assertNotNull(module);

		assertTrue(module.isOpenModule());

		List<CtComment> comments = module.getComments();
		assertEquals(1, comments.size());

		CtComment comment = comments.get(0);
		assertEquals("This is the main module of the application", comment.getContent());
		assertEquals(CtComment.CommentType.JAVADOC, comment.getCommentType());

		assertEquals(3, module.getModuleDirectives().size());

		CtModuleRequirement moduleRequirement = module.getRequiredModules().get(0);
		comments = moduleRequirement.getComments();
		assertEquals(1, comments.size());

		comment = comments.get(0);
		assertEquals("this is needed for logging stuff", comment.getContent());
		assertEquals(CtComment.CommentType.INLINE, comment.getCommentType());

		CtProvidedService providedService = module.getProvidedServices().get(0);
		comments = providedService.getComments();
		assertEquals(1, comments.size());

		comment = comments.get(0);
		assertEquals("A specific implementation", comment.getContent());
		assertEquals(CtComment.CommentType.JAVADOC, comment.getCommentType());

		CtUsedService usedService = module.getUsedServices().get(0);
		comments = usedService.getComments();
		assertEquals(1, comments.size());

		comment = comments.get(0);
		assertEquals("A simple implementation", comment.getContent());
		assertEquals(CtComment.CommentType.BLOCK, comment.getCommentType());
	}

	@Test
	public void testDirectiveOrders() {
		// contract: module directive should be ordered the same way as in the original file

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.addInputResource(MODULE_RESOURCES_PATH+"/module-with-comments/module-info.java");
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());
		CtModule module = launcher.getFactory().Module().getModule("anothermodule");
		assertNotNull(module);

		List<CtModuleDirective> moduleDirectives = module.getModuleDirectives();
		assertEquals(3, moduleDirectives.size());

		assertTrue(moduleDirectives.get(0) instanceof CtModuleRequirement);
		assertTrue(moduleDirectives.get(1) instanceof CtProvidedService);
		assertTrue(moduleDirectives.get(2) instanceof CtUsedService);
	}

	@Test
	public void testMultipleModules() {
		// contract: Spoon is able to build a model with multiple modules

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.addInputResource(MODULE_RESOURCES_PATH+"/code-multiple-modules");
		launcher.buildModel();

		assertEquals(3, launcher.getModel().getAllModules().size());
	}
}
