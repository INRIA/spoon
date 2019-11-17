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
package spoon;

import spoon.decompiler.CFRDecompiler;
import spoon.decompiler.Decompiler;
import spoon.support.compiler.FileSystemFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Creates a FileSystemFolder containing the decompiled byte from another input directory
 */
public class DecompiledResource extends FileSystemFolder {

	/**
	 * Creates a FileSystemFolder containing the decompiled .class and .jar from path.
	 * The actual folder is located at pathToDecompiledSources.
	 * @param path path to the folder containing the bytecode to decompile
	 * @param classpath additional classpath that could help decompilation.
	 * @param decompiler decompiler to use. If null the default will be used.
	 * @param pathToDecompiledSources output directory for decompiled sources.
	 */
	public DecompiledResource(String path, String[] classpath, Decompiler decompiler, String pathToDecompiledSources) {
		super(pathToDecompiledSources);

		final Decompiler decompilerToUse;
		if (decompiler == null) {
			decompilerToUse = new CFRDecompiler();
		} else {
			decompilerToUse = decompiler;
		}

		File output = new File(pathToDecompiledSources);
		output.mkdirs();

		try (Stream<Path> walk = Files.walk(Paths.get(path))) {

			walk.filter(Files::isRegularFile)
					.filter(x -> x.toString().endsWith(".class") || x.toString().endsWith(".jar"))
					.map(x -> x.toString())
					.forEach(
						f -> decompilerToUse.decompile(f, pathToDecompiledSources, classpath)
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
