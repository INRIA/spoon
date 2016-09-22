/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
	 * Creates an interface.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtInterface<T> create(String qualifiedName) {
		if (hasInnerType(qualifiedName) > 0) {
			return create(
					(CtInterface<T>) create(getDeclaringTypeName(qualifiedName)),
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
