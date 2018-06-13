package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import spoon.Launcher;
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
	final File WORKING_DIR = Files.createTempDir();
	final File CACHE_DIR = new File(WORKING_DIR, "cache");

	private CtType<?> getTypeByName(Collection<CtType<?>> types, String name) {
		return types.stream().filter(t -> t.getSimpleName().equals(name)).findFirst().get();
	}

	@Test
	public void testCache() throws IOException {
		// Build model from A.java, B.java, C.java, D.java, and then load the same model from cache several times.
		FileUtils.deleteDirectory(WORKING_DIR);
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Launcher launcher1 = new Launcher();
		launcher1.addInputResource(WORKING_DIR.getAbsolutePath());
		launcher1.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher1.getEnvironment().setIncremental(true);

		CtModel originalModel = launcher1.buildModel();
		assertTrue(launcher1.isChangesPresent());

		Launcher launcher2 = new Launcher();
		launcher2.addInputResource(WORKING_DIR.getAbsolutePath());
		launcher2.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher2.getEnvironment().setIncremental(true);
		CtModel cachedModel = launcher2.buildModel();
		assertFalse(launcher2.isChangesPresent());

		assertEquals(originalModel.getAllTypes(), cachedModel.getAllTypes());

		Launcher launcher3 = new Launcher();
		launcher3.addInputResource(WORKING_DIR.getAbsolutePath());
		launcher3.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher3.getEnvironment().setIncremental(true);
		CtModel cachedCachedModel = launcher3.buildModel();
		assertFalse(launcher3.isChangesPresent());

		assertEquals(originalModel.getAllTypes(), cachedCachedModel.getAllTypes());

		for (CtType<?> t : cachedCachedModel.getAllTypes()) {
			assertNotNull(t.getPosition());
			assertNotNull(t.getPosition().getFile());
			assertTrue(t.getPosition().getLine() != -1);
		}
	}

	@Test
	public void testIncremental1() throws IOException, InterruptedException {
		// Build model from A.java, B.java, C.java, D.java, then change D.java => load A, B, C from cache and build D.
		FileUtils.deleteDirectory(WORKING_DIR);
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Launcher launcher1 = new Launcher();
		launcher1.addInputResource(WORKING_DIR.getAbsolutePath());
		launcher1.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher1.getEnvironment().setIncremental(true);
		CtModel originalModel = launcher1.buildModel();
		assertTrue(launcher1.isChangesPresent());

		TimeUnit.MILLISECONDS.sleep(1000);
		FileUtils.copyFile(new File(CHANGED_FILES_DIR, "D.java"), new File(WORKING_DIR, "D.java"), true);
		FileUtils.touch(new File(WORKING_DIR, "D.java"));

		Launcher launcher2 = new Launcher();
		launcher2.addInputResource(WORKING_DIR.getAbsolutePath());
		launcher2.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher2.getEnvironment().setIncremental(true);
		CtModel newModel = launcher2.buildModel();
		assertTrue(launcher2.isChangesPresent());

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
		assertEquals(a1, a2);
		assertEquals(b1, b2);
		assertEquals(c1, c2);
		assertNotEquals(d1, d2);

		assertEquals(0, d1.getDeclaredFields().size());
		assertEquals(2, d2.getDeclaredFields().size());
		assertEquals(0, d1.getMethods().size());
		assertEquals(1, d2.getMethods().size());
	}


	@Test
	public void testIncremental2() throws IOException {
		// Build model from A.java, B.java, C.java, then remove C.java and add D.java
		FileUtils.deleteDirectory(WORKING_DIR);
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Set<File> inputResources = new HashSet<>();
		inputResources.add(new File(WORKING_DIR, "A.java"));
		inputResources.add(new File(WORKING_DIR, "B.java"));
		inputResources.add(new File(WORKING_DIR, "C.java"));

		Launcher launcher1 = new Launcher();
		for (File inputResource : inputResources) {
			launcher1.addInputResource(inputResource.getAbsolutePath());
		}

		launcher1.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher1.getEnvironment().setIncremental(true);
		CtModel originalModel = launcher1.buildModel();
		assertTrue(launcher1.isChangesPresent());
		assertEquals(3, originalModel.getAllTypes().size());

		inputResources.removeIf(f -> f.getName().equals("C.java"));
		inputResources.add(new File(WORKING_DIR, "D.java"));

		Launcher launcher2 = new Launcher();
		for (File inputResource : inputResources) {
			launcher2.addInputResource(inputResource.getAbsolutePath());
		}
		launcher2.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher2.getEnvironment().setIncremental(true);

		CtModel newModel = launcher2.buildModel();
		assertTrue(launcher2.isChangesPresent());

		assertEquals(3, newModel.getAllTypes().size());

		Collection<CtType<?>> types1 = originalModel.getAllTypes();
		Collection<CtType<?>> types2 = newModel.getAllTypes();

		CtType<?> a1 = getTypeByName(types1, "A");
		CtType<?> b1 = getTypeByName(types1, "B");
		CtType<?> c1 = getTypeByName(types1, "C");
		CtType<?> a2 = getTypeByName(types2, "A");
		CtType<?> b2 = getTypeByName(types2, "B");
		CtType<?> d2 = getTypeByName(types2, "D");
		assertEquals(a1, a2);
		assertEquals(b1, b2);
		assertNotEquals(c1, d2);
	}


	@Test
	public void testIncremental3() throws IOException, InterruptedException {
		// Build model from A.java, B.java, C.java, then change type of field val in C.
		// B refers to C, so we should check reference resolution in B as well.
		FileUtils.deleteDirectory(WORKING_DIR);
		FileUtils.copyDirectory(ORIGINAL_FILES_DIR, WORKING_DIR);

		Set<File> inputResources = new HashSet<>();
		inputResources.add(new File(WORKING_DIR, "A.java"));
		inputResources.add(new File(WORKING_DIR, "B.java"));
		inputResources.add(new File(WORKING_DIR, "C.java"));

		Launcher launcher1 = new Launcher();
		for (File inputResource : inputResources) {
			launcher1.addInputResource(inputResource.getAbsolutePath());
		}
		launcher1.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher1.getEnvironment().setIncremental(true);

		CtModel originalModel = launcher1.buildModel();
		assertTrue(launcher1.isChangesPresent());

		CtType<?> c1 = getTypeByName(originalModel.getAllTypes(), "C");
		assertEquals("int", c1.getField("val").getType().getSimpleName());

		CtType<?> b1 = getTypeByName(originalModel.getAllTypes(), "B");
		CtMethod<?> method1 = b1.getMethodsByName("func").get(0);
		CtStatement stmt1 = method1.getBody().getStatement(0);
		CtAssignment<?, ?> assignment1 = (CtAssignment<?, ?>) stmt1;
		CtExpression<?> lhs1 = assignment1.getAssigned();
		assertEquals("int", assignment1.getType().getSimpleName());
		assertEquals("int", lhs1.getType().getSimpleName());

		TimeUnit.MILLISECONDS.sleep(1000);
		FileUtils.copyFile(new File(CHANGED_FILES_DIR, "C.java"), new File(WORKING_DIR, "C.java"), true);
		FileUtils.touch(new File(WORKING_DIR, "C.java"));

		Launcher launcher2 = new Launcher();
		for (File inputResource : inputResources) {
			launcher2.addInputResource(inputResource.getAbsolutePath());
		}
		launcher2.getEnvironment().setCacheDirectory(CACHE_DIR);
		launcher2.getEnvironment().setIncremental(true);
		CtModel newModel = launcher2.buildModel();
		assertTrue(launcher2.isChangesPresent());

		CtType<?> c2 = getTypeByName(newModel.getAllTypes(), "C");
		assertEquals("float", c2.getField("val").getType().getSimpleName());

		CtType<?> b2 = getTypeByName(newModel.getAllTypes(), "B");
		CtMethod<?> method2 = b2.getMethodsByName("func").get(0);
		CtStatement stmt2 = method2.getBody().getStatement(0);
		CtAssignment<?, ?> assignment2 = (CtAssignment<?, ?>) stmt2;
		CtExpression<?> lhs2 = assignment2.getAssigned();
		assertEquals("float", assignment2.getType().getSimpleName());
		assertEquals("float", lhs2.getType().getSimpleName());
	}

	@After
	public void cleanup() throws IOException {
		FileUtils.deleteDirectory(WORKING_DIR);
	}
}

