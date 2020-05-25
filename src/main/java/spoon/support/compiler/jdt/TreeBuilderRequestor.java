/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;

public class TreeBuilderRequestor implements ICompilerRequestor {

	private final JDTBasedSpoonCompiler jdtCompiler;

	/**
	 * @param jdtCompiler
	 */
	TreeBuilderRequestor(JDTBasedSpoonCompiler jdtCompiler) {
		this.jdtCompiler = jdtCompiler;
	}

	@Override
	public void acceptResult(CompilationResult result) {
		if (result.hasErrors()) {
			for (CategorizedProblem problem : result.problems) {
				this.jdtCompiler.reportProblem(problem);
			}
		}
	}
}
