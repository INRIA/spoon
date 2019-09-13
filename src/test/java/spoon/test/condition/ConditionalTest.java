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
package spoon.test.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.condition.testclasses.Foo;
import spoon.test.condition.testclasses.Foo2;
import spoon.testing.utils.ModelUtils;

public class ConditionalTest {
	String newLine = System.getProperty("line.separator");

	@Test
	public void testConditional() throws Exception {
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		final CtConditional aConditional = aFoo.getMethod("m2").getElements(new TypeFilter<>(CtConditional.class)).get(0);
		assertEquals("return a == 18 ? true : false", aConditional.getParent().toString());
	}

	@Test
	public void testConditionalWithAssignment() throws Exception {
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		final CtConditional aConditional = aFoo.getMethod("m").getElements(new TypeFilter<>(CtConditional.class)).get(0);
		assertEquals("x = (a == 18) ? true : false", aConditional.getParent().toString());
	}

	@Test
	public void testBlockInConditionAndLoop() throws Exception {
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		CtMethod<Object> method = aFoo.getMethod("m3");
		final List<CtIf> conditions = method.getElements(new TypeFilter<>(CtIf.class));
		assertEquals(4, conditions.size());
		for (CtIf condition : conditions) {
			assertTrue(condition.getThenStatement() instanceof CtBlock);
			if (condition.getElseStatement() != null && !(condition.getElseStatement() instanceof CtIf)) {
				assertTrue(condition.getElseStatement() instanceof CtBlock);
			}
		}

		assertEquals("if (true) {" + newLine + 
				"    java.lang.System.out.println();" + newLine + 
				"} else if (true) {" + newLine +
				"    java.lang.System.out.println();" + newLine +
				"} else {" + newLine +
				"    java.lang.System.out.println();" + newLine +
				"}",
				method.getBody().getStatement(0).toString());
	}

	@Test
	public void testNoBlockInConditionAndLoop() throws Exception {

		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		CtMethod<Object> method = aFoo.getMethod("m3");
		final List<CtIf> conditions = method.getElements(new TypeFilter<>(CtIf.class));
		for (CtIf ctIf : conditions) {
			// replace the block to a statement
			CtStatement then = ((CtBlock) ctIf.getThenStatement()).getStatement(0);
			ctIf.setThenStatement(then);
			if (ctIf.getElseStatement() != null) {
				CtStatement elseStatement = ((CtBlock) ctIf.getElseStatement()).getStatement(0);
				ctIf.setElseStatement(elseStatement);
			}
		}

		assertEquals("if (true)" + newLine
				+ "    java.lang.System.out.println();" + newLine
				+ "else if (true)" + newLine
				+ "    java.lang.System.out.println();" + newLine
				+ "else" + newLine
				+ "    java.lang.System.out.println();" + newLine,
				method.getBody().getStatement(0).toString());
	}

	@Test
	public void testNoThenBlock() throws Exception {
		// contract: corner cases provided by @Egor18 are well handled
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		CtMethod<Object> method = aFoo.getMethod("m4");
		final List<CtIf> conditions = method.getElements(new TypeFilter<>(CtIf.class));

		assertNotNull(conditions.get(0).getThenStatement());
		assertNull(conditions.get(0).getElseStatement());

		assertNull(conditions.get(1).getThenStatement());
		assertNull(conditions.get(1).getElseStatement());
	}

	@Test
	public void testNoThenBlockBug() throws Exception {
		// contract: an empty statement is correctly handled
		// the fix consists of addind visit(EmptyStatement) in JDTTreeBuilder
		final CtType aFoo = ModelUtils.buildClass(Foo2.class);
		CtMethod<Object> method = aFoo.getMethod("bug");
		final List<CtIf> conditions = method.getElements(new TypeFilter<>(CtIf.class));

		// contract: an empty statement is transformed into an empty block with a comment
		assertNull(conditions.get(0).getThenStatement());

		// and we have the correct else statement
		assertNotNull(conditions.get(0).getElseStatement());
		assertEquals("System.out.println();", conditions.get(0).getElseStatement().prettyprint().trim());
	}

	@Test
	public void testNoThenBlockBug2() throws Exception {
		// contract: an empty statement is correctly handled
		// no NPE should be produced during the model build
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/condition/testclasses/Foo2.java");
		CtModel m = launcher.buildModel();

		// check if we have correct then-else statements
		CtType<?> aFoo = m.getAllTypes().stream()
				.filter(t -> t.getSimpleName().equals("Foo2"))
				.findFirst()
				.get();

		CtMethod<Object> method = aFoo.getMethod("bug2");
		final List<CtIf> conditions = method.getElements(new TypeFilter<>(CtIf.class));
		CtIf cond = conditions.get(0);
		CtStatement thenStatement = cond.getThenStatement();
		CtStatement elseStatement = cond.getElseStatement();

		assertEquals("System.out.println(\"valid\");", thenStatement.prettyprint().trim());
		CtIf innerIf = ((CtBlock) elseStatement).getStatement(0);

		assertNull(innerIf.getThenStatement());
		assertEquals("System.out.println(\"invalid\");", innerIf.getElseStatement().prettyprint().trim());
	}
}
