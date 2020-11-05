package spoon.architecture.errorhandling;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

public class ErrorCollectorTest {



	@Test
	public void testEmptyDefaultErrorCollector() {
		// contract: An empty error collector does not print anything or throw any error.
		ErrorCollector<CtElement> collector = new ErrorCollector<>();
		assertDoesNotThrow(() -> collector.printCollectedErrors());
	}
	@Test
	public void testDefaultErrorCollector() {
		// contract: error collector prints all elements and then throws a spoonexception.
		ErrorCollector<CtElement> collector = new ErrorCollector<>();
		CtElement element = new Launcher().getFactory().createAnnotation();
		collector.printError(element);
		SpoonException exception = assertThrows(SpoonException.class, () -> collector.printCollectedErrors());
		assertThat(exception).hasMessageThat().contains("There are rule violations");
	}

	@Test
	public void testCustomErrorCollector() {
		// contract: error collector prints all elements and then throws a spoonexception.
		ErrorCollector<CtElement> collector = new ErrorCollector<>("<unit_test>");
		CtElement element = new Launcher().getFactory().createAnnotation();
		collector.printError(element);
		SpoonException exception = assertThrows(SpoonException.class, () -> collector.printCollectedErrors());
		assertThat(exception).hasMessageThat().contains("There are rule violations");
	}

}
