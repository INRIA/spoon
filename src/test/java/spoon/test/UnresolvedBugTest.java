/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import spoon.FluentLauncher;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class UnresolvedBugTest {

	private static final String OPEN_ISSUE_TEXT = "open";
	private static List<CtMethod<?>> testMethods = findTestMethods();
	private final String githubURL = "https://api.github.com/repos/INRIA/spoon/issues/";


	@BeforeClass
	public static void setup() {
		testMethods = testMethods.stream()
				.filter(v -> v.hasAnnotation(Test.class) &&  v.hasAnnotation(Ignore.class)
						&& v.getAnnotation(Ignore.class).value().equalsIgnoreCase("UnresolvedBug"))
				.collect(Collectors.toList());
	}

	/**
	 * Checks if every githubIssue annotation has an open github issue.
	 */
	@Ignore
	@Test
	public void checkGithubIssueAnnotations() throws IOException {
		// contract: every test GitHubIssue annotation points to a valid issue number and the issue is open.
		// Ignored by default as it is flacky when run in CI
		List<CtMethod<?>> testMethodsWithGHI = testMethods.stream()
				.filter(v -> v.hasAnnotation(Test.class) && v.hasAnnotation(GitHubIssue.class))
				.collect(Collectors.toList());
		for (CtMethod<?> ctMethod : testMethodsWithGHI) {
			int issueNumber = ctMethod.getAnnotation(GitHubIssue.class).issueNumber();
			URL url = new URL(githubURL + issueNumber);
			// because readAllBytes is jdk9 only
			String data = new BufferedReader(new InputStreamReader(url.openStream())).lines().collect(Collectors.joining());
			JsonObject issue = new Gson().fromJson(data, JsonObject.class);
			assertTrue(issue.get("number").getAsInt() == issueNumber);
			assertTrue(issue.get("state").getAsString().equals(OPEN_ISSUE_TEXT));
		}
	}

	static private List<CtMethod<?>> findTestMethods() {
		System.out.println("path: " + (new File("src/test/java/spoon")).getAbsolutePath());
		return new FluentLauncher().inputResource("src/test/java/spoon")
				.noClasspath(true)
				.disableConsistencyChecks()
				.buildModel()
				.getElements(new TypeFilter<>(CtMethod.class));
	}

	/**
	 * checks that no test method has the category annotation and no github issue
	 * tag. So every unresolved bug needs a github issue.
	 */
	@Test
	public void checkUnresolvedBugAnnotations() throws IOException {
		// contract: every test ignored with @Ignore("UnresolvedBug") has an open
		// issue.
		// Ignored by default as it is flacky when run in CI
		List<CtMethod<?>> testMethodsWithoutGHI = testMethods.stream()
				.filter(v -> !v.hasAnnotation(GitHubIssue.class))
				.collect(Collectors.toList());
		assertEquals(testMethodsWithoutGHI.size(), 0);
	}

	@Test
	public void checkThatUnresolvedBugTestFail() throws InitializationError {
		//contract: Test annotated with UnresolvedBug must fail
		System.out.println("Running @Ignore(\"UnresolvedBug\") tests: " + testMethods.size());

		JUnitCore junit = new JUnitCore();
		FailureListener failures = new FailureListener();
		junit.addListener(failures);
		for(CtMethod unresolvedTest: testMethods) {
			System.out.println("Running : " + unresolvedTest.getDeclaringType().getQualifiedName() + "#" + unresolvedTest.getSimpleName());
			Class testClass = unresolvedTest.getDeclaringType().getActualClass();
			String testMethod = unresolvedTest.getSimpleName();
			junit.run(new RunDespiteIgnore(testClass, testMethod));
			assertTrue(failures.failures.contains(testMethod + "(" + unresolvedTest.getDeclaringType().getQualifiedName() + ")"));
			System.out.println(unresolvedTest.getDeclaringType().getQualifiedName() + "#" + testMethod + " fails as expected");
		}
	}

	class FailureListener extends RunListener {
		public List<String> failures = new ArrayList<>();

		@Override
		public void testFailure(Failure failure) {
			failures.add(failure.getTestHeader());
		}
	}

	class RunDespiteIgnore extends BlockJUnit4ClassRunner {
		private String method;

		/**
		 * Creates a BlockJUnit4ClassRunner to run {@code klass}
		 *
		 * @param klass
		 * @throws InitializationError if the test class is malformed.
		 */
		public RunDespiteIgnore(Class<?> klass, String method) throws InitializationError {
			super(klass);
			this.method = method;
		}

		@Override
		protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
			if(method.getName().equals(this.method)) {
				Description description = describeChild(method);
				runLeaf(methodBlock(method), description, notifier);
			}
		}
	}
}
