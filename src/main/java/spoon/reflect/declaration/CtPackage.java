/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtPackageReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.Set;

import static spoon.reflect.path.CtRole.SUB_PACKAGE;
import static spoon.reflect.path.CtRole.CONTAINED_TYPE;

/**
 * This element defines a package declaration. The packages are represented by a
 * tree.
 */
public interface CtPackage extends CtNamedElement, CtShadowable {

	/**
	 * The separator for a string representation of a package.
	 */
	String PACKAGE_SEPARATOR = ".";

	char PACKAGE_SEPARATOR_CHAR = '.';

	/**
	 * The name for the top level package.
	 */
	String TOP_LEVEL_PACKAGE_NAME = "unnamed package";

	/**
	 * Gets the declaring module.
	 */
	@DerivedProperty
	CtModule getDeclaringModule();

	/**
	 * Gets the declaring package of the current one. Returns null if the package is not yet in another one.
	 */
	@DerivedProperty
	CtPackage getDeclaringPackage();

	/**
	 * Searches a child package by name.
	 *
	 * @param name
	 * 		the simple name of searched package
	 * @return the found package or null
	 */
	@PropertyGetter(role = SUB_PACKAGE)
	CtPackage getPackage(String name);

	/**
	 * Gets the set of included child packages.
	 */
	@PropertyGetter(role = SUB_PACKAGE)
	Set<CtPackage> getPackages();

	/**
	 * Returns the fully qualified name of this package. This is also known as
	 * the package's <i>canonical</i> name.
	 *
	 * @return the fully qualified name of this package, or the empty string if
	 * this is the unnamed package
	 */
	String getQualifiedName();

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	@Override
	@DerivedProperty
	CtPackageReference getReference();

	/**
	 * Finds a top-level type by name.
	 *
	 * @return the found type or null
	 */
	@PropertyGetter(role = CONTAINED_TYPE)
	<T extends CtType<?>> T getType(String simpleName);

	/**
	 * Returns the set of the top-level types in this package.
	 */
	@PropertyGetter(role = CONTAINED_TYPE)
	Set<CtType<?>> getTypes();

	/**
	 * Adds a type to this package.
	 */
	@PropertySetter(role = CONTAINED_TYPE)
	<T extends CtPackage> T addType(CtType<?> type);

	/**
	 * Removes a type from this package.
	 */
	@PropertySetter(role = CONTAINED_TYPE)
	void removeType(CtType<?> type);

	/**
	 * Sets the children defined in this package
	 *
	 * @param pack
	 * 		new set of child packages
	 */
	@PropertySetter(role = SUB_PACKAGE)
	<T extends CtPackage> T setPackages(Set<CtPackage> pack);

	/**
	 * add a subpackage
	 *
	 * @param pack
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = SUB_PACKAGE)
	<T extends CtPackage> T addPackage(CtPackage pack);

	/**
	 * remove a subpackage
	 *
	 * @param pack
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = SUB_PACKAGE)
	boolean removePackage(CtPackage pack);

	/**
	 * Sets the types defined in the package.
	 *
	 * @param types
	 * 		new Set of types
	 */
	@PropertySetter(role = CONTAINED_TYPE)
	<T extends CtPackage> T setTypes(Set<CtType<?>> types);

	@Override
	CtPackage clone();

	/**
	 * Returns {@code true} if this is an <i>unnamed</i> Java package.
	 * See JLS §7.4.2. Unnamed Packages.
	 */
	boolean isUnnamedPackage();

	/**
	* @return true if the package contains a package-info.java file
	*/
	boolean hasPackageInfo();

	/**
	 * @return true if the package contains no types nor any other packages
	 */
	boolean isEmpty();
}
