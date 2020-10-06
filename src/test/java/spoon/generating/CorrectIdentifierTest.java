package spoon.generating;

import org.junit.Test;
import spoon.FluentLauncher;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Ignore;

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
}
