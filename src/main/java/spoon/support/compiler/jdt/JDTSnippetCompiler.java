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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.compiler.SpoonFile;
import spoon.compiler.builder.AdvancedOptions;
import spoon.compiler.builder.ClasspathOptions;
import spoon.compiler.builder.ComplianceOptions;
import spoon.compiler.builder.JDTBuilder;
import spoon.compiler.builder.JDTBuilderImpl;
import spoon.compiler.builder.SourceOptions;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.SnippetCompilationError;
import spoon.support.compiler.VirtualFile;

public class JDTSnippetCompiler extends JDTBasedSpoonCompiler {

	private final AtomicLong snippetNumber = new AtomicLong(0);
	public static final String SNIPPET_FILENAME_PREFIX = JDTSnippetCompiler.class.getName() + "_spoonSnippet_";

	private CompilationUnit snippetCompilationUnit;

	public JDTSnippetCompiler(Factory factory, String contents) {
		super(factory);
		//give the Virtual file the unique name so JDTCommentBuilder.spoonUnit can be correctly initialized
		addInputSource(new VirtualFile(contents, SNIPPET_FILENAME_PREFIX + (snippetNumber.incrementAndGet())));
	}

	@Override
	public boolean build() {
		return build(null);
	}

	@Override
	public boolean build(JDTBuilder builder) {
		if (factory == null) {
			throw new SpoonException("Factory not initialized");
		}

		boolean srcSuccess;
		List<SpoonFile> allFiles = sources.getAllJavaFiles();
		factory.getEnvironment().debugMessage("compiling sources: " + allFiles);
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();
		try {
			srcSuccess = buildSources(builder);
		} finally {
			//remove snippet compilation unit from the cache (to clear memory) and remember it so client can use it
			for (SpoonFile spoonFile : allFiles) {
				if (spoonFile.getName().startsWith(SNIPPET_FILENAME_PREFIX)) {
					snippetCompilationUnit = factory.CompilationUnit().removeFromCache(spoonFile.getName());
				}
			}
		}
		reportProblems(factory.getEnvironment());
		factory.getEnvironment().debugMessage("compiled in " + (System.currentTimeMillis() - t) + " ms");
		return srcSuccess;
	}

	@Override
	protected boolean buildSources(JDTBuilder jdtBuilder) {
		if (sources.getAllJavaFiles().isEmpty()) {
			return true;
		}
		JDTBatchCompiler batchCompiler = createBatchCompiler(sources.getAllJavaFiles());

		File source = createTmpJavaFile(new File("."));
		String[] args;
		if (jdtBuilder == null) {
			String[] sourceClasspath = getSourceClasspath();
			args = new JDTBuilderImpl() //
					.classpathOptions(new ClasspathOptions().encoding(this.encoding).classpathFromListOrClassLoader(sourceClasspath)) //
					.complianceOptions(new ComplianceOptions().compliance(javaCompliance)) //
					.advancedOptions(new AdvancedOptions().preserveUnusedVars().continueExecution().enableJavadoc()) //
					.sources(new SourceOptions().sources(source.getPath())) //
					.build();
		} else {
			args = jdtBuilder.build();
		}

		getFactory().getEnvironment().debugMessage("build args: " + Arrays.toString(args));

		batchCompiler.configure(args);

		CompilationUnitDeclaration[] units = batchCompiler.getUnits(sources.getAllJavaFiles());

		if (source.exists()) {
			source.delete();
		}

		// here we build the model
		buildModel(units);

		return getProblems().size() == 0;
	}

	@Override
	protected void report(Environment environment, CategorizedProblem problem) {
		throw new SnippetCompilationError(problem.getMessage() + "at line " + problem.getSourceLineNumber());
	}

	/**
	 * @return CompilationUnit which was produced by compiling of this snippet
	 */
	public CompilationUnit getSnippetCompilationUnit() {
		return snippetCompilationUnit;
	}
}
