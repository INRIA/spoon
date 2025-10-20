package spoon.test.reference;

import org.junit.jupiter.api.Test;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
