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
import spoon.Launcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class CompilationUnitWrapper extends CompilationUnit {

	private final JDTBasedSpoonCompiler jdtCompiler;

	CompilationUnitWrapper(JDTBasedSpoonCompiler jdtCompiler, CompilationUnit wrappedUnit) {
		super(null, wrappedUnit.fileName != null ? new String(
				wrappedUnit.fileName) : null, null,
				wrappedUnit.destinationPath != null ? new String(
						wrappedUnit.destinationPath) : null, false);
		this.jdtCompiler = jdtCompiler;
	}

	@Override
	public char[] getContents() {
		String s = new String(getFileName());
		if (jdtCompiler.loadedContent.containsKey(s)) {
			return jdtCompiler.loadedContent.get(s);
		}

		InputStream stream = null;
		if (jdtCompiler.factory != null && jdtCompiler.factory.CompilationUnit().getMap().containsKey(s)) {
			stream = jdtCompiler.getCompilationUnitInputStream(s);
		} else {
			try {
				stream = new FileInputStream(s);
			} catch (FileNotFoundException e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		}

		if (stream == null) {
			return super.getContents();
		}

		try {
			char[] content = IOUtils.toCharArray(stream);
			this.jdtCompiler.loadedContent.put(s, content);
			return content;
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}

		return super.getContents();
	}

}
