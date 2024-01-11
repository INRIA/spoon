package spoon.test.pattern;

import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class RecordPatternTest {

	private static CtModel createModelFromString(String code) {
		System.out.println(code);
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(21);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	private static CtBinaryOperator<?> createFromInstanceOf(String recordDefinitions, String recordPattern) {
		return createModelFromString("""
						class Foo {
							void foo(Object arg) {
								%s
								boolean __ = arg instanceof %s;
							}
						}
			""".formatted(recordDefinitions, recordPattern))
			.getElements(new TypeFilter<>(CtBinaryOperator.class)).iterator().next();
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
}
