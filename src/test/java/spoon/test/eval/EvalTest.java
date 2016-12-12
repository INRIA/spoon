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
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.eval.InlinePartialEvaluator;
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
		assertEquals("// if removed", b.getStatements().get(0).toString());
	}

	@Test
	public void testVisitorPartialEvaluator_binary() throws Exception {
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
	public void testVisitorPartialEvaluator_if() throws Exception {
		Launcher launcher = new Launcher();
		{ // the untaken branch is removed
			CtCodeElement el = launcher.getFactory().Code().createCodeSnippetStatement("if (false) {System.out.println(\"foo\");} else {System.out.println(\"bar\");} ").compile();
			VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
			CtElement elnew = eval.evaluate(el);
			assertEquals("{" + System.lineSeparator() +
					"    java.lang.System.out.println(\"bar\");" + System.lineSeparator() +
					"}", elnew.toString());
		}

	}

	@Test
	public void testVisitorPartialEvaluatorScanner() throws Exception {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/eval/Foo.java");
		launcher.buildModel();
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		CtType<?> foo = launcher.getFactory().Type().get((Class<?>) Foo.class);
		foo.accept(new InlinePartialEvaluator(eval));
		assertEquals("false", foo.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0).getDefaultExpression().toString());
		// the if has been removed
		assertEquals(0, foo.getElements(new TypeFilter<>(CtIf.class)).size());
	}

}
