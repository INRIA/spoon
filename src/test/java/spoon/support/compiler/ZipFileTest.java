package spoon.support.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ZipFileTest {

	@Test
	void fetchingSourceFragmentWorks(@TempDir Path tempDir) throws IOException {
		// contract: Fetching an original source code fragment from a zip folder input works
		Launcher launcher = new Launcher();
		launcher.addInputResource(new ZipFolder(createZip(tempDir).toFile()));
		launcher.buildModel();

		CtType<?> type = launcher.getFactory().Type().get("a.Test");
		CtMethod<?> method = type.getMethod("foo");
		assertNotNull(method.getOriginalSourceFragment().getSourceCode());
		assertThat(method.getOriginalSourceFragment().getSourceCode(), containsString("/**"));
		assertThat(method.getOriginalSourceFragment().getSourceCode(), containsString("foo()"));
	}

	private Path createZip(Path tempDir) throws IOException {
		Files.createDirectories(tempDir.resolve("a"));

		Path testFile = tempDir.resolve("a").resolve("Test.java");
		Files.writeString(
			testFile,
			"package a;\n" +
				"class Test {\n" +
				"  /**\n" +
				"   * A foo method.\n" +
				"   *\n" +
				"   * @return some int\n" +
				"   */\n" +
				"  public int foo() {\n" +
				"    return 1 + 1;\n" +
				"  }" +
				"}"
		);

		Path zipPath = tempDir.resolve("test.zip");
		try (FileSystem zip = FileSystems.newFileSystem(
			URI.create("jar:" + zipPath.toUri()),
			Map.of("create", true)
		)) {
			Files.createDirectories(zip.getPath("a"));
			Files.copy(testFile, zip.getPath("a/Test.java"));
		}

		return zipPath;
	}
}
