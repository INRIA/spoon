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
package spoon.reflect.visitor;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;

import java.util.List;

/**
 * Used to compute the imports required to write readable code with no fully qualified names.
 */
public interface ImportScanner {

	/**
	 * Computes all imports of a {@link spoon.reflect.declaration.CtElement}
	 */
	void computeImports(CtElement element);

	/**
	 * Computes import of a {@link CompilationUnit}
	 */
	void computeImports(CompilationUnit cu);

	void setFactory(Factory factory);

	Factory getFactory();

	/**
	 * Use computeImports before getting the different imports.
	 *
	 * @return the list of computed imports or an empty collection if not imports has been computed.
	 */
	List<CtImport> getAllImports();

	/**
	 * Sets the list of imports. Note that this list might be modified when getting the imports, by adding new imports.
	 * @param importList A list of imports.
	 */
	void setImports(List<CtImport> importList);

	/**
	 * Add a new import
	 * @param ctImport
	 */
	void addImport(CtImport ctImport);

	void removeImport(CtImport ctImport);

	/**
	 * Returns true if and only if the given reference belongs to the list of imports.
	 */
	boolean isEffectivelyImported(CtReference reference);

	/**
	 * Returns true iff the given reference has been imported {@link #isEffectivelyImported(CtReference)}
	 * OR if the given reference is considered as implicitely imported (e.g. a reference from java.lang or from
	 * the current target type)
	 */
	boolean isImported(CtReference ref);

	/**
	 * Checks if the fully qualified name should be printed
	 * This can return false even if {@link #isImported(CtReference)} returns true:
	 * for example, java.lang could be printed even if it's always considered as imported.
	 */
	boolean printQualifiedName(CtReference ref);

	void reset();

	void setOriginalImports(List<CtImport> originalImports);
}
