package spoon.support.compiler.jdt;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
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
	private JDTBasedSpoonCompiler jdtCompiler;
	private boolean useFactory;

	public JDTBatchCompiler(JDTBasedSpoonCompiler jdtCompiler, boolean useFactory) {
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

	private Set<String> ignoredFiles = new HashSet<>();

	public void ignoreFile(String filePath) {
		ignoredFiles.add(filePath);
	}

	@Override
	public CompilationUnit[] getCompilationUnits() {
		CompilationUnit[] units = super.getCompilationUnits();
		if (!ignoredFiles.isEmpty()) {
			List<CompilationUnit> l = new ArrayList<>();
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
		Set<String> fileNames = new HashSet<>();
		List<SpoonFile> virtualFiles = new ArrayList<>();
		for (SpoonFile f : files) {
			if (!f.isActualFile()) {
				virtualFiles.add(f);
			} else {
				fileNames.add(f.getPath());
			}
		}

		List<CompilationUnit> culist = new ArrayList<>();
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
				Launcher.logger.error(e.getMessage(), e);
			}
		}
		return culist.toArray(new CompilationUnit[0]);
	}

	public CompilationUnitDeclaration[] getUnits(List<SpoonFile> files)
			throws Exception {
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