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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.SpoonException;
import spoon.compiler.SpoonFile;

public class ConfigurableCompiler extends JDTBatchCompiler {
	protected CompilationUnit[] compilationUnits;

	public ConfigurableCompiler(JDTBasedSpoonCompiler p_jdtCompiler) {
		super(p_jdtCompiler);
	}

	public ConfigurableCompiler(JDTBasedSpoonCompiler p_jdtCompiler, OutputStream p_outWriter, OutputStream p_errWriter) {
		super(p_jdtCompiler, p_outWriter, p_errWriter);
	}

	@Override
	public CompilationUnit[] getCompilationUnits() {
		return compilationUnits;
	}

	public void setCompilationUnits(CompilationUnit[] compilationUnits) {
		this.compilationUnits = compilationUnits;
	}

	public void setInputFiles(List<SpoonFile> files) {
		List<CompilationUnit> culist = new ArrayList<>(files.size());
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
						.getContent(), jdtCompiler.encoding), fName, null));
			} catch (Exception e) {
				throw new SpoonException(e);
			}
		}
		this.compilationUnits = culist.toArray(new CompilationUnit[0]);
	}
}
