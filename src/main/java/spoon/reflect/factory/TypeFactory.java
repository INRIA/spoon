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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The {@link CtType} sub-factory.
 */
public class TypeFactory extends SubFactory {

	private static final long serialVersionUID = 1L;

	CtTypeReference<?> nullType;

	/**
	 * Returns a reference on the null type (type of null).
	 */
	public CtTypeReference<?> nullType() {
		if (nullType == null) {
			nullType = createReference(CtTypeReference.NULL_TYPE_NAME);
		}
		return nullType;
	}

	/**
	 * Creates a new type sub-factory.
	 *
	 * @param factory
	 *            the parent factory
	 */
	public TypeFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a reference to an array of given type.
	 *
	 * @param <T>
	 *            type of array
	 * @param type
	 *            type of array values
	 */
	public <T> CtArrayTypeReference<T[]> createArrayReference(
			CtSimpleType<T> type) {
		CtArrayTypeReference<T[]> array = factory.Core()
				.createArrayTypeReference();
		array.setComponentType(createReference(type));
		return array;
	}

	/**
	 * Creates a reference to a one-dimension array of given type.
	 */
	public <T> CtArrayTypeReference<T[]> createArrayReference(
			CtTypeReference<T> reference) {
		CtArrayTypeReference<T[]> array = factory.Core()
				.createArrayTypeReference();
		array.setComponentType(reference);
		return array;
	}

	/**
	 * Creates a reference to an n-dimension array of given type.
	 */
	public CtArrayTypeReference<?> createArrayReference(
			CtTypeReference<?> reference, int n) {
		CtTypeReference<?> componentType = null;
		if (n == 1) {
			return createArrayReference(reference);
		}
		componentType = createArrayReference(reference, n - 1);
		CtArrayTypeReference<?> array = factory.Core()
				.createArrayTypeReference();
		array.setComponentType(componentType);
		return array;
	}

	/**
	 * Creates a reference to an array of given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtArrayTypeReference<T> createArrayReference(String qualifiedName) {
		CtArrayTypeReference<T> array = factory.Core()
				.createArrayTypeReference();
		array.setComponentType(createReference(qualifiedName));
		return array;
	}

	/**
	 * Creates a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(Class<T> type) {
		if (type.isArray()) {
			CtArrayTypeReference<T> array = factory.Core()
					.createArrayTypeReference();
			array.setComponentType(createReference(type.getComponentType()));
			return array;
		}
		return createReference(type.getName());
	}

	/**
	 * Create a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(CtSimpleType<T> type) {
		CtTypeReference<T> ref = factory.Core().createTypeReference();
		if (type.isTopLevel()) {
			ref
					.setPackage(factory.Package().createReference(
							type.getPackage()));
		} else {
			if (type.getDeclaringType() != null) {
				ref.setDeclaringType(createReference(type.getDeclaringType()));
			}
		}
		ref.setSimpleName(type.getSimpleName());
		return ref;
	}

	/**
	 * Create a reference to a simple type
	 */
	@SuppressWarnings("unchecked")
	public <T> CtTypeReference<T> createReference(String qualifiedName) {
		if (qualifiedName.endsWith("[]")) {
			return createArrayReference(qualifiedName.substring(0,
					qualifiedName.length() - 2));
		}
		CtTypeReference ref = factory.Core().createTypeReference();
		if (hasInnerType(qualifiedName) > 0) {
			ref
					.setDeclaringType(createReference(getDeclaringTypeName(qualifiedName)));
		} else if (hasPackage(qualifiedName) > 0) {
			ref.setPackage(factory.Package().createReference(
					getPackageName(qualifiedName)));
		}
		ref.setSimpleName(getSimpleName(qualifiedName));
		return ref;
	}

	/**
	 * Gets a created type from its qualified name.
	 *
	 * @return a found type or null if does not exist
	 */
	@SuppressWarnings("unchecked")
	public <T> CtSimpleType<T> get(String qualifiedName) {
		int inertTypeIndex = qualifiedName
				.lastIndexOf(CtSimpleType.INNERTTYPE_SEPARATOR);
		if (inertTypeIndex > 0) {
			String s = qualifiedName.substring(0, inertTypeIndex);
			CtSimpleType t = get(s);
			if (t == null) {
				return null;
			}
			return t.getNestedType(qualifiedName.substring(inertTypeIndex + 1));
		}

		int packageIndex = qualifiedName
				.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
		CtPackage pack;
		if (packageIndex > 0) {
			pack = factory.Package().get(
					qualifiedName.substring(0, packageIndex));
		} else {
			pack = factory.Package().get(CtPackage.TOP_LEVEL_PACKAGE_NAME);
		}

		if (pack == null) {
			return null;
		}

		return (CtSimpleType<T>) pack.getType(qualifiedName
				.substring(packageIndex + 1));
	}

	/**
	 * Gets the list of all top-level created types.
	 */
	public List<CtSimpleType<?>> getAll() {
		List<CtSimpleType<?>> types = new ArrayList<CtSimpleType<?>>();
		for (CtPackage pack : factory.Package().getAll()) {
			types.addAll(pack.getTypes());
		}
		return types;
	}

	/**
	 * Gets the list of all created types.
	 */
	public List<CtSimpleType<?>> getAll(boolean includeNestedTypes) {
		if (!includeNestedTypes) {
			return getAll();
		}
		List<CtSimpleType<?>> types = new ArrayList<CtSimpleType<?>>();
		for (CtPackage pack : factory.Package().getAll()) {
			for (CtSimpleType<?> type : pack.getTypes()) {
				addNestedType(types, type);
			}
		}
		return types;
	}

	private void addNestedType(List<CtSimpleType<?>> list, CtSimpleType<?> t) {
		list.add(t);
		for (CtSimpleType<?> nt : t.getNestedTypes()) {
			addNestedType(list, nt);
		}
	}

	/**
	 * Gets a type from its runtime Java class.
	 *
	 * @param <T>
	 *            actual type of the class
	 * @param cl
	 *            the java class: note that this class should be Class<T> but
	 *            it then poses problem when T is a generic type itself
	 */
	@SuppressWarnings("unchecked")
	public <T> CtSimpleType<T> get(Class<?> cl) {
		return (CtSimpleType<T>) get(cl.getName());
	}

	/**
	 * Gets the declaring type name for a given Java qualified name.
	 */
	protected String getDeclaringTypeName(String qualifiedName) {
		return qualifiedName.substring(0, hasInnerType(qualifiedName));
	}

	/**
	 * Creates a collection of type references from a collection of classes.
	 */
	public List<CtTypeReference<?>> createReferences(List<Class<?>> classes) {
		List<CtTypeReference<?>> refs = new ArrayList<CtTypeReference<?>>();
		for (Class<?> c : classes) {
			refs.add(createReference(c));
		}
		return refs;
	}

	/**
	 * Gets the package name for a given Java qualified name.
	 */
	protected String getPackageName(String qualifiedName) {
		if (hasPackage(qualifiedName) >= 0) {
			return qualifiedName.substring(0, hasPackage(qualifiedName));
		}
		return CtPackage.TOP_LEVEL_PACKAGE_NAME;
	}

	/**
	 * Gets the simple name for a given Java qualified name.
	 */
	protected String getSimpleName(String qualifiedName) {
		if (hasInnerType(qualifiedName) > 0) {
			return qualifiedName.substring(hasInnerType(qualifiedName) + 1);
		} else if (hasPackage(qualifiedName) > 0) {
			return qualifiedName.substring(hasPackage(qualifiedName) + 1);
		} else {
			return qualifiedName;
		}
	}

	/**
	 * Tells if a given Java qualified name is that of an inner type.
	 */
	protected int hasInnerType(String qualifiedName) {
		int ret = qualifiedName.lastIndexOf(CtSimpleType.INNERTTYPE_SEPARATOR);
		// if (ret < 0) {
		// if (hasPackage(qualifiedName) > 0) {
		// String buf = qualifiedName.substring(0,
		// hasPackage(qualifiedName));
		// int tmp = buf.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
		// if (Character.isUpperCase(buf.charAt(tmp + 1))) {
		// ret = hasPackage(qualifiedName);
		// }
		// }
		// }
		return ret;
	}

	/**
	 * Tells if a given Java qualified name contains a package name.
	 */
	protected int hasPackage(String qualifiedName) {
		return qualifiedName.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
	}

	/**
	 * Creates a type parameter with no bounds.
	 *
	 * @param owner
	 *            the owning declaration
	 * @param name
	 *            the name of the formal parameter
	 */
	public CtTypeParameter createTypeParameter(CtElement owner, String name) {
		CtTypeParameter typeParam = factory.Core().createTypeParameter();
		typeParam.setParent(owner);
		typeParam.setName(name);
		return typeParam;
	}

	/**
	 * Creates a type parameter.
	 *
	 * @param owner
	 *            the owning declaration
	 * @param name
	 *            the name of the formal parameter
	 * @param bounds
	 *            the bounds
	 */
	public CtTypeParameter createTypeParameter(CtElement owner, String name,
			List<CtTypeReference<?>> bounds) {
		CtTypeParameter typeParam = factory.Core().createTypeParameter();
		typeParam.setParent(owner);
		typeParam.setName(name);
		typeParam.setBounds(bounds);
		return typeParam;
	}

	/**
	 * Creates a type parameter reference with no bounds.
	 *
	 * @param name
	 *            the name of the formal parameter
	 */
	public CtTypeParameterReference createTypeParameterReference(String name) {
		CtTypeParameterReference typeParam = factory.Core()
				.createTypeParameterReference();
		typeParam.setSimpleName(name);
		return typeParam;
	}

	/**
	 * Creates a type parameter reference.
	 *
	 * @param name
	 *            the name of the formal parameter
	 * @param bounds
	 *            the bounds
	 */
	public CtTypeParameterReference createTypeParameterReference(String name,
			List<CtTypeReference<?>> bounds) {
		CtTypeParameterReference typeParam = factory.Core()
				.createTypeParameterReference();
		typeParam.setSimpleName(name);
		typeParam.setBounds(bounds);
		return typeParam;
	}

}
