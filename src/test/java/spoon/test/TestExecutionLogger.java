package spoon.test;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.kohsuke.MetaInfServices;


@MetaInfServices
public class TestExecutionLogger implements TestExecutionListener {

	@Override
	public void executionFinished(TestIdentifier testIdentifier,
			TestExecutionResult testExecutionResult) {
				// here we check if the execution was a test and not something like container or otherwise (like a suite)
				if (testIdentifier.getType().isTest()) {
					// now we simply print the result to sysout here could be a real logger(Our logger is off during testing?), markdown to a file
					// our more advanced logging happen.
					System.out.println("executionFinished: "
							+ testIdentifier.getDisplayName() + " with result: "
							+ testExecutionResult.getStatus());
				}
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
