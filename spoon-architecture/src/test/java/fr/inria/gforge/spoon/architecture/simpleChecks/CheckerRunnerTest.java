package fr.inria.gforge.spoon.architecture.simpleChecks;

import org.junit.Test;
import fr.inria.gforge.spoon.architecture.runner.CheckerRunner;

public class CheckerRunnerTest {

	@Test
	public void allChecksMustRun() {
		CheckerRunner runner = new CheckerRunner();
		runner.runChecks("./src/main/java", "./src/test/java");
	}
}
