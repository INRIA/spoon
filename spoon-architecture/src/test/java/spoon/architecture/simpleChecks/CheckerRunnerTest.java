package spoon.architecture.simpleChecks;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
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
		List<Pair<String, String>> testObjects = new ArrayList<>();
		testObjects.add(Pair.of("srcmodel", "src/main/java/"));
		testObjects.add(Pair.of("testmodel", "src/test/java/spoon/"));

		testObjects.add(Pair.of("fieldReferenceMatcher", "src/test/resources/FieldReferenceMatcher/"));
		// SpoonArchitecturalCheckerImpl.createChecker().runChecks();
		IModelBuilder<CtModel> builder = new ModelBuilder();
		testObjects.forEach(v -> builder.insertInputPath(v.getKey(), v.getValue()));
		ISpoonArchitecturalChecker checker = new SpoonArchitecturalChecker.Builder()
		.addRunner(new SpoonRunner(builder))
		.addModelBuilder(builder)
		.addReportPrinter(new ShellPrinter())
		.build();
		checker.runChecks("testmodel");
	}
}
