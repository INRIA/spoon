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
package spoon.reflect.factory;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
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
	 * Creates a new package (see also {@link #getOrCreate(String)}).
	 *
	 * @param parent
	 * 		the parent package (can be null)
	 * @param simpleName
	 * 		the package's simple name (no dots)
	 * @return the newly created package
	 */
	public CtPackage create(CtPackage parent, String simpleName) {
		return getOrCreate(parent.toString() + CtPackage.PACKAGE_SEPARATOR + simpleName);
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
		if (qualifiedName.contains(CtType.INNERTTYPE_SEPARATOR)) {
			throw new RuntimeException("Invalid package name " + qualifiedName);
		}
		StringTokenizer token = new StringTokenizer(qualifiedName, CtPackage.PACKAGE_SEPARATOR);
		CtPackage current = factory.getModel().getRootPackage();
		if (token.hasMoreElements()) {
			current = current.getPackage(token.nextToken());
			while (token.hasMoreElements() && current != null) {
				current = current.getPackage(token.nextToken());
			}
		}

		return current;
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

