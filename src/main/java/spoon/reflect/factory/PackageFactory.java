/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtPackageImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The {@link CtPackage} sub-factory.
 */
public class PackageFactory extends SubFactory implements Serializable {
	private static final long serialVersionUID = 1L;

	private CtPackage rootPackage;

	private static class CtRootPackage extends CtPackageImpl {
		{
			setSimpleName(CtPackage.TOP_LEVEL_PACKAGE_NAME);
			setParent(new CtElementImpl() {
				@Override
				public void accept(CtVisitor visitor) {

				}

				@Override
				public CtElement getParent() throws ParentNotInitializedException {
					return null;
				}
			});
		}

		@Override
		public String getSimpleName() {
			return super.getSimpleName();
		}

		@Override
		public String getQualifiedName() {
			return "";
		}

	}

	/**
	 * Creates a new package sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public PackageFactory(Factory factory) {
		super(factory);
		rootPackage = new CtRootPackage();
		rootPackage.setFactory(factory);
	}

	/**
	 * Creates a reference to an existing package.
	 */
	public CtPackageReference createReference(CtPackage pack) {
		if (pack == null) {
			throw new IllegalArgumentException();
		}
		return createReference(pack.getQualifiedName());
	}

	/**
	 * Creates a reference to a package by using its Java runtime
	 * representation.
	 *
	 * @param pack
	 * 		a runtime package
	 * @return reference to the package
	 */
	public CtPackageReference createReference(Package pack) {
		return createReference(pack.getName());
	}

	CtPackageReference topLevel;

	/**
	 * Returns a reference on the top level package.
	 */
	public CtPackageReference topLevel() {
		if (topLevel == null) {
			topLevel = createReference(CtPackage.TOP_LEVEL_PACKAGE_NAME);
		}
		return topLevel;
	}

	/**
	 * Creates a reference to a package.
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
	 * Gets or creates a package.
	 *
	 * @param qualifiedName
	 * 		the full name of the package
	 */
	public CtPackage getOrCreate(String qualifiedName) {
		StringTokenizer token = new StringTokenizer(qualifiedName, CtPackage.PACKAGE_SEPARATOR);
		CtPackage last = rootPackage;

		while (token.hasMoreElements()) {
			String name = token.nextToken();
			CtPackage next = last.getPackage(name);
			if (next == null) {
				next = factory.Core().createPackage();
				next.setSimpleName(name);
				next.setParent(last);
				last.getPackages().add(next);
			}
			last = next;
		}

		return last;
	}

	/**
	 * Gets a created package.
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
		CtPackage current = rootPackage;
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
		return getSubPackageList(rootPackage);
	}

	/**
	 * Return the unnamed top-level package.
	 */
	public CtPackage getRootPackage() {
		return rootPackage;
	}

	private List<CtPackage> getSubPackageList(CtPackage pack) {
		List<CtPackage> packs = new ArrayList<CtPackage>();
		packs.add(pack);
		for (CtPackage p : pack.getPackages()) {
			packs.addAll(getSubPackageList(p));
		}
		return packs;
	}

}
