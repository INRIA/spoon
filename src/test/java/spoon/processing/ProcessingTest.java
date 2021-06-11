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
package spoon.processing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.CtAssert;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.support.RuntimeProcessingManager;
import spoon.support.compiler.FileSystemFile;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.processing.processors.MyProcessor;
import spoon.test.template.testclasses.AssertToIfAssertedStatementTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.Assert.assertThat;

public class ProcessingTest {

	@Test
	public void testInterruptAProcessor() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/processing/testclasses");
		final MyProcessor processor = new MyProcessor();
		launcher.addProcessor(processor);
		try {
			launcher.run();
		} catch (ProcessInterruption e) {
			fail("ProcessInterrupt exception must be catch in the ProcessingManager.");
		}
		assertFalse(processor.isShouldStayAtFalse());
	}

	@Test
	public void testRuntimeProcessorManager() {
		// contract: RuntimeProcessorManager can be run without exception with several processors

		// collecting the processors
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/java/spoon/test/processing/processors");
		launcher.buildModel();


		// running on test classes
		final Launcher launcher2 = new Launcher();
		launcher2.getEnvironment().setNoClasspath(true);
		launcher2.addInputResource("./src/test/java/spoon/test/processing/testclasses");
		launcher2.buildModel();

		// we only the API of interface ProcessingManager
 		ProcessingManager processing = new RuntimeProcessingManager(launcher2.getFactory());
		for (CtType processor: launcher.getModel().getAllTypes()) {
			if (processor.getSimpleName().equals("MyProcessor")) {
				continue;
			}
			processing.addProcessor(processor.getQualifiedName());
		}
		processing.process(launcher2.getModel().getRootPackage());
	}

	@Test
	public void testSpoonTagger(@TempDir File tempDir) {
		// contract: after running SpoonTagger, the file spoon/Spoon.java should exist in the output directory
		final Launcher launcher = new Launcher();
		launcher.setSourceOutputDirectory(tempDir.getAbsolutePath());
		launcher.addProcessor("spoon.processing.SpoonTagger");
		launcher.run();
		assertTrue(new File(launcher.getModelBuilder().getSourceOutputDirectory() + "/spoon/Spoon.java").exists());
	}

	@Test
	public void testStaticImport() throws IOException {
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		String[] sourcePath = new String[0];
		e.setNoClasspath(false);
		e.setSourceClasspath(sourcePath);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/compilation3/A.java");
		l.addInputResource("src/test/resources/compilation3/subpackage/B.java");
		l.setSourceOutputDirectory(path.toFile());
		l.run();
	}

	@Test
	public void testNullPointerException() throws IOException {
		// https://github.com/INRIA/spoon/pull/3254
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		e.setNoClasspath(true);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/compilation5/A.java");
		l.setSourceOutputDirectory(path.toFile());
		l.run();
	}
	
	@Test
	public void testTemplateNotInOutput() throws IOException {
		// https://github.com/INRIA/spoon/issues/2987
		class AssertProcessor extends AbstractProcessor<CtAssert<?>> {
			public void process(CtAssert<?> element) {
				element.replace(
						new AssertToIfAssertedStatementTemplate(element)
								.apply(element.getParent(CtClass.class))
				);
			}
		}
		
		String templatePath = "src/test/java/spoon/test/template/testclasses/AssertToIfAssertedStatementTemplate.java";
		String resourcePath = "src/test/resources/spoon/test/template/";
		
		final Launcher l = new Launcher();
		Path outputPath = Files.createTempDirectory("emptydir");
		
		l.addProcessor(new AssertProcessor());
		l.addTemplateResource(new FileSystemFile(templatePath));
		
		l.addInputResource(resourcePath + "SimpleAssert.java");
		l.setSourceOutputDirectory(outputPath.toFile());
		l.run();

		// If template is applied to itself then there will be modified spoon/...Template.java on output
		assertArrayEquals(new String[]{"SimpleAssert.java"}, outputPath.toFile().list(), "Template source found in output");
		// Check that the template worked as intended
		assertThat(outputPath.toString() + "/SimpleAssert.java")
			.isEqualTo(resourcePath + "SimpleIfAsserted.java");
	}
}
