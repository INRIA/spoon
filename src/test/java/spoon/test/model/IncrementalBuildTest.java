package spoon.test.model;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.support.SerializationModelStreamer;

public class IncrementalBuildTest {

	private static Factory loadFactory(File file) throws IOException {
		return new SerializationModelStreamer().load(new FileInputStream(file));
	}

	private static void saveFactory(Factory factory, File file) throws IOException {
		ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		new SerializationModelStreamer().save(factory, outstr);
		OutputStream fileStream = new FileOutputStream(file);
		outstr.writeTo(fileStream);
	}

	private static void createFile(File file, String content) throws IOException {
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println(content);
		writer.close();
	}

	@Test
	public void testIncremental() throws Exception {
		final File PROJECT_DIR = new File("./src/test/resources/inremental-test-project/");

		FileUtils.deleteDirectory(PROJECT_DIR);
		PROJECT_DIR.mkdirs();
		createFile(new File(PROJECT_DIR, "A.java"), "public class A { int x; }");
		createFile(new File(PROJECT_DIR, "B.java"), "public class B { int y; }");
		createFile(new File(PROJECT_DIR, "C.java"), "package com.other.pkg; public class C { int z; }");
		createFile(new File(PROJECT_DIR, "Main.java"), "public class Main { public static void main(String[] args) { } }");

		File binaryOutputDirectory = new File(PROJECT_DIR, "incremental-cache");
		List<File> inputSources = new ArrayList<>();
		inputSources.add(new File(PROJECT_DIR, "A.java"));
		inputSources.add(new File(PROJECT_DIR, "B.java"));
		inputSources.add(new File(PROJECT_DIR, "C.java"));
		inputSources.add(new File(PROJECT_DIR, "Main.java"));
		String[] inputClasspath = new String[0];

		// Run first build, save .class files and factory
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		inputSources.forEach(e -> launcher.addInputResource(e.getAbsolutePath()));
		launcher.getEnvironment().setSourceClasspath(inputClasspath);
		launcher.setBinaryOutputDirectory(binaryOutputDirectory);
		launcher.buildModel();
		launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
		saveFactory(launcher.getFactory(), new File(binaryOutputDirectory, "model"));

		// Apply some modifications
		new File(PROJECT_DIR, "A.java").delete();
		TimeUnit.MILLISECONDS.sleep(1000);
		createFile(new File(PROJECT_DIR, "A.java"), "public class A { String str; }"); // Modify A type

		new File(PROJECT_DIR, "B.java").delete(); // Remove B type
		inputSources.removeIf(f -> f.getName().equals("B.java"));

		createFile(new File(PROJECT_DIR, "D.java"), "public class D { int w; }"); // Add new D type
		inputSources.add(new File(PROJECT_DIR, "D.java"));

		new File(PROJECT_DIR, "C.java").delete(); // Remove C type from com.other.pkg
		inputSources.removeIf(f -> f.getName().equals("C.java"));

		// Prepare input for incremental build
		Factory oldFactory = loadFactory(new File(binaryOutputDirectory, "model"));
		IncrementalBuildTool incrementalTool = new IncrementalBuildTool(binaryOutputDirectory, oldFactory, inputSources, inputClasspath);
		List<File> incrementalSources = incrementalTool.getInputSourcesForIncrementalBuild();
		String[] incrementalClasspath = incrementalTool.getClasspathForIncrementalBuild();
		Factory incrementalFactory = incrementalTool.getFactoryForIncrementalBuild();

		// Run incremental build
		Launcher incrementalLauncher = new Launcher(incrementalFactory);
		incrementalLauncher.getEnvironment().setNoClasspath(true);
		incrementalSources.forEach(e -> incrementalLauncher.addInputResource(e.getAbsolutePath()));
		//incrementalLauncher.getEnvironment().setSourceClasspath(incrementalClasspath);
		CtModel m = incrementalLauncher.buildModel();

		/* Optionally, we can save .class files and factory here again,
		 * to use it in the next incremental build.
		 * No information should be lost.
		 */

		// Check results
		assertTrue(incrementalSources.size() == 2);
		assertTrue(incrementalClasspath.length == 1);
		assertTrue(m.getAllTypes().size() == 3);
		assertTrue(m.getAllTypes().stream().anyMatch(t -> t.getSimpleName().equals("A")));
		assertTrue(m.getAllTypes().stream().anyMatch(t -> t.getSimpleName().equals("D")));
		assertTrue(m.getAllTypes().stream().anyMatch(t -> t.getSimpleName().equals("Main")));
		CtType<?> typeA = m.getAllTypes().stream().filter(t -> t.getSimpleName().equals("A")).findFirst().get();
		assertTrue(typeA.getFields().size() == 1);
		assertTrue(typeA.getField("str").getType().getSimpleName().equals("String"));
		assertTrue(m.getAllPackages().size() == 1);
	}
}
