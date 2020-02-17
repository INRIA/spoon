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
package spoon.test.variable;

import org.junit.Test;
import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.main.MainTest;
import spoon.test.variable.testclasses.ArrayAccessSample;
import spoon.test.variable.testclasses.FieldAccessSample;
import spoon.test.variable.testclasses.RHSSample;
import spoon.test.variable.testclasses.StackedAssignmentSample;
import spoon.test.variable.testclasses.VariableAccessSample;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

public class AccessTest {
	@Test
	public void testCanVisitVariableAccessAndSubClasses() throws Exception {
		final Factory factory = build(VariableAccessSample.class);

		final List<CtVariableRead<?>> variablesRead =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableRead<?>>(CtVariableRead.class) {
									  @Override
									  public boolean matches(CtVariableRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, variablesRead.size());// System.out and s

		final List<CtVariableWrite<?>> variablesWrite =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableWrite<?>>(CtVariableWrite.class) {
									  @Override
									  public boolean matches(CtVariableWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(1, variablesWrite.size());

		final List<CtVariableAccess<?>> variablesAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableAccess<?>>(CtVariableAccess.class) {
									  @Override
									  public boolean matches(CtVariableAccess<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(3, variablesAccess.size());
	}

	@Test
	public void testCanVisitFieldAccessAndSubClasses() throws Exception {
		final Factory factory = build(FieldAccessSample.class);

		final List<CtFieldRead<?>> fieldsRead =
				Query.getElements(factory,
								  new AbstractFilter<CtFieldRead<?>>(CtFieldRead.class) {
									  @Override
									  public boolean matches(CtFieldRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, fieldsRead.size());

		final List<CtFieldWrite<?>> fieldsWrite =
				Query.getElements(factory,
								  new AbstractFilter<CtFieldWrite<?>>(CtFieldWrite.class) {
									  @Override
									  public boolean matches(CtFieldWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(3, fieldsWrite.size());

		final List<CtFieldAccess<?>> fieldsAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtFieldAccess<?>>(CtFieldAccess.class) {
									  @Override
									  public boolean matches(CtFieldAccess<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(5, fieldsAccess.size());
	}

	@Test
	public void testCanVisitArrayAccessAndSubClasses() throws Exception {
		final Factory factory = build(ArrayAccessSample.class);

		final List<CtArrayRead<?>> arraysRead =
				Query.getElements(factory,
								  new AbstractFilter<CtArrayRead<?>>(CtArrayRead.class) {
									  @Override
									  public boolean matches(CtArrayRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, arraysRead.size());

		final List<CtArrayWrite<?>> arraysWrite =
				Query.getElements(factory,
								  new AbstractFilter<CtArrayWrite<?>>(CtArrayWrite.class) {
									  @Override
									  public boolean matches(CtArrayWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(1, arraysWrite.size());

		final List<CtArrayAccess<?, CtExpression<?>>> arraysAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtArrayAccess<?, CtExpression<?>>>(
										  CtArrayAccess.class) {
									  @Override
									  public boolean matches(
											  CtArrayAccess<?, CtExpression<?>> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(3, arraysAccess.size());
	}

	@Test
	public void testStackedAssignments() throws Exception {
		CtType<StackedAssignmentSample> type = buildClass(StackedAssignmentSample.class);
		List<CtAssignment> l = type.getElements(new TypeFilter<>(CtAssignment.class));
		assertEquals(3, l.size());
	}

	@Test
	public void testRHS() throws Exception {
		CtType<RHSSample> type = buildClass(RHSSample.class);
		assertEquals(4,  type.getElements(new TypeFilter<>(CtRHSReceiver.class)).size());
	}

	@Test
	public void testFieldWriteDeclaredInTheSuperclass() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/resources/spoon/test/variable/Tacos.java",
				"-o", "target/spooned/variable",
				"--compliance", "8",
				"--level", "OFF"
		});

		new ContractVerifier(launcher.getFactory().Package().getRootPackage()).checkAssignmentContracts();
	}

	@Test
	public void testVariableAccessInNoClasspath() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/variable");
		launcher.setSourceOutputDirectory("./target/variable/");
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.argouml.uml.ui.behavior.use_cases.PropPanelUseCase");
		final List<CtFieldRead> elements = aClass.getElements(new TypeFilter<>(CtFieldRead.class));

		for (CtFieldRead element : elements) {
			assertNotNull(element.getVariable());
		}

		assertEquals("java.lang.Class mclass = ((java.lang.Class) (org.argouml.model.ModelFacade.USE_CASE))", elements.get(0).getParent().toString());
		assertEquals("new org.argouml.uml.ui.PropPanelButton(this, buttonPanel, _navUpIcon, org.argouml.i18n.Translator.localize(\"UMLMenu\", \"button.go-up\"), \"navigateNamespace\", null)", elements.get(2).getParent().toString());
	}

	@Test
	public void testAccessToStringOnPostIncrement() {
		// contract: a target to a post increment on a variable access write brackets.
		Factory factory = createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement( //
				"class X {" //
						+ "public void foo() {" //
						+ " Integer i = 1;" //
						+ " (i++).toString();" //
						+ " int k = 0;" //
						+ " k++;" //
						+ "}};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];
		assertEquals("(i++).toString()", foo.getBody().getStatement(1).toString());
		assertEquals("k++", foo.getBody().getStatement(3).toString());
	}
}
