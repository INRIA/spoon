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

/**
 * This interface is used by instances of {@link spoon.SpoonModelBuilder} to
 * exclude particular {@link spoon.reflect.cu.CompilationUnit}s while
 * generating a {@link spoon.reflect.CtModel} with
 * {@link spoon.SpoonModelBuilder#build(spoon.compiler.builder.JDTBuilder)}.
 *
 * This interface is useful for large sized software system where traversing
 * all files takes several minutes. Unlike the approach of adding a subset of
 * the files to examine, filtering unwanted files produces a more precise
 * {@link spoon.reflect.CtModel} since all files will be compiled (but not
 * transformed).
 */
public interface CompilationUnitFilter {

	/**
	 * Tests if the file with path {@code path} should be excluded from the
     * {@link spoon.reflect.CtModel} create by
     * {@link spoon.SpoonModelBuilder#build(spoon.compiler.builder.JDTBuilder)}.
	 *
	 * @param path
	 *      Path to the file that may or may not be excluded.
	 * @return {@code true} if and only if {@code path} should be excluded,
	 *         {@code false} otherwise.
	 */
	boolean exclude(final String path);
}
