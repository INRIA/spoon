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
package spoon.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.visitor.SignaturePrinter;
import spoon.test.api.testclasses.Bar;

public class NoClasspathTest {

	@Test
	public void test() {
		// do we still have a correct model when the complete classpath is not given as input?
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setNoClasspath(true);
		spoon.getEnvironment().setLevel("OFF");
		spoon.getEnvironment().setCommentEnabled(false); // avoid getting the comments for the equals
		spoon.addInputResource("./src/test/resources/spoon/test/noclasspath/fields");
		spoon.getEnvironment().setSourceOutputDirectory(new File("target/spooned/apitest"));
		spoon.run();
		Factory factory = spoon.getFactory();
		CtClass<Object> clazz = factory.Class().get("Foo");

		assertEquals("Foo", clazz.getSimpleName());
		CtTypeReference<?> superclass = clazz.getSuperclass();
		// "Unknown" is not in the classpath at all
		assertEquals("Unknown", superclass.getSimpleName());
		try {
			superclass.getActualClass();
			fail();
		} catch (SpoonClassNotFoundException e) {
			// expected
		}
		assertNull(superclass.getDeclaration());

		// should be empty as in noClasspath the actual class cannot be retrieved
		assertTrue(superclass.getAllFields().isEmpty());

		// now we really make sure we don't have the class in the classpath
		try {
			superclass.getActualClass();
			fail();
		} catch (SpoonClassNotFoundException e) {
			// expected
		}

		{
			CtMethod<?> method = clazz.getMethod("method", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
			assertEquals(1, invocations.size());
			CtInvocation<?> c = invocations.get(0);
			assertEquals("method", c.getExecutable().getSimpleName());
			assertEquals("x.method()", method.getBody().getStatement(1).toString());
		}

		{
			CtMethod<?> method = clazz.getMethod("m2", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
			assertEquals(3, invocations.size());
			CtInvocation<?> c = invocations.get(1);
			assertEquals("second", c.getExecutable().getSimpleName());
			assertEquals("x.first().second().third()", method.getBody().getStatement(1).toString());
		}

		{
			CtMethod<?> method = clazz.getMethod("m1", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
			assertEquals(1, invocations.size());
			invocations.get(0);
			assertEquals("x.y.z.method()", method.getBody().getStatement(0).toString());
		}

		{
			CtMethod<?> method = clazz.getMethod("m3", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
			assertEquals(1, invocations.size());
			invocations.get(0);
			CtLocalVariable<?> statement = method.getBody().getStatement(0);
			CtFieldAccess<?>  fa = (CtFieldAccess<?>) statement.getDefaultExpression();
			assertTrue(fa.getTarget() instanceof CtInvocation);
			assertEquals("field", fa.getVariable().getSimpleName());
			assertEquals("int x = first().field", statement.toString());
		}
	}

	@Test
	public void testBug20141021() {
		// 2014/10/21 NPE is noclasspath mode on a large open-source project

		Launcher spoon = new Launcher();
		Factory f = spoon.getFactory();
		CtExecutableReference<Object> ref = f.Core().createExecutableReference();
		ref.setSimpleName("foo");

		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(ref);
		String s = pr.getSignature();

		assertEquals("foo()", s);
	}

	@Test
	public void testGetStaticDependency() {
		Launcher spoon = new Launcher();
		final Factory factory = spoon.getFactory();
		factory.getEnvironment().setAutoImports(false);
		spoon.addInputResource("./src/test/java/spoon/test/api/testclasses/");
		spoon.getEnvironment().setSourceOutputDirectory(new File("target/spooned/apitest"));
		spoon.run();

		CtTypeReference<?> expectedType = factory.Type().createReference(javax.sound.sampled.AudioFormat.Encoding.class);
		CtClass<?> clazz = factory.Class().get(Bar.class);

		CtMethod<?> method = clazz.getMethodsByName("doSomething").get(0);
		CtReturn<?> ctReturn = method.getElements(new TypeFilter<CtReturn<?>>(CtReturn.class)).get(0);

		assertTrue(ctReturn.getReferencedTypes().contains(expectedType));
	}

	@Test
	public void testIssue1747() {
		Launcher spoon = new Launcher();
		final Factory factory = spoon.getFactory();
		factory.getEnvironment().setNoClasspath(true);
		spoon.addInputResource("./src/test/resources/noclasspath/SubscriptionAdapter.java");
		spoon.buildModel();
	}

	@Test
	public void testInheritanceInNoClassPathWithClasses() {
		// contract: when using noclasspath in combination with a source classpath
		// spoon is able to resolve the inheritance between classes contained in source cp
		String sourceInputDirPath = "./src/test/resources/spoon/test/inheritance";
		String targetBinPath = "./target/spoon-nocp-bin";

		Launcher spoon = new Launcher();
		spoon.getEnvironment().setShouldCompile(true);
		spoon.addInputResource(sourceInputDirPath);
		spoon.setBinaryOutputDirectory(targetBinPath);
		spoon.run();

		spoon = new Launcher();
		spoon.getEnvironment().setNoClasspath(true);
		spoon.getEnvironment().setSourceClasspath(new String[] { targetBinPath });
		spoon.addInputResource(sourceInputDirPath + "/AnotherClass.java");
		spoon.buildModel();

		CtType anotherclass = spoon.getFactory().Type().get("org.acme.AnotherClass");
		assertEquals(1, anotherclass.getFields().size());

		CtField field = (CtField) anotherclass.getFields().get(0);

		CtTypeReference myClassReference = spoon.getFactory().Type().createReference("fr.acme.MyClass");
		assertEquals(myClassReference, field.getType());
		assertNotNull(myClassReference.getActualClass());

		CtTypeReference myInterfaceReference = spoon.getFactory().Type().createReference("org.myorganization.MyInterface");
		assertTrue(myClassReference.isSubtypeOf(myInterfaceReference));
		assertTrue(field.getType().isSubtypeOf(myInterfaceReference));
	}
}
