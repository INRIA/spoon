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
package spoon.test.parent;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

import static org.junit.Assert.assertEquals;

public class NullParentTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/parent/Foo.java"))
				.build();
	}

	private <T extends CtElement> T get(Class<T> elemType) {
		CtClass<Object> fooClass = factory.Class().get(Foo.class);
		CtMethod nullParent = fooClass.getMethodsByName("nullParent").get(0);
		return (T) nullParent.getBody().getElements(elemType::isInstance).get(0);
	}

	@Test
	public void testTargetedAccessNullTarget() {
		CtFieldAccess<?> access = get(CtFieldAccess.class);
		assertEquals("foo.bar", access.toString());
		access.setTarget(null);
		assertEquals("bar", access.toString());
	}

	@Test
	public void testTargetedExpressionNullTarget() {
		CtInvocation<?> inv = get(CtInvocation.class);
		assertEquals("foo.foo()", inv.toString());
		inv.setTarget(null);
		assertEquals("foo()", inv.toString());
	}

	@Test
	public void testAssertNullExpression() {
		CtAssert<?> asert = get(CtAssert.class);
		assertEquals("assert true : \"message\"", asert.toString());
		asert.setExpression(null);
		assertEquals("assert true", asert.toString());
	}

	static String noSpaceToString(Object obj) {
		return obj.toString().replaceAll("\\s+", "");
	}

	@Test
	public void testForLoopNullChildren() {
		CtFor forLoop = get(CtFor.class);
		assertEquals("for(inti=0;i<10;i++){}", noSpaceToString(forLoop));
		forLoop.setExpression(null);
		assertEquals("for(inti=0;;i++){}", noSpaceToString(forLoop));
		forLoop.setBody(null);
		assertEquals("for(inti=0;;i++);", noSpaceToString(forLoop));
	}

	@Test
	public void testIfNullBranches() {
		CtIf ctIf = get(CtIf.class);
		assertEquals("if(true){}else{}", noSpaceToString(ctIf));
		ctIf.setThenStatement(null);
		assertEquals("if(true);else{}", noSpaceToString(ctIf));
		ctIf.setElseStatement(null);
		assertEquals("if(true);", noSpaceToString(ctIf));
	}

	@Test
	public void testLocalVariableNullDefaultExpression() {
		CtLocalVariable<?> local = get(CtLocalVariable.class);
		assertEquals("int i = 0", local.toString());
		local.setDefaultExpression(null);
		assertEquals("int i", local.toString());
	}

	@Test
	public void testFieldNullDefaultExpression() {
		CtField<?> field = get(CtField.class);
		assertEquals("int bar = 0;", field.toString());
		field.setDefaultExpression(null);
		assertEquals("int bar;", field.toString());
	}

	@Test
	public void testReturnNullExpression() {
		CtReturn<?> ret = get(CtReturn.class);
		assertEquals("return 0", ret.toString());
		ret.setReturnedExpression(null);
		assertEquals("return", ret.toString());
	}
}
