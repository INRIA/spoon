package spoon.support.compiler.classpath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.builder.ClasspathOptions;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class ComputeClasspathTest {

	private final static String TEST_CLASSPATH =
			"./src/test/java/spoon/test/annotation/" +
					File.pathSeparator +
					"./src/test/java/spoon/test/api/" +
					File.pathSeparator +
					"./src/test/java/spoon/test/arrays/" +
					File.pathSeparator +
					"./src/test/java/spoon/test/casts/" +
					File.pathSeparator;

	private JDTBasedSpoonCompiler compiler;
	private Class<? extends JDTBasedSpoonCompiler> compilerClass;

	private String[] systemClasspath;

	@Before
	public void setUp() {
		Launcher launcher = new Launcher() {

			public SpoonCompiler createCompiler(Factory factory) {
				return new JDTBasedSpoonCompiler(factory);
			}

		};
		launcher.getEnvironment().setLevel("OFF");

		this.compiler = (JDTBasedSpoonCompiler) launcher.createCompiler();
		this.compilerClass = compiler.getClass();

		this.systemClasspath = TEST_CLASSPATH.split(File.pathSeparator);
	}

	@Test
	public void testSourceClasspath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final ClasspathOptions options = new ClasspathOptions().classpath(systemClasspath);
		Assert.assertEquals("-cp " + TEST_CLASSPATH, String.join(" ", options.build()));
	}
}
