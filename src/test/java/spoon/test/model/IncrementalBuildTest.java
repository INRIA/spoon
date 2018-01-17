package spoon.test.model;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

public class IncrementalBuildTest {

	@Test
	public void testIncremental() throws Exception {
		final File TEST_DIR = new File("./src/test/resources/incremental-test");
		final File PROJECT_DIR = new File(TEST_DIR, "temp");

		FileUtils.deleteDirectory(PROJECT_DIR);
		FileUtils.forceMkdir(PROJECT_DIR);

		File cacheDirectory = new File(PROJECT_DIR, "incremental-cache");
		Set<String> inputResources = Collections.singleton(PROJECT_DIR.getPath());
		Set<String> inputClasspath = new HashSet<>();

		// Prepare first build
		FileUtils.copyFileToDirectory(new File(TEST_DIR, "A.java"), PROJECT_DIR);
		FileUtils.copyFileToDirectory(new File(TEST_DIR, "B.java"), PROJECT_DIR);
		FileUtils.copyFileToDirectory(new File(TEST_DIR, "C.java"), PROJECT_DIR);
		FileUtils.copyFileToDirectory(new File(TEST_DIR, "Main.java"), PROJECT_DIR);

		// Run first build
		Launcher launcher = new Launcher(inputResources, inputClasspath, cacheDirectory, false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();
		launcher.updateCacheDirectory();

		// Apply some modifications
		FileUtils.forceDelete(new File(PROJECT_DIR, "A.java"));
		TimeUnit.MILLISECONDS.sleep(1000);
		FileUtils.copyFile(new File(TEST_DIR, "A2.java"), new File(PROJECT_DIR, "A.java"));
		FileUtils.touch(new File(PROJECT_DIR, "A.java")); // Modify A type
		FileUtils.forceDelete(new File(PROJECT_DIR, "B.java")); // Remove B type
		FileUtils.copyFileToDirectory(new File(TEST_DIR, "D.java"), PROJECT_DIR); // Add new D type
		FileUtils.forceDelete(new File(PROJECT_DIR, "C.java")); // Remove C type from com.other.pkg

		// Run second build
		Launcher launcher2 = new Launcher(inputResources, inputClasspath, cacheDirectory, false);
		launcher2.getEnvironment().setNoClasspath(true);
		CtModel m = launcher2.buildModel();
		launcher2.updateCacheDirectory();

		// Check stuff
		assertTrue(m.getAllTypes().size() == 3);
		assertTrue(m.getAllTypes().stream().anyMatch(t -> t.getSimpleName().equals("A")));
		assertTrue(m.getAllTypes().stream().anyMatch(t -> t.getSimpleName().equals("D")));
		assertTrue(m.getAllTypes().stream().anyMatch(t -> t.getSimpleName().equals("Main")));
		CtType<?> typeA = m.getAllTypes().stream().filter(t -> t.getSimpleName().equals("A")).findFirst().get();
		assertTrue(typeA.getFields().size() == 1);
		assertTrue(typeA.getField("str").getType().getSimpleName().equals("String"));
		assertTrue(m.getAllPackages().size() == 1);

		FileUtils.deleteDirectory(PROJECT_DIR);
	}
}
