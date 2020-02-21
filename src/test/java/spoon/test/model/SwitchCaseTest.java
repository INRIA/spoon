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
package spoon.test.model;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class SwitchCaseTest {

	private static String toSingleLineString(CtElement e) {
		return e.toString().replace("\n", "").replace("\r", "");
	}

	@Test
	public void testJava12ArrowCase() {
		String arrow = "class A { public void f(int i) { int x; switch(i) { case 1 -> x = 10; case 2 -> x = 20; default -> x = 30; }; } }";
		String arrowWithBlock = "class B { public void f(int i) { int x; switch(i) { case 1 -> { x = 10; break; } case 2 -> x = 20; default -> x = 30; }; } }";
		String colon = "class C { public void f(int i) { int x; switch(i) { case 1: x = 10; x = 1; break; case 2: x = 20; break; default: x = 30; break; }; } }";

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(12);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new VirtualFile(arrow));
		launcher.addInputResource(new VirtualFile(arrowWithBlock));
		launcher.addInputResource(new VirtualFile(colon));
		CtModel model = launcher.buildModel();

		CtType<?> classA = model.getAllTypes().stream().filter(c -> c.getSimpleName().equals("A")).findFirst().get();
		CtCase caseA1 = classA.getElements(new TypeFilter<>(CtCase.class)).get(0);
		CtCase caseA2 = classA.getElements(new TypeFilter<>(CtCase.class)).get(1);
		CtCase caseA3 = classA.getElements(new TypeFilter<>(CtCase.class)).get(2);

		// contract: we should print arrows like in the original source code
		assertEquals("case 1 ->    x = 10;", toSingleLineString(caseA1));
		assertEquals("case 2 ->    x = 20;", toSingleLineString(caseA2));
		assertEquals("default ->    x = 30;", toSingleLineString(caseA3));
		assertEquals(caseA1.getCaseKind(), CaseKind.ARROW);
		assertEquals(caseA2.getCaseKind(), CaseKind.ARROW);
		assertEquals(caseA3.getCaseKind(), CaseKind.ARROW);

		// contract: we should have implicit breaks (with expressions) for arrows
		assertTrue(caseA1.getElements(new TypeFilter<>(CtBreak.class)).get(0).isImplicit());
		assertEquals("x = 10", caseA1.getElements(new TypeFilter<>(CtBreak.class)).get(0).getExpression().toString());
		assertTrue(caseA2.getElements(new TypeFilter<>(CtBreak.class)).get(0).isImplicit());
		assertEquals("x = 20", caseA2.getElements(new TypeFilter<>(CtBreak.class)).get(0).getExpression().toString());
		assertTrue(caseA3.getElements(new TypeFilter<>(CtBreak.class)).get(0).isImplicit());
		assertEquals("x = 30", caseA3.getElements(new TypeFilter<>(CtBreak.class)).get(0).getExpression().toString());

		CtType<?> classB = model.getAllTypes().stream().filter(c -> c.getSimpleName().equals("B")).findFirst().get();
		CtCase caseB1 = classB.getElements(new TypeFilter<>(CtCase.class)).get(0);

		// contract: explicit break (inside the block) should be printed
		assertFalse(caseB1.getElements(new TypeFilter<>(CtBreak.class)).get(0).isImplicit());
		assertEquals("break", caseB1.getElements(new TypeFilter<>(CtBreak.class)).get(0).toString());

		CtType<?> classC = model.getAllTypes().stream().filter(c -> c.getSimpleName().equals("C")).findFirst().get();
		CtCase caseC1 = classC.getElements(new TypeFilter<>(CtCase.class)).get(0);
		CtCase caseC2 = classC.getElements(new TypeFilter<>(CtCase.class)).get(1);
		CtCase caseC3 = classC.getElements(new TypeFilter<>(CtCase.class)).get(2);

		// contract: old switch should work as usual
		assertEquals("case 1 :    x = 10;    x = 1;    break;", toSingleLineString(caseC1));
		assertEquals("case 2 :    x = 20;    break;", toSingleLineString(caseC2));
		assertEquals("default :    x = 30;    break;", toSingleLineString(caseC3));
		assertEquals(caseC1.getCaseKind(), CaseKind.COLON);
		assertEquals(caseC2.getCaseKind(), CaseKind.COLON);
		assertEquals(caseC3.getCaseKind(), CaseKind.COLON);
	}

	@Test
	public void testJava12MultipleCaseExpressions() {
		// contract: we should handle multiple case expressions correctly
		String arrow = "class A { public void f(int i) { int x; switch(i) { case 1,2,3 -> x = 10; case 4 -> x = 20; default -> x = 30; }; } }";
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(12);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new VirtualFile(arrow));
		CtModel model = launcher.buildModel();
		CtType<?> classA = model.getAllTypes().stream().filter(c -> c.getSimpleName().equals("A")).findFirst().get();
		CtCase caseA1 = classA.getElements(new TypeFilter<>(CtCase.class)).get(0);
		CtCase caseA2 = classA.getElements(new TypeFilter<>(CtCase.class)).get(1);
		CtCase caseA3 = classA.getElements(new TypeFilter<>(CtCase.class)).get(2);

		assertEquals(3, caseA1.getCaseExpressions().size());
		assertEquals("1", caseA1.getCaseExpressions().get(0).toString());
		assertEquals("2", caseA1.getCaseExpressions().get(1).toString());
		assertEquals("3", caseA1.getCaseExpressions().get(2).toString());

		assertEquals(1, caseA2.getCaseExpressions().size());
		assertEquals("4", caseA2.getCaseExpressions().get(0).toString());

		assertEquals(0, caseA3.getCaseExpressions().size());
		assertEquals("default ->    x = 30;", toSingleLineString(caseA3));
	}

	@Test
	public void testIterationStatements() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;" +
								"switch(x) {"
								+ "case 0: x=x+1;break;"
								+ "case 1: x=0;"
								+ "default: x=-1;"
								+ "}"
								+ "}};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtSwitch<?> sw = foo.getElements(
				new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

		assertEquals(3, sw.getCases().size());

		CtCase<?> c = sw.getCases().get(0);

		assertEquals(0, ((CtLiteral<?>) c.getCaseExpression()).getValue());
		assertEquals(2, c.getStatements().size());

		List<CtStatement> l = new ArrayList<>();

		// this compiles (thanks to the new CtCase extends CtStatementList)
		for (CtStatement s : c) {
			l.add(s);
		}
		assertTrue(c.getStatements().equals(l));
	}

	@Test
	public void testSwitchStatementOnAString() throws Exception {
		CtClass<?> clazz = build("spoon.test.model.testclasses", "SwitchStringClass");

		CtMethod<?> method = (CtMethod<?>) clazz.getMethods().toArray()[0];
		CtSwitch<?> ctSwitch = method
				.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class))
				.get(0);

		// Checks the selector is a string.
		assertSame(String.class,
				ctSwitch.getSelector().getType().getActualClass());

		// Checks all cases are strings.
		for (CtCase<?> aCase : ctSwitch.getCases()) {
			if (aCase.getCaseExpression() == null) {
				// default case
				continue;
			}
			assertSame(String.class,
					aCase.getCaseExpression().getType().getActualClass());
		}
	}
}
