package spoon;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MavenLauncherTest {
	@Test
	public void spoonMavenLauncherTest() {
		// without the tests
		MavenLauncher launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.APP_SOURCE);

		assertEquals(7, launcher.getEnvironment().getSourceClasspath().length);
		// 52 because of the sub folders of src/main/java
		assertEquals(54, launcher.getModelBuilder().getInputSources().size());

		// with the tests
		launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		// 236 because of the sub folders of src/main/java and src/test/java
		assertTrue("size: "+launcher.getModelBuilder().getInputSources().size(), launcher.getModelBuilder().getInputSources().size() >= 220);

		// specify the pom.xml
		launcher = new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
		assertEquals(8, launcher.getEnvironment().getComplianceLevel());
	}

	@Test
	public void multiModulesProjectTest() {
		MavenLauncher launcher = new MavenLauncher("./src/test/resources/maven-launcher/pac4j", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		assertEquals(8, launcher.getEnvironment().getComplianceLevel());
		assertEquals(0, launcher.getModelBuilder().getInputSources().size());
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
		String lookingFor = "junit/junit/4.12/junit-4.12.jar";

		boolean findIt = false;
		for (String s : classpath) {
			findIt = findIt || s.contains(lookingFor);
		}

		assertTrue("Content of classpath: "+ StringUtils.join(classpath,":"), findIt);
	}
}
