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
package spoon.test.replace;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.visitor.replace.InvalidReplaceException;
import spoon.test.replace.testclasses.Mole;
import spoon.test.replace.testclasses.Tacos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class ReplaceTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/replace/testclasses"))
				.build();
	}

	@Test
	public void testReplaceSet() {

		CtClass<?> foo = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");
		assertEquals("Foo", foo.getSimpleName());
		CtClass<?> bar = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Bar");
		assertEquals("Bar", bar.getSimpleName());

		CtField<Number> i1 = (CtField<Number>) foo.getField("i");
		CtField<Number> i2 = (CtField<Number>) bar.getField("i");

		assertEquals("int", foo.getField("i").getType().getSimpleName());

		// do
		i1.replace(i2);
		assertSame(i2, foo.getField("i"));
		assertEquals("float", foo.getField("i").getType().getSimpleName());
		assertEquals(foo, i2.getParent());

		// undo
		i2.replace(i1);
		assertSame(i1, foo.getField("i"));
		assertEquals("int", foo.getField("i").getType().getSimpleName());
		assertEquals(foo, i1.getParent());
	}

	@Test
	public void testReplaceBlock() {
		CtClass<?> foo = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");
		CtMethod<?> m = foo.getElements(
				new NamedElementFilter<>(CtMethod.class,"foo")).get(0);
		assertEquals("foo", m.getSimpleName());

		final CtStatement parent = m.getBody().getStatements().get(2);
		CtAssignment<?, ?> assignment = (CtAssignment<?, ?>) parent;

		CtExpression<Integer> s1 = (CtExpression<Integer>) assignment.getAssignment();
		CtExpression<Integer> s2 = factory.Code().createLiteral(3);

		assertEquals("z = x + 1", assignment.toString());
		assertEquals("x + 1", s1.toString());

		// do
		s1.replace(s2);
		assertSame(s2, assignment.getAssignment());
		assertEquals("z = 3", assignment.toString());
		assertEquals(parent, s2.getParent());

		// undo
		s2.replace(s1);
		assertSame(s1, assignment.getAssignment());
		assertEquals("z = x + 1", assignment.toString());
		assertEquals(parent, s1.getParent());
	}

	@Test
	public void testReplaceReplace() {
		// bug found by Benoit
		CtClass<?> foo = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		CtMethod<?> fooMethod = foo.getElements(
				new NamedElementFilter<>(CtMethod.class,"foo")).get(0);
		assertEquals("foo", fooMethod.getSimpleName());

		CtMethod<?> barMethod = foo.getElements(
				new NamedElementFilter<>(CtMethod.class,"bar")).get(0);
		assertEquals("bar", barMethod.getSimpleName());

		CtLocalVariable<?> assignment = (CtLocalVariable<?>) fooMethod.getBody()
				.getStatements().get(0);
		CtLocalVariable<?> newAssignment = barMethod.getBody().getStatement(0);

		assignment.replace(newAssignment);

		assertEquals(fooMethod.getBody(), newAssignment.getParent());

		CtLiteral<Integer> lit = (CtLiteral<Integer>) foo.getElements(new TypeFilter<CtLiteral<?>>(CtLiteral.class))
				.get(0);
		final CtElement parent = lit.getParent();
		CtLiteral<Integer> newLit = factory.Code().createLiteral(0);
		lit.replace(newLit);
		assertEquals("int y = 0", fooMethod.getBody().getStatement(0).toString());
		assertEquals(parent, newLit.getParent());
	}

	@Test
	public void testReplaceStmtByList() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		// replace retry content by statements
		CtStatement stmt = sample.getMethod("retry").getBody().getStatement(0);
		CtBlock lst = sample.getMethod("statements").getBody();

		// replace a single statement by a statement list
		stmt.replace(lst);

		// we should have only 2 statements after (from the stmt list)
		assertEquals(1, sample.getMethod("retry").getBody().getStatements().size());
		assertEquals(2, ((CtBlock) sample.getMethod("retry").getBody().getStatement(0)).getStatements().size());
	}

	@Test
	public void testReplaceStmtByListStatements() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		// replace retry content by statements
		CtStatement stmt = sample.getMethod("retry").getBody().getStatement(0);
		List<CtStatement> lst = sample.getMethod("statements").getBody().getStatements();

		// replace a single statement by a statement list
		stmt.replace(lst);

		// we should have only 2 statements after (from the stmt list)
		assertEquals(2, sample.getMethod("retry").getBody().getStatements().size());
	}

	@Test
	public void testReplaceStmtByListStatementsAndNull() {
		//contract: null elements in list are ignored
		CtClass<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		// replace retry content by statements
		CtStatement stmt = sample.getMethod("retry").getBody().getStatement(0);
		List<CtStatement> lst = sample.getMethod("statements").getBody().getStatements();
		List<CtStatement> lstWithNulls = new ArrayList<>();
		lstWithNulls.add(null);
		lstWithNulls.add(lst.get(0));
		lstWithNulls.add(null);
		lstWithNulls.add(lst.get(1));
		lstWithNulls.add(null);

		// replace a single statement by a statement list
		stmt.replace(lstWithNulls);

		// we should have only 2 statements after (from the stmt list)
		assertEquals(2, sample.getMethod("retry").getBody().getStatements().size());
	}

	@Test
	public void testReplaceField() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		assertEquals(factory.Type().createReference(int.class), sample.getField("i").getType());

		// replace with another type
		CtField replacement = factory.Core().createField();
		replacement.setSimpleName("i");
		replacement.setType(factory.Type().createReference(double.class));
		sample.getField("i").replace(replacement);
		assertEquals(factory.Type().createReference(double.class), sample.getField("i").getType());

		// replace with another name
		replacement = factory.Core().createField();
		replacement.setSimpleName("j");
		replacement.setType(factory.Type().createReference(double.class));
		sample.getField("i").replace(replacement);
		assertNull(sample.getField("i"));
		assertNotNull(sample.getField("j"));
		assertEquals(factory.Type().createReference(double.class), sample.getField("j").getType());
	}

	@Test
	public void testReplaceMethod() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		assertNotNull(sample.getMethod("foo"));
		assertNull(sample.getMethod("notfoo"));

		CtMethod bar = factory.Core().createMethod();
		bar.setSimpleName("notfoo");
		bar.setType(factory.Type().createReference(void.class));
		sample.getMethod("foo").replace(bar);

		assertNull(sample.getMethod("foo"));
		assertNotNull(sample.getMethod("notfoo"));
	}

	@Test
	public void testReplaceTwoMethods() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo");

		assertNotNull(sample.getMethod("foo"));
		assertNull(sample.getMethod("notfoo"));

		CtMethod bar = factory.Core().createMethod();
		bar.setSimpleName("notfoo");
		bar.setType(factory.Type().createReference(void.class));
		CtMethod bar2 = factory.Core().createMethod();
		bar2.setSimpleName("notfoo2");
		bar2.setType(factory.Type().createReference(void.class));
		int originCountOfMethods = sample.getTypeMembers().size();
		sample.getMethod("foo").replace(Arrays.asList(bar, bar2));

		assertNull(sample.getMethod("foo"));
		assertNotNull(sample.getMethod("notfoo"));
		assertNotNull(sample.getMethod("notfoo2"));
		assertEquals(originCountOfMethods+1, sample.getTypeMembers().size());
	}

	@Test
	public void testReplaceExpression() {
		CtMethod<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo").getMethod("foo");

		CtVariable<?> var = sample.getBody().getStatement(0);

		assertTrue(var.getDefaultExpression() instanceof CtLiteral);
		assertEquals(3, ((CtLiteral<?>) var.getDefaultExpression()).getValue());

		CtLiteral replacement = factory.Core().createLiteral();
		replacement.setValue(42);
		var.getDefaultExpression().replace(replacement);

		assertEquals(42, ((CtLiteral<?>) var.getDefaultExpression()).getValue());

	}

	@Test
	public void testReplaceStatement() {
		CtMethod<?> sample = factory.Package().get("spoon.test.replace.testclasses")
				.getType("Foo").getMethod("foo");

		assertTrue(sample.getBody().getStatement(0) instanceof CtVariable);

		CtStatement replacement = factory.Core().createInvocation();
		sample.getBody().getStatement(0).replace(replacement);

		assertTrue(sample.getBody().getStatement(0) instanceof CtInvocation);
	}

	@Test
	public void testReplaceIntegerReference() throws Exception {
		// contract: replace a primitive type reference by another one.
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);
		final CtMethod<?> aMethod = aTacos.getMethodsByName("m").get(0);

		assertEquals(factory.Type().INTEGER_PRIMITIVE, aMethod.getType());

		aMethod.getType().replace(factory.Type().DOUBLE_PRIMITIVE);

		assertEquals(factory.Type().DOUBLE_PRIMITIVE, aMethod.getType());
	}

	@Test
	public void testReplaceAllTypeRefenceWithGenerics() throws Exception {
		// contract: replace all type references with a generic to the same type reference without generics.
		final Factory factory = build(Tacos.class);
		final List<CtTypeReference> references = Query.getElements(factory, new ReferenceTypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference reference) {
				return !reference.getActualTypeArguments().isEmpty() && super.matches(reference);
			}
		});

		references.get(0).replace(factory.Type().createReference(references.get(0).getQualifiedName()));

		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);
		final CtMethod<?> aMethod = aTacos.getMethodsByName("m2").get(0);

		final CtTypeReference<Object> expected = factory.Type().createReference("spoon.test.replace.testclasses.Tacos");
		assertEquals(expected, aMethod.getType());
		assertEquals(expected.getTypeDeclaration(), aMethod.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0).getType().getTypeDeclaration());
	}

	@Test
	public void testReplaceAPackageReferenceByAnotherOne() {
		// contract: replace a package reference of a reference to another package.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/reference-package");
		launcher.run();

		final CtType<Object> panini = launcher.getFactory().Type().get("Panini");

		final CtTypeReference<?> burritos = panini.getElements(new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				return "Burritos".equals(reference.getSimpleName()) && super.matches(reference);
			}
		}).get(0);

		assertEquals("com.awesome", burritos.getPackage().toString());
		assertEquals("com.awesome.Burritos", panini.getMethodsByName("m").get(0).getType().toString());

		burritos.getPackage().replace(launcher.getFactory().Package().createReference("com.best"));

		assertEquals("com.best", burritos.getPackage().toString());
		assertEquals("com.best.Burritos", panini.getMethodsByName("m").get(0).getType().toString());
	}

	@Test
	public void testReplaceAParameterReferenceToFieldReference() throws Exception {
		// contract: replace a parameter reference to a field reference.
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);

		final CtInvocation inv = aTacos.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		final CtVariableRead<?> variableRead = (CtVariableRead<?>) inv.getArguments().get(0);
		final CtParameterReference<?> aParameterReference = (CtParameterReference<?>) variableRead.getVariable();
		final CtFieldReference<?> aFieldReference = aTacos.getField("field").getReference();

		assertEquals(aParameterReference, variableRead.getVariable());
		assertEquals("java.lang.System.err.println(param)", inv.toString());

		aParameterReference.replace(aFieldReference);

		assertEquals(aFieldReference, variableRead.getVariable());
		assertEquals("java.lang.System.err.println(field)", inv.toString());
	}

	@Test
	public void testReplaceExecutableReferenceByAnotherOne() throws Exception {
		// contract: replace an executable reference to another one in an invocation.
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);

		final CtInvocation inv = aTacos.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		final CtExecutableReference oldExecutable = inv.getExecutable();
		final CtExecutableReference<Object> newExecutable = factory.Executable().createReference("void java.io.PrintStream#print(java.lang.String)");

		assertSame(oldExecutable, inv.getExecutable());

		oldExecutable.replace(newExecutable);

		assertSame(newExecutable, inv.getExecutable());
		assertEquals("print(java.lang.String)", inv.getExecutable().toString());
		assertEquals("java.io.PrintStream", inv.getExecutable().getDeclaringType().toString());

		//contract: replace of single value by multiple values in single value field must fail
		try {
			newExecutable.replace(Arrays.asList(oldExecutable, null));
			fail();
		} catch (InvalidReplaceException e) {
			//OK
		}
	}

	@Test
	public void testReplaceBlockTry() throws Exception {
		final CtType<Mole> aMole = buildClass(Mole.class);
		final CtBlock<?> newBlock = aMole.getFactory().Code().createCtBlock(aMole.getFactory().Code().createCodeSnippetStatement("int j = 0;").compile());
		final CtTry ctTry = aMole.getMethod("m").getElements(new TypeFilter<>(CtTry.class)).get(0);

		assertNotEquals(newBlock, ctTry.getBody());
		ctTry.getBody().replace(newBlock);
		assertEquals(newBlock, ctTry.getBody());
	}
}
