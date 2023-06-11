/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import spoon.SpoonException;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.reference.CtPackageReference;


/**
 * The {@link CtPackage} sub-factory.
 */
public class PackageFactory extends SubFactory {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new package sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public PackageFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a reference to an existing package.
	 * The simple name of the reference will be the FQN of the given package
	 */
	public CtPackageReference createReference(CtPackage pack) {
		if (pack == null) {
			throw new IllegalArgumentException();
		}
		return createReference(pack.getQualifiedName());
	}

	/**
	 * Creates a reference to a package by using its Java runtime
	 * representation. The simple name of the reference will be the FQN of the given package
	 *
	 * @param pack
	 * 		a runtime package
	 * @return reference to the package
	 */
	public CtPackageReference createReference(Package pack) {
		return createReference(pack.getName());
	}

	/**
	 * Returns a reference on the top level package.
	 */
	public CtPackageReference topLevel() {
		return factory.getModel().getRootPackage().getReference();
	}

	/**
	 * Creates a reference to a package. The given name has to be a fully qualified name.
	 *
	 * @param name
	 * 		full name of the package to reference
	 */
	public CtPackageReference createReference(String name) {
		CtPackageReference ref = factory.Core().createPackageReference();
		ref.setSimpleName(name);
		return ref;
	}

	/**
	 * Creates a package declaration of a package.
	 *
	 * @param packageRef a reference to a package
	 */
	public CtPackageDeclaration createPackageDeclaration(CtPackageReference packageRef) {
		CtPackageDeclaration pd = factory.Core().createPackageDeclaration();
		pd.setReference(packageRef);
		return pd;
	}

	/**
	 * Creates a new package (see also {@link #getOrCreate(String)}).
	 *
	 * @param parent
	 * 		the parent package (can be null)
	 * @param simpleName
	 * 		the package's simple name (no dots)
	 * @return the newly created package
	 */
	public CtPackage create(CtPackage parent, String simpleName) {
		if (parent == null) {
			return getOrCreate(simpleName);
		} else {
			return getOrCreate(parent + CtPackage.PACKAGE_SEPARATOR + simpleName);
		}
	}

	/**
	 * Gets or creates a package for the unnamed module
	 *
	 * @param qualifiedName
	 * 		the full name of the package
	 *
	 */
	public CtPackage getOrCreate(String qualifiedName) {
		return this.getOrCreate(qualifiedName, factory.getModel().getUnnamedModule());
	}

	/**
	 * Gets or creates a package and make its parent the given module
	 *
	 * @param qualifiedName
	 * 		the full name of the package
	 *
	 * @param rootModule
	 * 		The parent module of the package
	 */
	public CtPackage getOrCreate(String qualifiedName, CtModule rootModule) {
		if (qualifiedName.isEmpty()) {
			return rootModule.getRootPackage();
		}
		StringTokenizer token = new StringTokenizer(qualifiedName, CtPackage.PACKAGE_SEPARATOR);
		CtPackage last = rootModule.getRootPackage();

		while (token.hasMoreElements()) {
			String name = token.nextToken();
			CtPackage next = last.getPackage(name);
			if (next == null) {
				next = factory.Core().createPackage();
				next.setSimpleName(name);
				last.addPackage(next);
			}
			last = next;
		}

		return last;
	}

	/**
	 * Gets a package.
	 *
	 * @param qualifiedName
	 * 		the package to search
	 * @return a found package or null
	 */
	public CtPackage get(String qualifiedName) {

		// Find package with the most contained types. If a module exports package "foo.bar" and the
		// other "foo.bar.baz", *both modules* will contain a "foo.bar" package in spoon. As
		// javac (yes, javac. This is not in the spec but would be a colossal pain for many things if
		// it were ever allowed) does not allow overlapping packages, one of them will be a synthetic
		// package spoon creates as a parent for "foo.bar.baz". This package will *never* have any
		// types in it.
		// JDT does allow it, but the chances of a real-word program actually having overlap are slim.
		//
		// However, if the "foo.bar.baz" module is found first in "getAllModules()", we will find the
		// synthetic "foo.bar" package in it. As that one contains no types, all queries for types in
		// it will fail!
		//
		// To solve this we look for the package with at least one contained type, effectively
		// filtering out any synthetic packages.
		int foundPackageCount = 0;
		CtPackage packageWithTypes = null;
		CtPackage lastNonNullPackage = null;
		for (CtModule module : factory.getModel().getAllModules()) {
			CtPackage aPackage = getPackageFromModule(qualifiedName, module);
			if (aPackage == null) {
				continue;
			}
			lastNonNullPackage = aPackage;
			if (aPackage.hasTypes()) {
				packageWithTypes = aPackage;
				foundPackageCount++;
			}
		}

		if (foundPackageCount > 1) {
			throw new SpoonException(
					"Ambiguous package name detected. If you believe the code you analyzed is correct, please"
							+ " file an issue and reference https://github.com/INRIA/spoon/issues/4051. "
							+ "Error details: Found " + foundPackageCount + " non-empty packages with name "
							+ "'" + qualifiedName + "'"
			);
		}

		// Return a non synthetic package but if *no* package had any types we return the last one.
		// This ensures that you can also retrieve empty packages with this API
		return packageWithTypes != null ? packageWithTypes : lastNonNullPackage;
	}

	/**
	 * @param qualifiedName Qualified name of a package.
	 * @param ctModule A module in which to search for the package.
	 * @return The package if found in this module, otherwise null.
	 */
	private static CtPackage getPackageFromModule(String qualifiedName, CtModule ctModule) {
		int index = 0;
		int nextIndex;
		CtPackage current = ctModule.getRootPackage();

		if (qualifiedName.isEmpty() || current == null) {
			return current;
		}

		while ((nextIndex = qualifiedName.indexOf(CtPackage.PACKAGE_SEPARATOR_CHAR, index)) >= 0) {
			current = current.getPackage(qualifiedName.substring(index, nextIndex));
			index = nextIndex + 1;

			if (current == null) {
				return null;
			}
		}

		return current.getPackage(qualifiedName.substring(index));
	}

	/**
	 * Gets the list of all created packages. It includes all the top-level
	 * packages and their sub-packages.
	 */
	public Collection<CtPackage> getAll() {
		return factory.getModel().getAllPackages();
	}

	/**
	 * Return the unnamed top-level package.
	 */
	public CtPackage getRootPackage() {
		return factory.getModel().getRootPackage();
	}

	private List<CtPackage> getSubPackageList(CtPackage pack) {
		List<CtPackage> packs = new ArrayList<>();
		packs.add(pack);
		for (CtPackage p : pack.getPackages()) {
			packs.addAll(getSubPackageList(p));
		}
		return packs;
	}

}

