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
package spoon.test.snippets;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResource;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.SnippetCompilationHelper;
import spoon.support.compiler.VirtualFile;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.utils.ModelUtils.createFactory;

public class SnippetTest {
	Factory factory = createFactory();
	@Test
	public void testSnippetFullClass() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ "}" + "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		assertEquals(1, foo.getBody().getStatements().size());
	}

	@Test
	public void testSnippetWihErrors() {
		try {
			factory.Code()
					.createCodeSnippetStatement(
							"" + "class X {" + "public void foo() {"
									+ " int x=0 sdfsdf;" + "}" + "};")
					.compile();
			fail();
		} catch (Exception e) {
			// we expect an exception the code is incorrect
		}
	}

	@Test
	public void testCompileSnippetSeveralTimes() {
		// contract: a snippet object can be reused several times
		final Factory factory = createFactory();
		final CtCodeSnippetExpression<Object> snippet = factory.Code().createCodeSnippetExpression("1 > 2");

		// Compile a first time the snippet.
		final CtExpression<Object> compile = snippet.compile();

		// contract: the element is fready to be used, not in any statement (#2318)
		assertFalse(compile.isParentInitialized());

		// Compile a second time the same snippet.
		final CtExpression<Object> secondCompile = snippet.compile();

		assertTrue(compile instanceof CtBinaryOperator);
		assertEquals("1 > 2", compile.toString());
		assertTrue(secondCompile instanceof CtBinaryOperator);
		assertEquals("1 > 2", secondCompile.toString());

		// Compile a third time a snippet but with an expression set.
		snippet.setValue("1 > 3");
		final CtExpression<Object> thirdCompile = snippet.compile();
		assertTrue(thirdCompile instanceof CtBinaryOperator);
		assertEquals("1 > 3", thirdCompile.toString());
	}

	@Test
	public void testCompileSnippetWithContext() {
		// contract: a snippet object can be compiled with a context in the factory.
		// Add a class in the context.
		factory.Class().create("AClass");
		// Try to compile a snippet with a context.
		CtStatement statement = factory.Code().createCodeSnippetStatement("int i = 1;").compile();

		// contract: the element is fready to be used, not in any statement (#2318)
		assertFalse(statement.isParentInitialized());
	}

	@Test
	public void testCompileStatementWithReturn() {
		// contract: a snippet with return can be compiled.
		CtElement el = SnippetCompilationHelper.compileStatement(
				factory.Code().createCodeSnippetStatement("return 3"),
				factory.Type().integerType()
		);
		assertTrue(CtReturn.class.isAssignableFrom(el.getClass()));
		assertEquals("return 3", el.toString());
	}

	@Test
	public void testIssue981() {
		// contract: one can get the package of a string
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setNoClasspath(true);
		SpoonResource input = new VirtualFile("package foo.bar; class X {}");
		spoon.addInputResource(input);
		spoon.buildModel();
		assertEquals("foo.bar", spoon.getFactory().Type().get("foo.bar.X").getPackage().getQualifiedName());
	}

	@Test
	public void testCompileAndReplaceSnippetsIn() {

        /*
            contract:
                We have a method, and we have a CodeSnippetStatement.
                In the code snippet, there is a reference to a declared variable, e.g. an object.
                After the call to CtType().compileAndReplaceSnippetsIn,
                The snippet must be replaced by the reference of the good object.
         */

		final Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/snippet/SnippetResources.java");
		launcher.buildModel();

		final Factory factory = launcher.getFactory();

		final CtCodeSnippetStatement codeSnippetStatement = factory.createCodeSnippetStatement("s.method(23)");
		final CtClass<?> snippetClass = launcher.getFactory().Class().get("snippet.test.resources.SnippetResources");
		CtMethod<?> staticMethod = snippetClass.getMethodsByName("staticMethod").get(0);
		staticMethod.getBody().insertEnd(codeSnippetStatement);

		snippetClass.compileAndReplaceSnippets(); // should not throw any exception

		assertSame(snippetClass, factory.Type().get(snippetClass.getQualifiedName()));

		assertTrue(staticMethod.getBody().getLastStatement() instanceof CtInvocation<?>); // the last statement, i.e. the snippet, has been replaced by its real node: a CtInvocation
		final CtInvocation<?> lastStatement = (CtInvocation<?>) staticMethod.getBody().getLastStatement();
		final CtLocalVariableReference<?> reference = staticMethod.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0).getReference();
		assertEquals(factory.createVariableRead(reference, false), lastStatement.getTarget()); // the target of the inserted invocation has been resolved as the reference of the declared object "s"
	}

	@Test
	public void testCompileSnippetsWithCtComment() {
		// contract: snippets are correctly compiled when followed by a CtComment
		final Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/snippet/SnippetCommentResource.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> testClass = factory.Class().get("snippet.test.resources.SnippetCommentResource");
		CtMethod method = testClass.getMethodsByName("modifiedMethod").get(0);
		CtBlock body = method.getBody();
		body.addStatement(body.getStatements().size()-1,launcher.getFactory().createInlineComment("inline comment"));
		body.addStatement(body.getStatements().size()-1,launcher.getFactory().createCodeSnippetStatement("invokedMethod()"));
		CtBlock innerBlock = body.getStatement(0);
		innerBlock.addStatement(0,factory.createCodeSnippetStatement("invokedMethod()"));
		body.addStatement(0,factory.createComment("block comment", CtComment.CommentType.BLOCK));
		testClass.compileAndReplaceSnippets();
		assertTrue(body.getStatements().get(0) instanceof CtComment);
		assertTrue(body.getStatements().get(1) instanceof CtBlock);
		assertTrue(body.getStatements().get(2) instanceof CtComment);
		assertTrue(body.getStatements().get(3) instanceof CtInvocation);
		assertTrue(body.getStatements().get(4) instanceof CtReturn);
		assertTrue(innerBlock.getStatements().get(0) instanceof CtInvocation);
		assertEquals(5,body.getStatements().size());
		assertEquals(1,innerBlock.getStatements().size());
		assertEquals(0,body.getStatements().get(0).getComments().size());
		assertEquals(0,body.getStatements().get(1).getComments().size());
		assertEquals(0,body.getStatements().get(2).getComments().size());
		assertEquals(0,body.getStatements().get(3).getComments().size());
	}

	@Test
	public void testCommentSnippetCompilation() {
		// contract: a snippet with only comments should be replaced with corresponding CtComments
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		launcher.addInputResource("src/test/resources/snippet/SnippetCommentResource.java");
		launcher.buildModel();
		CtClass<?> snippetClass = factory.Class().get("snippet.test.resources.SnippetCommentResource");
		CtMethod method = snippetClass.getMethodsByName("methodForCommentOnlySnippet").get(0);
		CtBlock body = method.getBody();
		body.addStatement(1,factory.createCodeSnippetStatement("/* a \n block \n comment */\n// inline"));
		body.addStatement(0,factory.createCodeSnippetStatement("/* a \n block \n comment */"));
		body.addStatement(0,factory.createCodeSnippetStatement("int x"));
		body.addStatement(0,factory.createCodeSnippetStatement("  // inline"));
		snippetClass.compileAndReplaceSnippets();
		assertTrue(body.getStatements().get(0) instanceof CtComment);
		assertTrue(body.getStatements().get(1) instanceof CtLocalVariable);
		assertTrue(body.getStatements().get(2) instanceof CtComment);
		assertTrue(body.getStatements().get(3) instanceof CtReturn);
		assertTrue(body.getStatements().get(4) instanceof CtComment);
		assertTrue(body.getStatements().get(5) instanceof CtComment);
		assertEquals(CtComment.CommentType.INLINE,((CtComment) body.getStatements().get(0)).getCommentType());
		assertEquals(CtComment.CommentType.BLOCK,((CtComment) body.getStatements().get(2)).getCommentType());
		assertEquals(CtComment.CommentType.BLOCK,((CtComment) body.getStatements().get(4)).getCommentType());
		assertEquals(CtComment.CommentType.INLINE,((CtComment) body.getStatements().get(5)).getCommentType());
		assertEquals(6,body.getStatements().size());
	}

	@Test
	public void testSnippetCompilationInUnnamedPackage() {
		// contract: a snippet can be successfully compiled in a class with an unnamed package
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		launcher.addInputResource("src/test/resources/snippet/UnnamedPackageSnippetResource.java");
		launcher.buildModel();
		CtClass<?> snippetClass = factory.Class().get("UnnamedPackageSnippetResource");
		CtMethod method = snippetClass.getMethodsByName("method").get(0);
		CtBlock body = method.getBody();
		body.addStatement(0,factory.createCodeSnippetStatement("int x"));
		snippetClass.compileAndReplaceSnippets();
		assertTrue(body.getStatements().get(0) instanceof CtLocalVariable);
		assertEquals(1,body.getStatements().size()); 
	}

	@Test
	public void testCodeSnippetExpressionsWithNonEqualValuesAreNotEqual() {
		// contract: Two code snippet expressions with non-equal values are not equal
		Factory factory = new Launcher().getFactory();

		CtCodeSnippetExpression<Integer> one = factory.createCodeSnippetExpression("1");
		CtCodeSnippetExpression<Integer> two = factory.createCodeSnippetExpression("2");

		assertThat(one.equals(two), is(false));
	}

	@Test
	public void testCodeSnippetExpressionsWithEqualValuesAreEqual() {
		// contract: Two code snippet expressions with equal values, that are also otherwise equal,
		// are equal
		Factory factory = new Launcher().getFactory();

		CtCodeSnippetExpression<Integer> one = factory.createCodeSnippetExpression("1");
		CtCodeSnippetExpression<Integer> alsoOne = factory.createCodeSnippetExpression("1");

		assertThat(one.equals(alsoOne), is(true));
	}

	@Test
	public void testCodeSnippetStatementsWithNonEqualValuesAreNotEqual() {
		// contract: Two code snippet statements with non-equal values are not equal
		Factory factory = new Launcher().getFactory();

		CtCodeSnippetStatement intDeclaration = factory.createCodeSnippetStatement("int a;");
		CtCodeSnippetStatement doubleDeclaration = factory.createCodeSnippetStatement("double a;");

		assertThat(intDeclaration.equals(doubleDeclaration), is(false));
	}

	@Test
	public void testCodeSnippetStatementsWithEqualValuesAreEqual() {
		// contract: Two code snippet statements with equal values, that are also otherwise equal,
		// are equal
		Factory factory = new Launcher().getFactory();

		CtCodeSnippetStatement intDeclaration = factory.createCodeSnippetStatement("int a;");
		CtCodeSnippetStatement alsoIntDeclaration = factory.createCodeSnippetStatement("int a;");

		assertThat(intDeclaration.equals(alsoIntDeclaration), is(true));
	}
}
