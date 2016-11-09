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
package spoon.reflect.factory;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.Import;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.cu.ImportImpl;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * A factory to create some evaluation utilities on the Spoon metamodel.
 */
public class CompilationUnitFactory extends SubFactory {

	/**
	 * Creates the evaluation factory.
	 */
	public CompilationUnitFactory(Factory factory) {
		super(factory);
	}

	private transient Map<String, CompilationUnit> cachedCompilationUnits = new TreeMap<>();

	/**
	 * Gets the compilation unit map.
	 *
	 * @return a map (path -&gt; {@link CompilationUnit})
	 */
	public Map<String, CompilationUnit> getMap() {
		return cachedCompilationUnits;
	}

	/**
	 * Creates a compilation unit with no associated files.
	 */
	public CompilationUnit create() {
		CompilationUnit cu = factory.Core().createCompilationUnit();
		return cu;
	}

	/**
	 * Creates or gets a compilation unit for a given file path.
	 */
	public CompilationUnit create(String filePath) {
		CompilationUnit cu = cachedCompilationUnits.get(filePath);
		if (cu == null) {
			if (filePath.startsWith(JDTSnippetCompiler.SNIPPET_FILENAME_PREFIX)) {
				cu = factory.Core().createCompilationUnit();
				//put the virtual compilation unit of code snippet into cache too, so the JDTCommentBuilder can found it
				cachedCompilationUnits.put(filePath, cu);
				return cu;
			}
			cu = factory.Core().createCompilationUnit();
			cu.setFile(new File(filePath));
			cachedCompilationUnits.put(filePath, cu);
		}
		return cu;
	}

	/**
	 * Removes compilation unit from the cache and returns it
	 * Used by JDTSnippetCompiler to remove processed snippet from the cache
	 * @param filePath
	 * @return a cached compilation unit or null
	 */
	public CompilationUnit removeFromCache(String filePath) {
		return cachedCompilationUnits.remove(filePath);
	}

	/**
	 * Creates an import for the given type.
	 */
	public Import createImport(CtTypeReference<?> type) {
		return new ImportImpl(type);
	}

	/**
	 * Creates an import for the given type.
	 */
	public Import createImport(Class<?> type) {
		return new ImportImpl(factory.Type().createReference(type));
	}

	/**
	 * Creates an import for the given field.
	 */
	public Import createImport(CtFieldReference<?> field) {
		return new ImportImpl(field);
	}

	/**
	 * Creates an import for the given package.
	 */
	public Import createImport(CtPackageReference pack) {
		return new ImportImpl(pack);
	}

}
