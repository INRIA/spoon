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
package spoon.test.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import spoon.IncrementalLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

public class IncrementalLauncherTest {

	final File RESOURCES_DIR = new File("./src/test/resources/incremental");
	final File ORIGINAL_FILES_DIR = new File(RESOURCES_DIR, "original-files");
	final File CHANGED_FILES_DIR = new File(RESOURCES_DIR, "changed-files");
	final File WORKING_DIR = new File(RESOURCES_DIR, "temp");
	final File CACHE_DIR = new File(WORKING_DIR, "cache");

	private CtType<?> getTypeByName(Collection<CtType<?>> types, String name) {
		return types.stream().filter(t -> t.getSimpleName().equals(name)).findFirst().get();
	}

	@Test
	public void testCache() throws IOException {
		// Build model from A.java, B.java, C.java, D.java, and then load the same model from cache several times.
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Set<File> inputResources = Collections.singleton(WORKING_DIR);
		Set<String> sourceClasspath = Collections.emptySet();

		IncrementalLauncher launcher1 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher1.changesPresent());
		CtModel originalModel = launcher1.buildModel();
		launcher1.saveCache();

		IncrementalLauncher launcher2 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertFalse(launcher2.changesPresent());
		CtModel cachedModel = launcher2.buildModel();
		launcher2.saveCache();

		assertTrue(originalModel.getAllTypes().equals(cachedModel.getAllTypes()));

		IncrementalLauncher launcher3 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertFalse(launcher3.changesPresent());
		CtModel cachedCachedModel = launcher3.buildModel();
		launcher3.saveCache();

		assertTrue(originalModel.getAllTypes().equals(cachedCachedModel.getAllTypes()));

		for (CtType<?> t : cachedCachedModel.getAllTypes()) {
			assertNotNull(t.getPosition());
			assertNotNull(t.getPosition().getFile());
			assertTrue(t.getPosition().getLine() != -1);
		}
	}

	@Test
	public void testIncremental1() throws IOException, InterruptedException {
		// Build model from A.java, B.java, C.java, D.java, then change D.java => load A, B, C from cache and build D.
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Set<File> inputResources = Collections.singleton(WORKING_DIR);
		Set<String> sourceClasspath = Collections.emptySet();

		IncrementalLauncher launcher1 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher1.changesPresent());
		CtModel originalModel = launcher1.buildModel();
		launcher1.saveCache();

		TimeUnit.MILLISECONDS.sleep(1000);
		FileUtils.copyFile(new File(CHANGED_FILES_DIR, "D.java"), new File(WORKING_DIR, "D.java"), true);
		FileUtils.touch(new File(WORKING_DIR, "D.java"));

		IncrementalLauncher launcher2 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher2.changesPresent());
		CtModel newModel = launcher2.buildModel();
		launcher2.saveCache();

		Collection<CtType<?>> types1 = originalModel.getAllTypes();
		Collection<CtType<?>> types2 = newModel.getAllTypes();

		assertNotEquals(types1, types2);

		CtType<?> a1 = getTypeByName(types1, "A");
		CtType<?> b1 = getTypeByName(types1, "B");
		CtType<?> c1 = getTypeByName(types1, "C");
		CtType<?> d1 = getTypeByName(types1, "D");
		CtType<?> a2 = getTypeByName(types2, "A");
		CtType<?> b2 = getTypeByName(types2, "B");
		CtType<?> c2 = getTypeByName(types2, "C");
		CtType<?> d2 = getTypeByName(types2, "D");
		assertTrue(a1.equals(a2));
		assertTrue(b1.equals(b2));
		assertTrue(c1.equals(c2));
		assertNotEquals(d1, d2);

		assertTrue(d1.getDeclaredFields().isEmpty());
		assertTrue(d2.getDeclaredFields().size() == 2);
		assertTrue(d1.getMethods().isEmpty());
		assertTrue(d2.getMethods().size() == 1);
	}

	@Test
	public void testIncremental2() throws IOException {
		// Build model from A.java, B.java, C.java, then remove C.java and add D.java
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Set<File> inputResources = new HashSet<>();
		inputResources.add(new File(WORKING_DIR, "A.java"));
		inputResources.add(new File(WORKING_DIR, "B.java"));
		inputResources.add(new File(WORKING_DIR, "C.java"));
		Set<String> sourceClasspath = Collections.emptySet();

		IncrementalLauncher launcher1 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher1.changesPresent());
		CtModel originalModel = launcher1.buildModel();
		launcher1.saveCache();
		assertTrue(originalModel.getAllTypes().size() == 3);

		inputResources.removeIf(f -> "C.java".equals(f.getName()));
		inputResources.add(new File(WORKING_DIR, "D.java"));

		IncrementalLauncher launcher2 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher2.changesPresent());
		CtModel newModel = launcher2.buildModel();
		launcher2.saveCache();
		assertTrue(newModel.getAllTypes().size() == 3);

		Collection<CtType<?>> types1 = originalModel.getAllTypes();
		Collection<CtType<?>> types2 = newModel.getAllTypes();

		CtType<?> a1 = getTypeByName(types1, "A");
		CtType<?> b1 = getTypeByName(types1, "B");
		CtType<?> c1 = getTypeByName(types1, "C");
		CtType<?> a2 = getTypeByName(types2, "A");
		CtType<?> b2 = getTypeByName(types2, "B");
		CtType<?> d2 = getTypeByName(types2, "D");
		assertTrue(a1.equals(a2));
		assertTrue(b1.equals(b2));
		assertNotEquals(c1, d2);
	}

	@Test
	public void testIncremental3() throws IOException, InterruptedException {
		// Build model from A.java, B.java, C.java, then change type of field val in C.
		// B refers to C, so we should check reference resolution in B as well.
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Set<File> inputResources = new HashSet<>();
		inputResources.add(new File(WORKING_DIR, "A.java"));
		inputResources.add(new File(WORKING_DIR, "B.java"));
		inputResources.add(new File(WORKING_DIR, "C.java"));
		Set<String> sourceClasspath = Collections.emptySet();

		IncrementalLauncher launcher1 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher1.changesPresent());
		CtModel originalModel = launcher1.buildModel();
		launcher1.saveCache();

		CtType<?> c1 = getTypeByName(originalModel.getAllTypes(), "C");
		assertTrue("int".equals(c1.getField("val").getType().getSimpleName()));

		CtType<?> b1 = getTypeByName(originalModel.getAllTypes(), "B");
		CtMethod<?> method1 = b1.getMethodsByName("func").get(0);
		CtStatement stmt1 = method1.getBody().getStatement(0);
		CtAssignment<?, ?> assignment1 = (CtAssignment<?, ?>) stmt1;
		CtExpression<?> lhs1 = assignment1.getAssigned();
		assertTrue("int".equals(assignment1.getType().getSimpleName()));
		assertTrue("int".equals(lhs1.getType().getSimpleName()));

		TimeUnit.MILLISECONDS.sleep(1000);
		FileUtils.copyFile(new File(CHANGED_FILES_DIR, "C.java"), new File(WORKING_DIR, "C.java"), true);
		FileUtils.touch(new File(WORKING_DIR, "C.java"));

		IncrementalLauncher launcher2 = new IncrementalLauncher(inputResources, sourceClasspath, CACHE_DIR);
		assertTrue(launcher2.changesPresent());
		CtModel newModel = launcher2.buildModel();
		launcher2.saveCache();

		CtType<?> c2 = getTypeByName(newModel.getAllTypes(), "C");
		assertTrue("float".equals(c2.getField("val").getType().getSimpleName()));

		CtType<?> b2 = getTypeByName(newModel.getAllTypes(), "B");
		CtMethod<?> method2 = b2.getMethodsByName("func").get(0);
		CtStatement stmt2 = method2.getBody().getStatement(0);
		CtAssignment<?, ?> assignment2 = (CtAssignment<?, ?>) stmt2;
		CtExpression<?> lhs2 = assignment2.getAssigned();
		assertTrue("float".equals(assignment2.getType().getSimpleName()));
		assertTrue("float".equals(lhs2.getType().getSimpleName()));
	}

	@Before
	@After
	public void cleanup() throws IOException {
		FileUtils.deleteDirectory(WORKING_DIR);
	}
}

