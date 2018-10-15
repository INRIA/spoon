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
package spoon.test.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.comparator.DeepRepresentationComparator;
import spoon.support.compiler.jdt.JDTSnippetCompiler;

public class SignatureTest {

	@Test
	public void testNullSignature() {
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

		// since the signature is null, CtElement.equals throws an exception and
		// should not
		CtLiteral<?> lit2 = ((CtLiteral<?>) lit).clone();
		HashSet<CtExpression<?>> s = new HashSet<>();
		s.add(lit);
		s.add(lit2);
	}

	@Test
	public void testNullSignatureInUnboundVariable() {
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

		String content = "" + "class X {" + "public Object foo(java.util.List<String> l) {"
				+ " Integer.toString(" + unboundVarAccess + ");"
				+ " return null;" + "}};";

		SpoonModelBuilder builder = new JDTSnippetCompiler(factory, content);
		try {
			builder.build();
			fail();
		} catch (Exception e) {
			// Must fail due to the unbound element "Complex.I"
		}
		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);

		Set<CtMethod<?>> methods = clazz1.getMethods();
		CtMethod<?> method = (CtMethod<?>) methods.toArray()[0];
		assertEquals("foo(java.util.List)", method.getSignature());


		CtInvocation<?> invo = (CtInvocation<?>) method.getBody().getStatement(0);

		CtExpression<?> argument1 = invo.getArguments().get(0);

		assertEquals(unboundVarAccess, argument1.toString());

	}

	@Test
	public void testLiteralSignature(){
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		CtStatement sta1 = (factory).Code().createCodeSnippetStatement("System.out.println(\"hello\")")
				.compile();

		CtStatement sta2 = (factory).Code().createCodeSnippetStatement("String hello =\"t1\"; System.out.println(hello)")
				.compile();

		assertNotEquals(sta1, sta2);// equals depends on deep equality

		String parameterWithQuotes = ((CtInvocation<?>)sta1).getArguments().get(0).toString();
		assertEquals("\"hello\"",parameterWithQuotes);

		(factory).Code().createCodeSnippetStatement("Integer.toBinaryString(20)")
				.compile();
	}

	@Test
	public void testMethodInvocationSignatureStaticFieldsVariables(){
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		CtStatement sta1 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(Integer.MAX_VALUE)")
				.compile();

		CtStatement sta2 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(Integer.MIN_VALUE)")
				.compile();

		String signature1 = ((CtInvocation)sta1).getExecutable().getSignature();
		String signature2 = ((CtInvocation)sta2).getExecutable().getSignature();
		assertEquals(signature1,  signature2);
		assertNotEquals(sta1, sta2);


		CtStatement stb1 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(20)")
				.compile();

		CtStatement stb2 = (factory).Code().createCodeSnippetStatement("Integer.toBinaryString(30)")
				.compile();
		String signature1b = ((CtInvocation)sta1).getExecutable().getSignature();
		String signature2b = ((CtInvocation)sta2).getExecutable().getSignature();
		assertEquals(signature1b,  signature2b);
		assertNotEquals(stb1, stb2);


		CtStatement stc1 = (factory).Code().createCodeSnippetStatement("String.format(\"format1\",\"f2\" )")
				.compile();
		CtStatement stc2 = (factory).Code().createCodeSnippetStatement("String.format(\"format2\",\"f2\" )")
				.compile();
		String signaturestc1 = ((CtInvocation)sta1).getExecutable().getSignature();
		String signaturestc2 = ((CtInvocation)sta2).getExecutable().getSignature();
		assertEquals(signaturestc1,  signaturestc2);
		assertNotEquals(stc1, stc2);
	}

	@Test
	public void testMethodInvocationSignatureWithVariableAccess() {

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

		SpoonModelBuilder builder = new JDTSnippetCompiler(factory, content);

		builder.build();

		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		assertNotNull(clazz1);

		//**FIRST PART: passing local variable access.
		///--------From the first method we take the method invocations
		TreeSet<CtMethod<?>> ts = new TreeSet<>(new DeepRepresentationComparator());
		ts.addAll(clazz1.getMethods());
		CtMethod[] methodArray = ts.toArray(new CtMethod[0]);
		CtMethod<?> methodInteger = methodArray[0];
		assertEquals("foo(int)", methodInteger.getSignature());

		CtInvocation<?> invoToInt1 = (CtInvocation<?>) methodInteger.getBody().getStatement(1);
		CtExpression<?> argumentToInt1 = invoToInt1.getArguments().get(0);

		//----------From the second method we take the Method Inv
		CtMethod<?> methodString = (CtMethod<?>) methodArray[1];
		assertEquals("foo(java.lang.String)", methodString.getSignature());

		CtInvocation<?> invoToString = (CtInvocation<?>) methodString.getBody().getStatement(1);
		CtExpression<?> argumentToString = invoToString.getArguments().get(0);

		//we compare the signatures of " this.foo(s);"	from both methods
		assertNotEquals(invoToInt1, invoToString);


		//Now we check that we have two invocation to "foo(s)",
		//but one invocation is with var 's' type integer, the other var 's' type int
		assertNotEquals(argumentToInt1, argumentToString);

		/// ***SECOND PART, passing Parameters
		CtInvocation<?> invoToString2 = (CtInvocation<?>) methodInteger.getBody().getStatement(2);
		CtExpression<?> argumentToString2 = invoToString2.getArguments().get(0);

		CtInvocation<?> invoToInt2 = (CtInvocation<?>) methodString.getBody().getStatement(2);
		CtExpression<?> argumentToInt2 = invoToInt2.getArguments().get(0);
		///

		//Compare the method invo signature (same real argument's name, different type)
		assertNotEquals(invoToString2,invoToInt2);
		//Compare signature of parameters (same var name, different type)
		assertNotEquals(argumentToString2,argumentToInt2);


	}

	@Test
	public void testUnboundFieldSignature(){

		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		factory.getEnvironment().setNoClasspath(true);


		String content = "" + "class PR {"
				+ "public java.io.File foo(String p) {"
				+ " this.mfield = p; 	"
				+ " return null;"
				+ "}"
				+ "};";

		SpoonModelBuilder builder = new JDTSnippetCompiler(factory, content);
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
		CtMethod<?> methodString = (CtMethod<?>) clazz1.getMethods().toArray()[0];
		assertEquals("foo(java.lang.String)", methodString.getSignature());

		CtAssignment<?,?> invoToInt1 = (CtAssignment<?,?>) methodString.getBody().getStatement(0);

		CtExpression<?> left = invoToInt1.getAssigned();
		assertEquals("this.mfield",left.toString());
		assertNull(left.getType());// null because noclasspath
		assertEquals("this.mfield = p",invoToInt1.toString());


	}

	@Test
	public void testArgumentNotNullForExecutableReference() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/resources/variable/PropPanelUseCase_1.40.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final List<CtExecutableReference> references = Query.getElements(launcher.getFactory(), new ReferenceTypeFilter<CtExecutableReference>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference reference) {
				return "addField".equals(reference.getSimpleName()) && super.matches(reference);
			}
		});
		assertEquals("addField(<unknown>,<unknown>)", references.get(0).getSignature());
		assertEquals("addField(<unknown>,org.argouml.uml.ui.UMLComboBoxNavigator)", references.get(1).getSignature());
		for (CtExecutableReference reference : references) {
			assertNotEquals("addField(null,null)", reference.getSignature());
		}
	}

	@Test
	public void testBugSignature() throws Exception {
		// contract: two methods with same name and return type yet different argument types
		// must have different signatures
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/main/java/spoon/SpoonModelBuilder.java"));
		comp.build();
		CtType<?> ctClass = (CtType<?>) comp.getFactory().Type().get(SpoonModelBuilder.class);
		List<CtMethod> methods = ctClass.getElements(new NamedElementFilter<>(CtMethod.class,"addInputSource"));
		assertEquals(2, methods.size());
		CtMethod<?> method = methods.get(0);
		assertEquals(
				"addInputSource(java.io.File)",
				method.getSignature());
		CtMethod<?> method2 = methods.get(1);
		assertEquals(
				"addInputSource(spoon.compiler.SpoonResource)",
				method2.getSignature());
		assertNotEquals(method, method2);

	}
}
