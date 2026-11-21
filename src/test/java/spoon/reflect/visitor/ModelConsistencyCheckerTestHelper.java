package spoon.reflect.visitor;

import java.util.stream.Collectors;
import spoon.reflect.factory.Factory;

public class ModelConsistencyCheckerTestHelper {

	public static void assertModelIsConsistent(Factory factory) {
		// contract: each elements direct descendants should have the element as parent
		factory.getModel().getAllModules().forEach(ctModule -> {
			var invalidElements = ModelConsistencyChecker.listInconsistencies(ctModule);

			if (!invalidElements.isEmpty()) {
				throw new AssertionError("Model is inconsistent, %d elements have invalid parents:%n%s".formatted(
					invalidElements.size(),
					invalidElements.stream()
						.map(ModelConsistencyChecker.InconsistentElements::toString)
						.limit(5)
						.collect(Collectors.joining(System.lineSeparator()))
				));
			}
		});
	}

}
