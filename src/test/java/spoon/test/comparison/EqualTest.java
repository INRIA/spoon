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
package spoon.test.comparison;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.visitor.equals.EqualsVisitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class EqualTest {

	@Test
	public void testEqualsEmptyException() {

		Factory factory = new Launcher().createFactory();

		String realParam1 = "\"\"";

		String content = "" + "class X {" + "public Object foo() {"
				+ " Integer.getInteger(" + realParam1 + ");"
				+ " return \"\";" + "}};";

		SpoonModelBuilder builder = new JDTSnippetCompiler(factory, content);
		try {
			builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unable create model");
		}

		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		CtMethod<?> method = (CtMethod<?>) clazz1.getMethods().toArray()[0];

		CtInvocation<?> invo = (CtInvocation<?>) method.getBody().getStatement(0);

		CtLiteral<?> argument1 = (CtLiteral<?>) invo.getArguments().get(0);

		assertEquals(realParam1, argument1.toString());


		CtReturn<?> returnStatement = (CtReturn<?>) method.getBody().getStatement(1);

		CtLiteral<?> returnExp = (CtLiteral<?>) returnStatement.getReturnedExpression();

		assertEquals(realParam1, returnExp.toString());

		try {
			assertEquals(argument1, returnExp);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testEqualsComment() {
		Factory factory = new Launcher().createFactory();
		CtLocalVariable<?> var = factory.Code().createCodeSnippetStatement("int i=0").compile();
		CtLocalVariable<?> var2 = var.clone();
		var2.addComment(factory.Code().createComment("foo", CtComment.CommentType.INLINE));
		assertNotEquals(1, var.getComments().size());
		assertNotEquals(var2, var);
	}

	@Test
	public void testEqualsMultitype() {
		Factory factory = new Launcher().createFactory();
		CtTry var = factory.Code().createCodeSnippetStatement("try{}catch(RuntimeException | AssertionError e){}").compile();
		CtTry var2 = var.clone();
		assertEquals(2, var2.getCatchers().get(0).getParameter().getMultiTypes().size());
		// removing a multitype
		var2.getCatchers().get(0).getParameter().getMultiTypes().remove(0);
		assertEquals(1, var2.getCatchers().get(0).getParameter().getMultiTypes().size());
		assertNotEquals(var2, var);
	}

	@Test
	public void testEqualsActualTypeRef() {
		// contract: actual type refs are part of the identity
		Factory factory = new Launcher().createFactory();
		CtLocalVariable var = factory.Code().createCodeSnippetStatement("java.util.List<String> l ").compile();
		CtLocalVariable var2 = factory.Code().createCodeSnippetStatement("java.util.List<Object> l ").compile();
		assertNotEquals(var2, var);
	}

	@Test
	public void testEqualsDetails() {
		Factory factory = new Launcher().createFactory();
		CtTry var = factory.Code().createCodeSnippetStatement("try{}catch(RuntimeException | AssertionError e){}").compile();
		CtTry var2 = var.clone();
		assertEquals(2, var2.getCatchers().get(0).getParameter().getMultiTypes().size());
		// removing a multitype
		var2.getCatchers().get(0).getParameter().getMultiTypes().remove(0);
		assertEquals(1, var2.getCatchers().get(0).getParameter().getMultiTypes().size());
		EqualsVisitor ev = new EqualsVisitor();
		assertFalse(ev.checkEquals(var2, var));
		assertSame(var2.getCatchers().get(0).getParameter().getMultiTypes(), ev.getNotEqualElement());
		assertSame(var.getCatchers().get(0).getParameter().getMultiTypes(), ev.getNotEqualOther());
		assertSame(CtRole.MULTI_TYPE, ev.getNotEqualRole());
	}
}
