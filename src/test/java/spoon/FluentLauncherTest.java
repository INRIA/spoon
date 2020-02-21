package spoon;

import java.io.IOException;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.AstParentConsistencyChecker;

/**
 * FluentLauncherTest
 */
public class FluentLauncherTest {
	@Rule
	public TemporaryFolder folderFactory = new TemporaryFolder();

	// following test cases showcase use cases
	public void useCase1() throws IOException {
		CtModel model = new FluentLauncher().inputResource("src/test/resources/deprecated/input").noClasspath(true)
				.outputDirectory(folderFactory.newFolder().getPath()).buildModel();
	}

	public void useCase2() throws IOException {
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.outputDirectory(folderFactory.newFolder().getPath()).processor(new AbstractProcessor<CtType<?>>() {
					public void process(CtType<?> element) {
						System.out.println(element.toString());
					}
				}).noClasspath(true).buildModel();
	}

	public void useCase3() throws IOException {
		new FluentLauncher().inputResource("src/test/resources/deprecated/input").noClasspath(true)
				.outputDirectory(folderFactory.newFolder().getPath()).buildModel().getUnnamedModule()
				.accept(new AstParentConsistencyChecker());
	}

	public void useCase4() throws IOException {
		CtModel model = new FluentLauncher().inputResource("src/test/resources/deprecated/input").noClasspath(true)
				.outputDirectory(folderFactory.newFolder().getPath()).buildModel();
	}

	/**
	 * shows using the FluentLauncher with different launchers.
	 *
	 * @throws IOException
	 */
	public void useCase7() throws IOException {
		new FluentLauncher(new MavenLauncher(null, null)).outputDirectory(folderFactory.newFolder().getPath()).buildModel();
		IncrementalLauncher launcher = new IncrementalLauncher(null, null, null, false);
		new FluentLauncher(launcher).inputResource("src/test/resources/deprecated/input").noClasspath(true)
				.outputDirectory(folderFactory.newFolder().getPath()).buildModel();
		// now you can still use method from the incrementalLauncher like
		launcher.changesPresent();
	}
}
