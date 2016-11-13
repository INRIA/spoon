package spoon.test.intercession;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.ast.IntercessionScanner;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.createFactory;

public class IntercessionTest {
	Factory factory = createFactory();
	@Test
	public void testInsertBegin() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ "}" + "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(1, body.getStatements().size());

		// adding a new statement;
		CtReturn<Object> returnStmt = factory.Core().createReturn();
		body.insertBegin(returnStmt);
		assertEquals(2, body.getStatements().size());
		assertSame(returnStmt, body.getStatements().get(0));
	}

	@Test
	public void testInsertEnd() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ " String foo=\"toto\";" + "}" + "};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];
		CtMethod<?> fooClone = foo.clone();
		Assert.assertEquals(foo, fooClone);
		CtBlock<?> body = foo.getBody();
		assertEquals(2, body.getStatements().size());

		// adding a new statement;
		CtReturn<Object> returnStmt = factory.Core().createReturn();
		body.insertEnd(returnStmt);
		assertEquals(3, body.getStatements().size());
		assertSame(returnStmt, body.getStatements().get(2));

		Assert.assertNotEquals(foo, fooClone);

	}

	@Test
	public void testEqualConstructor() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X { public X() {} };")
				.compile();
		CtConstructor<?> foo = (CtConstructor<?>) clazz.getConstructors().toArray()[0];
		CtConstructor<?> fooClone = foo.clone();
		Assert.assertEquals(foo, fooClone);

		CtBlock<?> body = foo.getBody();

		// there is an implicit call to super()
		assertEquals(1, body.getStatements().size());
		assertEquals("super()", body.getStatements().get(0).toString());

		// adding a new statement;
		CtStatement stmt = factory.Core().createCodeSnippetStatement();
		body.insertEnd(stmt);
		assertEquals(2, body.getStatements().size());

		// constructor are not equals anymore
		Assert.assertNotEquals(foo, fooClone);
	}

	@Test
	public void test_setThrownExpression() {
		CtThrow throwStmt = factory.Core().createThrow();
		CtExpression<Exception> exp = factory.Code()
				.createCodeSnippetExpression("e");
		throwStmt.setThrownExpression(exp);
		assertEquals("throw e", throwStmt.toString());
	}

	@Test
	public void testInsertIfIntercession() {
		String ifCode = "if (1 == 0)\n" + "    return 1;\n" + "else\n"
				+ "    return 0;\n" + "";
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public int bar() {" + ifCode + "}"
								+ "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(1, body.getStatements().size());

		CtIf ifStmt = (CtIf) foo.getBody().getStatements().get(0);
		String s = ifStmt.toString().replace("\r", "");
		assertEquals(ifCode, s);
		CtBlock<?> r1 = ifStmt.getThenStatement();
		CtBlock<?> r2 = ifStmt.getElseStatement();

		assertTrue(r1.isImplicit());
		assertTrue(r2.isImplicit());

		ifStmt.setThenStatement(r2);
		assertSame(r2, ifStmt.getThenStatement());
		ifStmt.setElseStatement(r1);
		assertSame(r1, ifStmt.getElseStatement());

		s = ifStmt.toString().replace("\r", "");
		String ifCodeNew = "if (1 == 0)\n" + "    return 0;\n" + "else\n"
				+ "    return 1;\n" + "";
		assertEquals(ifCodeNew, s);
	}

	@Test
	public void testInsertAfter() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ " int y=0;" + " int z=x+y;" + "}" + "};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(3, body.getStatements().size());

		CtStatement s = body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());

		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core()
				.createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertAfter(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(3));
	}

	@Test
	public void testSettersAreAllGood() throws Exception {
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/main/java",
				"--output-type", "nooutput",
				"--source-classpath", systemClassPath
		});
		final Factory factory = launcher.getFactory();
		final List<CtMethod<?>> setters = Query
				.getElements(factory, new AbstractFilter<CtMethod<?>>(CtMethod.class) {
					@Override
					public boolean matches(CtMethod<?> element) {
						return element.getDeclaringType().isInterface() &&
								element.getDeclaringType().getSimpleName().startsWith("Ct") &&
								(element.getSimpleName().startsWith("set") ||
								element.getSimpleName().startsWith("add"));
					}
				});

		for (CtMethod<?> setter : setters) {
			final String methodLog = setter.getSimpleName() + " in " +
					setter.getDeclaringType().getSimpleName();
			if (setter.getFormalCtTypeParameters().size() <= 0) {
				fail("Your setter " + methodLog + " don't have a generic type for its return type.");
			}
			boolean isMatch = false;
			// New type parameter declaration.
			for (CtTypeParameter typeParameter : setter.getFormalCtTypeParameters()) {
				if (setter.getType().getSimpleName().equals(typeParameter.getSimpleName())) {
					isMatch = true;

					if (setter.getAnnotation(Override.class) != null) {
						// Override annotation means that the current method come from a super
						// interface. So the return type can't be the declaring interface.
						continue;
					}

					if (!setter.getDeclaringType().getSimpleName().equals(typeParameter.getSuperclass().getSimpleName())) {
						fail("Your setter " + methodLog + " has a type reference who don't extends " + setter.getDeclaringType().getSimpleName());
					}
				}
			}
			assertTrue("The type of " + methodLog + " don't match with generic types.", isMatch);
		}
	}

	@Test
	public void testResetCollectionInSetters() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		final Factory factory = launcher.getFactory();
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.buildModel();

		new IntercessionScanner(factory) {

			@Override
			protected boolean isToBeProcessed(CtMethod<?> candidate) {
				return candidate.getSimpleName().startsWith("set") //
						&& candidate.hasModifier(ModifierKind.PUBLIC) //
						&& takeSetterCollection(candidate) //
						&& avoidInterfaces(candidate) //
						&& avoidSpecificMethods(candidate) //
						&& avoidThrowUnsupportedOperationException(candidate);
			}

			private boolean takeSetterCollection(CtMethod<?> candidate) {
				final CtTypeReference<?> type = candidate.getParameters().get(0).getType();
				final List<CtTypeReference<?>> actualTypeArguments = type.getActualTypeArguments();
				return COLLECTIONS.contains(type) && actualTypeArguments.size() == 1 && actualTypeArguments.get(0).isSubtypeOf(CTELEMENT_REFERENCE);
			}

			@Override
			protected void process(CtMethod<?> element) {
				final CtStatement statement = element.getBody().getStatement(0);
				if (!(statement instanceof CtIf)) {
					fail(log(element, "First statement should be an if to check the parameter of the setter"));
				}
				final CtIf anIf = (CtIf) statement;
				if (!createCheckNull(element.getParameters().get(0)).equals(anIf.getCondition())) {
					fail(log(element, "Condition should test if the parameter is null.\nThe condition was " + anIf.getCondition()));
				}

				if (!(anIf.getThenStatement() instanceof CtBlock)) {
					fail(log(element, "Should have a block in the if condition to have the initialization and the return."));
				}

				if (element.getParameters().get(0).getType().equals(SET_REFERENCE)) {
					if (!hasCallEmptyInv(anIf.getThenStatement(), SET_REFERENCE)) {
						fail(log(element, "Should initilize the list with CtElementImpl#emptySet()."));
					}
				} else {
					if (!hasCallEmptyInv(anIf.getThenStatement(), LIST_REFERENCE)) {
						fail(log(element, "Should initilize the list with CtElementImpl#emptyList()."));
					}
				}
			}

			private boolean hasCallEmptyInv(CtBlock thenStatement, CtTypeReference<? extends Collection> collectionReference) {
				if (!(thenStatement.getStatement(0) instanceof CtAssignment)) {
					return false;
				}
				final CtExpression assignment = ((CtAssignment) thenStatement.getStatement(0)).getAssignment();
				if (!(assignment instanceof CtInvocation)) {
					return false;
				}
				final CtInvocation inv = (CtInvocation) assignment;
				if (collectionReference.equals(SET_REFERENCE)) {
					if (!inv.getExecutable().getSimpleName().equals("emptySet")) {
						return false;
					}
				} else if (collectionReference.equals(LIST_REFERENCE)) {
					if (!inv.getExecutable().getSimpleName().equals("emptyList")) {
						return false;
					}
				}
				return true;
			}

			/**
			 * Creates <code>list == null && list.isEmpty()</code>.
			 *
			 * @param ctParameter <code>list</code>
			 */
			private CtBinaryOperator<Boolean> createCheckNull(CtParameter<?> ctParameter) {
				final CtVariableAccess<?> variableRead = factory.Code().createVariableRead(ctParameter.getReference(), true);

				final CtLiteral nullLiteral = factory.Code().createLiteral(null);
				nullLiteral.setType(factory.Type().nullType());

				final CtBinaryOperator<Boolean> checkNull = factory.Code().createBinaryOperator(variableRead, nullLiteral, BinaryOperatorKind.EQ);
				checkNull.setType(factory.Type().BOOLEAN_PRIMITIVE);

				final CtMethod<Boolean> isEmptyMethod = ctParameter.getType().getTypeDeclaration().getMethod(factory.Type().booleanPrimitiveType(), "isEmpty");
				final CtInvocation<Boolean> isEmpty = factory.Code().createInvocation(variableRead, isEmptyMethod.getReference());

				final CtBinaryOperator<Boolean> condition = factory.Code().createBinaryOperator(checkNull, isEmpty, BinaryOperatorKind.OR);
				return condition.setType(factory.Type().booleanPrimitiveType());
			}

			private String log(CtMethod<?> element, String message) {
				return message + "\nin " + element.getSignature() + "\ndeclared in " + element.getDeclaringType().getQualifiedName();
			}
		}.scan(factory.getModel().getRootPackage());
	}
}
