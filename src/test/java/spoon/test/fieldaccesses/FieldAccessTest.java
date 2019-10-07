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
package spoon.test.fieldaccesses;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.ImportConflictDetector;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.fieldaccesses.testclasses.B;
import spoon.test.fieldaccesses.testclasses.Kuu;
import spoon.test.fieldaccesses.testclasses.Mouse;
import spoon.test.fieldaccesses.testclasses.Panini;
import spoon.test.fieldaccesses.testclasses.Pozole;
import spoon.test.fieldaccesses.testclasses.Tacos;
import spoon.test.fieldaccesses.testclasses.MyClass;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class FieldAccessTest {

	@Test
	public void testModelBuildingFieldAccesses() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses.testclasses", "Mouse");
		assertEquals("Mouse", type.getSimpleName());

		CtMethod<?> meth1 = type.getElements(
				new NamedElementFilter<>(CtMethod.class,"meth1")).get(0);
		CtMethod<?> meth1b = type.getElements(
				new NamedElementFilter<>(CtMethod.class,"meth1b")).get(0);

		assertEquals(
				3,
				meth1.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		assertEquals(
				2,
				meth1b.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		CtMethod<?> meth2 = type.getElements(
				new NamedElementFilter<>(CtMethod.class,"meth2")).get(0);
		assertEquals(
				2,
				meth2.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		CtMethod<?> meth3 = type.getElements(
				new NamedElementFilter<>(CtMethod.class,"meth3")).get(0);
		assertEquals(
				3,
				meth3.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		CtMethod<?> meth4 = type.getElements(
				new NamedElementFilter<>(CtMethod.class,"meth4")).get(0);
		assertEquals(
				1,
				meth4.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

	}

	@Test
	public void testBCUBug20140402() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses.testclasses",
				"BCUBug20140402");
		assertEquals("BCUBug20140402", type.getSimpleName());

		CtLocalVariable<?> var = type.getElements(
				new TypeFilter<CtLocalVariable<?>>(CtLocalVariable.class)).get(0);
		CtFieldAccess<?> expr = (CtFieldAccess<?>) var.getDefaultExpression();
		assertEquals(
				"length",
				expr.getVariable().toString());
		assertEquals(
				"int",
				expr.getType().getSimpleName());

		// in the model the top-most field access is .length get(0)
		// and the second one is ".data" get(1)
		CtFieldAccess<?> fa = expr.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class)).get(1);
		// we check that we have the data
		assertEquals(
				"data",
				fa.getVariable().toString());
		assertEquals(
				"java.lang.Object[]",
				fa.getType().toString());

		// testing the proxy method setAssignment/getAssignment on local variables
		var.setAssignment(null);
		assertNull(var.getAssignment());
		assertEquals("int a", var.toString());

		// testing the proxy method setAssignment/getAssignment on fields
		CtField<?> field = type.getElements(
				new TypeFilter<CtField<?>>(CtField.class)).get(0);
		assertNotNull(field.getAssignment());
		field.setAssignment(null);
		assertNull(field.getAssignment());
		assertEquals("java.lang.Object[] data;", field.toString());

	}

	@Test
	public void testBUG20160112() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses.testclasses", "BUG20160112");
		assertEquals("BUG20160112", type.getSimpleName());
		CtOperatorAssignment<?, ?> ass = type.getElements(
				new TypeFilter<CtOperatorAssignment<?, ?>>(CtOperatorAssignment.class)).get(0);
		assertNotNull("z+=a.us", ass);
		CtExpression<?> righthand = ass.getAssignment();
		assertTrue("a.us should be CtFieldRead", righthand instanceof CtFieldRead);
	}

	@Test
	public void testTargetedAccessPosition() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses.testclasses", "TargetedAccessPosition");
		List<CtFieldAccess<?>> vars = type.getElements(new TypeFilter<>(CtFieldAccess.class));
		//vars is [t.ta.ta.ta.ta, t.ta.ta.ta, t.ta.ta, t.ta]
		assertEquals(4, vars.size());

		assertEquals(vars.get(1), vars.get(0).getTarget());

		// 6 is length(t.ta.ta) - 1
		assertEquals(6, vars.get(2).getPosition().getSourceEnd() - vars.get(0).getPosition().getSourceStart());

		// 3 is length(t.ta) - 1
		assertEquals(3, vars.get(2).getTarget().getPosition().getSourceEnd() - vars.get(0).getTarget().getPosition().getSourceStart());

		// 0 is length(t)-1
		assertEquals(0, ((CtFieldAccess<?>) vars.get(2).getTarget()).getTarget().getPosition().getSourceEnd() -
				((CtFieldAccess<?>) vars.get(2).getTarget()).getTarget().getPosition().getSourceStart());
	}

	@Test
	public void testFieldAccess() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses.testclasses", "TargetedAccessPosition");
		List<Object> list1 = type.filterChildren(new TypeFilter<>(CtFieldWrite.class)).list();
		assertEquals(1, list1.size());

		List<CtFieldRead> list2 = type.filterChildren(new TypeFilter<>(CtFieldRead.class)).list();
		assertEquals(3, list2.size());

		// this is the tricky part :-)
		Collections.reverse(list2);

		// now we replace them all
		for(CtFieldRead r : list2) {
			// quick'n'dirty solution with snippets
			// a better way is to create a CtInvocation
			CtCodeSnippetExpression s = r.getFactory().createCodeSnippetExpression("f("+r.toString()+")");
			r.replace(s);
		}

		assertEquals("f(f(f(t.ta).ta).ta).ta = t", type.getMethodsByName("foo").get(0).getBody().getStatement(1).toString());
	}

	@Test
	public void testFieldAccessInLambda() throws Exception {
		Factory build = null;
		try {
			build = build(MyClass.class);
		} catch (NullPointerException ignore) {
			fail();
		}

		final CtFieldAccess logFieldAccess = Query.getElements(build, new TypeFilter<>(CtFieldAccess.class)).get(0);

		assertSame(Logger.class, logFieldAccess.getType().getActualClass());
		assertEquals("LOG", logFieldAccess.getVariable().getSimpleName());
		assertSame(MyClass.class, logFieldAccess.getVariable().getDeclaringType().getActualClass());

		String expectedLambda = "() -> {" + System.lineSeparator() + "    spoon.test.fieldaccesses.testclasses.MyClass.LOG.info(\"bla\");" + System.lineSeparator() + "}";
		assertEquals(expectedLambda, logFieldAccess.getParent(CtLambda.class).toString());
	}

	@Test
	public void testFieldAccessInAnonymousClass() throws Exception {
		final Factory factory = build(Panini.class);
		final CtType<Panini> panini = factory.Type().get(Panini.class);

		final CtFieldRead fieldInAnonymous = panini.getElements(new TypeFilter<>(CtFieldRead.class)).get(0);
		assertEquals("ingredient", fieldInAnonymous.getTarget().toString());
		assertEquals("next", fieldInAnonymous.getVariable().getSimpleName());
		assertEquals("ingredient.next", fieldInAnonymous.toString());
	}


	@Test
	public void testFieldAccessNoClasspath() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/import-resources/fr/inria/");
		launcher.getEnvironment().setNoClasspath(true);

		launcher.run();

		CtType<?> ctType = launcher.getFactory().Class().get("FooNoClassPath");

		CtFieldAccess ctFieldAccess = ctType
				.getElements(new TypeFilter<>(CtFieldAccess.class)).get(0);
		assertEquals("game.board.width", ctFieldAccess.toString());

		CtFieldReference ctFieldReferenceWith = ctFieldAccess.getVariable();
		assertEquals("width", ctFieldReferenceWith.getSimpleName());

		CtFieldAccess ctFieldAccessBoard = (CtFieldAccess) ctFieldAccess.getTarget();
		assertEquals("game.board", ctFieldAccessBoard.toString());

		CtFieldReference ctFieldReferenceBoard = ctFieldAccessBoard.getVariable();
		assertEquals("board", ctFieldReferenceBoard.getSimpleName());

		CtFieldAccess ctFieldAccessGame = (CtFieldAccess) ctFieldAccessBoard.getTarget();
		assertEquals("game.board", ctFieldAccessBoard.toString());

		CtFieldReference ctFieldReferenceGame = ctFieldAccessGame.getVariable();
		assertEquals("game", ctFieldReferenceGame.getSimpleName());
	}

	@Test
	public void testIncrementationOnAVarIsAUnaryOperator() throws Exception {
		// contract: When we use var++, the variable is a read access with an unary operator.
		final CtType<Panini> aMole = buildClass(Panini.class);
		final CtMethod<?> make = aMole.getMethodsByName("make").get(0);
		final List<CtUnaryOperator<?>> unaryOperators = make.getElements(new TypeFilter<>(CtUnaryOperator.class));

		final CtFieldWrite<Object> fieldRead = aMole.getFactory().Core().createFieldWrite();
		fieldRead.setTarget(aMole.getFactory().Code().createThisAccess(aMole.getReference(), true));
		final CtFieldReference fieldReference = aMole.getField("i").getReference();
		fieldRead.setVariable(fieldReference);

		assertEquals(2, unaryOperators.size());
		final CtUnaryOperator<?> first = unaryOperators.get(0);
		assertEquals(UnaryOperatorKind.POSTINC, first.getKind());
		assertEquals(fieldRead, first.getOperand());
		assertEquals("i++", first.toString());

		final CtUnaryOperator<?> second = unaryOperators.get(1);
		assertEquals(UnaryOperatorKind.PREINC, second.getKind());
		assertEquals(fieldRead, second.getOperand());
		assertEquals("++i", second.toString());
	}

	@Test
	public void testFieldWriteWithPlusEqualsOperation() throws Exception {
		// contract: When we use var += value, the var is a write access.
		final CtType<Panini> aPanini = buildClass(Panini.class);
		final CtMethod<?> prepare = aPanini.getMethodsByName("prepare").get(0);

		final List<CtFieldWrite<?>> fields = prepare.getElements(new TypeFilter<>(CtFieldWrite.class));
		assertEquals(1, fields.size());
		assertEquals(aPanini.getField("i").getReference(), fields.get(0).getVariable());
		assertEquals("i += 0", fields.get(0).getParent().toString());
		assertEquals("i", fields.get(0).toString());

		final List<CtVariableWrite<?>> variables = prepare.getElements(new TypeFilter<>(CtVariableWrite.class));
		assertEquals(2, variables.size());
		assertEquals("j += 0", variables.get(1).getParent().toString());
		assertEquals("j", variables.get(1).toString());

		final List<CtArrayWrite<?>> arrays = prepare.getElements(new TypeFilter<>(CtArrayWrite.class));
		assertEquals(1, arrays.size());
		assertEquals("array[0] += 0", arrays.get(0).getParent().toString());
		assertEquals("array[0]", arrays.get(0).toString());
	}

	@Test
	public void testTypeDeclaredInAnonymousClass() throws Exception {
		// contract: Type declared in an anonymous class shouldn't include the anonymous qualified name
		// in its own fully qualified name.
		final CtType<Pozole> aPozole = buildClass(Pozole.class);
		final List<CtField> elements = aPozole.getElements(new TypeFilter<>(CtField.class));

		assertEquals(1, elements.size());
		assertTrue(elements.get(0).getType().getDeclaringType().isAnonymous());
		assertThat(elements.get(0)).isEqualTo("private final Test test = new Test();");
	}

	@Test
	public void testFieldAccessDeclaredInADefaultClass() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/fieldaccesses/testclasses/Tacos.java");
		launcher.addInputResource("./src/test/java/spoon/test/fieldaccesses/testclasses/internal/Foo.java");
		launcher.addInputResource("./src/test/java/spoon/test/fieldaccesses/testclasses/internal/Bar.java");
		launcher.run();

		final CtType<Object> aTacos = launcher.getFactory().Type().get(Tacos.class);
		final CtType<Object> aFoo = launcher.getFactory().Type().get("spoon.test.fieldaccesses.testclasses.internal.Foo");
		final CtTypeAccess<Object> aFooAccess = launcher.getFactory().Code().createTypeAccess(aFoo.getReference());
		final CtType<Object> aSubInner = launcher.getFactory().Type().get("spoon.test.fieldaccesses.testclasses.internal.Bar$Inner$SubInner");
		aFoo.addNestedType(aSubInner);
		final CtTypeAccess<Object> aSubInnerAccess = launcher.getFactory().Code().createTypeAccess(aSubInner.getReference());
		final CtType<Object> aKnowOrder = launcher.getFactory().Type().get("spoon.test.fieldaccesses.testclasses.internal.Bar$Inner$KnownOrder");
		aFoo.addNestedType(aKnowOrder);
		final CtTypeAccess<Object> aKnownOrderAccess = launcher.getFactory().Code().createTypeAccess(aKnowOrder.getReference());
		final CtMethod<Object> aMethod = aTacos.getMethod("m");
		final List<CtInvocation<?>> invs = aMethod.getElements(new TypeFilter<>(CtInvocation.class));

		assertEquals(aFooAccess, ((CtFieldAccess) invs.get(0).getArguments().get(0)).getTarget());
		assertEquals("inv(spoon.test.fieldaccesses.testclasses.internal.Foo.i)", invs.get(0).toString());
		assertEquals(aFooAccess, ((CtFieldAccess) invs.get(1).getArguments().get(0)).getTarget());
		assertEquals("inv(spoon.test.fieldaccesses.testclasses.internal.Foo.i)", invs.get(1).toString());
		assertEquals(aSubInnerAccess, ((CtFieldAccess) invs.get(2).getArguments().get(0)).getTarget());
		assertEquals("inv(spoon.test.fieldaccesses.testclasses.internal.Foo.SubInner.j)", invs.get(2).toString());
		assertEquals(aSubInnerAccess, ((CtFieldAccess) invs.get(3).getArguments().get(0)).getTarget());
		assertEquals("inv(spoon.test.fieldaccesses.testclasses.internal.Foo.SubInner.j)", invs.get(3).toString());
		assertEquals(aKnownOrderAccess, ((CtFieldAccess) invs.get(4).getArguments().get(0)).getTarget());
		assertEquals("runIteratorTest(spoon.test.fieldaccesses.testclasses.internal.Foo.KnownOrder.KNOWN_ORDER)", invs.get(4).toString());
		assertEquals(aKnownOrderAccess, ((CtFieldAccess) invs.get(5).getArguments().get(0)).getTarget());
		assertEquals("runIteratorTest(spoon.test.fieldaccesses.testclasses.internal.Foo.KnownOrder.KNOWN_ORDER)", invs.get(5).toString());

		final CtParameter<?> aKnownOrderParameter = aTacos.getMethod("runIteratorTest", aKnowOrder.getReference()).getParameters().get(0);
		assertEquals(aKnowOrder.getReference(), aKnownOrderParameter.getType());
		assertEquals("spoon.test.fieldaccesses.testclasses.internal.Foo.KnownOrder knownOrder", aKnownOrderParameter.toString());

		final CtParameter<?> aSubInnerParameter = aTacos.getMethod("inv", aSubInner.getReference()).getParameters().get(0);
		assertEquals(aSubInner.getReference(), aSubInnerParameter.getType());
		assertEquals("spoon.test.fieldaccesses.testclasses.internal.Foo.SubInner foo", aSubInnerParameter.toString());
	}

	@Test
	public void testTypeOfFieldAccess() throws Exception {
		CtType<Panini> aPanini = buildClass(Panini.class);
		List<CtFieldAccess> fieldAccesses = aPanini.getMethod("prepare").getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, fieldAccesses.size());
		assertNotNull(fieldAccesses.get(0).getType());
		assertEquals(fieldAccesses.get(0).getVariable().getType(), fieldAccesses.get(0).getType());
	}

	@Test
	public void testFieldAccessWithoutAnyImport() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/fieldaccesses/testclasses/Kuu.java");
		launcher.addInputResource("./src/test/java/spoon/test/fieldaccesses/testclasses/Mole.java");
		launcher.run();

		final CtType<Kuu> aType = launcher.getFactory().Type().get(Kuu.class);
		final DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(aType.getFactory().getEnvironment());
		assertEquals("spoon.test.fieldaccesses.testclasses.Mole.Delicious delicious", aType.getMethodsByName("m").get(0).getParameters().get(0).toString());
	}

	@Test
	public void testFieldAccessOnUnknownType() {
		final Launcher launcher = new Launcher();

		launcher.addInputResource("./src/test/resources/noclasspath/FieldAccessRes.java");

		launcher.getEnvironment().setNoClasspath(true);

		launcher.buildModel();

		class CounterScanner extends CtScanner {
			private int visited = 0;
			@Override
			public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
				visited++;
				assertEquals("a", ((CtVariableWrite) fieldWrite.getTarget()).getVariable().getSimpleName());
				assertEquals("l", fieldWrite.getVariable().getSimpleName());
			}
		}

		CounterScanner scanner = new CounterScanner();
		launcher.getFactory().Class().get("FieldAccessRes").accept(scanner);

		assertEquals(1, scanner.visited);
	}

	@Test
	public void testGetReference() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setShouldCompile(true);
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/fieldaccesses/testclasses/");
		launcher.run();

		final CtClass<B> aClass = launcher.getFactory().Class().get(B.class);

		// now static fields are used with the name of the parent class
		assertEquals("spoon.test.fieldaccesses.testclasses.A.myField", aClass.getElements(new TypeFilter<>(CtFieldWrite.class)).get(0).toString());

		// contract: accesses to final fields in static initializers are never fully-qualified
		// this was initial correct, and the regression was introduced in 85a3ab11f6e5caacd09f8402d0b674310c9d8ce5 on Oct 9 2019
		assertEquals("finalField", aClass.getElements(new TypeFilter<>(CtFieldWrite.class)).get(1).toString());
	}
	@Test
	public void testFieldAccessAutoExplicit() throws Exception {
		CtClass mouse = (CtClass) buildClass(Mouse.class);
		CtMethod method = mouse.filterChildren((CtMethod m)->"meth1".equals(m.getSimpleName())).first();
		
		CtFieldReference ageFR = method.filterChildren((CtFieldReference fr)->"age".equals(fr.getSimpleName())).first();
		//first is the field printed with implicit "this."
 		assertEquals("age", ageFR.getParent().toString());
 		//add local variable declaration which hides the field declaration 
 		method.getBody().insertBegin((CtStatement) mouse.getFactory().createCodeSnippetStatement("int age = 1").compile());
 		//run model validator to fix the problem
 		new ImportConflictDetector().process(mouse.getPosition().getCompilationUnit());
		//now the field access must use explicit "this."
 		assertEquals("this.age", ageFR.getParent().toString());
	}

	@Test
	public void testFieldAccessWithParenthesis() {
		// contract: there should not be any redundant parentheses around fields
		// https://github.com/INRIA/spoon/pull/3021
		CtClass<?> c1 = Launcher.parseClass("class C1 { int count ; void m() { for(int i=0;i<count;i++){}}}");
		assertEquals("count", c1.getElements(new TypeFilter<>(CtFieldAccess.class)).get(0).toString());

		CtClass c2 = Launcher.parseClass("class C1 { int count ; void m() { for(int i=0;i<(long)count;i++){}}}");
		assertEquals("((long) (count))", c2.getElements(new TypeFilter<>(CtFieldAccess.class)).get(0).toString());
	}
}
