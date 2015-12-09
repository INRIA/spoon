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

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;

/**
 * The {@link CtClass} sub-factory.
 */
public class ClassFactory extends TypeFactory {

	/**
	 * Creates a class sub-factory.
	 *
	 * @param factory
	 *            the parent factory
	 */
	public ClassFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates an inner class.
	 *
	 * @param declaringClass
	 *            declaring class
	 * @param simpleName
	 *            simple name of inner class (without . or $)
	 */
	public <T> CtClass<T> create(CtClass<?> declaringClass, String simpleName) {
		CtClass<T> c = factory.Core().createClass();
		c.setSimpleName(simpleName);
		return c;
	}

	/**
	 * Creates a top-level class.
	 *
	 * @param owner
	 *            the declaring package
	 * @param simpleName
	 *            the simple name
	 */
	public <T> CtClass<T> create(CtPackage owner, String simpleName) {
		CtClass<T> c = factory.Core().createClass();
		c.setSimpleName(simpleName);
		if (owner.getTypes().contains(c)) {
			owner.removeType(c);
		}
		owner.addType(c);
		c.setParent(owner);
		return c;
	}

	/**
	 * Creates a class from its qualified name.
	 *
	 * @param <T>
	 *            type of created class
	 * @param qualifiedName
	 *            full name of class to create. Name can contain . or $ for
	 *            inner types
	 */
	public <T> CtClass<T> create(String qualifiedName) {
		if (hasInnerType(qualifiedName) > 0) {
			CtClass<?> declaringClass = create(getDeclaringTypeName(qualifiedName));
			return create(declaringClass, getSimpleName(qualifiedName));
		}
		return create(factory.Package().getOrCreate(
				getPackageName(qualifiedName)), getSimpleName(qualifiedName));
	}

	/**
	 * Gets a class from its runtime Java class.
	 *
	 * @param <T>
	 *            type of created class
	 * @param cl
	 *            the java class: note that this class should be Class&lt;T&gt; but
	 *            it then poses problem when T is a generic type itself
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CtClass<T> get(Class<?> cl) {
		try {
			return (CtClass<T>) super.get(cl);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Searches for a class from his qualified name.
	 *
	 * @param <T>
	 *            the type of the class
	 * @param qualifiedName
	 *            to search
	 * @return found class or null
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CtClass<T> get(String qualifiedName) {
		try {
			return (CtClass<T>) super.get(qualifiedName);
		} catch (Exception e) {
			return null;
		}
	}

}
