package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.testing.assertions.SpoonAssertions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

public class RecordPatternTest {

	private static CtModel createModelFromString(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(22);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	private static CtBinaryOperator<?> createFromInstanceOf(String recordDefinitions, String recordPattern) {
		return createModelFromString(
			"""
				class Foo {
					void foo(Object arg) {
						%s
						boolean __ = arg instanceof %s;
					}
				}
				""".formatted(recordDefinitions, recordPattern))
			.getElements(new TypeFilter<>(CtBinaryOperator.class)).iterator().next();
	}
	private static CtSwitch<?> createFromSwitch(String recordDefinitions, String... cases) {
		for (int i = 0; i < cases.length; i++) {
			cases[i] = "case " + cases[i] + " -> {}";
		}
		return createModelFromString("""
			class Foo {
				void foo(Object arg) {
					%s
					switch (arg) {
						%s
						default -> {}
					}
				}
			}
			""".formatted(recordDefinitions, String.join("\n", cases)))
			.getElements(new TypeFilter<>(CtSwitch.class)).iterator().next();
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
				int value
				int i
				int a, int b
				java.lang.String s, int cc
		""")
	void testPrintSimpleInstanceOfRecordPattern(String typePatterList) {
		// contract: printing a record pattern works
		String recordPattern = "Simple(" + typePatterList + ")";
		String recordDefinition = "record " + recordPattern + " {}";
		CtBinaryOperator<?> instanceOf = createFromInstanceOf(recordDefinition, recordPattern);
		assertEquals(recordPattern, instanceOf.getRightHandOperand().toString());
	}

	@Test
	void testNestedRecordPatterns1() {
		// contract: nesting record patterns works
		CtBinaryOperator<?> instanceOf = createFromInstanceOf("record Box<T>(T v) {}", "Box(Box(Box(Integer i)))");
		CtRecordPattern recordPattern = assertInstanceOf(CtRecordPattern.class, instanceOf.getRightHandOperand());
		assertEquals(1, recordPattern.getPatternList().size());
		CtRecordPattern inner1 = assertInstanceOf(CtRecordPattern.class, recordPattern.getPatternList().get(0));
		assertEquals(1, inner1.getPatternList().size());
		CtRecordPattern inner2 = assertInstanceOf(CtRecordPattern.class, inner1.getPatternList().get(0));
		assertEquals(1, inner2.getPatternList().size());
		assertInstanceOf(CtTypePattern.class, inner2.getPatternList().get(0));
	}

	@Test
	void testNestedRecordPatterns2() {
		// contract: nesting record patterns works
		CtBinaryOperator<?> instanceOf = createFromInstanceOf(
			"record Box<A>(A a) {} record Box2<B extends Box<?>>(B box) {}",
			"Box2(Box(Box(Integer i)))"
		);
		CtRecordPattern recordPattern = assertInstanceOf(CtRecordPattern.class, instanceOf.getRightHandOperand());
		assertEquals(1, recordPattern.getPatternList().size());
		CtRecordPattern inner1 = assertInstanceOf(CtRecordPattern.class, recordPattern.getPatternList().get(0));
		assertEquals(1, inner1.getPatternList().size());
		CtRecordPattern inner2 = assertInstanceOf(CtRecordPattern.class, inner1.getPatternList().get(0));
		assertEquals(1, inner2.getPatternList().size());
		assertInstanceOf(CtTypePattern.class, inner2.getPatternList().get(0));
	}

	@Test
	void testNestedRecordPatternsTree() {
		// contract: nesting record patterns works
		CtBinaryOperator<?> instanceOf = createFromInstanceOf(
			"""
				interface Node<T> {}
				record Leaf<T>(T value) implements Node<T> {}
				record Inner<T>(Node<T> left, Node<T> right) implements Node<T> {}
				""",
			"""
				Inner(
					Inner(
						Leaf(String a),
						Leaf(String b)
					),
					Inner(
						Leaf(String c),
						Inner(
							Leaf(String d),
							Leaf(String e)
						)
					)
				)
				"""
		);
		Node pattern = new Inner(
			new Inner(
				new Leaf("a"),
				new Leaf("b")
			),
			new Inner(
				new Leaf("c"),
				new Inner(
					new Leaf("d"),
					new Leaf("e")
				)
			)
		);
		pattern.assertMatches(assertInstanceOf(CtPattern.class, instanceOf.getRightHandOperand()));
	}

	@ParameterizedTest
	@ValueSource(strings = {"var", "final var"})
	void testTypePatternWithVar(String var) {
		// contract: type patterns in record patterns can use type inference
		String pattern = "Simple(" + var + " value)";
		CtBinaryOperator<?> instanceOf = createFromInstanceOf("record Simple(String s) {}", pattern);
		CtRecordPattern recordPattern = assertInstanceOf(CtRecordPattern.class, instanceOf.getRightHandOperand());
		List<CtPattern> patternList = recordPattern.getPatternList();
		assertEquals(1, patternList.size());
		CtTypePattern ctTypePattern = assertInstanceOf(CtTypePattern.class, patternList.get(0));
		CtLocalVariable<?> variable = ctTypePattern.getVariable();
		assertTrue(variable.isInferred());
		assertEquals("String", variable.getType().getSimpleName());
		assertEquals(pattern.contains("final"), variable.isFinal());
	}

	@Test
	void testUnnamedPatternInRecordPattern() {
		// contract: an unnamed pattern has the proper inferred type and is printed correctly
		CtSwitch<?> ctSwitch = createFromSwitch("var i = 1; record Int(int i) {}", "Int(_)");
		List<CtRecordPattern> recordPatterns = ctSwitch.getElements(new TypeFilter<>(CtRecordPattern.class));
		assertThat(recordPatterns).hasSize(1);
		CtRecordPattern pattern = recordPatterns.get(0);
		assertThat(pattern).getPatternList().hasSize(1);
		CtPattern component = pattern.getPatternList().get(0);
		assertThat(component).isInstanceOf(CtUnnamedPattern.class);
		assertThat((CtUnnamedPattern) component)
			.getType()
			.getSimpleName()
			.isNotNull()
			.isEqualTo("int");
		assertThat(pattern).asString().contains("Int(_)");
	}

	@Test
	void testRecordPatternInSwitch() {
		CtSwitch<?> ctSwitch = createFromSwitch(
			"""
				interface Node {}
				record Leaf(Object value) implements Node {}
				record Inner(Node left, Node right) implements Node {}
				""",
			"Leaf(String s) when s.length() > 10",
			"Leaf(var s)",
			"Inner(Leaf leaf, var right)",
			"Inner(var left, Node right)"
		);
		List<CtCase<?>> cases = (List<CtCase<?>>) (List) ctSwitch.getCases();
		assertEquals(cases.size(), 5); // includes default
		CtCasePattern c0 = assertInstanceOf(CtCasePattern.class, cases.get(0).getCaseExpression());
		new Leaf("s").assertMatches(c0.getPattern());
		assertNotNull(cases.get(0).getGuard());
		assertThat(cases.get(0).toString()).contains(" when "); // guard must be printed

		CtCasePattern c1 = assertInstanceOf(CtCasePattern.class, cases.get(1).getCaseExpression());
		new Leaf("s").assertMatches(c1.getPattern());
		assertNull(cases.get(1).getGuard());

		CtCasePattern c2 = assertInstanceOf(CtCasePattern.class, cases.get(2).getCaseExpression());
		new Inner(new SimpleLeaf("leaf"), new SimpleLeaf("right")).assertMatches(c2.getPattern());
		assertNull(cases.get(2).getGuard());

		CtCasePattern c3 = assertInstanceOf(CtCasePattern.class, cases.get(3).getCaseExpression());
		new Inner(new SimpleLeaf("left"), new SimpleLeaf("right")).assertMatches(c3.getPattern());
		assertNull(cases.get(3).getGuard());
	}

	interface Node {
		void assertMatches(CtPattern pattern);
	}
	record Inner(Node left, Node right) implements Node {
		@Override
		public void assertMatches(CtPattern pattern) {
			CtRecordPattern recordPattern = assertInstanceOf(CtRecordPattern.class, pattern);
			List<CtPattern> patternList = recordPattern.getPatternList();
			assertEquals(2, patternList.size());
			left().assertMatches(patternList.get(0));
			right().assertMatches(patternList.get(1));

		}
	}
	record Leaf(String name) implements Node {
		@Override
		public void assertMatches(CtPattern pattern) {
			CtRecordPattern recordPattern = assertInstanceOf(CtRecordPattern.class, pattern);
			List<CtPattern> patternList = recordPattern.getPatternList();
			assertEquals(1, patternList.size());
			CtTypePattern ctTypePattern = assertInstanceOf(CtTypePattern.class, patternList.get(0));
			String simpleName = ctTypePattern.getVariable().getSimpleName();
			assertEquals(name(), simpleName);
		}
	}

	record SimpleLeaf(String name) implements Node {

		@Override
		public void assertMatches(CtPattern pattern) {
			CtTypePattern ctTypePattern = assertInstanceOf(CtTypePattern.class, pattern);
			String simpleName = ctTypePattern.getVariable().getSimpleName();
			assertEquals(name(), simpleName);
		}
	}
}
