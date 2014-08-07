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

package spoon.reflect.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;

/**
 * The {@link CtPackage} sub-factory.
 */
public class PackageFactory extends SubFactory implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * top-level package are indexed by name to improve search
	 */
	private Map<String, CtPackage> packages = new TreeMap<String, CtPackage>();

	/**
	 * Creates a new package sub-factory.
	 * 
	 * @param factory
	 *            the parent factory
	 */
	public PackageFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a reference to an existing package.
	 *
	 * @param pack the pakcage to reference
	 *
	 * @return a new package reference
	 *
	 * @throws java.lang.IllegalArgumentException if the given package is null
	 */
	public CtPackageReference createReference(CtPackage pack) {
		if (pack==null) { 
			throw new IllegalArgumentException();
		}
		return createReference(pack.getQualifiedName());
	}

	/**
	 * Creates a reference to a package by using its Java runtime
	 * representation.
	 * 
	 * @param pack
	 *            a runtime package
	 *
	 * @return reference to the package
	 */
	public CtPackageReference createReference(Package pack) {
		return createReference(pack.getName());
	}

	CtPackageReference topLevel;
	
	/**
	 * Returns a reference on the top level package.
	 *
	 * @return the top level package referenc
	 */
	public CtPackageReference topLevel() {
		if(topLevel==null) {
			topLevel=createReference(CtPackage.TOP_LEVEL_PACKAGE_NAME);
		}
		return topLevel;
	}
	
	/**
	 * Creates a reference to a package.
	 * 
	 * @param name
	 *            full name of the package to reference
	 *
	 * @return a new package reference
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
	 *            the parent package (can be null)
	 * @param simpleName
	 *            the package's simple name (no dots)
	 *
	 * @return the newly created package
	 */
	public CtPackage create(CtPackage parent, String simpleName) {
		return getOrCreate(parent.toString() + CtPackage.PACKAGE_SEPARATOR
				+ simpleName);
	}

	/**
	 * Gets or creates a package.
	 * 
	 * @param qualifiedName
	 *            the full name of the package
	 *
	 * @return a new package or the existing one
	 */
	public CtPackage getOrCreate(String qualifiedName) {
		StringTokenizer token = new StringTokenizer(qualifiedName,
				CtPackage.PACKAGE_SEPARATOR);
		CtPackage last = null;

		if (token.hasMoreElements()) {
			String name = token.nextToken();
			if (name.length() < 0) {
				name = CtPackage.TOP_LEVEL_PACKAGE_NAME;
			}
			if (packages.containsKey(name)) {
				last = packages.get(name);
			} else {
				last = factory.Core().createPackage();
				last.setSimpleName(name);
				register(last);
			}
		}

		while (token.hasMoreElements()) {
			String name = token.nextToken();
			CtPackage next = last.getPackage(name);
			if (next == null) {
				next = factory.Core().createPackage();
				next.setSimpleName(name);
				last.getPackages().add(next);
				next.setParent(last);
			}
			last = next;
		}

		return last;
	}

	/**
	 * Gets a created package.
	 * 
	 * @param qualifiedName
	 *            the package to search
	 *
	 * @return a found package or null of not found
	 */
	public CtPackage get(String qualifiedName) {
		if (qualifiedName.contains(CtType.INNERTTYPE_SEPARATOR)) {
			throw new RuntimeException("Invalid package name " + qualifiedName);
		}
		StringTokenizer token = new StringTokenizer(qualifiedName,
				CtPackage.PACKAGE_SEPARATOR);
		CtPackage current = null;
		if (token.hasMoreElements()) {
			current = packages.get(token.nextElement());
			while (token.hasMoreElements() && current != null) {
				current = current.getPackage(token.nextToken());
			}
		}
		

		return current;
	}

	/**
	 * Gets the list of all created packages. It includes all the top-level
	 * packages and their sub-packages.
	 *
	 * @return the Collection of all packages
	 */
	public Collection<CtPackage> getAll() {
		Collection<CtPackage> packs = new ArrayList<CtPackage>();
		for (CtPackage pack : packages.values()) {
			packs.addAll(getSubPackageList(pack));
		}
		return packs;
	}

	/**
	 * Gets the list of all created root packages
	 *
	 * @return a Collection of all root packages
	 */
	public Collection<CtPackage> getAllRoots() {
		return packages.values();
	}

	private List<CtPackage> getSubPackageList(CtPackage pack) {
		List<CtPackage> packs = new ArrayList<CtPackage>();
		packs.add(pack);
		for (CtPackage p : pack.getPackages()) {
			packs.addAll(getSubPackageList(p));
		}
		return packs;
	}

	/**
	 * Registers a top-level package.
	 *
	 * @param pack the package to register
	 */
	public void register(CtPackage pack) {
		if (packages.containsKey(pack.getQualifiedName())) {
			throw new RuntimeException("package " + pack.getQualifiedName()
					+ " already created");
		}
		packages.put(pack.getQualifiedName(), pack);
	}

}