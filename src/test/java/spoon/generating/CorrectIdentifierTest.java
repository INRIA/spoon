package spoon.generating;


import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import spoon.FluentLauncher;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.VirtualFile;

/**
 * for correct identifier see JLS chapter 3.8 and for keywords 3.9.
 * Ignored tests because we have to cut some corners between spec and jdt.
 */
public class CorrectIdentifierTest {

	@Test
	public void wrongIdentifer() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("tacos.EatIt()"));
	}

	@Test
	public void wrongIdentifer2() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName(";tacos"));
	}


	@Test
	public void keyWord2() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("null"));
	}

	@Test
	public void keyWord3() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("true"));
	}

	@Test
	public void correctIdentifer() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("EatIt"));
	}

	@Test
	public void correctIdentifer2() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("ClassFoo"));
	}

	@Test
	public void correctIdentiferUtfFrench() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("UneClasseFrançaiseEtAccentuéeVoilà"));
	}

	@Test
	public void correctIdentiferUtfChinese() {
		CtLocalVariableReference<Object> localVariableRef =
				new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("処理"));
	}

	@Test
	public void intersectionTypeIdentifierTest() {
		//contract: intersectionTypes can have simpleNames with '?' for wildcards.
		assertDoesNotThrow(() -> new FluentLauncher()
				.inputResource("./src/test/resources/identifier/InliningImplementationMatcher.java")
				.buildModel());
	}

	@Test
	public void correctSquareBrackets() {
		CtTypeReference localVariableRef = new Launcher().getFactory().createTypeReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("List<String>[]"));
	}

	@Test
	public void mainTest() {
		//contract: TODO:
		assertDoesNotThrow(() -> new FluentLauncher().inputResource("./src/main/java").buildModel());
	}

	@Nested
	class CtAnnotationTypeTest {

		private String topLevelAnnotation = "public @interface %s { }";

		private CtAnnotationType<?> type;
		@BeforeEach
		private void createAnnotation() {
			type = new Launcher().getFactory().createAnnotationType();;
		}
		@Test
		public void testInnerAnnotation() {
			String path = "src/test/resources/identifier/annotationType/InnerAnnotation.java";
			assertDoesNotThrow(() -> createModelFromPath(path));
		}

		@Test
		public void testKeywordsAnnotation() {
			checkKeywordsAsIdentifier(type);
		}
		@Test
		public void testTypeNamesAnnotation() {
			checkTypeLiteralAsIdentifier(type);
		}
		@Test
		public void testNullLiteralAnnotation() {
			checkNullLiteralAsIdentifier(type);
		}


		@Test
		public void testClassLiteralAnnotation() {
			checkClassLiteralAsIdentifier(type);
		}

		@Test
		public void testBooleanLiteralsAnnotation() {
			checkBooleanLiteralAsIdentifier(type);
		}

		@Test
		public void testWrongLiteralsAnnotation() {
			checkWrongLiterals(type);
		}

		@Test
		public void testCorrectLiteralsAnnotation() {
			for (String input : correctIdentifier) {
				assertDoesNotThrow(() -> createModelFromString(String.format(topLevelAnnotation, input)));
			}
		}
	}
	@Nested
	class CtClassTest {

		private String code = "public class %s {}";
		private String innerClass = "public class fobar { class %s {}}";
		private String localClass = "public class fobar { fobar(){class %s {}}}";
		private CtClass<?> type;
		@BeforeEach
		private void createCtClass() {
			type = new Launcher().getFactory().createClass();
		}

		@Test
		public void testKeywordsCtClass() {
			checkKeywordsAsIdentifier(type);
		}

		@Test
		public void testNullLiteralCtClass() {
			checkNullLiteralAsIdentifier(type);
		}

		@Test
		public void testClassLiteralCtClass() {
			checkClassLiteralAsIdentifier(type);
		}

		@Test
		public void testBooleanLiteralsCtClass() {
			checkBooleanLiteralAsIdentifier(type);
		}

		@Test
		public void testWrongLiteralsCtClass() {
			checkWrongLiterals(type);
		}
		@Test
		public void checkCorrectLiterals() {
			assertDoesNotThrow(() -> type.setSimpleName("foo"));
			assertDoesNotThrow(() -> type.setSimpleName("foo[]"));
			assertDoesNotThrow(() -> type.setSimpleName("foo[][][]"));
			assertDoesNotThrow(() -> createModelFromString(String.format(code, "Foo")));
			assertDoesNotThrow(() -> createModelFromString(String.format(innerClass, "Foo")));
			assertDoesNotThrow(() -> createModelFromString(String.format(localClass, "Foo")));
		}
	}
	@Nested
	class CtTypeParameterTest {

		private String topLevelCtClass = "public class bar<%s> { }";

		@Test
		public void testKeywordsCtTypeParameter() {
			CtTypeParameter type = new Launcher().getFactory().createTypeParameter();
			String nameBefore = type.getSimpleName();
			for (String keyword : keywords) {
				assertThrows(SpoonException.class, () -> type.setSimpleName(keyword));
				// name mustn't change, after setting an invalid
				assertEquals(type.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testNullLiteralCtTypeParameter() {
			CtTypeParameter type = new Launcher().getFactory().createTypeParameter();
			String nameBefore = type.getSimpleName();
			for (String input : nullLiteral) {
				assertThrows(SpoonException.class, () -> type.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(type.getSimpleName(), nameBefore);
			}
		}


		@Test
		public void testClassLiteralCtTypeParameter() {
			CtTypeParameter type = new Launcher().getFactory().createTypeParameter();
			String nameBefore = type.getSimpleName();
			for (String input : classLiteral) {
				assertThrows(SpoonException.class, () -> type.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(type.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testBooleanLiteralsCtTypeParameter() {
			CtTypeParameter type = new Launcher().getFactory().createTypeParameter();
			String nameBefore = type.getSimpleName();
			for (String input : booleanLiterals) {
				assertThrows(SpoonException.class, () -> type.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(type.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testWrongLiteralsCtTypeParameter() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, arrayIdentifier));
			inputs.addAll(combineTwoLists(correctIdentifier, genericSuffixes));
			CtTypeParameter type = new Launcher().getFactory().createTypeParameter();
			String nameBefore = type.getSimpleName();
			for (String input : inputs) {
				assertThrows(SpoonException.class, () -> type.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(type.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testCorrectLiteralsCtTypeParameter() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, genericSuffixes));
			inputs.addAll(combineTwoLists(correctIdentifier, correctIdentifier));

			for (String input : inputs) {
				assertDoesNotThrow(() -> createModelFromString(String.format(topLevelCtClass, input)));
			}
		}
	}

	@Nested
	class CtConstructorTest {
		@Test
		public void testInitName() {
			CtConstructor<?> cons = new Launcher().getFactory().createConstructor();
			assertDoesNotThrow(() -> cons.setSimpleName("foo"));
			// SetSimpleName does not change the name
			assertNotEquals("foo", cons.getSimpleName());
		}
	}

	@Nested
	class CtEnumTest {

		private String topLevelCtEnum = "public enum %s { }";

		@Test
		public void testKeywordsCtEnum() {

		}

		@Test
		public void testNullLiteralCtEnum() {

		}


		@Test
		public void testClassLiteralCtEnum() {

		}

		@Test
		public void testBooleanLiteralsCtEnum() {

		}

		@Test
		public void testWrongLiteralsCtEnum() {

		}

		@Test
		public void testCorrectLiteralsCtEnum() {
		}
	}

	@Nested
	class CtExecutableReferenceTest {

		private String methodInvocation = "public void bar {%s(); }";

		@Test
		public void testKeywordsCtExecutableReference() {
			CtExecutableReference<?> reference = new Launcher().getFactory().createExecutableReference();
			String nameBefore = reference.getSimpleName();
			for (String keyword : keywords) {
				assertThrows(SpoonException.class, () -> reference.setSimpleName(keyword));
				// name mustn't change, after setting an invalid
				assertEquals(reference.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testNullLiteralCtExecutableReference() {
			CtExecutableReference<?> reference = new Launcher().getFactory().createExecutableReference();
			String nameBefore = reference.getSimpleName();
			for (String input : nullLiteral) {
				assertThrows(SpoonException.class, () -> reference.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(reference.getSimpleName(), nameBefore);
			}
		}


		@Test
		public void testClassLiteralCtExecutableReference() {
			CtExecutableReference<?> reference = new Launcher().getFactory().createExecutableReference();
			String nameBefore = reference.getSimpleName();
			for (String input : classLiteral) {
				assertThrows(SpoonException.class, () -> reference.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(reference.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testBooleanLiteralsCtExecutableReference() {
			CtExecutableReference<?> reference = new Launcher().getFactory().createExecutableReference();
			String nameBefore = reference.getSimpleName();
			for (String input : booleanLiterals) {
				assertThrows(SpoonException.class, () -> reference.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(reference.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testWrongLiteralsCtExecutableReference() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, arrayIdentifier));
			inputs.addAll(combineTwoLists(correctIdentifier, genericSuffixes));
			CtExecutableReference<?> reference = new Launcher().getFactory().createExecutableReference();
			String nameBefore = reference.getSimpleName();
			for (String input : inputs) {
				assertThrows(SpoonException.class, () -> reference.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(reference.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testCorrectLiteralsCtExecutableReference() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, correctIdentifier));

			for (String input : inputs) {
				assertDoesNotThrow(() -> createModelFromString(String.format(methodInvocation, input)));
			}
		}
	}
	@Nested
	class CtFieldTest {

		private String fieldTestString = "public class Foo { int %s;}";
		private String innerFieldTest = "public class Foo { class Bar { int %s}}";

		@Test
		public void testKeywordsCtField() {
			CtField<?> field = new Launcher().getFactory().createField();
			String nameBefore = field.getSimpleName();
			for (String keyword : keywords) {
				assertThrows(SpoonException.class, () -> field.setSimpleName(keyword));
				// name mustn't change, after setting an invalid
				assertEquals(field.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testNullLiteralCtField() {
			CtField<?> field = new Launcher().getFactory().createField();
			String nameBefore = field.getSimpleName();
			for (String input : nullLiteral) {
				assertThrows(SpoonException.class, () -> field.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(field.getSimpleName(), nameBefore);
			}
		}


		@Test
		public void testClassLiteralCtField() {
			CtField<?> field = new Launcher().getFactory().createField();
			String nameBefore = field.getSimpleName();
			for (String input : classLiteral) {
				assertThrows(SpoonException.class, () -> field.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(field.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testBooleanLiteralsCtField() {
			CtField<?> field = new Launcher().getFactory().createField();
			String nameBefore = field.getSimpleName();
			for (String input : booleanLiterals) {
				assertThrows(SpoonException.class, () -> field.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(field.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testWrongLiteralsCtField() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, arrayIdentifier));
			inputs.addAll(combineTwoLists(correctIdentifier, genericSuffixes));
			CtField<?> field = new Launcher().getFactory().createField();
			String nameBefore = field.getSimpleName();
			for (String input : inputs) {
				assertThrows(SpoonException.class, () -> field.setSimpleName(input));
				// name mustn't change, after setting an invalid
				assertEquals(field.getSimpleName(), nameBefore);
			}
		}

		@Test
		public void testCorrectLiteralsCtField() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, genericSuffixes));
			inputs.addAll(combineTwoLists(correctIdentifier, correctIdentifier));

			for (String input : inputs) {
				assertDoesNotThrow(() -> createModelFromString(String.format(fieldTestString, input)));
				assertDoesNotThrow(() -> createModelFromString(String.format(innerFieldTest, input)));

			}
		}
	}



	private CtModel createModelFromPath(String path) {
		Launcher launcher = new Launcher();
		launcher.addInputResource(path);
		launcher.buildModel();
		return launcher.getModel();
	}

	private CtModel createModelFromString(String path) {
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile(path));
		launcher.buildModel();
		return launcher.getModel();
	}

	private Collection<String> combineTwoLists(Collection<String> input1, Collection<String> input2) {
		Set<String> result = new HashSet<>();
		for (String string : input1) {
			for (String string2 : input2) {
				result.add(string + string2);
			}
		}
		return result;
	}

	private static Collection<String> arrayIdentifier = Arrays.asList("[]", "[][]", "[][][][]");
	private static Collection<String> correctIdentifier =
			Arrays.asList("fii", "bar", "batz", "hahjashjashjdsaj");
	private static List<String> keywords = Arrays.asList("package", "new", "_", "strictfp");
	private static Collection<String> booleanLiterals = Arrays.asList("true", "false");
	private static Collection<String> nullLiteral = Arrays.asList("null");
	private static Collection<String> classLiteral = Arrays.asList("class");
	private static Collection<String> genericSuffixes =
			Arrays.asList("<?>", "<? extends Foo>", "<? super X>", "<? extends <X super Y>>");

	private void checkKeywordsAsIdentifier(CtNamedElement element) {
		String nameBefore = element.getSimpleName();
		assertThrows(SpoonException.class, () -> element.setSimpleName("package"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("_"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("strictfp"));
		assertEquals(element.getSimpleName(), nameBefore);
	}
	private void checkBooleanLiteralAsIdentifier(CtNamedElement element) {
		String nameBefore = element.getSimpleName();
		assertThrows(SpoonException.class, () -> element.setSimpleName("true"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("false"));
		assertEquals(element.getSimpleName(), nameBefore);
	}

	private void checkNullLiteralAsIdentifier(CtNamedElement element) {
		String nameBefore = element.getSimpleName();
		assertThrows(SpoonException.class, () -> element.setSimpleName("null"));
		assertEquals(element.getSimpleName(), nameBefore);
	}
	private void checkClassLiteralAsIdentifier(CtNamedElement element) {
		String nameBefore = element.getSimpleName();
		assertThrows(SpoonException.class, () -> element.setSimpleName("class"));
		assertEquals(element.getSimpleName(), nameBefore);
	}
	private void checkTypeLiteralAsIdentifier(CtNamedElement element) {
		String nameBefore = element.getSimpleName();
		assertThrows(SpoonException.class, () -> element.setSimpleName("int"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("float"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("double"));
		assertEquals(element.getSimpleName(), nameBefore);
	}
	private void checkWrongLiterals(CtNamedElement element) {
		String nameBefore = element.getSimpleName();
		assertThrows(SpoonException.class, () -> element.setSimpleName("fobar["));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("fobar[[]"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("fobar<"));
		assertEquals(element.getSimpleName(), nameBefore);
		assertThrows(SpoonException.class, () -> element.setSimpleName("fobar<klmdfaskmldsakl>><y<<"));
		assertEquals(element.getSimpleName(), nameBefore);
	}
}
