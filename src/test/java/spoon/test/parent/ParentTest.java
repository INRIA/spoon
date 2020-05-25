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
package spoon.test.parent;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.SpoonResourceHelper;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.intercession.IntercessionScanner;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.UnsettableProperty;
import spoon.test.replace.testclasses.Tacos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class ParentTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/parent/Foo.java"))
				.build();
	}

	@Test
	public void testParent() {
		// toString should not throw a parent exception even if parents are not set
		try {
			CtLiteral<Object> literal = factory.Core().createLiteral();
			literal.setValue(1);
			CtBinaryOperator<?> minus = factory.Core().createBinaryOperator();
			minus.setKind(BinaryOperatorKind.MINUS);
			minus.setRightHandOperand(literal);
			minus.setLeftHandOperand(literal);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testParentSet() {
		CtClass<?> foo = factory.Package().get("spoon.test.parent")
				.getType("Foo");

		CtMethod<?> fooMethod = foo.getMethodsByName("foo").get(0);
		assertEquals("foo", fooMethod.getSimpleName());

		CtLocalVariable<?> localVar = (CtLocalVariable<?>) fooMethod.getBody()
				.getStatements().get(0);

		CtAssignment<?,?> assignment = (CtAssignment<?,?>) fooMethod.getBody()
				.getStatements().get(1);


		CtLiteral<?> newLit = factory.Code().createLiteral(0);
		localVar.setDefaultExpression((CtExpression) newLit);
		assertEquals(localVar, newLit.getParent());

		CtLiteral<?> newLit2 = factory.Code().createLiteral(1);
		assignment.setAssignment((CtExpression) newLit2);
		assertEquals(assignment, newLit2.getParent());

	}

	@Test
	public void testAddType() {
		// contract: addType should set Parent
		CtClass<?> clazz = factory.Core().createClass();
		clazz.setSimpleName("Foo");
		CtPackage pack = factory.Core().createPackage();
		pack.setSimpleName("bar");
		pack.addType(clazz);
		assertTrue(pack.getTypes().contains(clazz));
		assertEquals(pack, clazz.getParent());

		// contract: addType always retains the latest version of the type
		CtClass<?> clone = clazz.clone();
		clone.putMetadata("metadata", "bar");
		// clone and clazz have the same qualified name
		// so the latest version (clone) replaces the previous one
		pack.addType(clone);
		assertEquals("bar", pack.getType("Foo").getMetadata("metadata"));
	}

	@Test
	public void testParentOfCtPackageReference() {
		// contract: a parent at a top level must be the root package and in the code, the element which call getParent().
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/reference-package");
		launcher.run();

		final CtType<Object> panini = launcher.getFactory().Type().get("Panini");

		CtElement topLevelParent = panini.getPackage().getParent();
		assertNotNull(topLevelParent);
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME, panini.getPackage().getSimpleName());
		CtPackage pack1 = factory.Package().getRootPackage();

		// the factory are not the same
		assertNotEquals(factory, launcher.getFactory());
		// so the root packages are not deeply equals
		assertNotEquals(pack1, topLevelParent);

		final CtTypeReference<?> burritos = panini.getElements(new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				return "Burritos".equals(reference.getSimpleName()) && super.matches(reference);
			}
		}).get(0);

		assertNotNull(burritos.getPackage().getParent());
		assertEquals("com.awesome", burritos.getPackage().getSimpleName());
		assertEquals(burritos, burritos.getPackage().getParent());
	}

	@Test
	public void testParentOfCtVariableReference() throws Exception {
		// contract: parent of a variable reference is the element which call getVariable().
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);

		final CtInvocation inv = aTacos.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		final CtVariableRead<?> variableRead = (CtVariableRead<?>) inv.getArguments().get(0);
		final CtParameterReference<?> aParameterReference = (CtParameterReference<?>) variableRead.getVariable();

		assertNotNull(aParameterReference.getParent());
		assertEquals(variableRead, aParameterReference.getParent());
	}

	@Test
	public void testParentOfCtExecutableReference() throws Exception {
		// contract: parent of a executable reference is the element which call getExecutable().
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);

		final CtInvocation inv = aTacos.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		final CtExecutableReference oldExecutable = inv.getExecutable();

		assertNotNull(oldExecutable.getParent());
		assertEquals(inv, oldExecutable.getParent());
	}

	@Test
	public void testParentOfGenericInTypeReference() throws Exception {
		// contract: parent of a generic in a type reference is the type reference.
		final Factory factory = build(Tacos.class);
		final CtTypeReference referenceWithGeneric = Query.getElements(factory, new ReferenceTypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference reference) {
				return !reference.getActualTypeArguments().isEmpty() && super.matches(reference);
			}
		}).get(0);
		final CtTypeReference<?> generic = referenceWithGeneric.getActualTypeArguments().get(0);

		assertNotNull(generic.getParent());
		assertEquals(referenceWithGeneric, generic.getParent());
	}

	@Test
	public void testParentOfPrimitiveReference() throws Exception {
		// contract: parent of a primitive different isn't different of other type. Its parent is the element which used this type.
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);
		final CtMethod<?> aMethod = aTacos.getMethodsByName("m").get(0);

		assertNotNull(aMethod.getType().getParent());
		assertEquals(factory.Type().INTEGER_PRIMITIVE, aMethod.getType());
		assertEquals(aMethod, aMethod.getType().getParent());
	}

	@Test
	public void testGetParentWithFilter() {
		// addType should set Parent
		CtClass<Foo> clazz = (CtClass<Foo>) factory.Class().getAll().get(0);

		CtMethod<Object> m = clazz.getMethod("m");
		// get three = "" in one = two = three = "";
		CtExpression statement = ((CtAssignment)((CtAssignment)m.getBody().getStatement(3)).getAssignment()).getAssignment();
		CtPackage ctPackage = statement.getParent(new TypeFilter<>(CtPackage.class));
		assertEquals(Foo.class.getPackage().getName(), ctPackage.getQualifiedName());

		CtStatement ctStatement = statement
				.getParent(new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement element) {
						return element.getParent() instanceof CtStatementList && super.matches(element);
					}
				});
		// the filter has to return one = two = three = ""
		assertEquals(m.getBody().getStatement(3), ctStatement);

		m = clazz.getMethod("internalClass");
		CtStatement ctStatement1 = m.getElements(
				new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement element) {
						return element instanceof CtLocalVariable && super.matches(element);
					}
				}).get(0);

		// get the top class
		ctStatement1.getParent(CtType.class);
		CtType parent = ctStatement1
				.getParent(new AbstractFilter<CtType>(CtType.class) {
					@Override
					public boolean matches(CtType element) {
						return !element.isAnonymous() && element.isTopLevel() && super.matches(element);
					}
				});
		assertEquals(clazz, parent);
		assertNotEquals(ctStatement1.getParent(CtType.class), parent);

		// not present element
		CtWhile ctWhile = ctStatement1.getParent(new TypeFilter<>(CtWhile.class));
		assertNull(ctWhile);

		CtStatement statementParent = statement
				.getParent(new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement element) {
						return true;
					}
				});
		// getParent must not return the current element
		assertNotEquals(statement, statementParent);
	}

	@Test
	public void testHasParent() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/resources/reference-package/Panini.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		try {
			final CtType<Object> aPanini = launcher.getFactory().Type().get("Panini");
			assertNotNull(aPanini);
			assertFalse(aPanini.hasParent(aPanini.getFactory().Core().createAnnotation()));
			assertTrue(aPanini.getMethod("m").hasParent(aPanini));
		} catch (NullPointerException e) {
			fail();
		}
	}

	@Test
	@Ignore // too fragile because of conventions
	public void testParentSetInSetter() {
		// contract: Check that all setters protect their parameter.
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		// Utils.
		launcher.addInputResource("./src/test/java/spoon/reflect/ast/");
		launcher.buildModel();

		// Asserts.
		new IntercessionScanner(launcher.getFactory()) {

			@Override
			protected boolean isToBeProcessed(CtMethod<?> candidate) {
				return (candidate.getSimpleName().startsWith("set") //
						|| candidate.getSimpleName().startsWith("add")) //
						&& candidate.hasModifier(ModifierKind.PUBLIC) //
						&& takeSetterForCtElement(candidate) //
						&& avoidInterfaces(candidate) //
						&& avoidThrowUnsupportedOperationException(candidate);
			}

			@Override
			public void process(CtMethod<?> element) {
				if (element.getAnnotation(UnsettableProperty.class) != null) {
					// we don't check the contracts for unsettable setters
					return;
				}
				if (element.getSimpleName().startsWith("add")) {
					checkAddStrategy(element);
				} else {
					checkSetStrategy(element);
				}
			}

			private void checkAddStrategy(CtMethod<?> element) {
				final CtStatement statement = element.getBody().getStatement(0);
				if (!(statement instanceof CtIf)) {
					fail("First statement should be an if to check the parameter of the setter." + element.getSignature() + " declared in " + element.getDeclaringType().getQualifiedName());
				}
				if (!createCheckNull(element.getParameters().get(0)).equals(((CtIf) statement).getCondition())) {
					fail("Condition should test if the parameter is null. The condition was " + ((CtIf) statement).getCondition() + "in " + element.getSignature() + " declared in " + element
							.getDeclaringType().getQualifiedName());
				}
			}

			private void checkSetStrategy(CtMethod<?> element) {
				final CtTypeReference<?> type = element.getParameters().get(0).getType();
				if (!COLLECTIONS.contains(type) && !(type instanceof CtArrayTypeReference)) {
					CtInvocation<?> setParent = searchSetParent(element.getBody());
					if (setParent == null) {
						return;
					}
					try {
						if (setParent.getParent(CtIf.class) == null) {
							fail("Missing condition in " + element.getSignature() + " declared in the class " + element.getDeclaringType().getQualifiedName());
						}
					} catch (ParentNotInitializedException e) {
						fail("Missing parent condition in " + element.getSignature() + " declared in the class " + element.getDeclaringType().getQualifiedName());
					}
				}
			}

			/**
			 * Creates <code>parameter == null</code>.
			 *
			 * @param ctParameter <code>parameter</code>
			 */
			private CtBinaryOperator<Boolean> createCheckNull(CtParameter<?> ctParameter) {
				final CtLiteral nullLiteral = factory.Code().createLiteral(null);
				nullLiteral.setType(factory.Type().NULL_TYPE.clone());
				final CtBinaryOperator<Boolean> operator = factory.Code().createBinaryOperator( //
						factory.Code().createVariableRead(ctParameter.getReference(), true), //
						nullLiteral, BinaryOperatorKind.EQ);
				operator.setType(factory.Type().BOOLEAN_PRIMITIVE);
				return operator;
			}

			private CtInvocation<?> searchSetParent(CtBlock<?> body) {
				final List<CtInvocation<?>> ctInvocations = body.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
					@Override
					public boolean matches(CtInvocation<?> element) {
						return "setParent".equals(element.getExecutable().getSimpleName()) && super.matches(element);
					}
				});
				return !ctInvocations.isEmpty() ? ctInvocations.get(0) :  null;
			}
		}.scan(launcher.getModel().getRootPackage());
	}


	@Test
	public void testParentNotInitializedException() throws IOException {
		// contract: does not crash on hasImplicitParent
		final Launcher l = new Launcher();
		Environment e = l.getEnvironment();

		e.setNoClasspath(true);
		e.setAutoImports(true);
		e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(l.getEnvironment()));

		Path path = Files.createTempDirectory("emptydir");
		l.addInputResource("src/test/resources/compilation4/A.java");
		l.setSourceOutputDirectory(path.toFile());
		l.run();
	}

}
