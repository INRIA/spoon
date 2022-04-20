package spoon.test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to log the execution of tests with their execution time.
 * It is used to generate a report at the end of the test execution.
 * JUnit 5 loads this class automatically when tests are run.
 * <p>
 * The runtime should <b>not</b> be used for performance mesurements.
 */
@MetaInfServices
public class TestExecutionLogger implements TestExecutionListener {

	private static final String TEST_RESULTS_FILE_NAME = "testResults.spoon";
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static Map<TestIdentifier, LocalDateTime> timers = new HashMap<>();
	@Override
	public void executionFinished(TestIdentifier testIdentifier,
			TestExecutionResult testExecutionResult) {
		long duration = getTestRuntime(testIdentifier);
		// here we check if the execution was a test and not something like container or otherwise (like a suite)
		if (testIdentifier.getType().isTest()) {
			String testName = getTestName(testIdentifier);
			String result = "executionFinished: " + testName
					+ " with result: " + testExecutionResult.getStatus() + " in duration: " + duration
					+ " ms\n";
			try {
				Files.writeString(Path.of(TEST_RESULTS_FILE_NAME), result, StandardOpenOption.APPEND,
						StandardOpenOption.CREATE);
			} catch (IOException e) {
				LOGGER.error("Error writing to file", e);
			}
		}
	}
	/**
	 * Returns the time in milliseconds since the test started.
	 * @param testIdentifier  the test identifier
	 * @return  the time in milliseconds since the test started or 0 if any error occurred.
	 */
	private long getTestRuntime(TestIdentifier testIdentifier) {
		LocalDateTime finish = LocalDateTime.now();
		LocalDateTime startTime = timers.get(testIdentifier);
		timers.remove(testIdentifier);
		return startTime != null ? ChronoUnit.MILLIS.between(startTime, finish) : 0;
	}
	/**
	 * Returns the test name in the format <className>#<methodName>.
	 * @param testIdentifier  the testIdentifier to get the test name from.
	 * @return  the testname or empty string if the testIdentifier is not a methodsource.
	 */
  private String getTestName(TestIdentifier testIdentifier) {
		return testIdentifier.getSource().stream()
				.filter(MethodSource.class::isInstance)
				.map(MethodSource.class::cast)
				.map(v -> v.getClassName() + "#" + v.getMethodName())
				.findFirst()
				.orElse("");
  }

	@Override
	public void executionSkipped(TestIdentifier testIdentifier, String reason) {
		long duration = getTestRuntime(testIdentifier);
		if (testIdentifier.getType().isTest()) {
			String testName = getTestName(testIdentifier);
			String result = "executionSkipped: " + testName +" in duration: "+ duration + " ms"
					+ " with reason: " + reason+"\n";
			try {
				Files.writeString(Path.of(TEST_RESULTS_FILE_NAME), result, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				LOGGER.error("Error writing to file", e);
			}
		}
	}

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		timers.put(testIdentifier, LocalDateTime.now());
	}
}
