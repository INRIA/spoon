package spoon.test.exceptions;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.InvalidClassPathException;
import spoon.compiler.ModelBuildingException;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

public class ExceptionTest {

	@Test
	public void testExceptionIfNotCompilable() throws Exception {
		try {
			Launcher spoon = new Launcher();
			Factory factory = spoon.createFactory();
			spoon.createCompiler(
					factory,
					SpoonResourceHelper
							.resources("./src/test/resources/spoon/test/exceptions/ClassWithError.java"))
					.build();
			fail();
		} catch (ModelBuildingException e) {
			// perfect
		}

	}

	@Test
	public void testExceptionNoFile() throws Exception {
		try {
			Launcher spoon = new Launcher();
			Factory factory = spoon.createFactory();
			spoon.createCompiler(
					factory,
					SpoonResourceHelper
							.resources("this_file_does_not_exist.java"))
					.build();
			fail();
		} catch (FileNotFoundException e) {
			// perfect
		}

	}

	@Test
	public void testExceptionInSnippet() {
		try {
			Factory factory = TestUtils.createFactory();
			CtClass<?> clazz = factory
					.Code()
					.createCodeSnippetStatement(
							"" + "class X {" + "public void foo() {"
									+ " int x=Foo;" // does not compile here
									+ "}};").compile();
			fail();
		} catch (ModelBuildingException e) {
			// perfect
		}
	}

	@Test
	public void testExceptionInvalidAPI() throws Exception {
		try {
			Launcher spoon = new Launcher();
			SpoonCompiler comp = spoon.createCompiler();
			comp.setSourceClasspath("does_not_exist.jar");
			fail();
		} catch (InvalidClassPathException e) {
		}

		try {
			Launcher spoon = new Launcher();
			SpoonCompiler comp = spoon.createCompiler();
			comp.setSourceClasspath("src");
			fail();
		} catch (InvalidClassPathException e) {
			// you're trying to give source code in the classpath, this should be given to addInputSource
		}
	}
}
