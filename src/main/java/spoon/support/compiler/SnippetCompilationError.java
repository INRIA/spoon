/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;

public class SnippetCompilationError extends SpoonException {

	private static final long serialVersionUID = 7805276558728052328L;

	public List<String> problems;

	public SnippetCompilationError(List<String> problems) {
		this.problems = problems;
	}

	public SnippetCompilationError(String string) {
		super(string);
		this.problems = new ArrayList<>();
		this.problems.add(string);

	}

}
