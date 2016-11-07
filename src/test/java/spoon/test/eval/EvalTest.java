package spoon.test.eval;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.eval.VisitorPartialEvaluator;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

public class EvalTest {

	@Test
	public void testStringConcatenation() throws Exception {
		CtClass<?> type = build("spoon.test.eval", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());

		CtBlock<?> b = type.getMethodsByName("testStrings").get(0).getBody();
		assertEquals(4, b.getStatements().size());
		b = b.partiallyEvaluate();
		b = type.getMethodsByName("testInts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals(0, b.getStatements().size());
	}

	@Test
	public void testVisitorPartialEvaluator_binary() throws Exception {
		Launcher launcher = new Launcher();

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("0+1").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(null, el);
			assertEquals("1", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(null, el);
			assertEquals("3", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+1)*3>0").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(null, el);
			assertEquals("true", elnew.toString());
		}

		{ // binary operator
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetExpression("(0+3-1)*3<=0").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(null, el);
			assertEquals("false", elnew.toString());
		}

	}

	@Test
	public void testVisitorPartialEvaluator_if() throws Exception {
		Launcher launcher = new Launcher();
		{ // the untaken branch is removed
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetStatement("if (false) {System.out.println(\"foo\");} else {System.out.println(\"bar\");} ").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(null, el);
			assertEquals("{" + System.lineSeparator() +
					"    java.lang.System.out.println(\"bar\");" + System.lineSeparator() +
					"}", elnew.toString());
		}

	}

}
