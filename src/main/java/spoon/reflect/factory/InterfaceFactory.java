/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * The {@link CtInterface} sub-factory.
 */
public class InterfaceFactory extends TypeFactory {

	/**
	 * Creates a new interface sub-factory.
	 *
	 * @param factory
	 *            the parent factory
	 */
	public InterfaceFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates an interface.
	 */
	public <T> CtInterface<T> create(CtPackage owner, String simpleName) {
		CtInterface<T> i = factory.Core().createInterface();
		i.setSimpleName(simpleName);
		owner.addType(i);
		return i;
	}

	/**
	 * Creates an inner interface
	 */
	public <T> CtInterface<T> create(CtType<T> owner, String simpleName) {
		CtInterface<T> ctInterface = factory.Core().createInterface();
		ctInterface.setSimpleName(simpleName);
		owner.addNestedType(ctInterface);
		return ctInterface;
	}

	/**
	 * Creates an interface from its qualified name.
	 *
	 * @param <T>
	 * 		type of created interface
	 *
	 * @param qualifiedName
	 * 		full name of interface to create. Name can contain $ for inner types
	 */
	@SuppressWarnings("unchecked")
	public <T> CtInterface<T> create(String qualifiedName) {
		if (hasInnerType(qualifiedName) > 0) {
			return create(
					create(getDeclaringTypeName(qualifiedName)),
					getSimpleName(qualifiedName));
		}
		return create(factory.Package().getOrCreate(
				getPackageName(qualifiedName)), getSimpleName(qualifiedName));
	}

	/**
	 * Gets a created interface
	 *
	 * @return the interface or null if does not exist
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CtInterface<T> get(String qualifiedName) {
		try {
			return (CtInterface<T>) super.get(qualifiedName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a interface from its runtime Java class.
	 *
	 * @param <T>
	 *            type of created class
	 * @param cl
	 *            the java class: note that this class should be Class&lt;T&gt; but
	 *            it then poses problem when T is a generic type itself
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CtInterface<T> get(Class<?> cl) {
		try {
			return (CtInterface<T>) super.get(cl);
		} catch (Exception e) {
			return null;
		}
	}

}
