package spoon.architecture.errorhandling;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

public class ExceptionErrorTest {

	@Test
	public void testSimpleError() {
		// contract: Error messages are not empty by default
		ExceptionError<CtElement> error = new ExceptionError<>();
		CtElement element = new Launcher().getFactory().createAnnotation();
		SpoonException exception = assertThrows(SpoonException.class, () -> error.printError(element));
		assertThat(exception).hasMessageThat().isNotEmpty();
	}
	@Test
	public void testCustomError() {
		// contract: Error messages are not empty by default
		ExceptionError<CtElement> error = new ExceptionError<>("<unit_test>");
		CtElement element = new Launcher().getFactory().createAnnotation();
		SpoonException exception = assertThrows(SpoonException.class, () -> error.printError(element));
		assertThat(exception).hasMessageThat().contains("<unit_test>");;
	}
}
