/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.exceptions;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.InvalidClassPathException;
import spoon.compiler.ModelBuildingException;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.createFactory;

public class ExceptionTest {

	@Test
	public void testExceptionIfNotCompilable() throws Exception {
		try {
			Launcher spoon = new Launcher();
			Factory factory = spoon.createFactory();
			factory.getEnvironment().setNoClasspath(false);
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
	public void testExceptionNoFile() {
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
			Factory factory = createFactory();
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
	public void testExceptionInvalidAPI() {
		try {
			Launcher spoon = new Launcher();
			spoon.getFactory().getEnvironment().setLevel("OFF");
			SpoonModelBuilder comp = spoon.createCompiler();
			comp.setSourceClasspath("does_not_exist.jar");
			fail();
		} catch (InvalidClassPathException e) {
		}

		try {
			Launcher spoon = new Launcher();
			spoon.getFactory().getEnvironment().setLevel("OFF");
			SpoonModelBuilder comp = spoon.createCompiler();
			comp.setSourceClasspath("src");
		} catch (InvalidClassPathException e) {
			fail();
			// you're trying to give source code in the classpath, this should be accepted but causes a warn log entry
		}
	}

	@Test(expected = ModelBuildingException.class)
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

	@Test
	public void testUnionCatchExceptionInsideLambdaInNoClasspath() {
		// contract: the model should be built when defining a union catch inside a lambda which is not known (noclasspath)
		// and the catch variable types should be the same than outside a lambda
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/UnionCatch.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		List<CtCatch> catches = launcher.getFactory().getModel().getElements(new TypeFilter<>(CtCatch.class));
		assertEquals(2, catches.size());

		CtCatchVariable variable1 = catches.get(0).getParameter(); // inside a lambda
		CtCatchVariable variable2 = catches.get(1).getParameter(); // outside the lambda

		assertEquals(variable1.getMultiTypes(), variable2.getMultiTypes());
		assertEquals(variable2, variable1);
	}

	@Test
	public void testIssue2860() {
		// contract: no exceptions is thrown in mutlicatch in lambda
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/spoon/test/noclasspath/exceptions/Bar.java");
		launcher.buildModel();
		CtType<Object> type = launcher.getFactory().Type().get("spoon.test.noclasspath.exceptions.Bar");
		CtCatch c = type.filterChildren(new TypeFilter<CtCatch>(CtCatch.class)).first();
		// the upper type of java.io.IOException | java.lang.InterruptedException is java.lang.Exception
		assertEquals("java.lang.Exception", c.getParameter().getType().toString());
		assertEquals("java.io.IOException", c.getParameter().getMultiTypes().get(0).toString());
		assertEquals("java.lang.InterruptedException", c.getParameter().getMultiTypes().get(1).toString());
		assertEquals("InterruptedException", c.getParameter().getMultiTypes().get(1).getSimpleName().toString());
	}
}
