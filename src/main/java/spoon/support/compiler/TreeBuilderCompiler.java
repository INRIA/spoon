package spoon.support.compiler;

import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;

public class TreeBuilderCompiler extends org.eclipse.jdt.internal.compiler.Compiler {

	@SuppressWarnings("deprecation")
	public TreeBuilderCompiler(INameEnvironment environment,
			IErrorHandlingPolicy policy, Map<?,?> settings,
			ICompilerRequestor requestor, IProblemFactory problemFactory,
			PrintWriter out, boolean statementsRecovery) {
		super(environment, policy, settings, requestor, problemFactory,
				statementsRecovery);
	}
	
	public CompilationUnitDeclaration[] compileUnits(
			CompilationUnit[] sourceUnits) {
	  
	  // //////////////////////////////////////////////////////////////////////////
	  // This code is largely inspired from JDT's CompilationUnitResolver.resolve
	  
	  
		CompilationUnitDeclaration unit = null;
		int i = 0;
		// build and record parsed units
		beginToCompile(sourceUnits);
		
		// process all units (some more could be injected in the loop by
		// the lookup environment)
		for (; i < this.totalUnits; i++) {
			unit = unitsToProcess[i];
			//System.err.println(unit);
			this.parser.getMethodBodies(unit);

			// fault in fields & methods
			if (unit.scope != null)
              unit.scope.faultInTypes();
			
			// verify inherited methods
			if (unit.scope != null)
				unit.scope.verifyMethods(lookupEnvironment.methodVerifier());
			
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