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

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

import org.apache.commons.io.IOUtils;
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

	public FileCompilerConfig(SpoonFolder folder) {
		this(folder.getAllJavaFiles());
	}

	@Override
	public void initializeCompiler(JDTBatchCompiler compiler) {
		JDTBasedSpoonCompiler jdtCompiler = compiler.getJdtCompiler();
		List<CompilationUnit> culist = new ArrayList<>();
		for (SpoonFile f : getFiles(compiler)) {
			if (compiler.filesToBeIgnored.contains(f.getPath())) {
				continue;
			}
			try {
				String fName = "";
				if (f.isActualFile()) {
					fName = f.getPath();
				} else {
					fName = f.getName();
				}
				culist.add(new CompilationUnit(IOUtils.toCharArray(f
						.getContent(), jdtCompiler.encoding), fName, null));
			} catch (Exception e) {
				throw new SpoonException(e);
			}
		}
		compiler.setCompilationUnits(culist.toArray(new CompilationUnit[0]));
	}

	protected List<SpoonFile> getFiles(JDTBatchCompiler compiler) {
		return files;
	}
}
