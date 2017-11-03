package spoon.test.model;
import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;

public class IncrementalBuildTest {
	@Test
	public void testIncrementalBuild() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/incremental/in");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.createCompiler();
		launcher.setSourceOutputDirectory("./src/test/resources/incremental/out");
		launcher.getModelBuilder().setBuildOnlyOutdatedFiles(true);
		launcher.buildModel();
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
	}
}
