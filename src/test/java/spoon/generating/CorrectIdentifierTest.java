package spoon.generating;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Ignore;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import spoon.FluentLauncher;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * for correct identifier see JLS chapter 3.8 and for keywords 3.9.
 * Ignored tests because we have to cut some corners between spec and jdt.
 */
public class CorrectIdentifierTest {

	@Test
	public void wrongIdentifer() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("tacos.EatIt()"));
	}

	@Test
	public void wrongIdentifer2() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName(";tacos"));
	}
	@Ignore
	@Test
	public void keyWord() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("class"));
	}

	@Test
	public void keyWord2() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("null"));
	}

	@Test
	public void keyWord3() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("true"));
	}

	@Test
	public void correctIdentifer() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("EatIt"));
	}

	@Test
	public void correctIdentifer2() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("ClassFoo"));
	}

	@Test
	public void correctIdentiferUtfFrench() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("UneClasseFrançaiseEtAccentuéeVoilà"));
	}

	@Test
	public void correctIdentiferUtfChinese() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertDoesNotThrow(() -> localVariableRef.setSimpleName("処理"));
	}

	@Test
	public void intersectionTypeIdentifierTest() {
		//contract: intersectionTypes can have simpleNames with '?' for wildcards.
		assertDoesNotThrow(() -> new FluentLauncher().inputResource("./src/test/resources/identifier/InliningImplementationMatcher.java").buildModel());
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
	class AnnotationType {

		private String topLevelAnnotation = "public @interface %s { }";

		@Test
		public void testInnerAnnotation() {
			String path = "src/test/resources/identifier/annotationType/InnerAnnotation.java";
			assertDoesNotThrow(() -> createModelFromPath(path));
		}

		@Test
		public void testKeywordsAnnotation() {
			for (String keyword : keywords) {
				assertThrows(SpoonException.class,
						() -> createModelFromString(String.format(topLevelAnnotation, keyword)));
			}
		}
		@Test
		public void testNullLiteralAnnotation() {
			for (String input : nullLiteral) {
				assertThrows(SpoonException.class,
						() -> createModelFromString(String.format(topLevelAnnotation, input)));
			}
		}
		@Test
		public void testBooleanLiteralsAnnotation() {
			for (String input : booleanLiterals) {
				assertThrows(SpoonException.class,
						() -> createModelFromString(String.format(topLevelAnnotation, input)));
			}
		}
		@Test
		public void testWrongLiteralsAnnotation() {
			List<String> inputs = new ArrayList<>();
			inputs.addAll(combineTwoLists(correctIdentifier, arrayIdentifier));
			inputs.addAll(combineTwoLists(correctIdentifier, genericSuffixes));
			for (String input : inputs) {
				assertThrows(SpoonException.class,
						() -> createModelFromString(String.format(topLevelAnnotation, input)));
			}
		}

		@Test
		public void testCorrectLiteralsAnnotation() {
			for (String input : correctIdentifier) {
				assertDoesNotThrow(() -> createModelFromString(String.format(topLevelAnnotation, input)));
			}
		}
	}


	private CtModel createModelFromPath(String path) {
		return new FluentLauncher().inputResource(path).buildModel();
	}

	private CtModel createModelFromString(String path) {
		return new FluentLauncher().inputResource(path).buildModel();
	}

	private static Collection<String> combineTwoLists(Collection<String> input1,
			Collection<String> input2) {
		Set<String> result = new HashSet<>();
		for (String string : input1) {
			for (String string2 : result) {
				result.add(string + string2);
			}
		}
		return result;
	}

	private static Collection<String> arrayIdentifier = Arrays.asList("[]", "[][]", "[][][][]");
	private static Collection<String> correctIdentifier =
			Arrays.asList("fii", "bar", "batz", "hahjashjashjdsaj");
	private static List<String> keywords = Arrays.asList("package", "new", "_");
	private static Collection<String> booleanLiterals = Arrays.asList("true", "false");
	private static Collection<String> nullLiteral = Arrays.asList("null");
	private static Collection<String> genericSuffixes =
			Arrays.asList("<?>", "<? extends Foo>", "<? super X>", "<? extends <X super Y>>");
}
