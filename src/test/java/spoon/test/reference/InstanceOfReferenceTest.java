package spoon.test.reference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.filter.VariableReferenceFunction;
import spoon.testing.utils.BySimpleName;
import spoon.testing.utils.GitHubIssue;
import spoon.testing.utils.ModelTest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static spoon.test.SpoonTestHelpers.createModelFromString;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

/**
 * Tests that references to pattern variables declared using the <code>instanceof</code> operator can be resolved.
 * Pattern matching for instanceof was introduced in Java 16, cf. <a href=https://openjdk.java.net/jeps/394>JEP 394</a>.
 * Variables declared in pattern matches have <a href="https://openjdk.org/projects/amber/design-notes/patterns/pattern-match-semantics">flow scope semantics</a>.
 */
public class InstanceOfReferenceTest {
	@Test
	public void testVariableDeclaredInIf() {
		String code = """
				class X {
				    String typePattern(Object obj) {
				        boolean someCondition = true;
				        if (someCondition && obj instanceof String s) {
				            return s;
				        }
				        return "";
				    }
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(1);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testVariableDeclaredInWhileLoop() {
		String code = """
				class X {
					public void processShapes(List<Object> shapes) {
						var iter = 0;
						while (iter < shapes.size() && shapes.get(iter) instanceof String shape) {
							iter++;
							System.out.println(shape);
						}
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(3);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testVariableDeclaredInForLoop() {
		String code = """
				class X {
					public void processShapes(List<Object> shapes) {
						for (var iter = 0; iter < shapes.size() && shapes.get(iter) instanceof String shape; iter++) {
							System.out.println(shape);
						}
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(3);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testDeclaredVariableUsedInSameCondition() {
		String code = """
				class X {
					public void processShapes(Object obj) {
						if (obj instanceof String s && s.length() > 5) {
							// NOP
						}
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(0);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testDeclaredVariableUsedInSameCondition2() {
		String code = """
				class X {
					public void hasRightSize(Shape s) throws MyException {
						return s instanceof Circle c && c.getRadius() > 10;
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(0);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testFlowScope() {
		String code = """
				class X {
					public void onlyForStrings(Object o) throws MyException {
						if (!(o instanceof String s))
							throw new MyException();
						// s is in scope
						System.out.println(s);
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(0);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testFlowScope2() {
		String code = """
				class X {
					String s = "abc";

					public void method2(Object o) {
						if (!(o instanceof String s)) {
							System.out.println("not a string");
						} else {
							System.out.println(s); // The local variable is in scope here!
						}
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(0);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testFlowScope3() {
		String code = """
				class X {
					String typePattern(Object obj) {
						if (obj instanceof String s) {
							System.out.println("It's a string");
						} else {
							throw new RuntimeException("It's not a string");
						}
						return s; // We can still access s here!
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariableReference<?> ref = model.getElements(new TypeFilter<>(CtLocalVariableReference.class)).get(0);
		var decl = ref.getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@ModelTest(code = """
		class Test {
			void typePattern(Object o) {
				if (o instanceof String i) {
					System.out.println(i);
				}

				if (!(o instanceof String i)) {
				} else {
					System.out.println(i);
				}

				if (!(o instanceof String i)) {
					throw new IllegalArgumentException();
				}
				System.out.println(i);
			}
		}
		""", complianceLevel = 21)
	public void testFlowScope4(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: references to pattern variables hiding other variables with the same name are resolved correctly
		List<CtLocalVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtLocalVariable.class));
		List<CtLocalVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtLocalVariableReference.class));

		assertThat(variables)
			.hasSize(3);
		assertThat(variables).extracting(CtLocalVariable::getSimpleName).allMatch("i"::equals);

		assertThat(references).hasSize(3);
		assertThat(references).extracting(CtLocalVariableReference::getSimpleName).allMatch("i"::equals);

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(0));
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(variables.get(1));
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(variables.get(2));
	}

	@ModelTest(code = """
		class Test {
			void typePattern(Object o) {
				do {
					if (!(o instanceof Integer i)) {
						continue;
					}

					System.out.println(i);
				} while (!(o instanceof String i));

				System.out.println(i);
			}
		}
		""", complianceLevel = 21)
	public void testDoWhilePattern(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: references to pattern variables introduced in a do-while are resolved correctly
		List<CtLocalVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtLocalVariable.class));
		List<CtLocalVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtLocalVariableReference.class));

		assertThat(variables)
			.hasSize(2);
		assertThat(variables).extracting(CtLocalVariable::getSimpleName).allMatch("i"::equals);

		CtLocalVariable<?> doWhileVariable = variables.get(0);
		CtLocalVariable<?> ifVariable = variables.get(1);

		assertThat(doWhileVariable).getType().isEqualTo(String.class);
		assertThat(ifVariable).getType().isEqualTo(Integer.class);


		assertThat(references).hasSize(2);
		assertThat(references).extracting(CtLocalVariableReference::getSimpleName).allMatch("i"::equals);

		assertThat(references.get(0)).getType().isEqualTo(Integer.class);
		assertThat(references.get(0)).hasExactlyPotentialDeclarations(ifVariable, doWhileVariable);

		assertThat(references.get(1)).getType().isEqualTo(String.class);
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(doWhileVariable);
	}

	@ModelTest(code = """
		class Test {
			void typePattern(Object o) {
				while (!(o instanceof String i)) {
					if (!(o instanceof Integer i)) {
						continue;
					}

					System.out.println(i);
				}

				System.out.println(i);
			}
		}
		""", complianceLevel = 21)
	public void testNegatedWhilePattern(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: a pattern variable is introduced by while (e) S iff it is introduced by e when false
		List<CtLocalVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtLocalVariable.class));
		List<CtLocalVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtLocalVariableReference.class));

		assertThat(variables)
			.hasSize(2);
		assertThat(variables).extracting(CtLocalVariable::getSimpleName).allMatch("i"::equals);

		CtLocalVariable<?> whileVariable = variables.get(0);
		CtLocalVariable<?> ifVariable = variables.get(1);

		assertThat(whileVariable).getType().isEqualTo(String.class);
		assertThat(ifVariable).getType().isEqualTo(Integer.class);


		assertThat(references).hasSize(2);
		assertThat(references).extracting(CtLocalVariableReference::getSimpleName).allMatch("i"::equals);

		assertThat(references.get(0)).getType().isEqualTo(Integer.class);
		assertThat(references.get(0)).hasExactlyPotentialDeclarations(ifVariable, whileVariable);

		assertThat(references.get(1)).getType().isEqualTo(String.class);
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(whileVariable);
	}


	@ModelTest(code = """
		class Test {
			String i = "";
			void typePattern(Object o) {
				label: while (!(o instanceof String i)) {
					if (!(o instanceof Integer i)) {
						break label;
					}

					System.out.println(i);
				}

				System.out.println(i);
			}
		}
		""", complianceLevel = 21)
	public void testNegatedWhilePatternWithBreakLabel(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: a pattern variable is introduced by while (e) S iff it is introduced by e when false
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables).hasSize(4);

		CtVariable<?> fieldVariable = variables.get(0);
		assertThat(fieldVariable).getType().isEqualTo(String.class);
		assertThat(fieldVariable).getSimpleName().isEqualTo("i");

		CtVariable<?> whileVariable = variables.get(2);
		assertThat(whileVariable).getSimpleName().isEqualTo("i");

		CtVariable<?> ifVariable = variables.get(3);
		assertThat(ifVariable).getSimpleName().isEqualTo("i");
		assertThat(ifVariable).getType().isEqualTo(Integer.class);

		assertThat(references).hasSize(6); // System.out are references to fields

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(1));
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(variables.get(1));
		// skip the System.out reference
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(ifVariable, fieldVariable);
		// skip the System.out reference

		// Because of the break target label the while pattern variable is not in scope in the last print statement:
		assertThat(references.get(5)).hasExactlyPotentialDeclarations(fieldVariable);
	}

	@ModelTest(code = """
		class Test {
			String i = "";

			void typePattern(Object o) {
				while (o instanceof String i) {
					System.out.println(i);
				}

				System.out.println(i);
			}
		}
		""", complianceLevel = 21)
	public void testMatchingWhilePattern(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: a pattern variable introduced by e when true is definitely matched at S.
		List<CtLocalVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtLocalVariable.class));
		List<CtLocalVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtLocalVariableReference.class));

		assertThat(variables)
			.hasSize(1);
		assertThat(variables).extracting(CtLocalVariable::getSimpleName).allMatch("i"::equals);

		CtLocalVariable<?> whileVariable = variables.get(0);
		assertThat(whileVariable).getType().isEqualTo(String.class);


		assertThat(references).hasSize(1);
		assertThat(references).extracting(CtLocalVariableReference::getSimpleName).allMatch("i"::equals);

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(whileVariable, ctClass.getField("i"));
	}

	@ModelTest(code = """
		class Test {
			String s1 = "";
			String s2 = "";

			void typePattern(Object a, Object b) {
				if (a instanceof String s1 && b instanceof String s2) {
					System.out.println("s1" + s1 + "s2" + s2);
				}

				if (!(a instanceof String s1) && b instanceof String s2) {
					System.out.println("s1" + s1 + "s2" + s2);
				}

				if (a instanceof String s1 && !(b instanceof String s2)) {
					System.out.println("s1" + s1 + "s2" + s2);
				}

				if (!(a instanceof String s1) && !(b instanceof String s2)) {
					System.out.println("s1" + s1 + "s2" + s2);
				}

				System.out.println(s1 + s2);
			}
		}
		""", complianceLevel = 21)
	public void testBinaryOperatorAnd(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: references to pattern variables introduced by a && b are resolved correctly
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(12);

		// The fields:
		assertThat(variables.get(0)).getSimpleName().isEqualTo("s1");
		assertThat(variables.get(1)).getSimpleName().isEqualTo("s2");

		var s1Field = variables.get(0);
		var s2Field = variables.get(1);

		// The parameters:
		assertThat(variables.get(2)).getSimpleName().isEqualTo("a");
		assertThat(variables.get(3)).getSimpleName().isEqualTo("b");

		var aParameter = variables.get(2);
		var bParameter = variables.get(3);

		// For the first if condition, the first reference will be to the parameters, then to the pattern variables:
		assertThat(references.get(0)).hasExactlyPotentialDeclarations(aParameter);
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(bParameter);

		assertThat(references.get(3)).hasExactlyPotentialDeclarations(variables.get(4), s1Field);
		assertThat(references.get(4)).hasExactlyPotentialDeclarations(variables.get(5), s2Field);

		// The second if condition
		assertThat(references.get(5)).hasExactlyPotentialDeclarations(aParameter);
		assertThat(references.get(6)).hasExactlyPotentialDeclarations(bParameter);

		assertThat(references.get(8)).hasExactlyPotentialDeclarations(s1Field);
		assertThat(references.get(9)).hasExactlyPotentialDeclarations(variables.get(7), s2Field);

		// The third if condition
		assertThat(references.get(10)).hasExactlyPotentialDeclarations(aParameter);
		assertThat(references.get(11)).hasExactlyPotentialDeclarations(bParameter);

		assertThat(references.get(13)).hasExactlyPotentialDeclarations(variables.get(8), s1Field);
		assertThat(references.get(14)).hasExactlyPotentialDeclarations(s2Field);

		// The fourth if condition
		assertThat(references.get(15)).hasExactlyPotentialDeclarations(aParameter);
		assertThat(references.get(16)).hasExactlyPotentialDeclarations(bParameter);

		assertThat(references.get(18)).hasExactlyPotentialDeclarations(s1Field);
		assertThat(references.get(19)).hasExactlyPotentialDeclarations(s2Field);

		// The last print statement (the else) references the negated pattern variables:
		assertThat(references.get(21)).hasExactlyPotentialDeclarations(s1Field);
		assertThat(references.get(22)).hasExactlyPotentialDeclarations(s2Field);
	}


	private static Stream<Arguments> provideTestCasesForNegatedScoping() {
		return Stream.of(
			Arguments.of("o instanceof String s", List.of("s"), List.of()),
			Arguments.of("!(o instanceof String s)", List.of(), List.of("s")),
			Arguments.of("!(!(o instanceof String s))", List.of("s"), List.of()),
			Arguments.of("!(!(!(o instanceof String s)))", List.of(), List.of("s")),
			Arguments.of("o instanceof String s && s.length() > 5", List.of("s"), List.of()),
			Arguments.of("o instanceof String s || number > 5", List.of(), List.of()),
			Arguments.of("!(o instanceof String s) || s.length() > 5", List.of(), List.of("s")),
			Arguments.of("!(o instanceof String s1) || !(obj instanceof String s2)", List.of(), List.of("s1", "s2"))
		);
	}

	@ParameterizedTest
	@MethodSource("provideTestCasesForNegatedScoping")
	public void testNegatedInstanceofScoping(String condition, Collection<String> patternVarsInThen, Collection<String> patternVarsInElse) {
		// The code declares three variables that are both referenced in the then and else branch.
		//
		// If a pattern is defined, this will shadow the field where it is true.
		// The test then checks that the references resolve to either the pattern variable or the local variable.
		String code = """
				class Test {
					String s = "abc";
					String s1 = "def";
					String s2 = "ghi";

					void test(Object o, Object obj, int number) {
						if (%s) {
							System.out.printf("", s, s1, s2);
							throw new IllegalArgumentException();
						}

						System.out.printf("", s, s1, s2);
					}
				}
				""".formatted(condition);

		CtModel model = createModelFromString(code, 21);

		List<? extends CtLocalVariable<?>> patternVariables = model.getElements(new TypeFilter<>(CtTypePattern.class))
			.stream()
			.map(CtTypePattern::getVariable)
			.toList();

		var invocations = model.getElements(new TypeFilter<>(CtInvocation.class)).stream().filter(
			ctInvocation -> ctInvocation.getExecutable().getSimpleName().equals("printf")
		).toList();
		CtInvocation<?> thenPrint = invocations.get(0);
		CtInvocation<?> elsePrint = invocations.get(1);

		for (var fallback : model.getElements(new TypeFilter<>(CtField.class))) {
			CtLocalVariable<?> patternVariable = patternVariables.stream()
				.filter(variable -> variable.getSimpleName().equals(fallback.getSimpleName()))
				.findFirst()
				.orElse(null);

			CtVariableRead<?> thenVariableRead = thenPrint.getArguments()
				.stream()
				.filter(arg -> arg instanceof CtVariableRead<?>)
				.map(arg -> (CtVariableRead<?>) arg)
				.filter(access -> access.getVariable().getSimpleName().equals(fallback.getSimpleName()))
				.findFirst()
				.orElseThrow();

			CtVariableRead<?> elseVariableRead = elsePrint.getArguments()
				.stream()
				.filter(arg -> arg instanceof CtVariableRead<?>)
				.map(arg -> (CtVariableRead<?>) arg)
				.filter(access -> access.getVariable().getSimpleName().equals(fallback.getSimpleName()))
				.findFirst()
				.orElseThrow();

			boolean refersToPatternVarInThen = patternVarsInThen.contains(fallback.getSimpleName());
			boolean refersToPatternVarInElse = patternVarsInElse.contains(fallback.getSimpleName());

			assertThat(thenVariableRead.getVariable().getDeclaration())
				.as("'%s' should reference the %s variable in 'then'", thenVariableRead, refersToPatternVarInThen ? "pattern" : "field")
				.isSameAs(refersToPatternVarInThen ? patternVariable : fallback);

			assertThat(elseVariableRead.getVariable().getDeclaration())
				.as("'%s' should reference the %s variable in 'else'", elseVariableRead, refersToPatternVarInElse ? "pattern" : "field")
				.isSameAs(refersToPatternVarInElse ? patternVariable : fallback);

		}
	}


	@ModelTest(code = """
		class Test {
			String s = "";

			void method(Object obj) {
				System.out.println(obj instanceof String s ? s : s);
				System.out.println(!(obj instanceof String s) ? s : s);
			}
		}
		""", complianceLevel = 21)
	public void testConditionalPatternScope(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: references to pattern variables introduced in a conditional are resolved correctly
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		// System.out
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(variables.get(1)); // (obj instanceof ...
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(variables.get(2), variables.get(0)); // instanceof String s ? s
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(variables.get(0)); // : s
		// System.out
		assertThat(references.get(5)).hasExactlyPotentialDeclarations(variables.get(1)); // !(obj instanceof ...
		assertThat(references.get(6)).hasExactlyPotentialDeclarations(variables.get(0)); // instanceof String s ? s
		assertThat(references.get(7)).hasExactlyPotentialDeclarations(variables.get(3), variables.get(0)); // : s
	}


	@Test
	public void testCorrectScoping() {
		String code = """
			class Example2 {
				Point p;

				void test2(Object o) {
					if (o instanceof Point p) {
						// p refers to the pattern variable
						System.out.println(p);
					} else {
						// p refers to the field
						System.out.println(p);
					}
				}
			}
		""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		var refs = model.getElements(new TypeFilter<>(CtLocalVariableReference.class));
		assertEquals(1, refs.size());
		var decl = refs.get(0).getDeclaration();
		assertNotNull(decl);
		assertEquals(variable, decl);
	}

	@Test
	public void testRecordPatterns() {
		String code = """
			record Point(int x, int y) {}
			record Circle(Point center, int radius) {}

			public class Y {
				public void test() {
					Object obj = new Circle(new Point(10, 20), 5);
					if (obj instanceof Circle(Point (int x, int y), int r)) {
							System.out.println("Object is a Circle at center (" + x + ", " + y + ") with radius " + r);
					}
				}
			}
		""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> varX = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(0).getVariable();
		CtLocalVariable<?> varY = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(1).getVariable();
		CtLocalVariable<?> varR = model.getElements(new TypeFilter<>(CtTypePattern.class)).get(2).getVariable();
		var refs = model.getElements(new TypeFilter<>(CtLocalVariableReference.class));
		assertEquals(4, refs.size()); // includes reference to 'obj'
		var declX = refs.get(1).getDeclaration();
		var declY = refs.get(2).getDeclaration();
		var declR = refs.get(3).getDeclaration();
		assertEquals(varX, declX);
		assertEquals(varY, declY);
		assertEquals(varR, declR);
	}

	@Test
	@GitHubIssue(issueNumber = 6591, fixed = true)
	void testVarUsageInIf() {
		// contract: Variable reference finding should look into if bodies
		String code = """
				class X {
					public void foo() {
						String buffer = "hello";
						if (true) {
						  buffer += " world";
						}
					}
				}
				""";
		CtModel model = createModelFromString(code, 21);
		CtLocalVariable<?> variable = model.getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);

		List<CtVariableReference<?>> references = variable.map(new VariableReferenceFunction()).list();
		assertThat(references).hasSize(1);
	}
}
