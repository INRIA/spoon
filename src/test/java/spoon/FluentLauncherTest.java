package spoon;

import java.io.File;
import java.io.FileNotFoundException;

import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;

/**
 * FluentLauncherTest
 */
public class FluentLauncherTest {
	// following testcases showcase usecases
	public void useCase1() throws FileNotFoundException {
		CtModel model = new FluentLauncher().inputResource("fooo")
				.inputResource(SpoonResourceHelper.createResource(new File("test"))).noClasspath(true)
				.outputDirectory("foo/out").buildModel();
	}
}
