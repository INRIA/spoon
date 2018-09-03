package spoon.test.eval;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.AccessibleVariablesFinder;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.eval.InlinePartialEvaluator;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.test.eval.testclasses.Foo;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
		assertEquals("java.lang.System.out.println((((\"enter: \" + className) + \" - \") + methodName))", b.getStatements().get(0).toString());
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
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		CtType<?> foo = launcher.getFactory().Type().get((Class<?>) Foo.class);
		foo.accept(new InlinePartialEvaluator(eval));
		assertEquals("false", foo.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0).getDefaultExpression().toString());
		// the if has been removed
		assertEquals(0, foo.getElements(new TypeFilter<>(CtIf.class)).size());
	}
}
