/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.testing.utils;

import static org.junit.jupiter.api.Assertions.fail;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(GitHubIssue.UnresolvedBugExtension.class)
/**
 * This meta annotation is used to mark a test method as a test that should fail if {@link #fixed} is false.
 * The test will be executed and the result will be checked. If the test fails, the test will be marked as success.
 * Mark {@link #fixed} as true if you want to signal that the test should succeed and the issue is fixed.
 *
 * Example usage:
 * <pre>
 * {@literal @}Test
 * {@literal @}GitHubIssue(issueNumber = 123, fixed = false)
 * public void testSomething() {
 *     // Perform some test that should fail if issue #123 is not fixed
 *     Assertions.fail("This test should fail if issue #123 is not fixed");
 * }
 * </pre>
 */
public @interface GitHubIssue {

	/**
	 * The github number of the issue.
	 */
	int issueNumber();

	/*
	 * Signals if the issue is fixed. If the issue is marked as not fixed a failing testcase is a success. 
	 */
	boolean fixed();

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
	static class UnresolvedBugExtension implements AfterTestExecutionCallback, TestExecutionExceptionHandler {

		@Override
		public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
				throws Throwable {
			if (shouldFail(context)) {
				context.getStore(ExtensionContext.Namespace.create(GitHubIssue.class)).put("failed", true);
				return;
			}
			// rethrow the exception to fail the test case if it was not expected to fail
			throw throwable;
		}

		@Override
		public void afterTestExecution(ExtensionContext context) throws Exception {
			if (shouldFail(context) && !context
					.getStore(ExtensionContext.Namespace.create(GitHubIssue.class))
					.getOrDefault("failed", Boolean.class, false)) {
				fail("Test " + context.getDisplayName() + " must fail");
			}
		}
		private boolean shouldFail(ExtensionContext context) {
			return context.getTestMethod()
				.map(method -> method.getAnnotation(GitHubIssue.class))
				.filter(Objects::nonNull)
				.map(v -> !v.fixed())
				.orElse(false);
		}
	}
}
