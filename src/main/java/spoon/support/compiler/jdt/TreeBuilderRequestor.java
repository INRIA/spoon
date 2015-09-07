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

	public void acceptResult(CompilationResult result) {
		if (result.hasErrors()) {
			for (CategorizedProblem problem : result.problems) {
				this.jdtCompiler.reportProblem(problem);
			}
		}
	}
}
