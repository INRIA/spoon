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
package spoon.test.eval;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.visitor.AccessibleVariablesFinder;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.eval.EvalHelper;
import spoon.support.reflect.eval.InlinePartialEvaluator;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.test.eval.testclasses.Foo;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class EvalTest {

	@Test
	public void testStringConcatenation() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testStrings").get(0).getBody();
		assertEquals(4, b.getStatements().size());
		b = b.partiallyEvaluate();
		b = type.getMethodsByName("testInts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("// if removed", b.getStatements().get(0).toString());
	}

	@Test
	public void testArrayLength() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testArray").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("// if removed", b.getStatements().get(0).toString());
	}

	@Test
	public void testDoNotSimplify() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testDoNotSimplify").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("java.lang.System.out.println(((\"enter: \" + className) + \" - \") + methodName)", b.getStatements().get(0).toString());
	}

	@Test
	public void testDoNotSimplifyCasts() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testDoNotSimplifyCasts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("return ((U) ((java.lang.Object) (spoon.test.eval.testclasses.ToEvaluate.castTarget(element).getClass())))", b.getStatements().get(0).toString());
	}

	@Test
	public void testScanAPartiallyEvaluatedElement() throws Exception {
		// contract: once partially evaluated a code element should be still visitable to find variables
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testDoNotSimplifyCasts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();

		AccessibleVariablesFinder avf = new AccessibleVariablesFinder(b);
		List<CtVariable> ctVariables = avf.find();
		assertEquals(1, ctVariables.size());
	}

	@Test
	public void testTryCatchAndStatement() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("tryCatchAndStatement").get(0).getBody();
		assertEquals(2, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals(2, b.getStatements().size());
	}

	@Test
	public void testDoNotSimplifyToExpressionWhenStatementIsExpected() throws Exception {
		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("simplifyOnlyWhenPossible").get(0).getBody();
		assertEquals(3, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals("spoon.test.eval.testclasses.ToEvaluate.class.getName()", b.getStatements().get(0).toString());
		assertEquals("java.lang.System.out.println(spoon.test.eval.testclasses.ToEvaluate.getClassLoader())", b.getStatements().get(1).toString());
		assertEquals("return \"spoon.test.eval.testclasses.ToEvaluate\"", b.getStatements().get(2).toString());
	}

	@Test
	public void testIsKnownAtCompileTime() throws Exception {
		// contract: one can ask whether an expression is known at compile time
		Launcher launcher = new Launcher();
		CtExpression el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3").compile();
		assertTrue(EvalHelper.isKnownAtCompileTime(el));

		CtClass<?> type = build("spoon.test.eval.testclasses", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());
		CtExpression<?> foo = ((CtReturn)type.getMethodsByName("foo").get(0).getBody().getStatement(0)).getReturnedExpression();
		assertFalse(EvalHelper.isKnownAtCompileTime(foo));

		CtExpression<?> foo2 = ((CtReturn)type.getMethodsByName("foo2").get(0).getBody().getStatement(0)).getReturnedExpression();
		assertTrue(EvalHelper.isKnownAtCompileTime(foo2));

		CtExpression<?> foo3 = ((CtReturn)type.getMethodsByName("foo3").get(0).getBody().getStatement(0)).getReturnedExpression();
		assertTrue(EvalHelper.isKnownAtCompileTime(foo3));

	}

	@Test
	public void testVisitorPartialEvaluator_binary() {
		Launcher launcher = new Launcher();

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("0+1").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("1", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("3", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3>0").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("true", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+3-1)*3<=0").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("false", elnew.toString());
		}
	}

	@Test
	public void testVisitorPartialEvaluator_unary() {

    	{ // NEG urnary operator
      		Launcher launcher = new Launcher();
      		CtCodeElement el =
          		launcher.getFactory().Code().createCodeSnippetExpression("-(100+1)").compile();
      		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
      		CtElement element = eval.evaluate(el);
      		assertEquals("-101", element.toString());
    	}
	}

	@Test
	public void testVisitorPartialEvaluator_if() {
		Launcher launcher = new Launcher();
		{ // the untaken branch is removed
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetStatement("if (false) {System.out.println(\"foo\");} else {System.out.println(\"bar\");} ").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("{" + System.lineSeparator()
					+ "    java.lang.System.out.println(\"bar\");" + System.lineSeparator()
					+ "}", elnew.toString());
		}
	}

	@Test
	public void testVisitorPartialEvaluatorScanner() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/eval/testclasses/Foo.java");
		launcher.buildModel();
		PartialEvaluator eval = launcher.getFactory().Eval().createPartialEvaluator();
		CtType<?> foo = launcher.getFactory().Type().get((Class<?>) Foo.class);
		foo.accept(new InlinePartialEvaluator(eval));
		assertEquals("false", foo.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0).getDefaultExpression().toString());
		// the if has been removed
		assertEquals(0, foo.getElements(new TypeFilter<>(CtIf.class)).size());
	}

	@Test
	public void testconvertElementToRuntimeObject() {
		// contract: getCorrespondingRuntimeObject works well for all kinds of expression
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/eval/testclasses/Foo.java");
		launcher.buildModel();
		CtType<?> foo = launcher.getFactory().Type().get((Class<?>) Foo.class);

		// also works for non-enum fields with partial evaluation embedded in convertElementToRuntimeObject
		assertEquals(false, EvalHelper.convertElementToRuntimeObject(foo.getField("b6").getDefaultExpression()));

		// impossible with no partial evaluation
		try {
			assertEquals(false, EvalHelper.getCorrespondingRuntimeObject(foo.getField("b6").getDefaultExpression()));
			fail();
		} catch (SpoonException expected) {}

		// also works for static runtime fields
		assertEquals(Integer.MAX_VALUE, EvalHelper.convertElementToRuntimeObject(foo.getField("i1").getDefaultExpression()));
		assertEquals(Integer.MAX_VALUE, EvalHelper.getCorrespondingRuntimeObject(foo.getField("i1").getDefaultExpression()));
		assertEquals(File.pathSeparator, EvalHelper.convertElementToRuntimeObject(foo.getField("str1").getDefaultExpression()));
		assertEquals(File.pathSeparator, EvalHelper.getCorrespondingRuntimeObject(foo.getField("str1").getDefaultExpression()));

	}
}
