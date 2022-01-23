/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spoon.FluentLauncher;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class UnresolvedBugTest {

	private static final String OPEN_ISSUE_TEXT = "open";
	private static List<CtMethod<?>> testMethods = findTestMethods();
	private final String githubURL = "https://api.github.com/repos/INRIA/spoon/issues/";


	@BeforeAll
	public static void setup() {
		testMethods = testMethods.stream()
				.filter(v -> v.hasAnnotation(Test.class) &&  v.hasAnnotation(GitHubIssue.class))
				.collect(Collectors.toList());
	}

	/**
	 * Checks if every githubIssue annotation has an open github issue.
	 */
	
	@Test
	@Disabled
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
	@Disabled("The test is flacky when run in CI")
	public void checkUnresolvedBugAnnotations() throws IOException {
		// contract: every test ignored with @Ignore("UnresolvedBug") has an open
		// issue.
		// Ignored by default as it is flacky when run in CI
		List<CtMethod<?>> testMethodsWithoutGHI = testMethods.stream()
				.filter(v -> !v.hasAnnotation(GitHubIssue.class)).collect(Collectors.toList());
		assertEquals(testMethodsWithoutGHI.size(), 0);
	}
}
