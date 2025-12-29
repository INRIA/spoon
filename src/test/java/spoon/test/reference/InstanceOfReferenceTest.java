package spoon.test.reference;

import org.junit.jupiter.api.Test;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.GitHubIssue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static spoon.test.SpoonTestHelpers.createModelFromString;

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
}
