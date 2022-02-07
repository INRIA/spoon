/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;
import static org.junit.jupiter.api.Assertions.fail;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Test
@ExtendWith(GitHubIssue.UnresolvedBugExtension.class)
/**
 * This meta annotation is used to mark a test method as a test that should fail if {@link #open} is true.
 * The test will be executed and the result will be checked. If the test fails, the test will be marked as success.
 * As this is a meta annotation you can simple only add this to a test method and omit the {@link Test} aonntation.
 * Mark {@link #open} as false if you want to signal that the test should succed and the issue is fixed.
 */
public @interface GitHubIssue {

	/**
	 * The github number of the issue.
	 */
	int issueNumber();

	/**
	 * If true, the test will be marked as success if the test fails.
	 * Signals that the issue is not fixed yet.
	 */
	boolean open() default true;

	/**
	 * This extension does two things:
	 * <ul>
	 * <li>Fails any successful testcase with {@link GitHubIssue} and {@link Disabled}annotation</li>
	 * <li>Passes any failing test case with {@link GitHubIssue} and {@link Disabled}annotation</li>
	 * </ul>
	 * <p>
	 * This is useful to check if each testcase fails as expected. Internal in junit 5 failing testcases simply throw an exception.
	 * Swallowing this exceptions marks the testcase as not failing.
	 */
	 class UnresolvedBugExtension implements TestWatcher, TestExecutionExceptionHandler {

		private Set<ExtensionContext> correctFailingTestCases = new HashSet<>();

		@Override
		public void testSuccessful(ExtensionContext context) {
			if (shouldFail(context) && !correctFailingTestCases.contains(context)) {
				fail("Method " + context.getTestMethod().get().getName() + " must fail");
			}
		}

		@Override
		public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
				throws Throwable {
			if (shouldFail(context)) {
				correctFailingTestCases.add(context);
				return;
			}
			// rethrow the exception to fail the test case if it was not expected to fail
			throw throwable;
		}

		private boolean shouldFail(ExtensionContext context) {
			return context.getTestMethod()
					.map(v -> v.getAnnotation(GitHubIssue.class) != null && v.getAnnotation(GitHubIssue.class).open())
					.orElse(false);
		}
	}
}
