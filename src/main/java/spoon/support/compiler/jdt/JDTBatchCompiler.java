/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;
import spoon.SpoonException;
import spoon.support.compiler.SpoonProgress;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
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

	/**
	 * This method returns the compilation units that will be processed and/or compiled by JDT.
	 * Note that this method also process the CUs to associate the right module information.
	 * Warning: this method cannot be replaced by a call to its supermethod as we manage the CUs differently
	 * in Spoon. We might indeed have CUs coming from virtual files or ignored CU due to the configuration.
	 * The the CUs are created from the {@link FileCompilerConfig}.
	 */
	@Override
	public CompilationUnit[] getCompilationUnits() {

		Map<String, char[]> pathToModName = new HashMap<>();

		for (int round = 0; round < 2; round++) {
			for (CompilationUnit compilationUnit : this.compilationUnits) {
				char[] charName = compilationUnit.getFileName();
				boolean isModuleInfo = CharOperation.endsWith(charName, JDTConstants.MODULE_INFO_FILE_NAME);
				if (isModuleInfo == (round == 0)) { // 1st round: modules, 2nd round others (to ensure populating pathToModCU well in time)

					String fileName = new String(charName);
					if (isModuleInfo) {
						int lastSlash = CharOperation.lastIndexOf(File.separatorChar, charName);
						if (lastSlash != -1) {
							char[] modulePath = CharOperation.subarray(charName, 0, lastSlash);

							lastSlash = CharOperation.lastIndexOf(File.separatorChar, modulePath);
							if (lastSlash == -1) {
								lastSlash = 0;
							} else {
								lastSlash += 1;
							}
							//TODO the module name parsed by JDK compiler is in `this.modNames`
							compilationUnit.module = CharOperation.subarray(modulePath, lastSlash, modulePath.length);
							pathToModName.put(String.valueOf(modulePath), compilationUnit.module);
						}
					} else {
						for (Map.Entry<String, char[]> entry : pathToModName.entrySet()) {
							if (fileName.startsWith(entry.getKey())) { // associate CUs to module by common prefix
								compilationUnit.module = entry.getValue();
								break;
							}
						}
					}
				}
			}
		}

		return compilationUnits;
	}

	public void setCompilationUnits(CompilationUnit[] compilationUnits) {
		this.compilationUnits = compilationUnits;
	}

	@Override
	public ICompilerRequestor getBatchRequestor() {
		final ICompilerRequestor r = super.getBatchRequestor();
		return new ICompilerRequestor() {
			@Override
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

		IErrorHandlingPolicy errorHandlingPolicy;

		if (jdtCompiler.getEnvironment().getNoClasspath()) {

			// in no classpath, we should proceed on error,
			// as we will encounter some
			errorHandlingPolicy = new IErrorHandlingPolicy() {
				@Override
				public boolean proceedOnErrors() {
					return true;
				}

				@Override
				public boolean stopOnFirstError() {
					return false;
				}

				// we cannot ignore them, because JDT will continue its process
				// and it led to NPE in several places
				@Override
				public boolean ignoreAllErrors() {
					return false;
				}
			};
		} else {

			// when there is a classpath, we should not have any error
			errorHandlingPolicy = new IErrorHandlingPolicy() {
				@Override
				public boolean proceedOnErrors() {
					return false;
				}

				// we wait for all errors to be gathered before stopping
				@Override
				public boolean stopOnFirstError() {
					return false;
				}

				@Override
				public boolean ignoreAllErrors() {
					return false;
				}
			};
		}

		IProblemFactory problemFactory = getProblemFactory();
		TreeBuilderCompiler treeBuilderCompiler = new TreeBuilderCompiler(
				environment, errorHandlingPolicy, compilerOptions,
				this.jdtCompiler.requestor, problemFactory, this.out, new CompilationProgress() {

			private String currentElement = null;
			private int totalTask = -1;

			@Override
			public void begin(int i) { }

			@Override
			public void done() { }

			@Override
			public boolean isCanceled() {
				return false;
			}

			@Override
			public void setTaskName(String s) {
				if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
					String strToFind = "Processing ";
					int processingPosition = s.indexOf(strToFind);
					if (processingPosition != -1) {
						currentElement = s.substring(processingPosition + strToFind.length());
					}
				}
			}

			@Override
			public void worked(int increment, int remaining) {
				if (totalTask == -1) {
					totalTask = remaining + 1;
				}
				if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
					jdtCompiler.getEnvironment().getSpoonProgress().step(SpoonProgress.Process.COMPILE, currentElement, totalTask - remaining, totalTask);
				}
			}
		});
		if (jdtCompiler.getEnvironment().getNoClasspath()) {
			treeBuilderCompiler.lookupEnvironment.problemReporter = new ProblemReporter(errorHandlingPolicy, compilerOptions, problemFactory) {
				@Override
				public int computeSeverity(int problemID) {
					// ignore all the problem and continue the build creation
					return 256;
				}
			};
			treeBuilderCompiler.lookupEnvironment.mayTolerateMissingType = true;
		}
		if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
			jdtCompiler.getEnvironment().getSpoonProgress().start(SpoonProgress.Process.COMPILE);
		}
		// they have to be done all at once
		final CompilationUnitDeclaration[] result = treeBuilderCompiler.buildUnits(getCompilationUnits());
		if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
			jdtCompiler.getEnvironment().getSpoonProgress().end(SpoonProgress.Process.COMPILE);
		}
		// now adding the doc
		if (jdtCompiler.getEnvironment().isCommentsEnabled()) {
			if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
				jdtCompiler.getEnvironment().getSpoonProgress().start(SpoonProgress.Process.COMMENT);
			}
			//compile comments only if they are needed
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

				if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
					jdtCompiler.getEnvironment().getSpoonProgress().step(SpoonProgress.Process.COMMENT, new String(unit.getFileName()), i + 1, result.length);
				}
			}
			if (jdtCompiler.getEnvironment().getSpoonProgress() != null) {
				jdtCompiler.getEnvironment().getSpoonProgress().end(SpoonProgress.Process.COMMENT);
			}
		}
		return result;
	}

	public JDTBasedSpoonCompiler getJdtCompiler() {
		return jdtCompiler;
	}

}
