/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.visitor.CtAbstractVisitor;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.GenericTypeAdapter;
import spoon.support.visitor.MethodTypingContext;
import spoon.support.visitor.java.JavaReflectionTreeBuilder;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The {@link CtType} sub-factory.
 */
public class TypeFactory extends SubFactory {

	private static final Set<String> NULL_PACKAGE_CLASSES = Set.of(
			"void", "boolean", "byte", "short", "char", "int", "float", "long", "double",
			// TODO (leventov) it is questionable to me that nulltype should also be here
			CtTypeReference.NULL_TYPE_NAME);

	/**
	 * @deprecated Use {@link #nullType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<?> NULL_TYPE = createReference(CtTypeReference.NULL_TYPE_NAME);
	/**
	 * @deprecated Use {@link #voidType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Void> VOID = createReference(Void.class);
	/**
	 * @deprecated Use {@link #stringType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<String> STRING = createReference(String.class);
	/**
	 * @deprecated Use {@link #booleanType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Boolean> BOOLEAN = createReference(Boolean.class);
	/**
	 * @deprecated Use {@link #byteType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Byte> BYTE = createReference(Byte.class);
	/**
	 * @deprecated Use {@link #characterType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Character> CHARACTER = createReference(Character.class);
	/**
	 * @deprecated Use {@link #integerType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Integer> INTEGER = createReference(Integer.class);
	/**
	 * @deprecated Use {@link #longType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Long> LONG = createReference(Long.class);
	/**
	 * @deprecated Use {@link #floatType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Float> FLOAT = createReference(Float.class);
	/**
	 * @deprecated Use {@link #doubleType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Double> DOUBLE = createReference(Double.class);
	/**
	 * @deprecated Use {@link #voidPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Void> VOID_PRIMITIVE = createReference(void.class);
	/**
	 * @deprecated Use {@link #booleanPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Boolean> BOOLEAN_PRIMITIVE = createReference(boolean.class);
	/**
	 * @deprecated Use {@link #bytePrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Byte> BYTE_PRIMITIVE = createReference(byte.class);
	/**
	 * @deprecated Use {@link #characterPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Character> CHARACTER_PRIMITIVE = createReference(char.class);
	/**
	 * @deprecated Use {@link #integerPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Integer> INTEGER_PRIMITIVE = createReference(int.class);
	/**
	 * @deprecated Use {@link #longPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Long> LONG_PRIMITIVE = createReference(long.class);
	/**
	 * @deprecated Use {@link #floatPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Float> FLOAT_PRIMITIVE = createReference(float.class);
	/**
	 * @deprecated Use {@link #doublePrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Double> DOUBLE_PRIMITIVE = createReference(double.class);
	/**
	 * @deprecated Use {@link #shortType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Short> SHORT = createReference(Short.class);
	/**
	 * @deprecated Use {@link #shortPrimitiveType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Short> SHORT_PRIMITIVE = createReference(short.class);
	/**
	 * @deprecated Use {@link #dateType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Date> DATE = createReference(Date.class);
	/**
	 * @deprecated Use {@link #objectType()} instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Object> OBJECT = createReference(Object.class);
	/**
	 * @deprecated Use {@link #createReference(Class) TypeFactory#createReference(Iterable.class)}  instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Iterable> ITERABLE = createReference(Iterable.class);
	/**
	 * @deprecated Use {@link #createReference(Class) TypeFactory#createReference(Collection.class)}  instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Collection> COLLECTION = createReference(Collection.class);
	/**
	 * @deprecated Use {@link #createReference(Class) TypeFactory#createReference(List.class)}  instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<List> LIST = createReference(List.class);
	/**
	 * @deprecated Use {@link #createReference(Class) TypeFactory#createReference(Set.class)}  instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Set> SET = createReference(Set.class);
	/**
	 * @deprecated Use {@link #createReference(Class) TypeFactory#createReference(Map.class)}  instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Map> MAP = createReference(Map.class);
	/**
	 * @deprecated Use {@link #createReference(Class) TypeFactory#createReference(Enum.class)}  instead.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<Enum> ENUM = createReference(Enum.class);
	/**
	 * @deprecated Deprecated for removal without replacement.
	 */
	@Deprecated(since = "11.0.0", forRemoval = true)
	public final CtTypeReference<?> OMITTED_TYPE_ARG_TYPE = createReference(CtTypeReference.OMITTED_TYPE_ARG_NAME);

	// This map MUST provide a useful computeIfAbsent method in the face of concurrency.
	// Therefore, we declare it as a ConcurrentHashMap directly.
	private final ConcurrentHashMap<Class<?>, CtType<?>> shadowCache = new ConcurrentHashMap<>();

	/**
	 * Returns a reference on the null type (type of null).
	 */
	public CtTypeReference<?> nullType() {
		return ((CtTypeReference<?>) createReference(CtTypeReference.NULL_TYPE_NAME)).clone();
	}

	/**
	 * Returns a reference on the void type.
	 */
	public CtTypeReference<Void> voidType() {
		return createReference(Void.class);
	}

	/**
	 * Returns a reference on the void primitive type.
	 */
	public CtTypeReference<Void> voidPrimitiveType() {
		return createReference(void.class);
	}

	/**
	 * Returns a reference on the string type.
	 */
	public CtTypeReference<String> stringType() {
		return createReference(String.class);
	}

	/**
	 * Returns a reference on the boolean type.
	 */
	public CtTypeReference<Boolean> booleanType() {
		return createReference(Boolean.class);
	}

	/**
	 * Returns a reference on the boolean primitive type.
	 */
	public CtTypeReference<Boolean> booleanPrimitiveType() {
		return createReference(boolean.class);
	}

	/**
	 * Returns a reference on the byte type.
	 */
	public CtTypeReference<Byte> byteType() {
		return createReference(Byte.class);
	}

	/**
	 * Returns a reference on the byte primitive type.
	 */
	public CtTypeReference<Byte> bytePrimitiveType() {
		return createReference(byte.class);
	}

	/**
	 * Returns a reference on the character type.
	 */
	public CtTypeReference<Character> characterType() {
		return createReference(Character.class);
	}

	/**
	 * Returns a reference on the character primitive type.
	 */
	public CtTypeReference<Character> characterPrimitiveType() {
		return createReference(char.class);
	}

	/**
	 * Returns a reference on the integer type.
	 */
	public CtTypeReference<Integer> integerType() {
		return createReference(Integer.class);
	}

	/**
	 * Returns a reference on the integer primitive type.
	 */
	public CtTypeReference<Integer> integerPrimitiveType() {
		return createReference(int.class);
	}

	/**
	 * Returns a reference on the long type.
	 */
	public CtTypeReference<Long> longType() {
		return createReference(Long.class);
	}

	/**
	 * Returns a reference on the long primitive type.
	 */
	public CtTypeReference<Long> longPrimitiveType() {
		return createReference(long.class);
	}

	/**
	 * Returns a reference on the float type.
	 */
	public CtTypeReference<Float> floatType() {
		return createReference(Float.class);
	}

	/**
	 * Returns a reference on the float primitive type.
	 */
	public CtTypeReference<Float> floatPrimitiveType() {
		return createReference(float.class);
	}

	/**
	 * Returns a reference on the double type.
	 */
	public CtTypeReference<Double> doubleType() {
		return createReference(Double.class);
	}

	/**
	 * Returns a reference on the double primitive type.
	 */
	public CtTypeReference<Double> doublePrimitiveType() {
		return createReference(double.class);
	}

	/**
	 * Returns a reference on the short type.
	 */
	public CtTypeReference<Short> shortType() {
		return createReference(Short.class);
	}

	/**
	 * Returns a reference on the short primitive type.
	 */
	public CtTypeReference<Short> shortPrimitiveType() {
		return createReference(short.class);
	}

	/**
	 * Returns a reference on the date type.
	 */
	public CtTypeReference<Date> dateType() {
		return createReference(Date.class);
	}

	/**
	 * Returns a reference on the object type.
	 */
	public CtTypeReference<Object> objectType() {
		return createReference(Object.class);
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

	public TypeFactory() {
		this(new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment()));
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
	 * Creates a reference to a n-dimension array of given type.
	 */
	public CtArrayTypeReference<?> createArrayReference(CtTypeReference<?> reference, int n) {
		CtTypeReference<?> componentType;
		if (n < 1) {
			throw new SpoonException("Array dimension must be >= 1");
		}
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

	public <T> CtTypeReference<T> createReference(Class<T> type) {
		return createReference(type, false);
	}

	/**
	 * Creates a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(Class<T> type, boolean includingFormalTypeParameter) {
		if (type == null) {
			return null;
		}
		if (type.isArray()) {
			CtArrayTypeReference<T> array = factory.Core().createArrayTypeReference();
			array.setComponentType(createReference(type.getComponentType(), includingFormalTypeParameter));
			return array;
		}
		CtTypeReference typeReference = createReference(type.getName());

		if (includingFormalTypeParameter) {
			for (TypeVariable<Class<T>> generic : type.getTypeParameters()) {
				typeReference.addActualTypeArgument(createTypeParameterReference(generic.getName()));
			}
		}

		return typeReference;
	}

	/**
	 * Create a reference to a simple type
	 */
	public <T> CtTypeReference<T> createReference(CtType<T> type) {
		return createReference(type, false);
	}

	/**
	 * Create a wildcard reference to a simple type
	 */
	public CtTypeMemberWildcardImportReference createTypeMemberWildcardImportReference(CtTypeReference typeReference) {
		CtTypeMemberWildcardImportReference ref = factory.Core().createTypeMemberWildcardImportReference();
		ref.setTypeReference(typeReference.clone());
		return ref;
	}

	/**
	 * @param includingFormalTypeParameter if true then references to formal type parameters
	 * 	are added as actual type arguments of returned {@link CtTypeReference}
	 */
	public <T> CtTypeReference<T> createReference(CtType<T> type, boolean includingFormalTypeParameter) {
		CtTypeReference<T> ref = factory.Core().createTypeReference();

		if (type.getDeclaringType() != null) {
			ref.setDeclaringType(createReference(type.getDeclaringType(), includingFormalTypeParameter));
		} else if (type.getPackage() != null) {
			ref.setPackage(factory.Package().createReference(type.getPackage()));
		}

		ref.setSimpleName(type.getSimpleName());

		if (includingFormalTypeParameter) {
			for (CtTypeParameter formalTypeParam : type.getFormalCtTypeParameters()) {
				ref.addActualTypeArgument(formalTypeParam.getReference());
			}
		}
		return ref;
	}

	/**
	 * Create a reference to a simple type
	 */
	public CtTypeParameterReference createReference(CtTypeParameter type) {
		CtTypeParameterReference ref = factory.Core().createTypeParameterReference();
		ref.setSimpleName(type.getSimpleName());
		ref.setParent(type);
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
		} else if (!NULL_PACKAGE_CLASSES.contains(qualifiedName)) {
			ref.setPackage(factory.Package().topLevel());
		}
		ref.setSimpleName(getSimpleName(qualifiedName));
		return ref;
	}

	/**
	 * Create a reference to a simple type, setting the beginning of its fully qualified name as implicit
	 */
	public <T> CtTypeReference<T> createSimplyQualifiedReference(String qualifiedName) {
		CtTypeReference ref = createReference(qualifiedName);
		ref.getPackage().setImplicit(true);
		return ref;
	}

	/**
	 * Gets a created type from its qualified name if source in the source classpath.
	 *
	 * `TypeFactory#get(String)` returns null if the class is not in the source classpath (even if it is in the binary classpath).
	 * `TypeFactory#get(Class)` returns null if the class is neither in the source classpath nor in the binary classpath,
	 * and returns a [shadow class](http://spoon.gforge.inria.fr/reflection.html) if it is only in the binary classpath.
	 * Note that a shadow class has empty method bodies, if you need a shadow class with method bodies, see [spoon-decompiler](https://github.com/INRIA/spoon/tree/master/spoon-decompiler))
	 * @return a type if source in the source classpath or null if does not exist
	 */
	@SuppressWarnings("unchecked")
	public <T> CtType<T> get(final String qualifiedName) {
		int packageIndex = qualifiedName.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
		CtPackage pack;
		if (packageIndex > 0) {
			pack = factory.Package().get(qualifiedName.substring(0, packageIndex));
		} else {
			pack = factory.Package().getRootPackage();
		}

		if (pack != null) {
			CtType<T> type = pack.getType(qualifiedName.substring(packageIndex + 1));
			if (type != null) {
				return type;
			}
		}

		int inertTypeIndex = qualifiedName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR);
		if (inertTypeIndex > 0) {
			String s = qualifiedName.substring(0, inertTypeIndex);
			CtType<T> t = factory.Type().get(s);
			if (t == null) {
				return null;
			}
			String className = qualifiedName.substring(inertTypeIndex + 1);
			if (t.isLocalType()) {
				final List<CtClass<T>> enclosingClasses = t.getElements(new TypeFilter<CtClass<T>>(CtClass.class) {
					@Override
					public boolean matches(CtClass<T> element) {
						return super.matches(element) && element.getQualifiedName().equals(qualifiedName);
					}
				});
				if (enclosingClasses.isEmpty()) {
					return null;
				}
				return enclosingClasses.get(0);
			}
			if (isNumber(className)) {
				// If the class name is an integer, the class is an anonymous class, otherwise,
				// it is a standard class.
				//TODO reset cache when type is modified
				return getAnonymousTypeFromCache(t, className, k -> {
					//the searching for declaration of anonymous class is expensive
					//do that only once and store it in cache of CtType
					final List<CtNewClass> anonymousClasses = t.getElements(new TypeFilter<CtNewClass>(CtNewClass.class) {
						@Override
						public boolean matches(CtNewClass element) {
							return super.matches(element) && element.getAnonymousClass().getQualifiedName().equals(qualifiedName);
						}
					});
					if (anonymousClasses.isEmpty()) {
						return null;
					}
					return anonymousClasses.get(0).getAnonymousClass();
				});
			} else {
				return t.getNestedType(className);
			}
		}
		return null;
	}

	private static final String ANONYMOUS_TYPES_CACHE_KEY = TypeFactory.class.getName() + "-AnnonymousTypeCache";

	private <T, K> T getAnonymousTypeFromCache(CtElement element, K key, Function<K, T> valueResolver) {
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (element) {
			@SuppressWarnings("unchecked")
			Map<K, T> cache = (Map<K, T>) element.getMetadata(ANONYMOUS_TYPES_CACHE_KEY);
			if (cache == null) {
				cache = new HashMap<>();
				element.putMetadata(ANONYMOUS_TYPES_CACHE_KEY, cache);
			}
			return cache.computeIfAbsent(key, valueResolver);
		}
	}

	private boolean isNumber(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the list of all top-level created types.
	 */
	public List<CtType<?>> getAll() {
		return new ArrayList<>(factory.getModel().getAllTypes());
	}

	/**
	 * Gets the list of all created types.
	 */
	public List<CtType<?>> getAll(boolean includeNestedTypes) {
		if (!includeNestedTypes) {
			return getAll();
		}
		List<CtType<?>> types = new ArrayList<>();
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
	 * Gets a type from its runtime Java class. If the class isn't in the spoon path,
	 * the class will be built from the Java reflection and will be marked as
	 * shadow (see {@link spoon.reflect.declaration.CtShadowable}).
	 *
	 * @param <T>
	 * 		actual type of the class
	 * @param cl
	 * 		the java class: note that this class should be Class&lt;T&gt; but it
	 * 		then poses problem when T is a generic type itself
	 */
	@SuppressWarnings("unchecked")
	public <T> CtType<T> get(Class<?> cl) {
		final CtType<T> aType = get(cl.getName());
		if (aType == null) {
			return (CtType<T>) this.shadowCache.computeIfAbsent(cl, this::buildNewShadowClass);
		}
		return aType;
	}

	private CtType<?> buildNewShadowClass(Class<?> cl) {
		CtType<?> newShadowClass;
		try {
			newShadowClass = new JavaReflectionTreeBuilder(getShadowFactory()).scan(cl);
		} catch (Throwable e) {
			Launcher.LOGGER.warn("cannot create shadow class: {}", cl.getName(), e);

			newShadowClass = getShadowFactory().Core().createClass();
			newShadowClass.setSimpleName(cl.getSimpleName());
			newShadowClass.setShadow(true);
			getShadowFactory().Package().getOrCreate(cl.getPackage().getName()).addType(newShadowClass);
		}
		newShadowClass.setFactory(factory);
		newShadowClass.accept(new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null) {
					element.setFactory(factory);
				}
			}
		});
		return newShadowClass;
	}

	private transient Factory shadowFactory;
	private Factory getShadowFactory() {
		if (shadowFactory == null) {
			//use existing environment to use correct class loader
			shadowFactory = new FactoryImpl(new DefaultCoreFactory(), factory.getEnvironment());
		}
		return shadowFactory;
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
		List<CtTypeReference<?>> refs = new ArrayList<>(classes.size());
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
		return "";
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
		return qualifiedName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR);
	}

	/**
	 * Tells if a given Java qualified name contains a package name.
	 */
	protected int hasPackage(String qualifiedName) {
		return qualifiedName.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
	}

	/**
	 * Creates a type parameter reference with no bounds.
	 *
	 * @param name
	 * 		the name of the formal parameter
	 */
	public CtTypeParameterReference createTypeParameterReference(String name) {
		if ("?".equals(name)) {
			throw new SpoonException("the Spoon metamodel has evolved, use Factory.createWildcardReference() instead");
		}
		CtTypeParameterReference typeParam = factory.Core().createTypeParameterReference();
		typeParam.setSimpleName(name);
		return typeParam;
	}

	/**
	 * Create a {@link GenericTypeAdapter} for adapting of formal type parameters from any compatible context to the context of provided `formalTypeDeclarer`
	 *
	 * @param formalTypeDeclarer
	 * 		the target scope of the returned {@link GenericTypeAdapter}
	 */
	public GenericTypeAdapter createTypeAdapter(CtFormalTypeDeclarer formalTypeDeclarer) {
		class Visitor extends CtAbstractVisitor {
			GenericTypeAdapter adapter;
			@Override
			public <T> void visitCtClass(CtClass<T> ctClass) {
				adapter = new ClassTypingContext(ctClass);
			}
			@Override
			public <T> void visitCtInterface(CtInterface<T> intrface) {
				adapter = new ClassTypingContext(intrface);
			}
			@Override
			public <T> void visitCtMethod(CtMethod<T> m) {
				adapter = new MethodTypingContext().setMethod(m);
			}
			@Override
			public <T> void visitCtConstructor(CtConstructor<T> c) {
				adapter = new MethodTypingContext().setConstructor(c);
			}
		}
		Visitor visitor = new Visitor();
		formalTypeDeclarer.accept(visitor);
		return visitor.adapter;
	}

	/**
	 * Creates an intersection type reference.
	 *
	 * @param bounds
	 * 		List of bounds saved in the intersection type. The first bound will be the intersection type.
	 * @param <T>
	 * 		Type of the first bound.
	 */
	public <T> CtIntersectionTypeReference<T> createIntersectionTypeReferenceWithBounds(List<CtTypeReference<?>> bounds) {
		final CtIntersectionTypeReference<T> intersectionRef = factory.Core().createIntersectionTypeReference();
		CtTypeReference<?> firstBound = bounds.toArray(new CtTypeReference<?>[0])[0].clone();
		intersectionRef.setSimpleName(firstBound.getSimpleName());
		intersectionRef.setDeclaringType(firstBound.getDeclaringType());
		intersectionRef.setPackage(firstBound.getPackage());
		intersectionRef.setActualTypeArguments(firstBound.getActualTypeArguments());
		intersectionRef.setBounds(bounds);
		CtTypeReference<?> lastBound = bounds.get(bounds.size() - 1);
		if (!(firstBound.getPosition() instanceof NoSourcePosition) && !(lastBound.getPosition() instanceof NoSourcePosition)) {
			SourcePosition pos = factory.createSourcePosition(firstBound.getPosition().getCompilationUnit(), firstBound.getPosition().getSourceStart(), lastBound.getPosition().getSourceEnd(), firstBound.getPosition().getCompilationUnit().getLineSeparatorPositions());
			intersectionRef.setPosition(pos);
		}
		return intersectionRef;
	}

	/**
	 * Returns the default bounding type value
	 */
	public CtTypeReference getDefaultBoundingType() {
		return objectType();
	}

	/**
	 * Creates an import declaration.
	 */
	public CtImport createImport(CtReference reference) {
		CtImport ctImport = factory.Core().createImport();
		CtReference importRef = reference.clone();
		//import reference is always fully qualified and has no generic arguments
		new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setImplicit(false);
				if (e instanceof CtActualTypeContainer) {
					CtActualTypeContainer atc = (CtActualTypeContainer) e;
					atc.setActualTypeArguments(Collections.emptyList());
				}
			}
		}.scan(importRef);
		return ctImport.setReference(importRef);
	}

	public CtImport createUnresolvedImport(String reference, boolean isStatic) {
		CtUnresolvedImport ctUnresolvedImport = (CtUnresolvedImport) factory.Core().createUnresolvedImport();
		ctUnresolvedImport.setUnresolvedReference(reference);
		ctUnresolvedImport.setStatic(isStatic);
		return ctUnresolvedImport;
	}
}
