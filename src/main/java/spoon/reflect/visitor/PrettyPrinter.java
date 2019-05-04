/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtCompilationUnit;
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
}
