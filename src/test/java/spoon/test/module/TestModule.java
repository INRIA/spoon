/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.module;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestModule {
	private static final String MODULE_RESOURCES_PATH = "./src/test/resources/spoon/test/module";

	private void checkJavaVersion() {
		String property = System.getProperty("java.version");
		if (property != null && !property.isEmpty()) {

			// java 8 and less are versionning "1.X" where 9 and more are directly versioned "X"
			Assume.assumeFalse(property.startsWith("1."));
		}
	}

	@BeforeClass
	public static void setUp() throws IOException {
		File directory = new File(MODULE_RESOURCES_PATH);
		try (Stream<Path> paths = Files.walk(directory.toPath())) {
			paths.forEach(path -> {
				if ("module-info-tpl".equals(path.toFile().getName())) {
					try {
						Files.copy(path, new File(path.getParent().toFile(), "module-info.java").toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@AfterClass
	public static void tearDown() throws IOException {
		File directory = new File(MODULE_RESOURCES_PATH);
		try (Stream<Path> paths = Files.walk(directory.toPath())) {
			paths.forEach(path -> {
				if ("module-info.java".equals(path.toFile().getName())) {
					try {
						Files.delete(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Test
	public void testCompleteModuleInfoContentNoClasspath() {
		// contract: all information of the module-info should be available through the model
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/module/simple_module/module-info.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());

		CtModule unnamedModule = launcher.getFactory().Module().getOrCreate(CtModule.TOP_LEVEL_MODULE_NAME);
		assertSame(unnamedModule, launcher.getModel().getUnnamedModule());
		CtModule moduleGreetings = launcher.getFactory().Module().getOrCreate("simple_module");

		assertEquals("simple_module", moduleGreetings.getSimpleName());

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
			if (!"com.other.module".equals(target.getSimpleName()) && !"com.second.module".equals(target.getSimpleName())) {
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

		File input = new File("./src/test/resources/spoon/test/module/simple_module/module-info.java");
		File output = new File("./target/spoon-module");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.setSourceOutputDirectory(output.getPath());
		launcher.addInputResource(input.getPath());
		launcher.run();

		assertEquals(2, launcher.getModel().getAllModules().size());

		try (Stream<Path> files = Files.list(output.toPath())) {
			assertEquals(2, files.count()); // should be 1 but for now we also have the module-info-tpl.
		}
		File fileOuput = new File(output, "simple_module/module-info.java");
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
		launcher.addInputResource(MODULE_RESOURCES_PATH+"/module_with_comments/module-info.java");
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());
		CtModule module = launcher.getFactory().Module().getModule("module_with_comments");
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
		launcher.addInputResource(MODULE_RESOURCES_PATH+"/module_with_comments/module-info.java");
		launcher.buildModel();

		assertEquals(2, launcher.getModel().getAllModules().size());
		CtModule module = launcher.getFactory().Module().getModule("module_with_comments");
		assertNotNull(module);

		List<CtModuleDirective> moduleDirectives = module.getModuleDirectives();
		assertEquals(3, moduleDirectives.size());

		assertTrue(moduleDirectives.get(0) instanceof CtModuleRequirement);
		assertTrue(moduleDirectives.get(1) instanceof CtProvidedService);
		assertTrue(moduleDirectives.get(2) instanceof CtUsedService);
	}

	@Test
	public void testGetParentOfRootPackageOfModule() {
		// contract: unnamed module root package should have unnamed module as parent

		final Launcher launcher = new Launcher();

		CtModule unnamedModule = launcher.getFactory().getModel().getUnnamedModule();
		assertSame(unnamedModule, unnamedModule.getRootPackage().getParent());
	}

	@Test
	public void testGetModuleAfterChangingItsName() {
		// contract: a module should be always available through ModuleFactory even after its name changed

		final Launcher launcher = new Launcher();
		CtModule module = launcher.getFactory().Module().getOrCreate("myModule");
		module.setSimpleName("newName");

		CtModule moduleNewName = launcher.getFactory().Module().getOrCreate("newName");

		assertSame(module, moduleNewName);
	}

	@Test
	public void testSimpleModuleCanBeBuilt() {
		checkJavaVersion();
		// contract: Spoon is able to build a simple model with a module in full classpath
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/resources/spoon/test/module/simple_module_with_code");
		launcher.run();

		CtModel model = launcher.getModel();

		// unnamed module and module 'simple_module_with_code'
		assertEquals(2, model.getAllModules().size());
		assertEquals(1, model.getAllTypes().size());

		CtClass simpleClass = model.getElements(new TypeFilter<>(CtClass.class)).get(0);
		assertEquals("SimpleClass", simpleClass.getSimpleName());

		CtModule simpleModule = model.getElements(new NamedElementFilter<>(CtModule.class, "simple_module_with_code")).get(0);
		assertNotNull(simpleModule);
		assertEquals("simple_module_with_code", simpleModule.getSimpleName());

		CtModule module = simpleClass.getParent(CtModule.class);
		assertNotNull(module);
		assertSame(simpleModule, module);
	}

	@Ignore
	@Test
	public void testMultipleModulesAndParents() {
		// contract: Spoon is able to build a model with multiple modules

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.addInputResource(MODULE_RESOURCES_PATH+"/code-multiple-modules");
		launcher.run();

		assertEquals(3, launcher.getModel().getAllModules().size());

		CtType barclass = launcher.getFactory().Type().get("packbar.BarClass");
		assertNotNull(barclass);

		assertTrue(barclass.getParent() instanceof CtPackage);

		CtPackage packBar = (CtPackage) barclass.getParent();

		assertTrue(packBar.getParent() instanceof CtModule);
	}

	@Test (expected = SpoonException.class)
	public void testModuleComplianceLevelException() {
		// contract: provide clear exception in case if module exists but the compliance level is < 9
		try {
			final Launcher launcher = new Launcher();
			launcher.getEnvironment().setComplianceLevel(8);
			launcher.addInputResource(MODULE_RESOURCES_PATH + "/simple_module");
			launcher.run();
		} catch (SpoonException e) {
			assertEquals("Modules are only available since Java 9. Please set appropriate compliance level.", e.getMessage());
			throw e;
		}
	}
}
