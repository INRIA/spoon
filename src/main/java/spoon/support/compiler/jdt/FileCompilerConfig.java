/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.compiler.SpoonFile;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

public class FileCompilerConfig implements SpoonModelBuilder.InputType {

	/**
	 * Default implementation of which initializes {@link JDTBatchCompiler} by all sources and templates registered in {@link SpoonModelBuilder}
	 */
	public static final SpoonModelBuilder.InputType INSTANCE = new FileCompilerConfig((List<SpoonFile>) null) {
		@Override
		public List<SpoonFile> getFiles(JDTBatchCompiler compiler) {
			JDTBasedSpoonCompiler jdtCompiler = compiler.getJdtCompiler();
			List<SpoonFile> files = new ArrayList<>();
			files.addAll(jdtCompiler.sources.getAllJavaFiles());
			files.addAll(jdtCompiler.templates.getAllJavaFiles());
			return files;
		}
	};

	private final List<SpoonFile> files;

	public FileCompilerConfig(List<SpoonFile> files) {
		this.files = files;
	}

	@Override
	public void initializeCompiler(JDTBatchCompiler compiler) {
		JDTBasedSpoonCompiler jdtCompiler = compiler.getJdtCompiler();
		List<CompilationUnit> cuList = new ArrayList<>();

		for (SpoonFile f : getFiles(compiler)) {

			if (compiler.filesToBeIgnored.contains(f.getPath())) {
				continue;
			}

			String fName = f.isActualFile() ? f.getPath() : f.getName();
			Environment env = jdtCompiler.getEnvironment();
			cuList.add(new CompilationUnit(f.getContentChars(env), fName, env.getEncoding().displayName()));
		}

		compiler.setCompilationUnits(cuList.toArray(new CompilationUnit[0]));
	}

	protected List<SpoonFile> getFiles(JDTBatchCompiler compiler) {
		return files;
	}
}
