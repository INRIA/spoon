package spoon.test.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashSet;

import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

public class SignatureTest {

	@Test
	public void testNullSignature() throws Exception {
		// bug found by Thomas Vincent et Mathieu Schepens (students at the
		// University of Lille) on Nov 4 2014
		// in their analysis, they put CtExpressions in a Map
		// if one expression has an empty signature, an exception is thrown
		
		// the solution is to improve the signature of null literals

		Factory factory = new Launcher().createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public Object foo() {"
								+ " return null;" + "}};").compile();
		CtReturn returnEl = (CtReturn) clazz.getElements(
				new TypeFilter<>(CtReturn.class)).get(0);
		CtExpression lit = returnEl.getReturnedExpression();
		assertTrue(lit instanceof CtLiteral);
		assertEquals("null", lit.toString());
		assertEquals("null", lit.getSignature());

		// since the signature is null, CtElement.equals throws an exception and
		// should not
		CtLiteral lit2 = (CtLiteral) factory.Core().clone(lit);
		HashSet s = new HashSet();
		s.add(lit);
		s.add(lit2);
	}

}
