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
package spoon;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spoon.compiler.SpoonResource;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.SpoonPom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.test.TemporaryDirectoryExecutionListener.TEMPDIR;

public class MavenLauncherTest {

	// fixme: the test consumes too much memory for now
	// we should reduce its footprint

	@Test
	@Disabled
	public void testTypeResolution() {
		MavenLauncher launcher = new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		launcher.buildModel();
		launcher.getModel().getRootPackage().accept(new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element instanceof CtTypeReference) {
					CtTypeReference ref = (CtTypeReference) element;
					if (ref.getSimpleName().contains(".")) { //Excludes nulltype, generics, ? extends E, etc
						//contract: For a maven project with a correct classpath, all type references should point to a resolvable type
						assertNotNull(ref.getTypeDeclaration(), "Reference to " + ref.getSimpleName() + " point to unresolved type");
					}
				}
				super.scan(element);
			}
		});
	}

	@Test
	public void spoonMavenLauncherTest() throws IOException {
		String targetPathString = copyResourceToFolder(TEMPDIR, ".");
		Path targetPath = Path.of(targetPathString);

		// without the tests
		MavenLauncher launcher = new MavenLauncher(targetPathString, MavenLauncher.SOURCE_TYPE.APP_SOURCE);

		//contract: classpath is not empty
		assertNotEquals(0, launcher.getEnvironment().getSourceClasspath().length);
		//contract: classpath contains only valid elements
		for (String cpe : launcher.getEnvironment().getSourceClasspath()) {
			assertTrue(new File(cpe).exists());
		}

		// contract: ModelBuilder contains all source folders
		int numberOfJavaSrcFolder = new FileSystemFolder("src/main/java/spoon")
			.getAllFiles()
			.stream()
			.map(SpoonResource::getParent)
			.collect(Collectors.toSet())
			.size();

		assertTrue(
			launcher.getModelBuilder().getInputSources().size() >= numberOfJavaSrcFolder,
			"size: " + launcher.getModelBuilder().getInputSources().size()
		);

		// with the tests, and test that if mavenProject leads to a directory containing a pom.xml it works
		launcher = new MavenLauncher(targetPathString, MavenLauncher.SOURCE_TYPE.ALL_SOURCE);

		//contract: classpath is not empty
		assertNotEquals(0, launcher.getEnvironment().getSourceClasspath().length);
		//contract: classpath contains only valid elements
		for (String cpe : launcher.getEnvironment().getSourceClasspath()) {
			assertTrue(new File(cpe).exists());
		}

		// number of the sub folders of src/main/java and src/test/java
		int numberOfJavaTestFolder = new FileSystemFolder("src/test/java/spoon")
			.getAllFiles()
			.stream()
			.map(SpoonResource::getParent)
			.collect(Collectors.toSet())
			.size();
		assertTrue(launcher.getModelBuilder().getInputSources().size() >= numberOfJavaSrcFolder + numberOfJavaTestFolder, "size: " + launcher.getModelBuilder().getInputSources().size());

		// specify the pom.xml
		launcher = new MavenLauncher(
			targetPath.resolve("pom.xml").toString(),
			MavenLauncher.SOURCE_TYPE.APP_SOURCE
		);
		assertEquals(8, launcher.getEnvironment().getComplianceLevel());

		// specify the pom.xml
		launcher = new MavenLauncher(
			targetPath.resolve("src/test/resources/maven-launcher/java-11/pom.xml").toString(),
			MavenLauncher.SOURCE_TYPE.APP_SOURCE
		);
		assertEquals(11, launcher.getEnvironment().getComplianceLevel());

		// without calling maven to generate classpath
		launcher = new MavenLauncher(
			targetPath.resolve("pom.xml").toString(),
			MavenLauncher.SOURCE_TYPE.APP_SOURCE,
			new String[]{}
		);
		assertEquals(0, launcher.getEnvironment().getSourceClasspath().length);
	}

	@Test
	public void multiModulesProjectTest() throws IOException {
		MavenLauncher launcher = new MavenLauncher(
			copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/pac4j"),
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		assertEquals(8, launcher.getEnvironment().getComplianceLevel());
		assertEquals(0, launcher.getModelBuilder().getInputSources().size());
		assertEquals(166, launcher.getEnvironment().getSourceClasspath().length);
	}

	@Test
	public void mavenLauncherOnANotExistingFileTest() {
		assertThrows(
			SpoonException.class,
			() -> new MavenLauncher(
				TEMPDIR.resolve("pom.xml").toAbsolutePath().toString(),
				MavenLauncher.SOURCE_TYPE.APP_SOURCE
			)
		);
	}

	@Test
	public void mavenLauncherOnDirectoryWithoutPomTest() {
		assertThrows(
			SpoonException.class,
			() -> new MavenLauncher(
				TEMPDIR.toAbsolutePath().toString(),
				MavenLauncher.SOURCE_TYPE.APP_SOURCE
			)
		);
	}

	@Test
	public void testSystemDependency() throws IOException {
		//contract: scope dependencies are added to classpath
		MavenLauncher launcher = new MavenLauncher(
			copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/system-dependency"),
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		assertEquals(1, launcher.getEnvironment().getSourceClasspath().length);
		assertTrue(Path.of(launcher.getEnvironment().getSourceClasspath()[0]).endsWith(Path.of("lib/bridge-method-annotation-1.13.jar")));
	}

	@Test
	public void testForceRefresh() throws IOException {
		String targetPath = copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/system-dependency");

		// ensure classpath file exists so first constructor invocation won't build classpath
		Files.writeString(Path.of(targetPath).resolve("spoon.classpath.tmp"), "");

		// contract: classpath is not built
		MavenLauncher launcher = new MavenLauncher(
			targetPath,
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		assertEquals(0, launcher.getEnvironment().getSourceClasspath().length);

		// contract: calling constructor with forceRefresh=true should result in classpath being rebuilt
		MavenLauncher newLauncher = new MavenLauncher(
			targetPath,
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE,
			true
		);
		assertEquals(1, newLauncher.getEnvironment().getSourceClasspath().length);
	}

	@Test
	public void testRebuildClasspath() throws IOException {
		String targetPath = copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/system-dependency");

		// ensure classpath file exists so first constructor invocation won't build classpath
		Files.writeString(Path.of(targetPath).resolve("spoon.classpath.tmp"), "");

		// contract: classpath is not built
		MavenLauncher launcher = new MavenLauncher(
			targetPath,
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		assertEquals(0, launcher.getEnvironment().getSourceClasspath().length);

		// contract: classpath should be rebuilt
		launcher.rebuildClasspath();
		assertEquals(1, launcher.getEnvironment().getSourceClasspath().length);
	}

	@Test
	public void mavenLauncherTestWithVerySimpleProject() throws IOException {
		MavenLauncher launcher = new MavenLauncher(
			copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/very-simple"),
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		assertEquals(1, launcher.getModelBuilder().getInputSources().size());
	}

	@Test
	public void testPomSourceDirectory() throws IOException {
		MavenLauncher launcher = new MavenLauncher(
			copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/source-directory"),
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		assertEquals(2, launcher.getModelBuilder().getInputSources().size());
	}

	@Test
	public void mavenLauncherTestMultiModulesAndVariables() throws IOException {
		// contract: variables coming from parent should be resolved
		MavenLauncher launcher = new MavenLauncher(
			copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/pac4j/pac4j-config"),
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		List<String> classpath = Arrays.asList(launcher.getEnvironment().getSourceClasspath());

		// we cannot guarantee that the dependency is present in .m2 cache and the test might fail
		// see https://github.com/INRIA/spoon/issues/3289

		String lookingFor = Paths.get("junit", "junit", "4.12", "junit-4.12.jar").toString();

		boolean findIt = false;
		for (String s : classpath) {
			findIt = findIt || s.contains(lookingFor);
		}

		assertTrue(findIt, "Content of classpath: " + StringUtils.join(classpath, ":"));
	}

	@Test
	public void testGuessMavenHome() {
		// contract: it should correctly fetch path to maven home
		String pathToMavenHome = SpoonPom.guessMavenHome();
		File mavenHome = new File(pathToMavenHome);
		assertTrue(mavenHome.exists());
		assertTrue(mavenHome.isDirectory());
	}

	@Test
	void mavenLauncherPassesEnvironmentVariables() throws IOException {
		MavenLauncher launcher = new MavenLauncher(
			copyResourceToFolder(TEMPDIR, "./src/test/resources/maven-launcher/with-environment-variables"),
			MavenLauncher.SOURCE_TYPE.ALL_SOURCE
		);
		launcher.setEnvironmentVariable("SPOON_VERSION", "10.1.0");
		launcher.rebuildClasspath();

		boolean containsSpoonDependency = Arrays
			.stream(launcher.getEnvironment().getSourceClasspath())
			.anyMatch(it -> it.matches(".*fr.inria.gforge.spoon.spoon-core.10.1.0.spoon-core-10.1.0.jar.*"));

		assertTrue(
			containsSpoonDependency,
			"Spoon dependency not found. Was the environment variable set? Classpath: "
				+ Arrays.toString(launcher.getEnvironment().getSourceClasspath())
		);
	}

	private static String copyResourceToFolder(Path tempDir, String resourcePath) throws IOException {
		FileUtils.copyDirectory(
			new File(resourcePath),
			tempDir.toFile()
		);
		return tempDir.toAbsolutePath().toString();
	}
}
