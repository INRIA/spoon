package spoon.support.compiler.jdt;

import org.junit.Test;

import spoon.Launcher;
import java.io.File;

import static org.junit.Assert.assertTrue;

public class JDTBatchCompilerTest {
	@Test
	public void testCompileGeneratedJavaFile() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/support/compiler/jdt/testclasses/Foo.java");
		launcher.setBinaryOutputDirectory("./target/binaries");
		launcher.getEnvironment().setShouldCompile(true);
		launcher.buildModel();

		launcher.getFactory().Class().create("spoon.Test");
		assertTrue(launcher.getModelBuilder().compile());
		assertTrue(new File("./target/binaries/spoon/Test.class").exists());
	}
}
