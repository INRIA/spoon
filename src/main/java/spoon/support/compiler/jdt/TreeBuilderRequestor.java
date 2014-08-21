package spoon.support.compiler.jdt;

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
			this.jdtCompiler.probs.add(result.problems);
		}
	}

}