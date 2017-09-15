package spoon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MavenLauncherTest {
	@Test
	public void spoonMavenLauncherTest() {
		// without the tests
		MavenLauncher launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.SOURCE);
		assertEquals(10, launcher.getEnvironment().getSourceClasspath().length);
		// 54 because of the sub folder
		assertEquals(54, launcher.getModelBuilder().getInputSources().size());

		// with the tests
		launcher = new MavenLauncher("./", MavenLauncher.SOURCE_TYPE.ALL);
		assertEquals(10, launcher.getEnvironment().getSourceClasspath().length);
		// 54 because of the sub folder
		assertEquals(235, launcher.getModelBuilder().getInputSources().size());
	}
}
