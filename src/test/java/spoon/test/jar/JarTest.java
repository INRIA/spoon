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
package spoon.test.jar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.VirtualFile;

public class JarTest {

	@Test
	public void testJar() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);

		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("./src/test/resources/sourceJar/test.jar"));
		Assert.assertTrue(compiler.build());
		assertEquals(1, factory.getModel().getAllTypes().size());
		assertEquals("spoon.test.strings.Main", factory.getModel().getAllTypes().iterator().next().getQualifiedName());
	}

	@Test
	public void testFile() throws Exception {
		Launcher launcher = new Launcher();

		SpoonModelBuilder compiler = launcher.createCompiler(
				launcher.getFactory(),
				Arrays.asList(
						SpoonResourceHelper.createFile(new File("./src/test/resources/spoon/test/api/Foo.java"))));
		Assert.assertTrue(compiler.build());

		Assert.assertNotNull(launcher.getFactory().Type().get("Foo"));
	}

	@Test
	public void testResource() {
		Launcher launcher = new Launcher();

		SpoonModelBuilder compiler = launcher.createCompiler(
				launcher.getFactory(),
				Arrays.asList(
						new VirtualFile("class Foo {}" , "Foo.java")
				));
		Assert.assertTrue(compiler.build());

		Assert.assertNotNull(launcher.getFactory().Type().get("Foo"));
	}

}
