/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.compiler.SpoonFile;
import spoon.compiler.builder.JDTBuilder;
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
		return buildUnitsAndModel(jdtBuilder, sources, getSourceClasspath(), "snippet ");
	}

	@Override
	protected void report(Environment environment, CategorizedProblem problem) {
		if (problem.isError()) {
			throw new SnippetCompilationError(problem.getMessage() + "at line " + problem.getSourceLineNumber());
		}
	}

	/**
	 * @return CompilationUnit which was produced by compiling of this snippet
	 */
	public CompilationUnit getSnippetCompilationUnit() {
		return snippetCompilationUnit;
	}
}
