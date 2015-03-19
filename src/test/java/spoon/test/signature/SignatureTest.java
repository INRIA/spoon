package spoon.test.signature;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTSnippetCompiler;

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
		CtReturn<?> returnEl = (CtReturn<?>) clazz.getElements(
				new TypeFilter<>(CtReturn.class)).get(0);
		CtExpression<?> lit = returnEl.getReturnedExpression();
		assertTrue(lit instanceof CtLiteral);
		assertEquals("null", lit.toString());
		assertEquals("null", lit.getSignature());

		// since the signature is null, CtElement.equals throws an exception and
		// should not
		CtLiteral<?> lit2 = (CtLiteral<?>) factory.Core().clone(lit);
		HashSet<CtExpression<?>> s = new HashSet<CtExpression<?>>();
		s.add(lit);
		s.add(lit2);
	}

	@Test
	public void testNullSignatureInUnboundVariable() throws Exception {
		//Unbound variable access bug fix: 
		//Bug description: The signature printer ignored the element Unbound variable reference 
		//(as well all Visitor that extend CtVisitor)
		//Fix description: modify CtVisitor (including SignaturePrinter) for visiting unbound variable access.
		
		
		Factory factory = new Launcher().createFactory();
		// We want to compile a class with an reference to a class that is not
		// in the classpath
		// As consequence, we set the option NoClasspath as true.
		factory.getEnvironment().setNoClasspath(true);

		String unboundVarAccess = "Complex.I";

		String content = "" + "class X {" + "public Object foo() {"
				+ " Integer.toString(" + unboundVarAccess + ");"
				+ " return null;" + "}};";

		SpoonCompiler builder = new JDTSnippetCompiler(factory, content);
		try {
			builder.build();
			Assert.fail();
		} catch (Exception e) {
			// Must fail due to the unbound element "Complex.I"
		}
		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		
		CtMethod<?> method = (CtMethod<?>) clazz1.getAllMethods().toArray()[0];

		CtInvocation<?> invo = (CtInvocation<?>) method.getBody().getStatement(0);

		CtExpression<?> argument1 = (CtExpression<?>) invo.getArguments().get(0);

		String signatureUnbound = argument1.getSignature();

		assertTrue(unboundVarAccess.equals(signatureUnbound));

		String toStringUnbound = argument1.toString();

		assertTrue(unboundVarAccess.equals(toStringUnbound));

	}
	
	@Test
	public void testLiteralSignature(){
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		CtStatement sta1 = (factory).Code().createCodeSnippetStatement("System.out.println(\"hello\")")
				.compile();
	
		
		String signatureParameterWithQuotes = ((CtInvocation<?>)sta1).getArguments().get(0).getSignature();
		assertEquals("\"hello\"",signatureParameterWithQuotes);
		
		CtStatement stb1 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(20)")
				.compile();
		
		String signatureParameterWithoutQuotes = ((CtInvocation<?>)stb1).getArguments().get(0).getSignature();
		assertEquals("20",signatureParameterWithoutQuotes);
	}
	
}
