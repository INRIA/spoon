/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */

package spoon.test.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
@DisplayName("Switchcase Tests")
public class SwitchCaseTest {

	private static String toSingleLineString(CtElement e) {
		return e.toString().replace("\n", "").replace("\r", "");
	}
	private static CtModel createModelFromString(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(13);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	@Nested
	class Misc {
		@DisplayName("Switch Creation")
		@Test
		public void testIterationStatements() {
			Factory factory = createFactory();
			CtClass<?> clazz = factory
					.Code()
					.createCodeSnippetStatement(
							"" + "class X {" + "public void foo() {" + " int x=0;"
									+	"switch(x) {"
									+ "case 0: x=x+1;break;"
									+ "case 1: x=0;"
									+ "default: x=-1;"
									+ "}"
									+ "}};")
					.compile();

			CtMethod<?> foo = clazz.getMethods().iterator().next();
			CtSwitch<?> sw = foo.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);
			assertEquals(3, sw.getCases().size());

			CtCase<?> c = sw.getCases().get(0);

			assertEquals(0, ((CtLiteral<?>) c.getCaseExpressions().get(0)).getValue());
			assertEquals(2, c.getStatements().size());

			List<CtStatement> l = new ArrayList<>();
			for (CtStatement s : c) {
				l.add(s);
			}
			assertTrue(c.getStatements().equals(l));
		}

		@DisplayName("Switch over String")
		@Test
		public void testSwitchStatementOnAString() throws Exception {
			CtClass<?> clazz = build("spoon.test.model.testclasses", "SwitchStringClass");

			CtMethod<?> method = clazz.getMethods().iterator().next();
			CtSwitch<?> ctSwitch = method.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

			// Checks the selector is a string.
			assertEquals(String.class.getName(), ctSwitch.getSelector().getType().getTypeDeclaration().getQualifiedName());

			// Checks all cases are strings.
			for (CtCase<?> aCase : ctSwitch.getCases()) {
				if (aCase.getCaseExpressions().isEmpty()) {
					// default case
					continue;
				}
				assertEquals(String.class.getName(), aCase.getCaseExpression().getType().getTypeDeclaration().getQualifiedName());
			}
		}
	}


	@Nested
	class SwitchArrows {
		private void checkArrowCase(CtCase<?> toCheck, String code) {
			assertEquals(code, toSingleLineString(toCheck));
			assertEquals(CaseKind.ARROW, toCheck.getCaseKind());
		}

		private void checkCaseExpression(CtCase<?> toCheck, List<String> caseExpressions, int size) {
			assertSame(size, toCheck.getCaseExpressions().size());
			for (int i = 0; i < caseExpressions.size(); i++) {
				assertTrue(toCheck.getCaseExpressions().get(i).toString().equals(caseExpressions.get(i).toString()),
				"Model print should be the same as the model");
			}
		}
		@DisplayName("SwitchExpression with arrow")
		@Test
		public void testJava14Arrow() {
			String arrow = "class A { public void f(int i) { int x; switch(i) { case 1 -> x = 10; case 2 -> x = 20; default -> x = 30; }; } }";
			CtModel model = createModelFromString(arrow);
			CtSwitch<?> switchArrows = model.getElements(new TypeFilter<>(CtSwitch.class)).get(0);

			// contract: we should print arrows like in the original source code
			assertAll(
				() -> checkArrowCase(switchArrows.getCases().get(0), "case 1 ->    x = 10;"),
				() -> checkArrowCase(switchArrows.getCases().get(1), "case 2 ->    x = 20;"),
				() -> checkArrowCase(switchArrows.getCases().get(2), "default ->    x = 30;")
				);
		}
		@DisplayName("SwitchExpression inside If")
		@Test
		public void testJava12SwitchExpressionInIf() {
			// contract: just another test for switch expressions
			String code = "class A { public void f(int i) { if (switch (i) { case 1, 2 -> true; default -> false; }) {} }; } }";
			CtModel model = createModelFromString(code);
			CtIf ctIf = model.getElements(new TypeFilter<>(CtIf.class)).get(0);
			assertEquals("if (switch (i) {    case 1, 2 ->        true;    default ->        false;}) {}", toSingleLineString(ctIf));
		}
		@DisplayName("SwitchExpression with arrow")
		@Test
		public void testJava12SwitchExpression() {
			// contract: we should handle switch expressions properly
			String code = "class A { public void f(int i) { int x = switch(i) { case 1 -> 10; case 2 -> 20; default -> 30; }; } }";
			CtModel model = createModelFromString(code);
			CtLocalVariable<?> localVariable = model.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
			assertTrue(localVariable.getAssignment() instanceof CtSwitchExpression, "Assignment by switch must be detected");
			assertEquals("int x = switch (i) {    case 1 ->        10;    case 2 ->        20;    default ->        30;}", toSingleLineString(localVariable));
		}
		@DisplayName("SwitchExpression with case blockStatement")
		@Test
		public void testJava14ArrowWithBlock() {
			String arrowWithBlock = "class B { public void f(int i) { int x; switch(i) { case 1 -> { x = 10; break; } case 2 -> x = 20; default -> x = 30; }; } }";
			CtModel model = createModelFromString(arrowWithBlock);
			CtSwitch<?> switchBreak = model.getElements(new TypeFilter<>(CtSwitch.class)).get(0);
			CtBreak ctBreak = switchBreak.getElements(new TypeFilter<>(CtBreak.class)).get(0);

			// contract: explicit break (inside the block) should be printed
			assertFalse(ctBreak.isImplicit());
			assertEquals("break", ctBreak.toString());
		}
		@DisplayName("SwitchExpression with multi case")
		@Test
		public void testJava14MultipleCaseExpressions() {
			// contract: we should handle multiple case expressions correctly
			String code = "class A { public void f(int i) { int x; switch(i) { case 1,2,3 -> x = 10; case 4 -> x = 20; default -> x = 30; }; } }";
			CtModel model = createModelFromString(code);
			CtSwitch<?> switchBreak = model.getElements(new TypeFilter<>(CtSwitch.class)).get(0);

			CtCase<?> caseA1 = switchBreak.getCases().get(0);
			CtCase<?> caseA2 = switchBreak.getCases().get(1);
			CtCase<?> caseA3 = switchBreak.getCases().get(2);

			assertAll(
					() -> checkCaseExpression(caseA1, Arrays.asList("1", "2", "3"), 3),
					() -> checkCaseExpression(caseA2, Arrays.asList("4"), 1),
					() -> checkCaseExpression(caseA3, Collections.emptyList(), 0),
					() -> assertEquals("default ->    x = 30;", toSingleLineString(caseA3))
			);
		}
	}
	@Nested
	class SwitchColons {
		private void checkColonCase(CtCase<?> toCheck, String code) {
			assertEquals(code, toSingleLineString(toCheck));
			assertEquals(CaseKind.COLON, toCheck.getCaseKind());
		}
		@DisplayName("Switch with \":\" caseKind")
		@Test
		public void testSwitchColons() {
			String colon = "class C { public void f(int i) { int x; switch(i) { case 1: x = 10; x = 1; break; case 2: x = 20; break; default: x = 30; break; }; } }";
			CtModel model = createModelFromString(colon);
			CtSwitch<?> switchColons = model.getElements(new TypeFilter<>(CtSwitch.class)).get(0);
			assertAll(
			() -> checkColonCase(switchColons.getCases().get(0), "case 1 :    x = 10;    x = 1;    break;"),
			() -> checkColonCase(switchColons.getCases().get(1), "case 2 :    x = 20;    break;"),
			() -> checkColonCase(switchColons.getCases().get(2), "default :    x = 30;    break;")
			);
		}

	}

	@Nested
	class Yield {

		public void checkYield(CtYieldStatement yield, String toString, boolean isImplicit) {
			assertNull(yield.getLabel(), "yields must not have labels");
			assertTrue(yield.isImplicit() == isImplicit, "implicit flag must be set correct");
			assertTrue(yield.toString().equals(toString), "toString for yields must respect implicit");
		}
		@DisplayName("SwitchExpression with case yield")
		@Test
		public void testJava14yield() {
			// contract: we should properly handle explicit yield with expression inside switch expression
			String code = "class A { public void f(String s) { int result = switch (s) { case \"Foo\" ->  1;  default ->{ int a = 3; yield a; }}; }";
			CtModel model = createModelFromString(code);

			List<CtYieldStatement> yields = model.getElements(new TypeFilter<>(CtYieldStatement.class));
			// the yield is implicit and added by the compiler.
			assertAll("after building the model lets check the yields",
					() -> checkYield(yields.get(0), "1", true),
					() -> checkYield(yields.get(1), "yield a", false));
		}
	}

}
