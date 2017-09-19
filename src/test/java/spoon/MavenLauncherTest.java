package spoon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MavenLauncherTest {
	@Test
	public void spoonMavenLauncherTest() {
		// without the tests
		MavenLauncher launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.APP_SOURCE);

		assertEquals(8, launcher.getEnvironment().getComplianceLevel());

		assertEquals(10, launcher.getEnvironment().getSourceClasspath().length);
		// 54 because of the sub folders of src/main/java
		assertEquals(54, launcher.getModelBuilder().getInputSources().size());

		// with the tests
		launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
		assertEquals(10, launcher.getEnvironment().getSourceClasspath().length);
		// 235 because of the sub folders of src/main/java and src/test/java
		assertEquals(235, launcher.getModelBuilder().getInputSources().size());
	}

	@Test(expected = SpoonException.class)
	public void MavenLauncherOnFileTest() {
		new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
	}

	@Test(expected = SpoonException.class)
	public void MavenLauncherOnDirectoryWithoutPomTest() {
		new MavenLauncher("./src", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
	}
}
