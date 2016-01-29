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

import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@link CtType} sub-factory.
 */
public class TypeFactory extends SubFactory {

	CtTypeReference<?> nullType;

	public final CtTypeReference<Void> VOID = createReference(Void.class);
	public final CtTypeReference<String> STRING = createReference(String.class);
	public final CtTypeReference<Boolean> BOOLEAN = createReference(Boolean.class);
	public final CtTypeReference<Byte> BYTE = createReference(Byte.class);
	public final CtTypeReference<Character> CHARACTER = createReference(Character.class);
	public final CtTypeReference<Integer> INTEGER = createReference(Integer.class);
	public final CtTypeReference<Long> LONG = createReference(Long.class);
	public final CtTypeReference<Float> FLOAT = createReference(Float.class);
	public final CtTypeReference<Double> DOUBLE = createReference(Double.class);
	public final CtTypeReference<Void> VOID_PRIMITIVE = createReference(void.class);
	public final CtTypeReference<Boolean> BOOLEAN_PRIMITIVE = createReference(boolean.class);
	public final CtTypeReference<Byte> BYTE_PRIMITIVE = createReference(byte.class);
	public final CtTypeReference<Character> CHARACTER_PRIMITIVE = createReference(char.class);
	public final CtTypeReference<Integer> INTEGER_PRIMITIVE = createReference(int.class);
	public final CtTypeReference<Long> LONG_PRIMITIVE = createReference(long.class);
	public final CtTypeReference<Float> FLOAT_PRIMITIVE = createReference(float.class);
	public final CtTypeReference<Double> DOUBLE_PRIMITIVE = createReference(double.class);
	public final CtTypeReference<Date> DATE = createReference(Date.class);
	public final CtTypeReference<Object> OBJECT = createReference(Object.class);

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
	 * 		the parent factory
	 */
	public TypeFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a reference to an array of given type.
	 *
	 * @param <T>
	 * 		type of array
	 * @param type
	 * 		type of array values
	 */
	public <T> CtArrayTypeReference<T[]> createArrayReference(CtType<T> type) {
		CtArrayTypeReference<T[]> array = factory.Core().createArrayTypeReference();
		array.setComponentType(createReference(type));
		return array;
	}

	/**
	 * Creates a reference to a one-dimension array of given type.
	 */
	public <T> CtArrayTypeReference<T[]> createArrayReference(CtTypeReference<T> reference) {
		CtArrayTypeReference<T[]> array = factory.Core().createArrayTypeReference();
		array.setComponentType(reference);
		return array;
	}

	/**
	 * Creates a reference to an n-dimension array of given type.
	 */
	public CtArrayTypeReference<?> createArrayReference(CtTypeReference<?> reference, int n) {
		CtTypeReference<?> componentType = null;
		if (n == 1) {
			return createArrayReference(reference);
		}
		componentType = createArrayReference(reference, n - 1);
		CtArrayTypeReference<?> array = factory.Core().createArrayTypeReference();
		array.setComponentType(componentType);
		return array;
	}

	/**
	 * Creates a reference to an array of given type.
	 */
	public <T> CtArrayTypeReference<T> createArrayReference(String qualifiedName) {
		CtArrayTypeReference<T> array = factory.Core().createArrayTypeReference();
		array.setComponentType(createReference(qualifiedName));
		return array;
	}

	/**
	 * Creates a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(Class<T> type) {
		if (type.isArray()) {
			CtArrayTypeReference<T> array = factory.Core().createArrayTypeReference();
			array.setComponentType(createReference(type.getComponentType()));
			return array;
		}
		return createReference(type.getName());
	}

	/**
	 * Create a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(CtType<T> type) {
		CtTypeReference<T> ref = factory.Core().createTypeReference();

		if (type.getPackage() != null) {
			ref.setPackage(factory.Package().createReference(type.getPackage()));
		}

		if (type.getDeclaringType() != null) {
			ref.setDeclaringType(createReference(type.getDeclaringType()));
		}

		ref.setSimpleName(type.getSimpleName());
		return ref;
	}

	/**
	 * Create a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(String qualifiedName) {
		if (qualifiedName.endsWith("[]")) {
			return createArrayReference(qualifiedName.substring(0, qualifiedName.length() - 2));
		}
		CtTypeReference<T> ref = factory.Core().createTypeReference();
		if (hasInnerType(qualifiedName) > 0) {
			ref.setDeclaringType(createReference(getDeclaringTypeName(qualifiedName)));
		} else if (hasPackage(qualifiedName) > 0) {
			ref.setPackage(factory.Package().createReference(getPackageName(qualifiedName)));
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
	public <T> CtType<T> get(final String qualifiedName) {
		int inertTypeIndex = qualifiedName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR);
		if (inertTypeIndex > 0) {
			String s = qualifiedName.substring(0, inertTypeIndex);
			CtType<T> t = get(s);
			if (t == null) {
				return null;
			}
			String className = qualifiedName.substring(inertTypeIndex + 1);
			final CtTypeReference<T> reference = t.getReference();
			if (reference.isLocalType()) {
				final List<CtClass<T>> enclosingClasses = t.getElements(new TypeFilter<CtClass<T>>(CtClass.class) {
					@Override
					public boolean matches(CtClass<T> element) {
						return super.matches(element) && element.getQualifiedName().equals(qualifiedName);
					}
				});
				if (enclosingClasses.size() == 0) {
					return null;
				}
				return enclosingClasses.get(0);
			}
			try {
				// If the class name can't be parsed in integer, the method throws an exception.
				// If the class name is an integer, the class is an anonymous class, otherwise,
				// it is a standard class.
				Integer.parseInt(className);
				final List<CtNewClass> anonymousClasses = t.getElements(new TypeFilter<CtNewClass>(CtNewClass.class) {
					@Override
					public boolean matches(CtNewClass element) {
						return super.matches(element) && element.getAnonymousClass().getQualifiedName().equals(qualifiedName);
					}
				});
				if (anonymousClasses.size() == 0) {
					return null;
				}
				return anonymousClasses.get(0).getAnonymousClass();
			} catch (NumberFormatException e) {
				return t.getNestedType(className);
			}
		}

		int packageIndex = qualifiedName.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
		CtPackage pack;
		if (packageIndex > 0) {
			pack = factory.Package().get(qualifiedName.substring(0, packageIndex));
		} else {
			pack = factory.Package().get(CtPackage.TOP_LEVEL_PACKAGE_NAME);
		}

		if (pack == null) {
			return null;
		}

		return (CtType<T>) pack.getType(qualifiedName.substring(packageIndex + 1));
	}

	/**
	 * Gets the list of all top-level created types.
	 */
	public List<CtType<?>> getAll() {
		List<CtType<?>> types = new ArrayList<CtType<?>>();
		for (CtPackage pack : factory.Package().getAll()) {
			types.addAll(pack.getTypes());
		}
		return types;
	}

	/**
	 * Gets the list of all created types.
	 */
	public List<CtType<?>> getAll(boolean includeNestedTypes) {
		if (!includeNestedTypes) {
			return getAll();
		}
		List<CtType<?>> types = new ArrayList<CtType<?>>();
		for (CtPackage pack : factory.Package().getAll()) {
			for (CtType<?> type : pack.getTypes()) {
				addNestedType(types, type);
			}
		}
		return types;
	}

	private void addNestedType(List<CtType<?>> list, CtType<?> t) {
		list.add(t);
		for (CtType<?> nt : t.getNestedTypes()) {
			addNestedType(list, nt);
		}
	}

	/**
	 * Gets a type from its runtime Java class.
	 *
	 * @param <T>
	 * 		actual type of the class
	 * @param cl
	 * 		the java class: note that this class should be Class&lt;T&gt; but it
	 * 		then poses problem when T is a generic type itself
	 */
	@SuppressWarnings("unchecked")
	public <T> CtType<T> get(Class<?> cl) {
		return (CtType<T>) get(cl.getName());
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
		List<CtTypeReference<?>> refs = new ArrayList<CtTypeReference<?>>(classes.size());
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
		int ret = qualifiedName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR);
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
	 * 		the owning declaration
	 * @param name
	 * 		the name of the formal parameter
	 */
	public CtTypeParameter createTypeParameter(CtElement owner, String name) {
		CtTypeParameter typeParam = factory.Core().createTypeParameter();
		typeParam.setSimpleName(name);
		return typeParam;
	}

	/**
	 * Creates a type parameter.
	 *
	 * @param owner
	 * 		the owning declaration
	 * @param name
	 * 		the name of the formal parameter
	 * @param bounds
	 * 		the bounds
	 */
	public CtTypeParameter createTypeParameter(CtElement owner, String name, List<CtTypeReference<?>> bounds) {
		CtTypeParameter typeParam = factory.Core().createTypeParameter();
		typeParam.setSimpleName(name);
		typeParam.setBounds(bounds);
		return typeParam;
	}

	/**
	 * Creates a type parameter reference with no bounds.
	 *
	 * @param name
	 * 		the name of the formal parameter
	 */
	public CtTypeParameterReference createTypeParameterReference(String name) {
		CtTypeParameterReference typeParam = factory.Core().createTypeParameterReference();
		typeParam.setSimpleName(name);
		return typeParam;
	}

	/**
	 * Creates a type parameter reference.
	 *
	 * @param name
	 * 		the name of the formal parameter
	 * @param bounds
	 * 		the bounds
	 */
	public CtTypeParameterReference createTypeParameterReference(String name, List<CtTypeReference<?>> bounds) {
		CtTypeParameterReference typeParam = factory.Core().createTypeParameterReference();
		typeParam.setSimpleName(name);
		typeParam.setBounds(bounds);
		return typeParam;
	}

}
