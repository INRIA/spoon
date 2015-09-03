package spoon.test.exceptions;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.InvalidClassPathException;
import spoon.compiler.ModelBuildingException;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
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
			factory
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
			spoon.getFactory().getEnvironment().setLevel("OFF");
			SpoonCompiler comp = spoon.createCompiler();
			comp.setSourceClasspath("does_not_exist.jar");
			fail();
		} catch (InvalidClassPathException e) {
		}

		try {
			Launcher spoon = new Launcher();
			spoon.getFactory().getEnvironment().setLevel("OFF");
			SpoonCompiler comp = spoon.createCompiler();
			comp.setSourceClasspath("src");
		} catch (InvalidClassPathException e) {
			fail();
			// you're trying to give source code in the classpath, this should be accepted but causes a warn log entry
		}
	}
	
	@Test(expected=ModelBuildingException.class)
	public void testExceptionDuplicateClass() throws Exception {
			Launcher spoon = new Launcher();
			Factory factory = spoon.createFactory();
			
			// contains twice the same class in the same package
			// an exception should be thrown, even in noclasspath mode
			spoon.createCompiler(
					factory,
					SpoonResourceHelper
							.resources("./src/test/resources/spoon/test/duplicateclasses/Foo.java", "./src/test/resources/spoon/test/duplicateclasses/Bar.java"))
					.build();
	}

}
