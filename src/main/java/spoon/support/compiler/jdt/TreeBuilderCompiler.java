package spoon.support.compiler.jdt;

import java.io.PrintWriter;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

class TreeBuilderCompiler extends
		org.eclipse.jdt.internal.compiler.Compiler {

	public TreeBuilderCompiler(INameEnvironment environment,
			IErrorHandlingPolicy policy, CompilerOptions options,
			ICompilerRequestor requestor,
			IProblemFactory problemFactory, PrintWriter out,
			CompilationProgress progress) {
		super(environment, policy, options, requestor, problemFactory,
				out, progress);
	}

	public CompilationUnitDeclaration[] buildUnits(
			CompilationUnit[] sourceUnits) {

		// //////////////////////////////////////////////////////////////////////////
		// This code is largely inspired from JDT's
		// CompilationUnitResolver.resolve

		CompilationUnitDeclaration unit = null;
		int i = 0;
		// build and record parsed units
		beginToCompile(sourceUnits);

		// process all units (some more could be injected in the loop by
		// the lookup environment)
		for (; i < this.totalUnits; i++) {
			unit = unitsToProcess[i];
			// System.err.println(unit);
			this.parser.getMethodBodies(unit);

			// fault in fields & methods
			if (unit.scope != null)
				unit.scope.faultInTypes();

			// verify inherited methods
			if (unit.scope != null)
				unit.scope.verifyMethods(lookupEnvironment
						.methodVerifier());

			// type checking
			unit.resolve();
			// flow analysis
			unit.analyseCode();

			unit.ignoreFurtherInvestigation = false;
			requestor.acceptResult(unit.compilationResult);
		}

		return this.unitsToProcess;
	}
}