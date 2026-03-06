package spoon.test.reference;

import org.junit.jupiter.api.Test;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.BySimpleName;
import spoon.testing.utils.ModelTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static spoon.testing.assertions.SpoonAssertions.assertThat;
import static spoon.test.SpoonTestHelpers.createModelFromString;

public class PatternMatchingReferenceTest {
	@Test
	public void testCasePatternReferenceArrow() {
		String code = """
				class X {
					public void processShape(Shape shape) {
						switch (shape) {
							case Circle c -> System.out.println("This is a circle with radius: " + c.getRadius());
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
	public void testCasePatternReferenceColon() {
		String code = """
				class X {
					public void processShape(Shape shape) {
						switch (shape) {
							case Circle c:
								System.out.println("This is a circle with radius: " + c.getRadius());
								break;
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

		/*class Test {
			String i = "";

			void method(int integer) {
				switch (0) {
					case 0:
						int i = 4;
						break;
					case 1:
						System.out.println(i);
						break;
				}
			}
		}*/

		List<CtVariable<?>> variables = ctClass.getElements(new TypeFilter<>(CtVariable.class));
		List<CtVariableReference<?>> references = ctClass.getElements(new TypeFilter<>(CtVariableReference.class));

		assertThat(variables)
			.hasSize(3);
		var field = variables.get(0);
		assertThat(field).getType().isEqualTo(String.class);
		assertThat(variables.get(1)).getSimpleName().isEqualTo("integer");

		assertThat(variables.get(2)).getSimpleName().isEqualTo("i");

		assertThat(references.get(0)).getDeclaration().isSameAs(variables.get(1));
		assertThat(references.get(2)).getDeclaration().isSameAs(field);
		assertThat(references.get(3)).getDeclaration().isSameAs(variables.get(2));
	}
}
