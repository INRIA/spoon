/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.util.List;
import java.util.Map;

/**
 * This interface defines the pretty printers.
 */
public interface PrettyPrinter {

	/**
	 * Prints the compilation unit of module-info, package-info or types.
	 */
	String printCompilationUnit(CtCompilationUnit compilationUnit);

	/**
	 * Prints the package info.
	 * It always resets the printing context at the beginning of this process.
	 */
	String printPackageInfo(CtPackage pack);

	/**
	 * Prints the module info.
	 * It always resets the printing context at the beginning of this process.
	 */
	String printModuleInfo(CtModule module);

	/**
	 * Prints the types of one compilation unit
	 * It always resets the printing context at the beginning of this process.
	 */
	String printTypes(CtType<?>... type);

	/**
	 * Prints an element. This method shall be called by the toString() method of an element.
	 * It is responsible for any initialization required to print an arbitrary element.
	 * @param element
	 * @return A string containing the pretty printed element (and descendants).
	 */
	String printElement(CtElement element);

	/**
	 * Gets the contents of the compilation unit.
	 */
	String getResult();

	/**
	 * Calculates the resulting source file for a list of types. The source
	 * compilation unit is required for calculating the line numbers mapping.
	 * It always resets the printing context at the beginning of this process.
	 */
	void calculate(CtCompilationUnit sourceCompilationUnit, List<CtType<?>> types);

	/**
	 * Gets the line number mapping between the generated code and the original
	 * code.
	 */
	Map<Integer, Integer> getLineNumberMapping();

	/** pretty-prints the element, call {@link #toString()} to get the result */
	String prettyprint(CtElement ctElement);
}
