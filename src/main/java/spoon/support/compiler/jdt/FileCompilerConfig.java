/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import java.io.InputStream;
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
		List<CompilationUnit> cuList = new ArrayList<>();
		InputStream inputStream = null;

		try {
			for (SpoonFile f : getFiles(compiler)) {

				if (compiler.filesToBeIgnored.contains(f.getPath())) {
					continue;
				}

				String fName = f.isActualFile() ? f.getPath() : f.getName();
				inputStream = f.getContent();
				char[] content = IOUtils.toCharArray(inputStream, jdtCompiler.encoding);
				cuList.add(new CompilationUnit(content, fName, null));
				IOUtils.closeQuietly(inputStream);
			}
		} catch (Exception e) {
			IOUtils.closeQuietly(inputStream);
			throw new SpoonException(e);
		}

		compiler.setCompilationUnits(cuList.toArray(new CompilationUnit[0]));
	}

	protected List<SpoonFile> getFiles(JDTBatchCompiler compiler) {
		return files;
	}
}
