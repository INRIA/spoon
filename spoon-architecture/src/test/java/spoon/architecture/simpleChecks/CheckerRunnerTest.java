package spoon.architecture.simpleChecks;

import org.junit.Test;
import spoon.architecture.report.ShellPrinter;
import spoon.architecture.runner.SpoonArchitecturalChecker;

public class CheckerRunnerTest {

	@Test
	public void allChecksMustRun() {
		// SpoonArchitecturalCheckerImpl.createChecker().runChecks();
		new SpoonArchitecturalChecker.Builder().useDefaultPath().addReportPrinter(new ShellPrinter()).build().runChecks();
	}
}
