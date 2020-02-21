package spoon.generating;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.reference.CtLocalVariableReference;
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

	@Ignore
	@Test
	public void keyWord2() {
		CtLocalVariableReference<Object> localVariableRef = new Launcher().getFactory().createLocalVariableReference();
		assertThrows(SpoonException.class, () -> localVariableRef.setSimpleName("null"));
	}

	@Ignore
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
}
