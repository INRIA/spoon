package spoon;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;


import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

		assertEquals(26, launcher.getEnvironment().getSourceClasspath().length);

		// 56 because of the sub folders of src/main/java
		assertEquals(59, launcher.getModelBuilder().getInputSources().size());

		// with the tests
		launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);

		assertEquals(33, launcher.getEnvironment().getSourceClasspath().length);

		// 236 because of the sub folders of src/main/java and src/test/java
		assertTrue("size: " + launcher.getModelBuilder().getInputSources().size(), launcher.getModelBuilder().getInputSources().size() >= 220);

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
