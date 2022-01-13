package spoon.test.prettyprinter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLoop;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import static org.hamcrest.MatcherAssert.assertThat;

public class LoopSniperJavaPrettyPrinterTest {

	private static final Path INPUT_PATH = Paths.get("src/test/java/");
	private static final Path OUTPUT_PATH = Paths.get("target/test-output");

	@BeforeAll
	public static void setup() throws IOException {
		FileUtils.deleteDirectory(OUTPUT_PATH.toFile());
	}

	@Test
	public void forNoBraces() throws IOException {
		runSniperJavaPrettyPrinter("spoon/test/prettyprinter/testclasses/loop/ForNoBraces.java");
	}

	@Test
	public void forEachBraces() throws IOException {
		runSniperJavaPrettyPrinter("spoon/test/prettyprinter/testclasses/loop/ForEachNoBraces.java");
	}

	@Test
	public void whileNoBraces() throws IOException {
		runSniperJavaPrettyPrinter("spoon/test/prettyprinter/testclasses/loop/WhileNoBraces.java");
	}

	
	@Test
	@Disabled
	public void whileWithBraces() throws IOException {
		runSniperJavaPrettyPrinter("spoon/test/prettyprinter/testclasses/loop/WhileWithBraces.java");
	}

	private void runSniperJavaPrettyPrinter(String path) throws IOException {
		final Launcher launcher = new Launcher();
		final Environment e = launcher.getEnvironment();
		e.setLevel("INFO");
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(e));

		launcher.addInputResource(INPUT_PATH.resolve(path).toString());
		launcher.setSourceOutputDirectory(OUTPUT_PATH.toString());

		launcher.run();

		// Verify result file exist and is not empty
		assertThat("Output file for " + path + " should exist",
				OUTPUT_PATH.resolve(path).toFile().exists(), CoreMatchers.equalTo(true));

		String content = new String(Files.readAllBytes(OUTPUT_PATH.resolve(path)),
				StandardCharsets.UTF_8);

		assertThat(content, CoreMatchers.notNullValue());
		assertThat("Result class should not be empty", content.trim(),
				CoreMatchers.not(CoreMatchers.equalTo("")));
	}

	public class LoopProcessor extends AbstractProcessor<CtLoop> {
		@Override
		public void process(CtLoop element) {
			CtBlock block = element.getFactory().Code().getOrCreateCtBlock(element.getBody());
			element.setBody(block);
			System.out.println(element);
		}
	}

}

