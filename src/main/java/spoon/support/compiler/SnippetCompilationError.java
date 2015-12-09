/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.compiler;

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;

public class SnippetCompilationError extends SpoonException {

	private static final long serialVersionUID = 7805276558728052328L;

	public List<String> problems;

	public SnippetCompilationError(List<String> problems) {
		super();
		this.problems = problems;
	}

	public SnippetCompilationError(String string) {
		super();
		this.problems = new ArrayList<String>();
		this.problems.add(string);

	}

}
