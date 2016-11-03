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

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.SpoonException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

public class FileCompiler extends JDTBatchCompiler {
	
	protected List<SpoonFile> inputFiles;

	public FileCompiler(JDTBasedSpoonCompiler jdtCompiler) {
		super(jdtCompiler);
	}

	public FileCompiler(JDTBasedSpoonCompiler jdtCompiler, List<SpoonFile> files) {
		super(jdtCompiler);
		inputFiles = files;
	}

	/**
	 * returns the compilation units corresponding to the types in the factory.
	 */
	@Override
	public CompilationUnit[] getCompilationUnits() {
		if(inputFiles==null) {
			setInpuFiles(jdtCompiler.sources, jdtCompiler.templates);
		}
		
		List<CompilationUnit> culist = new ArrayList<>();
		for (SpoonFile f : inputFiles) {
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
						.getContent(), jdtCompiler.getEncoding()), fName, null));
			} catch (Exception e) {
				throw new SpoonException(e);
			}
		}
		return culist.toArray(new CompilationUnit[0]);
	}

	public List<SpoonFile> getInpuFiles() {
		return inputFiles;
	}

	public void setInpuFiles(List<SpoonFile> p_inpuFiles) {
		inputFiles = p_inpuFiles;
	}
	
	public void setInpuFiles(SpoonFolder... folders) {
		inputFiles = new ArrayList<>();
		for (SpoonFolder folder : folders)
		{
			inputFiles.addAll(folder.getAllJavaFiles());
		}
	}

}
