/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.declaration;

import java.util.Set;

import spoon.reflect.reference.CtPackageReference;

/**
 * This element defines a package declaration. The packages are represented by a
 * tree.
 */
public interface CtPackage extends CtNamedElement {

	/**
	 * The separator for a string representation of a package.
	 */
	public static final String PACKAGE_SEPARATOR = ".";

	/**
	 * The name for the top level package (default is "unnamed package").
	 */
	public static final String TOP_LEVEL_PACKAGE_NAME = "unnamed package";

	/**
	 * Gets the declaring package of the current one.
	 */
	CtPackage getDeclaringPackage();

	/**
	 * Searches a child package by name.
	 * 
	 * @param name
	 *            the simple name of searched package
	 * @return the found package or null
	 */
	CtPackage getPackage(String name);

	/**
	 * Gets the set of included child packages.
	 */
	Set<CtPackage> getPackages();

	/**
	 * Returns the fully qualified name of this package. This is also known as
	 * the package's <i>canonical</i> name.
	 * 
	 * @return the fully qualified name of this package, or the empty string if
	 *         this is the unnamed package
	 */
	String getQualifiedName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	CtPackageReference getReference();

	/**
	 * Finds a top-level type by name.
	 * 
	 * @return the found type or null
	 */
	CtSimpleType<?> getType(String simpleName);

	/**
	 * Returns the set of the top-level types in this package.
	 */
	Set<CtSimpleType<?>> getTypes();

	/**
	 * Sets the childs defined in this package
	 * 
	 * @param pack
	 *            new set of child packages
	 */
	void setPackages(Set<CtPackage> pack);

	/**
	 * Sets the types defined in the package.
	 * 
	 * @param types
	 *            new Set of types
	 */
	void setTypes(Set<CtSimpleType<?>> types);
}
