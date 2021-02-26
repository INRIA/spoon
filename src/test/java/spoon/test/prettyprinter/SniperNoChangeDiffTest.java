package spoon.test.prettyprinter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.support.sniper.SniperJavaPrettyPrinter;

@RunWith(value = Parameterized.class)
public class SniperNoChangeDiffTest {
	private static final Path INPUT_PATH = Paths.get("src/test/java/");
	private static final Path OUTPUT_PATH = Paths.get("target/test-output");

	@Parameter(value = 0)
	public String fileName;

	@Parameters
	public static Collection<Object[]> data() {
		Path path = INPUT_PATH.resolve("spoon/test/prettyprinter/testclasses/difftest");
		List<Object[]> result = new ArrayList<>();

		for (File file : FileUtils.listFiles(path.toFile(), null, false)) {
			result.add(new String[] { file.getName() });
		}
		return result;
	}

	@BeforeClass
	public static void setup() throws IOException {
		FileUtils.deleteDirectory(OUTPUT_PATH.toFile());
	}

	/**
	 * Test various syntax by doing an change to every element that should not
	 * result in any change in source. This forces the sniper printer to recreate
	 * the output. Aseert that the output is the same as the input.
	 */
	@Test
	public void noChangeDiffTest() throws IOException {
		File file = INPUT_PATH.resolve("spoon/test/prettyprinter/testclasses/difftest").resolve(fileName).toFile();
		File outputFile = OUTPUT_PATH.resolve("spoon/test/prettyprinter/testclasses/difftest").resolve(fileName)
				.toFile();
		final Launcher launcher = new Launcher();
		final Environment e = launcher.getEnvironment();
		e.setLevel("INFO");
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(e));

		launcher.addInputResource(file.toString());
		launcher.setSourceOutputDirectory(OUTPUT_PATH.toString());
		launcher.addProcessor(new AbstractProcessor<CtElement>() {
			public void process(CtElement element) {
				// Do a no-op change, this will force the sniper printer to update the source
				SourcePosition pos = element.getPosition();
				element.setPosition(SourcePosition.NOPOSITION);
				element.setPosition(pos);
			};
		});
		launcher.run();

		assertTrue("File " + outputFile + " is different", FileUtils.contentEquals(file, outputFile));
	}
}
