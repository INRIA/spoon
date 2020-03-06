/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import spoon.FluentLauncher;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class UnresolvedBugTest {

	private List<CtMethod<?>> testMethods = findTestMethods();
	private final String githubURL = "https://api.github.com/repos/INRIA/spoon/issues/";

	/**
	 * Checks if every githubIssue annotation has an github issue.
	 */
	@Test
	public void checkGithubIssueAnnotations() throws IOException {
		// contract: every test GitHubIssue annotation points to a valid issue number.
		testMethods = testMethods.stream()
				.filter(v -> v.hasAnnotation(Test.class) && v.hasAnnotation(GitHubIssue.class))
				.collect(Collectors.toList());
		for (CtMethod<?> ctMethod : testMethods) {
			String issueNumber = ctMethod.getAnnotation(GitHubIssue.class).issueNumber();
			URL url = new URL(githubURL + issueNumber);
			// because readAllBytes is jdk9 only
			String data = new BufferedReader(new InputStreamReader(url.openStream())).lines().collect(Collectors.joining());
			JsonObject issue = new Gson().fromJson(data, JsonObject.class);
			assertTrue(issue.get("number").getAsString().equals(issueNumber));
		}
	}

	private List<CtMethod<?>> findTestMethods() {
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
		// contract: every test ignored with @Category(UnresolvedBug.class) has an open
		// issue.
		testMethods = testMethods.stream()
				.filter(
						v -> v.hasAnnotation(Test.class) && !v.hasAnnotation(GitHubIssue.class) && v.hasAnnotation(Category.class)
								&& v.getAnnotation(Category.class).value()[0].equals(UnresolvedBug.class))
				.collect(Collectors.toList());
		assertEquals(testMethods.size(), 0);
	}
}
