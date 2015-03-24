package spoon.test.comparison;

import static org.junit.Assert.*;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTSnippetCompiler;

public class EqualTest {

	@Test
	public void testEqualsEmptyException() throws Exception {
		
		Factory factory = new Launcher().createFactory();

		String realParam1 = "\"\"";

		String content = "" + "class X {" + "public Object foo() {"
				+ " Integer.getInteger(" + realParam1 + ");"
				+ " return \"\";" + "}};";

		SpoonCompiler builder = new JDTSnippetCompiler(factory, content);
		try {
			builder.build();
		} catch (Exception e) {
			fail("Unable create model");
		}
		
		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		
		CtMethod<?> method = (CtMethod<?>) clazz1.getAllMethods().toArray()[0];

		CtInvocation<?> invo = (CtInvocation<?>) method.getBody().getStatement(0);

		CtLiteral<?> argument1 = (CtLiteral<?>) invo.getArguments().get(0);

		String signatureArg1= argument1.getSignature();
		
		assertEquals(realParam1 , signatureArg1);
		
		
		CtReturn<?> returnStatement = (CtReturn<?>) method.getBody().getStatement(1);
		
		CtLiteral<?> returnExp = (CtLiteral<?>) returnStatement.getReturnedExpression();
		
		assertEquals(realParam1 , returnExp.getSignature() );
		
		try{
			assertEquals(argument1, returnExp);
		}
		catch(Exception e){
			fail(e.getMessage());
		}
	

	}


}
