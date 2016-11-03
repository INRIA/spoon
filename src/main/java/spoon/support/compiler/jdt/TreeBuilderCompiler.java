/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.compiler.jdt;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

class TreeBuilderCompiler extends org.eclipse.jdt.internal.compiler.Compiler {

	TreeBuilderCompiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options,
			ICompilerRequestor requestor, IProblemFactory problemFactory, PrintWriter out,
			CompilationProgress progress) {
		super(environment, policy, options, requestor, problemFactory, out, progress);
	}

	public CompilationUnitDeclaration[] buildUnits(CompilationUnit[] sourceUnits) {

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
			if (unit.scope != null) {
				unit.scope.faultInTypes();
			}

			// verify inherited methods
			if (unit.scope != null) {
				unit.scope.verifyMethods(lookupEnvironment.methodVerifier());
			}

			// type checking
			unit.resolve();
			// flow analysis
			unit.analyseCode();

			unit.ignoreFurtherInvestigation = false;
			requestor.acceptResult(unit.compilationResult);
		}

		ArrayList<CompilationUnitDeclaration> unitsToReturn = new ArrayList<CompilationUnitDeclaration>();
		for (CompilationUnitDeclaration cud : this.unitsToProcess) {
			if (cud != null) {
				unitsToReturn.add(cud);
			}
		}
		return unitsToReturn.toArray(new CompilationUnitDeclaration[unitsToReturn.size()]);
	}
}
