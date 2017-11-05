package spoon.test.model;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.CtModel;

public class IncrementalBuildTest {
	// IN_DIR contains First.java, Second.java and Third.java
	private static final String IN_DIR = "./src/test/resources/incremental/in";
	private static final String OUT_DIR = "./src/test/resources/incremental/out";
	
	@Test
	public void testIncrementalBuild() throws IOException, InterruptedException {
		int expectedNumOfTypes = firstBuild();
		int n = secondBuild();
		assertEquals(expectedNumOfTypes, n); // And here test fails 3 != 1
	}
	
	public int firstBuild() throws IOException {
		File out = new File(OUT_DIR);
		if (out.exists()) {
			// Clean OUT_DIR before the first build
			FileUtils.cleanDirectory(out);
		}
		
		Launcher launcher = new Launcher();
		launcher.addInputResource(IN_DIR);
		launcher.createCompiler();
		launcher.setSourceOutputDirectory(OUT_DIR);
		CtModel model = launcher.buildModel();
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
		
		return model.getAllTypes().size();
	}
	
	public int secondBuild() throws IOException, InterruptedException {
		// Wait a bit
		TimeUnit.MILLISECONDS.sleep(1000);
		
		// Modify First.java file
		Files.write(Paths.get(IN_DIR + "/First.java"), "//some modification\n".getBytes(), StandardOpenOption.APPEND);
		
		Launcher launcher = new Launcher();
		launcher.addInputResource(IN_DIR);
		launcher.createCompiler();
		launcher.setSourceOutputDirectory(OUT_DIR);
		launcher.getModelBuilder().setBuildOnlyOutdatedFiles(true);
		CtModel model = launcher.buildModel();
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
		
		return model.getAllTypes().size();
	}
}
