/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import java.util.*;

import spoon.reflect.declaration.*;
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
		return factory.getModel().getUnnamedModule().getRootPackage().getReference();
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
		return this.getOrCreate(qualifiedName, findModuleByPackage(qualifiedName));
	}

	private CtModule findModuleByPackage(String qualifiedName) {
		return findJavaModule(qualifiedName).map(Module::getName).map(factory.Module()::getOrCreate).orElseGet(factory.getModel()::getUnnamedModule);
	}

	private static Optional<Module> findJavaModule(String qualifiedName) {
		return ModuleLayer.boot().modules().stream().filter(module -> module.getPackages().contains(qualifiedName)).findFirst();
	}

	/**
	 * Gets or creates a package and make its parent the given module
	 *
	 * @param qualifiedName
	 * 		the full name of the package
	 *
	 * @param module
	 * 		The parent module of the package
	 */
	public CtPackage getOrCreate(String qualifiedName, CtModule module) {
		CtPackage known = module.getPackage(qualifiedName);
		if(known != null){
			return known;
		}

		StringTokenizer token = new StringTokenizer(qualifiedName, CtPackage.PACKAGE_SEPARATOR);
		CtPackage fresh = module.getRootPackage();
		while (token.hasMoreElements()) {
			String name = token.nextToken();
			fresh = createPackage(module, fresh, name);
		}

		return fresh;
	}

	private CtPackage createPackage(CtModule module, CtPackage parent, String name) {
		CtPackage known = parent != null ? parent.getPackage(name)
				: module.getPackage(name);
		if(known != null){
			return known;
		}

		CtPackage fresh = factory.Core().createPackage(module);
		fresh.setSimpleName(name);
		if(parent != null){
			parent.addPackage(fresh);
		}

		return fresh;
	}

	/**
	 * Return the unnamed top-level package.
	 */
	public CtPackage getRootPackage() {
		return factory.getModel().getUnnamedModule().getRootPackage();
	}

	/**
	 * Gets a package.
	 *
	 * @param qualifiedName
	 * 		the package to search
	 * @return a found package or null
	 */
	public CtPackage get(String qualifiedName) {
		return factory.getModel().getPackage(qualifiedName);
	}

	/**
	 * Gets the list of all created packages. It includes all the top-level
	 * packages and their sub-packages.
	 */
	public Collection<CtPackage> getAll() {
		return factory.getModel().getAllPackages();
	}
}

