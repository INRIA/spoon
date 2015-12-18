package spoon.test.signature;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
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
		CtReturn<?> returnEl = clazz.getElements(
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

		CtExpression<?> argument1 = invo.getArguments().get(0);

		String signatureUnbound = argument1.getSignature();

		assertTrue(unboundVarAccess.equals(signatureUnbound));

		String toStringUnbound = argument1.toString();

		assertTrue(unboundVarAccess.equals(toStringUnbound));

	}

	@Test
	public void testLiteralSignature(){
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		String stConstant = "\"hello\"";
		CtStatement sta1 = (factory).Code().createCodeSnippetStatement("System.out.println("+stConstant+")")
				.compile();

		CtStatement sta2 = (factory).Code().createCodeSnippetStatement("String hello =\"t1\"; System.out.println(hello)")
				.compile();

		CtStatement sta2bis = ((CtBlock<?>)sta2.getParent()).getStatement(1);

		String signature1 = sta1.getSignature();
		String signature2 = sta2bis.getSignature();

		assertFalse(signature1.equals(signature2));
		assertFalse(sta1.equals(sta2bis));

		String signatureParameterWithQuotes = ((CtInvocation<?>)sta1).getArguments().get(0).getSignature();
		assertEquals(stConstant,signatureParameterWithQuotes);

		CtStatement stb1 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(20)")
				.compile();

		String signatureParameterWithoutQuotes = ((CtInvocation<?>)stb1).getArguments().get(0).getSignature();
		assertEquals("20",signatureParameterWithoutQuotes);
	}

	@Test
	public void testMethodInvocationSignatureStaticFieldsVariables(){
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		CtStatement sta1 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(Integer.MAX_VALUE)")
				.compile();

		CtStatement sta2 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(Integer.MIN_VALUE)")
				.compile();

		String signature1 = sta1.getSignature();
		String signature2 = sta2.getSignature();

		assertFalse(signature1.equals(signature2));
		assertFalse(sta1.equals(sta2));


		CtStatement stb1 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(20)")
				.compile();

		CtStatement stb2 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(30)")
				.compile();

		assertFalse(stb1.equals(stb2));
		assertFalse(sta1.getSignature().equals(sta2.getSignature()));


		CtStatement stc1 = (factory).Code().createCodeSnippetStatement("String.format(\"format1\",\"f2\" )")
				.compile();

		CtStatement stc2 = (factory).Code().createCodeSnippetStatement("String.format(\"format2\",\"f2\" )")
				.compile();

		assertFalse(stc2.equals(stc1));
		assertFalse(stc2.getSignature().equals(stc1.getSignature()));

	}
	@Test
	public void testMethodInvocationSignatureWithVariableAccess() throws Exception{

		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());


		factory.getEnvironment().setNoClasspath(true);

		String content = "" + "class PR {"
				+ "static String PRS = null;"

				+ "public Object foo(String p) {"
				+ " int s = 0; 	"
				+ " this.foo(s);"
				+ "this.foo(p);"
				+ " return null;"
				+ "}"
				+ " public Object foo(int p) {"
				+ " String s = null;"
				+ " this.foo(s);"
				+ "this.foo(p);"
				+ "return null;"
				+ "}"
				+ "};";

		SpoonCompiler builder = new JDTSnippetCompiler(factory, content);

		builder.build();

		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		assertNotNull(clazz1);

		//**FIRST PART: passing local variable access.
		///--------From the first method we take the method invocations
		CtMethod<?> methodString = (CtMethod<?>) clazz1.getAllMethods().toArray()[0];

		CtInvocation<?> invoToInt1 = (CtInvocation<?>) methodString.getBody().getStatement(1);
		String signatureInvoToInt = invoToInt1.getSignature();
		CtExpression<?> argumentToInt1 = invoToInt1.getArguments().get(0);

		//----------From the second method we take the Method Inv
		CtMethod<?> methodInt = (CtMethod<?>) clazz1.getAllMethods().toArray()[1];
		CtInvocation<?> invoToString = (CtInvocation<?>) methodInt.getBody().getStatement(1);
		CtExpression<?> argumentToString = invoToString.getArguments().get(0);

		String signatureInvoToString = invoToString.getSignature();
		//we compare the signatures of " this.foo(s);"	from both methods
		assertNotEquals(signatureInvoToInt, signatureInvoToString);


		//Now we check that we have two invocation to "foo(s)",
		//but one invocation is with var 's' type integer, the other var 's' type int
		String sigArgToInt1 = argumentToInt1.getSignature();
		String sigArgToString1 = argumentToString.getSignature();

		assertNotEquals(sigArgToInt1, sigArgToString1);

		/// ***SECOND PART, passing Parameters
		CtInvocation<?> invoToString2 = (CtInvocation<?>) methodString.getBody().getStatement(2);
		CtExpression<?> argumentToString2 = invoToString2.getArguments().get(0);
		String signatureInvoToString2 = argumentToString2.getSignature();


		CtInvocation<?> invoToInt2 = (CtInvocation<?>) methodInt.getBody().getStatement(2);
		CtExpression<?> argumentToInt2 = invoToInt2.getArguments().get(0);
		String signatureInvoToInt2 = argumentToInt2.getSignature();
		///

		String sigParString = invoToString2.getSignature();
		String sigParInteger =  invoToInt2.getSignature();

		//Compare the method invo signature (same real argument's name, different type)
		assertNotEquals(sigParString,sigParInteger);
		//Compare signature of parameters (same var name, different type)
		assertNotEquals(signatureInvoToString2,signatureInvoToInt2);


	}

	@Test
	public void testUnboundFieldSignature(){

		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		factory.getEnvironment().setNoClasspath(true);


		String content = "" + "class PR {"
				+ "public Object foo(String p) {"
				+ " this.mfield = p; 	"
				+ " return null;"
				+ "}"
				+ "};";

		SpoonCompiler builder = new JDTSnippetCompiler(factory, content);
		try{
		builder.build();
		fail();
		}
		catch(Exception e){
			//must fail
		}

		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		assertNotNull(clazz1);

		//**FIRST PART: passing local variable access.
		///--------From the first method we take the method invocations
		CtMethod<?> methodString = (CtMethod<?>) clazz1.getAllMethods().toArray()[0];

		CtAssignment<?,?> invoToInt1 = (CtAssignment<?,?>) methodString.getBody().getStatement(0);

		String sigAssign = invoToInt1.getSignature();

		CtExpression<?> left = invoToInt1.getAssigned();
		String sigleft = left.getSignature();
		assertNotEquals("",sigleft.trim());

		assertTrue(sigAssign.contains("<no type>"));

	}

	@Test
	public void testArgumentNotNullForExecutableReference() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/variable/PropPanelUseCase_1.40.java");
		launcher.setSourceOutputDirectory("./target/trash");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final List<CtExecutableReference> references = Query.getReferences(launcher.getFactory(), new ReferenceTypeFilter<CtExecutableReference>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference reference) {
				return "addField".equals(reference.getSimpleName()) && super.matches(reference);
			}
		});
		assertEquals("#addField(<unknown>, <unknown>)", references.get(0).getSignature());
		assertEquals("#addField(<unknown>, org.argouml.uml.ui.UMLComboBoxNavigator)", references.get(1).getSignature());
		for (CtExecutableReference reference : references) {
			assertNotEquals("#addField(null, null)", reference.getSignature());
		}
	}
}
