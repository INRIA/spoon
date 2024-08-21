/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */

package spoon.test.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.testing.utils.GitHubIssue;

@DisplayName("Switchcase Tests")
public class SwitchCaseTest {

	private static String toSingleLineString(CtElement e) {
		return e.toString().replace("\n", "").replace("\r", "");
	}
	private static CtModel createModelFromString(String code) {
		return createModelFromString(code, 21);
	}
	private static CtModel createModelFromString(String code, int complianceLevel) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(complianceLevel);
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

		@DisplayName("Switch over Enum with qualified names since Java 21")
		@Test
		public void testSwitchStatementOnAnEnum() {
			CtModel model = createModelFromString(
				"""
					import java.nio.file.StandardCopyOption;
					class C {
						int m(StandardCopyOption option) {
							return switch (option) {
								case ATOMIC_MOVE -> 1;
								case StandardCopyOption.COPY_ATTRIBUTES -> 2;
								case java.nio.file.StandardCopyOption.REPLACE_EXISTING -> 3;
							};
						}
					}
					"""
			);

			CtSwitchExpression<?, ?> ctSwitch = model.getElements(new TypeFilter<CtSwitchExpression<?, ?>>(CtSwitchExpression.class)).get(0);

			// Checks the selector is the enum.
			assertEquals(StandardCopyOption.class.getName(), ctSwitch.getSelector().getType().getTypeDeclaration().getQualifiedName());

			// Checks all cases are the matching enum constants.
			var cases = ctSwitch.getCases();
			List<String> expectedPrinterOutputForceFQP = List.of(
				"case java.nio.file.StandardCopyOption.ATOMIC_MOVE ->",
				"case java.nio.file.StandardCopyOption.COPY_ATTRIBUTES ->",
				"case java.nio.file.StandardCopyOption.REPLACE_EXISTING ->"
			);
			List<String> expectedPrinterOutputForcePretty = List.of(
				"case ATOMIC_MOVE ->",
				"case StandardCopyOption.COPY_ATTRIBUTES ->",
				"case StandardCopyOption.REPLACE_EXISTING ->"
			);
			for (int i = 0; i < cases.size(); i++) {
				CtCase<?> aCase = cases.get(i);
				// make sure all are qualified when using toString (printer with ForceFullyQualifiedProcessor)
				Assertions.assertThat(aCase.toString()).contains(expectedPrinterOutputForceFQP.get(i));
				// make sure the auto-import strips the package name but not the class name if not implicit
				Assertions.assertThat(aCase.prettyprint()).contains(expectedPrinterOutputForcePretty.get(i));
				assertEquals(StandardCopyOption.class.getName(), aCase.getCaseExpression().getType().getTypeDeclaration().getQualifiedName());
			}
		}

		@DisplayName("Print switch on enum pre Java 21")
		@Test
		public void testSwitchStatementOnAnEnumPrintPre21() {
			CtModel model = createModelFromString(
				"""
					import java.nio.file.StandardCopyOption;
					class C {
						int m(StandardCopyOption option) {
							return switch (option) {
								case ATOMIC_MOVE -> 1;
								case COPY_ATTRIBUTES -> 2;
								case REPLACE_EXISTING -> 3;
							};
						}
					}
					""",
				20
			);
			CtSwitchExpression<?, ?> ctSwitch = model.getElements(new TypeFilter<CtSwitchExpression<?, ?>>(CtSwitchExpression.class)).get(0);
			Assertions.assertThat(ctSwitch.toString()).contains(
				"case ATOMIC_MOVE ->",
				"case COPY_ATTRIBUTES ->",
				"case REPLACE_EXISTING ->"
			);
		}

		@DisplayName("Parent is set")
		@Test
		public void testParentInCaseExpressions() {
				// contract: for all expressions in a case, the parent is set.
				Launcher launcher = new Launcher();
				launcher.getEnvironment().setComplianceLevel(14);
				launcher.addInputResource(new VirtualFile("class A { { int x = switch(1) { case 1, 3 -> 0; default -> 1}; } }"));
				launcher.buildModel();
				List<CtLiteral<?>> caseStatement = launcher.getModel().getElements(new TypeFilter<>(CtLiteral.class));
				assertTrue(caseStatement.stream().allMatch(CtLiteral::isParentInitialized));
		}

		@GitHubIssue(issueNumber = 2743, fixed = true)
		@Test
		void testNoSyntheticBreak() {
			// contract: no synthetic break is introduced in the model
			CtModel model = createModelFromString("""
					class Main {
						void main() {
							switch(0) {
								case 1 -> {
								}
							}
						}
					}
					"""
			);
			Assertions.assertThat(model.getElements(new TypeFilter<>(CtBreak.class))).isEmpty();
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

		@Test
		@GitHubIssue(issueNumber = 4696, fixed = true)
		void testVariableScopeInSwitch() {
			// contract: different cases do not introduce different scopes in colon-switches
			String code = "public class A {\n" +
					"  public void function(int value) {\n" +
					"    switch (value) {\n" +
					"      case 1: { String test; }\n" + // decoy variable declaration, should not be found
					"        String test;\n" +
					"        test = \"first\";\n" +
					"        break;\n" +
					"      default:\n" +
					"        test = \"not first\";\n" +
					"    }\n" +
					"  }\n" +
					"}";
			CtModel model = createModelFromString(code);
			List<CtVariableAccess<?>> accesses = model.<CtVariableAccess<?>>getElements(new TypeFilter<>(CtVariableAccess.class))
					.stream()
					.filter(a -> a.getVariable().getSimpleName().equals("test"))
					.collect(Collectors.<CtVariableAccess<?>>toList());
			assertEquals(2, accesses.size());

			// access their declarations
			CtVariable<?> caseOneVariableDeclaration = accesses.get(0).getVariable().getDeclaration();
			CtVariable<?> defaultVariableDeclaration = accesses.get(1).getVariable().getDeclaration();

			// expect the declarations to be equal (writing to the same variable)
			assertEquals(caseOneVariableDeclaration, defaultVariableDeclaration);
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

	@Nested
	class InsertCaseInSwitch {
		@DisplayName("cases should be inserted at the correct position")
		@Test
		public void test_addCaseAt_addsCaseAtSpecifiedPositionInSwitch() {
			// contract: case should be added at the specified position in `CtSwitch`
			Factory factory = new Launcher().getFactory();

			CtSwitch<Integer> switchBlock = factory.createSwitch();
			CtExpression<Integer> switchSelector = factory.createCodeSnippetExpression("x");
			switchBlock.setSelector(switchSelector);

			CtBreak ctBreak = factory.createBreak();

			CtCase<Integer> first = factory.createCase();
			CtExpression<Integer> firstExpression = factory.createCodeSnippetExpression("1");
			first.addCaseExpression(firstExpression);
			first.setCaseKind(CaseKind.COLON);
			first.addStatement(ctBreak);

			CtCase<Integer> second = factory.createCase();
			CtExpression<Integer> secondExpression = factory.createCodeSnippetExpression("2");
			second.addCaseExpression(secondExpression);
			second.setCaseKind(CaseKind.COLON);
			second.addStatement(ctBreak);

			CtCase<Integer> third = factory.createCase();
			third.setCaseKind(CaseKind.COLON);
			third.addStatement(ctBreak);

			switchBlock.addCaseAt(0, third);
			switchBlock.addCaseAt(0,first);
			switchBlock.addCaseAt(1, second);

			assertEquals(Arrays.asList(first, second, third), switchBlock.getCases());
		}

		@DisplayName("should throw IndexOutOfBounds exception")
		@Test
		public void test_addCaseAt_throwsIndexOutOfBoundsException_whenPositionIsOutOfBounds() {
			// contract: `addCaseAt` should throw an out of bounds exception when the the specified position is out of
			// bounds of the case collection
			Factory factory = new Launcher().getFactory();

			CtSwitch<Integer> switchBlock = factory.createSwitch();
			CtExpression<Integer> switchSelector = factory.createCodeSnippetExpression("x");
			switchBlock.setSelector(switchSelector);

			CtCase<Integer> onlyCase = factory.createCase();
			CtBreak caseStatement = factory.createBreak();
			onlyCase.setCaseKind(CaseKind.COLON);
			onlyCase.addStatement(caseStatement);

			assertThrows(IndexOutOfBoundsException.class, () -> switchBlock.addCaseAt(5, onlyCase));
		}
	}

	@Nested
	class InsertCaseInSwitchExpression {
		@DisplayName("cases should be inserted at the correct position")
		@Test
		public void test_addCaseAt_addsCaseAtSpecifiedPositionInSwitchExpression() {
			// contract: case should be added at the specified position in `CtSwitchExpression`
			Factory factory = new Launcher().getFactory();

			CtSwitchExpression<Integer, Integer> switchExpression = factory.createSwitchExpression();
			CtExpression<Integer> switchSelector = factory.createCodeSnippetExpression("x");
			switchExpression.setSelector(switchSelector);

			CtCase<Integer> first = factory.createCase();
			CtExpression<Integer> firstExpression = factory.createCodeSnippetExpression("1");
			CtStatement firstStatement = factory.createCodeSnippetStatement("1");
			first.addCaseExpression(firstExpression);
			first.setCaseKind(CaseKind.ARROW);
			first.addStatement(firstStatement);

			CtCase<Integer> second = factory.createCase();
			CtExpression<Integer> secondExpression = factory.createCodeSnippetExpression("2");
			CtStatement secondStatement = factory.createCodeSnippetStatement("2");
			second.addCaseExpression(secondExpression);
			second.setCaseKind(CaseKind.ARROW);
			second.addStatement(secondStatement);

			CtCase<Integer> third = factory.createCase();
			CtStatement thirdStatement = factory.createCodeSnippetStatement("3");
			third.setCaseKind(CaseKind.ARROW);
			third.addStatement(thirdStatement);

			switchExpression.addCaseAt(0, third);
			switchExpression.addCaseAt(0,first);
			switchExpression.addCaseAt(1, second);

			assertEquals(Arrays.asList(first, second, third), switchExpression.getCases());
		}

		@DisplayName("should throw IndexOutOfBounds exception")
		@Test
		public void test_addCaseAt_throwsIndexOutOfBoundsException_whenPositionIsOutOfBounds() {
			// contract: `addCaseAt` should throw an out of bounds exception when the the specified position is out of
			// bounds of the case collection
			Factory factory = new Launcher().getFactory();

			CtSwitchExpression<Integer, Integer> switchExpression = factory.createSwitchExpression();
			CtExpression<Integer> switchSelector = factory.createCodeSnippetExpression("x");
			switchExpression.setSelector(switchSelector);

			CtCase<Integer> onlyCase = factory.createCase();
			CtStatement caseStatement = factory.createCodeSnippetStatement("5");
			onlyCase.setCaseKind(CaseKind.ARROW);
			onlyCase.addStatement(caseStatement);

			assertThrows(IndexOutOfBoundsException.class, () -> switchExpression.addCaseAt(3, onlyCase));
		}
	}

	@Nested
	class CaseExpressionInSwitchCasesBeforeJava12 {
		@Test
		public void test_getCaseExpression_shouldReturnTheFirstCaseExpression() {
			// contract: `getCaseExpression` should return the first element in the list of case expressions stored in
			// each instance of `CtCase`
			Factory factory = new Launcher().getFactory();

			CtCase<Integer> ctCase = factory.createCase();
			CtExpression<Integer> first = factory.createLiteral(1);
			CtExpression<Integer> second = factory.createLiteral(2);
			CtExpression<Integer> third = factory.createLiteral(3);

			ctCase.setCaseExpressions(Arrays.asList(first, second, third));

			assertEquals(first, ctCase.getCaseExpression());
		}

		@Test
		public void test_setCaseExpression_removeExistingCaseExpressionsAndInsertTheSpecified() {
			// contract: `setCaseExpression` should clear the list of case expressions and insert the specified case
			// expression
			Factory factory = new Launcher().getFactory();

			CtCase<Integer> ctCase = factory.createCase();
			CtExpression<Integer> first = factory.createLiteral(1);
			CtExpression<Integer> second = factory.createLiteral(2);
			CtExpression<Integer> third = factory.createLiteral(3);
			CtExpression<Integer> fourth = factory.createLiteral(4);
			ctCase.setCaseExpressions(Arrays.asList(first, second, third));

			ctCase.setCaseExpression(fourth);

			assertEquals(1, ctCase.getCaseExpressions().size());
			assertEquals(fourth, ctCase.getCaseExpression());
		}

		@Test
		public void test_setCaseExpression_removeAllCaseExpressions() {
			// contract: `setCaseExpression` should clear the list of case expressions when `null` is passed as
			// argument
			String code = "class A { public void f(int i) { switch(i) { case 1,2,3: break; } } }";
			CtModel model = createModelFromString(code);
			CtCase<?> ctCase = model.getElements(new TypeFilter<>(CtCase.class)).get(0);

			ctCase.setCaseExpression(null);

			assertThat(ctCase.getCaseExpressions().size(), equalTo(0));
		}
	}
}
