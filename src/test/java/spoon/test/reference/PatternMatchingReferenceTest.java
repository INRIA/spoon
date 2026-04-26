package spoon.test.reference;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.BySimpleName;
import spoon.testing.utils.ModelTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

public class PatternMatchingReferenceTest {
	@ModelTest(code = """
		interface Shape {}

		record Circle(double radius) implements Shape {}

		class X {
			public void processShape(Shape shape) {
				switch (shape) {
					case Circle c -> System.out.println("This is a circle with radius: " + c.getRadius());
					default -> {}
				}
			}
		}
		""", complianceLevel = 21)
	public void testCasePatternReferenceArrow(@BySimpleName("X") CtClass<?> ctClass) {
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables).hasSize(2);
		assertThat(references).hasSize(3);

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(0));
		// ignore the reference to System.out
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(variables.get(1));
	}

	@ModelTest(code = """
		interface Shape {}

		record Circle(double radius) implements Shape {}

		class X {
			public void processShape(Shape shape) {
				switch (shape) {
					case Circle c:
						System.out.println("This is a circle with radius: " + c.getRadius());
						break;
					default:
						break;
				}
			}
		}
		""", complianceLevel = 21)
	public void testCasePatternReferenceColon(@BySimpleName("X") CtClass<?> ctClass) {
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables).hasSize(2);
		assertThat(references).hasSize(3);

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(0));
		// ignore the reference to System.out
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(variables.get(1));
	}

	@ModelTest(code = """
		class Test {
			String i = "";
			void method(int integer) {
				switch (integer) {
					case 0:
						System.out.println(i);
						break;
					case 1:
						int i = 4;
						break;
					case 2:
						i = 2;
						break;
				}
			}
		}
		""")
	public void testVariableInOtherCase(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: a variable declared in one case of a switch statement is accessible in all the following cases,
		//           but not in the previous cases.
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(3);
		var field = variables.get(0);
		assertThat(field).getType().isEqualTo(String.class);
		assertThat(variables.get(1)).getSimpleName().isEqualTo("integer");

		assertThat(variables.get(2)).getSimpleName().isEqualTo("i");

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(1));
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(field);
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(variables.get(2), field);
	}


	@ModelTest(code = """
		class Test {
			String i = "";
			void method(int integer) {
				switch (integer) {
					case 0:
						System.out.println(i);
						break;
					case 1: {
						int i = 4;
						break;
					}
					case 2:
						i = 2;
						break;
				}
			}
		}
		""")
	public void testVariableInOtherCaseWithBlock(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: a variable declared in one case of a switch statement does not escape the block surrounding its declaration.
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(3);
		var field = variables.get(0);
		assertThat(field).getType().isEqualTo(String.class);
		assertThat(variables.get(1)).getSimpleName().isEqualTo("integer");

		assertThat(variables.get(2)).getSimpleName().isEqualTo("i");

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(1));
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(field);
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(field);
	}

	@ModelTest(code = """
		class Test {
			String i = "";
			void method(int integer) {
				switch (integer) {
					case 0 -> System.out.println(i);
					case 1 -> {
						int i = 4;
					}
					case 2 -> i = 2;
				}
			}
		}
		""", complianceLevel = 17)
	public void testVariableInOtherCaseSwitchExpr(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: a variable declared in a switch expression case is inaccessible in other cases, even the following ones.
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(3);
		var field = variables.get(0);
		assertThat(field).getType().isEqualTo(String.class);
		assertThat(variables.get(1)).getSimpleName().isEqualTo("integer");

		assertThat(variables.get(2)).getSimpleName().isEqualTo("i");

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(1));
		assertThat(references.get(2)).hasExactlyPotentialDeclarations(field);
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(field);
	}


	@ModelTest(code = """
		class Test {
			void method(Object obj) {
				switch (obj) {
					case String string when string.length() > 4 -> System.out.println(string);
					default -> {}
				}
			}
		}
		""", complianceLevel = 21)
	public void testSwitchGuard(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: the variable reference in the guard of a switch resolves to the variable declared in the case pattern.
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(2);
		assertThat(variables.get(0)).getType().isEqualTo(Object.class);
		assertThat(variables.get(1)).getSimpleName().isEqualTo("string");

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(variables.get(0));
		// the variable reference in the guard should resolve to the variable declared in the case pattern:
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(variables.get(1));
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(variables.get(1));
	}

	@ModelTest(code = """
		class Test {
			String string = "";
			void method(int integer, Object obj) {
				switch (integer) {
					case 0:
						if (!(obj instanceof String string)) {
							throw new IllegalStateException();
						}
						System.out.println(string);
						break;
					case 1:
						System.out.println(string);
						break;
					default:
						break;
				}
			}
		}
		""", complianceLevel = 17)
	public void testPatternScopeOnlyInCase(@BySimpleName("Test") CtClass<?> ctClass) {
		// contract: A pattern variable introduced in a statement in a case is not accessible in the following cases.
		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(4);

		CtVariable<?> stringField = variables.get(0);
		CtVariable<?> integerVariable = variables.get(1);
		CtVariable<?> objParam = variables.get(2);
		CtVariable<?> localPattern = variables.get(3);

		assertThat(stringField).getSimpleName().isEqualTo("string");
		assertThat(integerVariable).getSimpleName().isEqualTo("integer");
		assertThat(objParam).getSimpleName().isEqualTo("obj");
		assertThat(localPattern).getSimpleName().isEqualTo("string");

		assertThat(references.get(0)).hasExactlyPotentialDeclarations(integerVariable); // switch (integer)
		assertThat(references.get(1)).hasExactlyPotentialDeclarations(objParam); // if (!(obj instanceof String string))
		// System.out
		assertThat(references.get(3)).hasExactlyPotentialDeclarations(localPattern, stringField); // println(string)
		// System.out
		assertThat(references.get(5)).hasExactlyPotentialDeclarations(stringField); // println(string)
	}
}
