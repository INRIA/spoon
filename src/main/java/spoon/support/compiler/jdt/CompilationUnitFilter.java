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

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.compiler.builder.JDTBuilder;

/**
 * This interface is used by instances of {@link spoon.SpoonModelBuilder} to
 * filter particular {@link CompilationUnitDeclaration}s from the
 * {@link spoon.reflect.CtModel} created with
 * {@link spoon.SpoonModelBuilder#build(JDTBuilder)}.
 */
public interface CompilationUnitFilter {

	/**
	 * Tests if {@code cud} should be excluded from the
     * {@link spoon.reflect.CtModel} create by
     * {@link spoon.SpoonModelBuilder#build(JDTBuilder)}.
	 *
	 * @param cud
	 *      The {@link CompilationUnitDeclaration} that may or may not be
	 *      excluded.
	 * @param path
	 *      {@code cud}'s file path (see
	 *      {@link CompilationUnitDeclaration#getFileName()})
	 * @return {@code true} if and only if {@code cud} should be excluded,
	 *         {@code false} otherwise.
	 */
	boolean exclude(final CompilationUnitDeclaration cud, final String path);
}
