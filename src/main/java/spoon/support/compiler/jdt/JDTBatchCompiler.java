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
package spoon.support.compiler.jdt;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import spoon.Launcher;
import spoon.compiler.SpoonFile;

// we use a fully qualified name to make it clear we are extending jdt
class JDTBatchCompiler extends org.eclipse.jdt.internal.compiler.batch.Main {

	/**
	 *
	 */
	protected JDTBasedSpoonCompiler jdtCompiler;
	private boolean useFactory;

	JDTBatchCompiler(JDTBasedSpoonCompiler jdtCompiler, boolean useFactory) {
		super(new PrintWriter(System.out), new PrintWriter(
		/* new NullOutputStream() */System.err), false, null, null);
		this.jdtCompiler = jdtCompiler;
		this.useFactory = useFactory;
	}

	@Override
	public ICompilerRequestor getBatchRequestor() {
		final ICompilerRequestor r = super.getBatchRequestor();
		return new ICompilerRequestor() {
			public void acceptResult(CompilationResult compilationResult) {
				if (compilationResult.hasErrors()) {
					for (CategorizedProblem problem:compilationResult.problems) {
						JDTBatchCompiler.this.jdtCompiler.reportProblem(problem);
					}
				}
				r.acceptResult(compilationResult);
			}
		};
	}

	private Set<String> ignoredFiles = new HashSet<String>();

	public void ignoreFile(String filePath) {
		ignoredFiles.add(filePath);
	}

	@Override
	public CompilationUnit[] getCompilationUnits() {
		CompilationUnit[] units = super.getCompilationUnits();
		if (!ignoredFiles.isEmpty()) {
			List<CompilationUnit> l = new ArrayList<CompilationUnit>();
			for (CompilationUnit unit : units) {
				if (!ignoredFiles.contains(new String(unit.getFileName()))) {
					l.add(unit);
				}
			}
			units = l.toArray(new CompilationUnit[0]);
		}
		if (useFactory) {
			for (int i = 0; i < units.length; i++) {
				CompilationUnit unit = units[i];
				units[i] = new CompilationUnitWrapper(this.jdtCompiler, unit);
			}
		}
		return units;
	}

	public CompilationUnit[] getCompilationUnits(List<SpoonFile> files) {
		Set<String> fileNames = new HashSet<String>();
		List<SpoonFile> virtualFiles = new ArrayList<SpoonFile>();
		for (SpoonFile f : files) {
			if (!f.isActualFile()) {
				virtualFiles.add(f);
			} else {
				fileNames.add(f.getPath());
			}
		}

		List<CompilationUnit> culist = new ArrayList<CompilationUnit>();
		CompilationUnit[] units = getCompilationUnits();
		for (CompilationUnit unit : units) {
			if (fileNames.contains(new String(unit.getFileName()))) {
				culist.add(unit);
			}
		}
		for (SpoonFile f : virtualFiles) {
			try {
				culist.add(new CompilationUnit(IOUtils.toCharArray(f
						.getContent()), f.getName(), null));
			} catch (Exception e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		}
		return culist.toArray(new CompilationUnit[0]);
	}

	public CompilationUnitDeclaration[] getUnits(List<SpoonFile> files) {
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
		CompilationUnitDeclaration[] units = treeBuilderCompiler
				.buildUnits(getCompilationUnits(files));
		return units;
	}

}
