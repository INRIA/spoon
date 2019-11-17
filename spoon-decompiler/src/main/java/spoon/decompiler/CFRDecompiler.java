/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.decompiler;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.benf.cfr.reader.Main;
import spoon.support.Experimental;

@Experimental
public class CFRDecompiler implements Decompiler {

	@Override
	public void decompile(String inputPath, String outputPath, String[] classpath) {
		if (classpath != null && classpath.length > 0) {
			Main.main(new String[]{inputPath, "--outputdir", outputPath, "--extraclasspath", Arrays.stream(classpath).collect(Collectors.joining(":"))});
		} else {
			Main.main(new String[]{inputPath, "--outputdir", outputPath});
		}
	}
}
