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

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtType;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.processing.processors.MyProcessor;
import spoon.test.properties.SimpleProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProcessingTest {

	@Test
	public void testInterruptAProcessor() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/processing/");
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
	public void testSpoonTagger() {
		final Launcher launcher = new Launcher();
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
}
