package spoon.support.compiler.classpath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

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
	public void testTemplateClasspath()
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {

		// load protected method which computes the template classpath
		Method method = this.compilerClass
				.getDeclaredMethod("computeTemplateClasspath");
		method.setAccessible(true);

		this.compiler.setTemplateClasspath(this.systemClasspath);

		Assert.assertEquals(TEST_CLASSPATH, method.invoke(this.compiler));
	}

	@Test
	public void testSourceClasspath()
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {

		// load protected method which computes the source classpath
		Method method = this.compilerClass
				.getDeclaredMethod("computeJdtClassPath");
		method.setAccessible(true);

		this.compiler.setSourceClasspath(this.systemClasspath);

		Assert.assertEquals(TEST_CLASSPATH, method.invoke(this.compiler));
	}
}
