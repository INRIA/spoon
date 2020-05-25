/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * The {@link CtEnum} sub-factory.
 */
public class EnumFactory extends TypeFactory {

	/**
	 * Creates a new enum sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public EnumFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a new enumeration type
	 *
	 * @param owner
	 * 		package
	 * @param simpleName
	 * 		the simple name
	 */
	public CtEnum<?> create(CtPackage owner, String simpleName) {
		CtEnum<?> e = factory.Core().createEnum();
		e.setSimpleName(simpleName);
		owner.addType(e);
		return e;
	}

	/**
	 * Creates an enum from its qualified name.
	 */
	public CtEnum<?> create(String qualifiedName) {
		return create(
				factory.Package().getOrCreate(getPackageName(qualifiedName)),
				getSimpleName(qualifiedName));
	}

	/**
	 * Gets an already created enumeration from its qualified name.
	 *
	 * @return the enumeration or null if does not exist
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CtEnum<?> get(String qualifiedName) {
		try {
			return (CtEnum<?>) super.get(qualifiedName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a class from its runtime Java class.
	 *
	 * @param <T>
	 * 		type of created class
	 * @param cl
	 * 		the java class: note that this class should be Class&lt;T&gt; but it
	 * 		then poses problem when T is a generic type itself
	 */
	public <T extends Enum<?>> CtEnum<T> getEnum(Class<T> cl) {
		try {
			CtType<T> t = super.get(cl);
			return (CtEnum<T>) t;
		} catch (Exception e) {
			return null;
		}
	}

}
