package spoon;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.compiler.SpoonResource;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;


import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import spoon.support.compiler.FileSystemFolder;

import static org.junit.Assert.*;

public class MavenLauncherTest {

	// fixme: the test consumes too much memory for now
	// we should reduce its footprint
	@Ignore
	@Test
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
						assertNotNull("Reference to " + ref.getSimpleName() + " point to unresolved type", ref.getTypeDeclaration());
					}
				}
				super.scan(element);
			}
		});
	}

	@Test
	public void spoonMavenLauncherTest() {
		// without the tests
		MavenLauncher launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.APP_SOURCE);

		//contract: classpath is not empty
		assertNotEquals(0, launcher.getEnvironment().getSourceClasspath().length);
		//contract: classpath contains only valid elements
		for(String cpe: launcher.getEnvironment().getSourceClasspath()) {
			assertTrue(new File(cpe).exists());
		}

		// contract: ModelBuilder contains all source folders
		int numberOfJavaSrcFolder = new FileSystemFolder("src/main/java/spoon")
				.getAllFiles()
				.stream()
				.map(SpoonResource::getParent)
				.collect(Collectors.toSet())
				.size();
		assertTrue("size: " + launcher.getModelBuilder().getInputSources().size(), launcher.getModelBuilder().getInputSources().size() >= (numberOfJavaSrcFolder));

		// with the tests, and test that if mavenProject leads to a directory containing a pom.xml it works
		launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);

		//contract: classpath is not empty
		assertNotEquals(0, launcher.getEnvironment().getSourceClasspath().length);
		//contract: classpath contains only valid elements
		for(String cpe: launcher.getEnvironment().getSourceClasspath()) {
			assertTrue(new File(cpe).exists());
		}

		// number of the sub folders of src/main/java and src/test/java
		int numberOfJavaTestFolder = new FileSystemFolder("src/test/java/spoon")
				.getAllFiles()
				.stream()
				.map(SpoonResource::getParent)
				.collect(Collectors.toSet())
				.size();
		assertTrue("size: " + launcher.getModelBuilder().getInputSources().size(), launcher.getModelBuilder().getInputSources().size() >= (numberOfJavaSrcFolder + numberOfJavaTestFolder));

		// specify the pom.xml
		launcher = new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
		assertEquals(8, launcher.getEnvironment().getComplianceLevel());

		// without calling maven to generate classpath
		launcher = new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE, new String[]{});
		assertEquals(0, launcher.getEnvironment().getSourceClasspath().length);
	}

	@Test
	public void multiModulesProjectTest() {
		MavenLauncher launcher = new MavenLauncher("./src/test/resources/maven-launcher/pac4j", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		assertEquals(8, launcher.getEnvironment().getComplianceLevel());
		assertEquals(0, launcher.getModelBuilder().getInputSources().size());
		assertEquals(166, launcher.getEnvironment().getSourceClasspath().length);
	}

	@Test(expected = SpoonException.class)
	public void mavenLauncherOnANotExistingFileTest() {
		new MavenLauncher("./pomm.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
	}

	@Test(expected = SpoonException.class)
	public void mavenLauncherOnDirectoryWithoutPomTest() {
		new MavenLauncher("./src", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
	}

	@Test
	public void mavenLauncherTestWithVerySimpleProject() {
		MavenLauncher launcher = new MavenLauncher("./src/test/resources/maven-launcher/very-simple", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		assertEquals(1, launcher.getModelBuilder().getInputSources().size());
	}

	@Test
	public void mavenLauncherTestMultiModulesAndVariables() {
		// contract: variables coming from parent should be resolved
		MavenLauncher launcher = new MavenLauncher("./src/test/resources/maven-launcher/pac4j/pac4j-config", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		List<String> classpath = Arrays.asList(launcher.getEnvironment().getSourceClasspath());
		// in order to work on CI, make sure the version is the same in Spoon pom.xml
		// else, we cannot guarantee that the dependency is present in .m2 cache and the test might fail

		String lookingFor = Paths.get("junit", "junit", "4.12", "junit-4.12.jar").toString();

		boolean findIt = false;
		for (String s : classpath) {
			findIt = findIt || s.contains(lookingFor);
		}

		assertTrue("Content of classpath: " + StringUtils.join(classpath, ":"), findIt);
	}
}
