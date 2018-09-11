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
package spoon.reflect.cu;

import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.Experimental;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Defines a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public interface CompilationUnit extends FactoryAccessor, SourcePositionHolder, Serializable {

	enum UNIT_TYPE {
		TYPE_DECLARATION,
		PACKAGE_DECLARATION,
		MODULE_DECLARATION,
		UNKNOWN
	}

	/**
	 * Returns the declaration type of the compilation unit.
	 */
	UNIT_TYPE getUnitType();

	/**
	 * Gets the file that corresponds to this compilation unit if any (contains
	 * the source code).
	 */
	File getFile();

	/**
	 * Sets the file that corresponds to this compilation unit.
	 */
	void setFile(File file);

	/**
	 * @return array of offsets in the origin source file, where occurs line separator
	 */
	int[] getLineSeparatorPositions();

	/**
	 * @param lineSeparatorPositions array of offsets in the origin source file, where occurs line separator
	 */
	void setLineSeparatorPositions(int[] lineSeparatorPositions);

	/**
	 * Gets all binary (.class) files that corresponds to this compilation unit
	 * and have been created by calling
	 * {@link spoon.SpoonModelBuilder#compile(spoon.SpoonModelBuilder.InputType...)}.
	 */
	List<File> getBinaryFiles();

	/**
	 * Gets all the types declared in this compilation unit.
	 */
	List<CtType<?>> getDeclaredTypes();

	/**
	 * Sets the types declared in this compilation unit.
	 */
	void setDeclaredTypes(List<CtType<?>> types);

	/**
	 * Add a type to the list of declared types
	 */
	void addDeclaredType(CtType type);

	/**
	 * Gets the declared module if the compilationUnit is "module-info.java"
	 */
	CtModule getDeclaredModule();

	/**
	 * Sets the declared module if the compilationUnit is "module-info.java"
	 */
	void setDeclaredModule(CtModule module);

	/**
	 * Gets the package declared in the top level type of the compilation unit.
	 */
	CtPackage getDeclaredPackage();

	/**
	 * Sets the package declared in the top level type of the compilation unit.
	 */
	void setDeclaredPackage(CtPackage ctPackage);

	/**
	 * Searches and returns the main type (the type which has the same name as
	 * the file).
	 */
	CtType<?> getMainType();

	/**
	 * Gets the original source code as a string.
	 */
	String getOriginalSourceCode();

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * given index.
	 *
	 * @param index
	 * 		an arbitrary index in the source code
	 * @return the index where the line starts
	 */
	int beginOfLineIndex(int index);

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * next line of the given index.
	 *
	 * @param index
	 * 		an arbitrary index in the source code
	 * @return the index where the next line starts
	 */
	int nextLineIndex(int index);

	/**
	 * Gets the number of tabulations for a given line.
	 *
	 * @param index
	 * 		the index where the line starts in the source code
	 * @return the number of tabs for this line
	 */
	int getTabCount(int index);

	/**
	 * Get the imports computed for this CU.
	 * WARNING: This method is tagged as experimental, as its signature and/or usage might change in future release.
	 * @return All the imports from the original source code
	 */
	@Experimental
	Set<CtImport> getImports();

	/**
	 * Set the imports of this CU
	 * WARNING: This method is tagged as experimental, as its signature and/or usage might change in future release.
	 * @param imports All the imports of the original source code
	 */
	@Experimental
	void setImports(Set<CtImport> imports);

}
