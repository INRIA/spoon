package examples;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import spoon.architecture.report.ShellPrinter;
import spoon.architecture.runner.IModelBuilder;
import spoon.architecture.runner.ISpoonArchitecturalChecker;
import spoon.architecture.runner.ModelBuilder;
import spoon.architecture.runner.SpoonArchitecturalChecker;
import spoon.architecture.runner.SpoonRunner;
import spoon.reflect.CtModel;

public class ExampleRunner {

	@Test
	public void spoonExampleMustRun() {
		List<Pair<String, String>> testObjects = new ArrayList<>();
		testObjects.add(Pair.of("testmodel", "src/test/java/examples/spoon"));

		testObjects.add(Pair.of("annotationTests", "src/test/resources/examples/spoon/annotations/"));
		// SpoonArchitecturalCheckerImpl.createChecker().runChecks();
		IModelBuilder<CtModel> builder = new ModelBuilder();
		testObjects.forEach(v -> builder.insertInputPath(v.getKey(), v.getValue()));
		ISpoonArchitecturalChecker checker = new SpoonArchitecturalChecker.Builder()
		.addRunner(new SpoonRunner(builder))
		.useDefaultPath()
		.addReportPrinter(new ShellPrinter())
		.build();
		checker.runChecks();
	}
}
