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

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import spoon.SpoonException;
import spoon.compiler.SpoonFile;

import java.util.ArrayList;
import java.util.List;

public class FileCompiler extends JDTBatchCompiler {

	public FileCompiler(JDTBasedSpoonCompiler jdtCompiler) {
		super(jdtCompiler);
	}

	/**
	 * returns the compilation units corresponding to the types in the factory.
	 */
	@Override
	public CompilationUnit[] getCompilationUnits() {
		List<SpoonFile> files = new ArrayList<>();
		files.addAll(jdtCompiler.sources.getAllJavaFiles());
		files.addAll(jdtCompiler.templates.getAllJavaFiles());
		List<CompilationUnit> culist = new ArrayList<>();
		for (SpoonFile f : files) {
			if (filesToBeIgnored.contains(f.getPath())) {
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
						.getContent()), fName, null));
			} catch (Exception e) {
				throw new SpoonException(e);
			}
		}
		return culist.toArray(new CompilationUnit[0]);
	}

}
