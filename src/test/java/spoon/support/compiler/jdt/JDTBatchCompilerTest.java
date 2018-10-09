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
package spoon.support.compiler.jdt;

import org.junit.Test;

import spoon.Launcher;
import java.io.File;

import static org.junit.Assert.assertTrue;

public class JDTBatchCompilerTest {

	@Test
	public void testCompileGeneratedJavaFile() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/support/compiler/jdt/testclasses/Foo.java");
		launcher.setBinaryOutputDirectory("./target/binaries");
		launcher.getEnvironment().setShouldCompile(true);
		launcher.buildModel();

		launcher.getFactory().Class().create("spoon.Test");
		assertTrue(launcher.getModelBuilder().compile());
		assertTrue(new File("./target/binaries/spoon/Test.class").exists());
	}
}
