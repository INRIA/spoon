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

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.ZipFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JarTest {

	@Test
	public void testJarResources() throws Exception {
		List<SpoonResource> resources = SpoonResourceHelper.resources("./src/test/resources/reference-test/EnumJar.jar");
		assertEquals(1, resources.size());
		ZipFolder folder = (ZipFolder) resources.get(0);
		List<SpoonFile> files = folder.getAllFiles();
		assertEquals(5, files.size());
		assertEquals("Manifest-Version: 1.0\r\n\r\n", readFileString(files.stream().filter(f -> "META-INF/MANIFEST.MF".equals(f.getName())).findFirst().get(), "ISO-8859-1"));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<classpath>\n" + 
				"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>\n" + 
				"	<classpathentry kind=\"src\" path=\"src\"/>\n" + 
				"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
				"</classpath>\n" + 
				"", readFileString(files.stream().filter(f -> ".classpath".equals(f.getName())).findFirst().get(), "ISO-8859-1"));
	}

	private byte[] readFileBytes(SpoonFile file) {
		byte[] buff = new byte[1024];
		try (InputStream is = file.getContent(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			while(true) {
				int count = is.read(buff);
				if (count < 0) {
					break; 
				}
				baos.write(buff, 0, count);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String readFileString(SpoonFile file, String encoding) {
		try {
			return new String(readFileBytes(file), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testJar() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);

		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("./src/test/resources/sourceJar/test.jar"));
		assertTrue(compiler.build());
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
		assertTrue(compiler.build());

		assertNotNull(launcher.getFactory().Type().get("Foo"));
	}

	@Test
	public void testResource() {
		Launcher launcher = new Launcher();

		SpoonModelBuilder compiler = launcher.createCompiler(
				launcher.getFactory(),
				Arrays.asList(
						new VirtualFile("class Foo {}" , "Foo.java")
				));
		assertTrue(compiler.build());

		assertNotNull(launcher.getFactory().Type().get("Foo"));
	}
}
