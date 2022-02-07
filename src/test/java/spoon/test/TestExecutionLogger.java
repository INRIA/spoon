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


@MetaInfServices
public class TestExecutionLogger implements TestExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static Map<TestIdentifier, LocalDateTime> timers = new HashMap<>();
	@Override
	public void executionFinished(TestIdentifier testIdentifier,
			TestExecutionResult testExecutionResult) {
		// here we check if the execution was a test and not something like container or otherwise (like a suite)
		if (testIdentifier.getType().isTest()) {
			long duration = getTestRunTime(testIdentifier);
			String classname = getClassName(testIdentifier);
			String result = "executionFinished: " + classname + "#" + testIdentifier.getDisplayName()
					+ " with result: " + testExecutionResult.getStatus() + " in duration: " + duration
					+ " miliseconds\n";
			try {
				Files.writeString(Path.of("testResults.spoon"), result, StandardOpenOption.APPEND,
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
	private long getTestRunTime(TestIdentifier testIdentifier) {
		LocalDateTime finish = LocalDateTime.now();
		LocalDateTime startTime = timers.get(testIdentifier);
		timers.remove(testIdentifier);
		long duration = 0;
		if (startTime != null) {
			duration = startTime.until(finish, ChronoUnit.MILLIS);
		}
		return duration;
	}
	/**
	 * Returns the classname or empty string if the testIdentifier is not a methodsource.
	 * @param testIdentifier  the testIdentifier to get the classname from.
	 * @return  the classname or empty string if the testIdentifier is not a methodsource.
	 */
  private String getClassName(TestIdentifier testIdentifier) {
		return testIdentifier.getSource().stream()
				.filter(MethodSource.class::isInstance)
				.map(MethodSource.class::cast)
				.map(v -> v.getClassName())
				.findFirst()
				.orElse("");
  }

	@Override
	public void executionSkipped(TestIdentifier testIdentifier, String reason) {
		LocalDateTime finish = LocalDateTime.now();
		LocalDateTime startTime = timers.get(testIdentifier);
		timers.remove(testIdentifier);
		long duration = 0;
		if (startTime != null) {
			duration = startTime.until(finish, java.time.temporal.ChronoUnit.MILLIS);
		}
		if (testIdentifier.getType().isTest()) {
			String classname = getClassName(testIdentifier);
			String result = "executionSkipped: " + classname + "#" + testIdentifier.getDisplayName() +" duration: "+ duration + "miliseconds"
					+ " with reason: " + reason+"\n";
			try {
				Files.writeString(Path.of("testResults.spoon"), result, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				LOGGER.error("Error writing to file", e);
			}
		}
	}

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		timers.put(testIdentifier, LocalDateTime.now());
		// here land all started executions for example test methods, test classes, test containers.
	}
}
