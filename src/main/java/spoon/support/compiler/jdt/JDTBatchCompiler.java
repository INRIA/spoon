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

import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;
import spoon.SpoonException;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/*
 * Overrides the getCompilationUnits() from JDT's class to pass the ones we want.
 *
 * (we use a fully qualified name in inheritance to make it clear we are extending jdt)
 */
public class JDTBatchCompiler extends org.eclipse.jdt.internal.compiler.batch.Main {

	protected final JDTBasedSpoonCompiler jdtCompiler;
	protected CompilationUnit[] compilationUnits;

	public JDTBatchCompiler(JDTBasedSpoonCompiler jdtCompiler) {
		// by default we don't want anything from JDT
		// the reports are sent with callbakcs to the reporter
		// for debuggging, you may use System.out/err instead
		this(jdtCompiler, new NullOutputStream(), new NullOutputStream());
	}

	JDTBatchCompiler(JDTBasedSpoonCompiler jdtCompiler, OutputStream outWriter, OutputStream errWriter) {
		super(new PrintWriter(outWriter), new PrintWriter(errWriter), false, null, null);
		this.jdtCompiler = jdtCompiler;
		if (jdtCompiler != null) {
			this.jdtCompiler.probs.clear();
		}
	}

	@Override
	public CompilationUnit[] getCompilationUnits() {
		return compilationUnits;
	}

	public void setCompilationUnits(CompilationUnit[] compilationUnits) {
		this.compilationUnits = compilationUnits;
	}

	@Override
	public ICompilerRequestor getBatchRequestor() {
		final ICompilerRequestor r = super.getBatchRequestor();
		return new ICompilerRequestor() {
			public void acceptResult(CompilationResult compilationResult) {
				if (compilationResult.hasErrors()) {
					for (CategorizedProblem problem:compilationResult.problems) {
						if (JDTBatchCompiler.this.jdtCompiler != null) {
							JDTBatchCompiler.this.jdtCompiler.reportProblem(problem);
						} else {
							throw new SpoonException(problem.toString());
						}
					}
				}
				r.acceptResult(compilationResult); // this is required to complete the compilation and produce the class files
			}
		};
	}

	protected Set<String> filesToBeIgnored = new HashSet<>();

	public void ignoreFile(String filePath) {
		filesToBeIgnored.add(filePath);
	}


	/** Calls JDT to retrieve the list of compilation unit declarations.
	 * Depends on the actual implementation of {@link #getCompilationUnits()}
	 */
	public CompilationUnitDeclaration[] getUnits() {
		startTime = System.currentTimeMillis();
		INameEnvironment environment = this.jdtCompiler.environment;
		if (environment == null) {
			environment = getLibraryAccess();
		}
		CompilerOptions compilerOptions = new CompilerOptions(this.options);
		compilerOptions.parseLiteralExpressionsAsConstants = false;
		TreeBuilderCompiler treeBuilderCompiler = new TreeBuilderCompiler(
				environment, getHandlingPolicy(), compilerOptions,
				this.jdtCompiler.requestor, getProblemFactory(), this.out,
				null);
		if (jdtCompiler.getEnvironment().getNoClasspath()) {
			treeBuilderCompiler.lookupEnvironment.mayTolerateMissingType = true;
		}

		// they have to be done all at once
		final CompilationUnitDeclaration[] result = treeBuilderCompiler.buildUnits(getCompilationUnits());

		// now adding the doc
		for (int i = 0; i < result.length; i++) {
			CompilationUnitDeclaration unit = result[i];
			CommentRecorderParser parser =
					new CommentRecorderParser(
							new ProblemReporter(
									DefaultErrorHandlingPolicies.proceedWithAllProblems(),
									compilerOptions,
									new DefaultProblemFactory(Locale.getDefault())),
							false);

			//reuse the source compilation unit
			ICompilationUnit sourceUnit = unit.compilationResult.compilationUnit;

			final CompilationResult compilationResult = new CompilationResult(sourceUnit, 0, 0, compilerOptions.maxProblemsPerUnit);
			CompilationUnitDeclaration tmpDeclForComment = parser.dietParse(sourceUnit, compilationResult);
			unit.comments = tmpDeclForComment.comments;
		}
		return result;
	}

	public JDTBasedSpoonCompiler getJdtCompiler() {
		return jdtCompiler;
	}

}
