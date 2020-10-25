package spoon.architecture.simpleChecks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import spoon.architecture.report.ShellPrinter;
import spoon.architecture.runner.IModelBuilder;
import spoon.architecture.runner.ISpoonArchitecturalChecker;
import spoon.architecture.runner.ModelBuilder;
import spoon.architecture.runner.SpoonArchitecturalChecker;
import spoon.architecture.runner.SpoonRunner;
import spoon.reflect.CtModel;

public class CheckerRunnerTest {

	@Test
	public void allChecksMustRun() {
		List<Map.Entry<String, String>> testObjects = new ArrayList<>();
		testObjects.add(Map.entry("srcmodel", "src/main/java/"));
		testObjects.add(Map.entry("testmodel", "src/test/java/"));

		testObjects.add(Map.entry("fieldReferenceMatcher", "src/test/resources/FieldReferenceMatcher/"));
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
