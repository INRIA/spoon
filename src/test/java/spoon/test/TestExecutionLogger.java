package spoon.test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

	@Override
	public void executionFinished(TestIdentifier testIdentifier,
			TestExecutionResult testExecutionResult) {
		// here we check if the execution was a test and not something like container or otherwise (like a suite)
		if (testIdentifier.getType().isTest()) {
			String classname = getClassName(testIdentifier);
			// now we simply print the result to sysout here could be a real logger(Our logger is off during testing?), markdown to a file
			// our more advanced logging happen.
			String result = "executionFinished: `" + classname + "#" + testIdentifier.getDisplayName()
					+ "` with result: `" + testExecutionResult.getStatus() + "`";
			try {
				Files.writeString(Path.of("testResults.md"), result, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				LOGGER.error("Error writing to file", e);
			}
		}
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
		// here land the skipped executions either by assumptions, annotations or by some other reason
	}

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		// here land all started executions for example test methods, test classes, test containers.
	}
}
