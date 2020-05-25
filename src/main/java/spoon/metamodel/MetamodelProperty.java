/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.metamodel;

import static spoon.metamodel.Metamodel.addUniqueObject;
import static spoon.metamodel.Metamodel.getOrCreate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.util.RtHelper;

/**
 * Represents a property of the Spoon metamodel.
 * A property:
 *   - is an abstraction of a concrete field in an implementation class
 *   - the {@link MetamodelConcept} is the owner of this role, it models the implementation class that contains the field.
 *   - encapsulates a pair ({@link CtRole}, {@link MetamodelConcept}).
 *   - captures both the type of the field (eg list) and the type of items (eg String).
 */
public class MetamodelProperty {
	/**
	 * Name of the field
	 */
	private final String name;
	/**
	 * {@link CtRole} of the field
	 */
	private final CtRole role;
	/**
	 * The list of {@link MetamodelConcept}s which contains this field
	 */
	private final MetamodelConcept ownerConcept;
	/**
	 * Type of value container [single, list, set, map]
	 */
	private ContainerKind valueContainerType;
	/**
	 * The type of value of this property - can be Set, List, Map or any non collection type
	 */
	private CtTypeReference<?> valueType;
	/**
	 * The item type of value of this property - can be non collection type
	 */
	private CtTypeReference<?> itemValueType;

	private RoleHandler roleHandler;

	private Boolean derived;
	private Boolean unsettable;

	private Map<MMMethodKind, List<MMMethod>> methodsByKind = new HashMap<>();
	private Map<String, MMMethod> methodsBySignature;

	/**
	 * methods of this field defined directly on ownerType.
	 * There is PropertyGetter or PropertySetter annotation with `role` of this {@link MetamodelProperty}
	 */
	private final List<MMMethod> roleMethods = new ArrayList<>();
	/**
	 * methods of this field grouped by signature defined directly on ownerType.
	 * There is PropertyGetter or PropertySetter annotation with `role` of this {@link MetamodelProperty}
	 * note: There can be up to 2 methods in this list. 1) declaration from interface, 2) implementation from class
	 */
	private final Map<String, MMMethod> roleMethodsBySignature = new HashMap<>();
	/**
	 * List of {@link MetamodelProperty} with same `role`, from super type of `ownerConcept` {@link MetamodelConcept}
	 */
	private final List<MetamodelProperty> superProperties = new ArrayList<>();

	private List<MMMethodKind> ambiguousMethodKinds = new ArrayList<>();

	MetamodelProperty(String name, CtRole role, MetamodelConcept ownerConcept) {
		this.name = name;
		this.role = role;
		this.ownerConcept = ownerConcept;
	}

	void addMethod(CtMethod<?> method) {
		addMethod(method, true);
	}

	/**
	 * @param method
	 * @param createIfNotExist
	 * @return existing {@link MMMethod}, which overrides `method` or creates and registers new one if `createIfNotExist`==true
	 */
	MMMethod addMethod(CtMethod<?> method, boolean createIfNotExist) {
		for (MMMethod mmMethod : roleMethods) {
			if (mmMethod.overrides(method)) {
				// linking this ctMethod to this mmMethod
				mmMethod.addRelatedMethod(method);
				return mmMethod;
			}
		}
		if (createIfNotExist) {
			MMMethod mmMethod = new MMMethod(this, method);
			roleMethods.add(mmMethod);
			getOrCreate(methodsByKind, mmMethod.getKind(), () -> new ArrayList<>()).add(mmMethod);
			MMMethod conflict = roleMethodsBySignature.put(mmMethod.getSignature(), mmMethod);
			if (conflict != null) {
				throw new SpoonException("Conflict on " + getOwner().getName() + "." + name + " method signature: " + mmMethod.getSignature());
			}
			return mmMethod;
		}
		return null;
	}

	void addSuperField(MetamodelProperty superMMField) {
		if (addUniqueObject(superProperties, superMMField)) {
			// we copy all methods of the super property
			for (MMMethod superMethod : superMMField.getRoleMethods()) {
				CtMethod<?> method;
				// we want the super method that is compatible with this property
				method = superMethod.getCompatibleMethod(getOwner());
				// we add this CtMethod to this property
				addMethod(method, true);
			}
		}
	}

	public String getName() {
		return name;
	}

	public CtRole getRole() {
		return role;
	}

	/** returns the concept that holds this property */
	public MetamodelConcept getOwner() {
		return ownerConcept;
	}

	/** returns the kind of property (list, value, etc) */
	public ContainerKind getContainerKind() {
		return valueContainerType;
	}

	CtTypeReference<?> detectValueType() {
		MMMethod mmGetMethod = getMethod(MMMethodKind.GET);
		if (mmGetMethod == null) {
			throw new SpoonException("No getter exists for " + getOwner().getName() + "." + getName());
		}
		MMMethod mmSetMethod = getMethod(MMMethodKind.SET);
		if (mmSetMethod == null) {
			return mmGetMethod.getReturnType();
		}
		CtTypeReference<?> getterValueType = mmGetMethod.getReturnType();
		CtTypeReference<?> setterValueType = mmSetMethod.getValueType();
		if (getterValueType.equals(setterValueType)) {
			return mmGetMethod.getReturnType();
		}
		if (containerKindOf(getterValueType.getActualClass()) != ContainerKind.SINGLE) {
			getterValueType = getTypeofItems(getterValueType);
			setterValueType = getTypeofItems(setterValueType);
		}
		if (getterValueType.equals(setterValueType)) {
			return mmGetMethod.getReturnType();
		}
		if (getterValueType.isSubtypeOf(setterValueType)) {
			/*
			 * Getter and setter have different type
			 * For example:
			 * CtBlock CtCatch#getBody
			 * and
			 * CtCatch#setBody(CtStatement)
			 * In current metamodel we take type of setter to keep it simple
			 */
			return mmSetMethod.getValueType();
		}
		throw new SpoonException("Incompatible getter and setter for " + getOwner().getName() + "." + getName());
	}

	void setValueType(CtTypeReference<?> valueType) {
		Factory f = valueType.getFactory();
		if (valueType instanceof CtTypeParameterReference) {
			valueType = ((CtTypeParameterReference) valueType).getBoundingType();
			if (valueType == null) {
				valueType = f.Type().OBJECT;
			}
		}
		if (valueType.isImplicit()) {
			valueType = valueType.clone();
			//never return type  with implicit==true, such type is then not pretty printed
			valueType.setImplicit(false);
		}
		this.valueType = valueType;
		this.valueContainerType = containerKindOf(valueType.getActualClass());
		if (valueContainerType != ContainerKind.SINGLE) {
			itemValueType = getTypeofItems(valueType);
		} else {
			itemValueType = valueType;
		}
	}

	/**
	 * Return the type of the field
	 * for List&lt;String&gt; field the ValueType is List
	 * for String field the ValueType is String
	 *
	 */
	public CtTypeReference<?> getTypeOfField() {
		if (valueType == null) {
			throw new SpoonException("Model is not initialized yet");
		}
		return valueType;
	}


	/**
	 * Returns the type of the property
	 * for List&lt;String&gt; field the ValueType is String
	 * for String field the ValueType is String (when getContainerKind == {@link ContainerKind#SINGLE}, {@link #getTypeofItems()} == {@link #getTypeOfField()}.
	 *
	 */
	public CtTypeReference<?> getTypeofItems() {
		if (itemValueType == null) {
			getTypeOfField();
		}
		return itemValueType;
	}

	public MMMethod getMethod(MMMethodKind kind) {
		List<MMMethod> ms = getMethods(kind);
		return !ms.isEmpty() ? ms.get(0) : null;
	}

	/**
	 * @return {@link MMMethod} accessing this property, which has signature `signature`
	 */
	public MMMethod getMethodBySignature(String signature) {
		if (methodsBySignature == null) {
			methodsBySignature = new HashMap<>();
			for (List<MMMethod> mmMethods : methodsByKind.values()) {
				for (MMMethod mmMethod : mmMethods) {
					String sigature = mmMethod.getSignature();
					methodsBySignature.put(sigature, mmMethod);
				}
			}
		}
		return methodsBySignature.get(signature);
	}

	/**
	 * @param kind {@link MMMethodKind}
	 * @return methods of required `kind`
	 */
	public List<MMMethod> getMethods(MMMethodKind kind) {
		List<MMMethod> ms = methodsByKind.get(kind);
		return ms == null ? Collections.emptyList() : Collections.unmodifiableList(ms);
	}

	/**
	 * @return all methods which are accessing this property
	 */
	public Set<MMMethod> getMethods() {
		Set<MMMethod> res = new HashSet<>();
		for (List<MMMethod> methods : methodsByKind.values()) {
			res.addAll(methods);
		}
		return Collections.unmodifiableSet(res);
	}

	void sortByBestMatch() {
		//resolve conflicts using value type. Move the most matching method to 0 index
		//in order GET, SET and others
		for (MMMethodKind mk : MMMethodKind.values()) {
			sortByBestMatch(mk);
		}
	}

	void sortByBestMatch(MMMethodKind key) {
		List<MMMethod> methods = methodsByKind.get(key);
		if (methods != null && methods.size() > 1) {
			int idx = getIdxOfBestMatch(methods, key);
			if (idx >= 0) {
				if (idx > 0) {
					//move the matching to the beginning
					methods.add(0, methods.remove(idx));
				}
			} else {
				//add all methods as ambiguous
				ambiguousMethodKinds.add(key);
			}
		}
	}

	/**
	 * @param methods
	 * @param key
	 * @return index of the method which best matches the `key` accessor of this field
	 *  -1 if it cannot be resolved
	 */
	private int getIdxOfBestMatch(List<MMMethod> methods, MMMethodKind key) {
		MMMethod mmMethod = methods.get(0);
		if (mmMethod.getActualCtMethod().getParameters().isEmpty()) {
			return getIdxOfBestMatchByReturnType(methods, key);
		} else {
			MMMethod mmGetMethod = getMethod(MMMethodKind.GET);
			if (mmGetMethod == null) {
				//we have no getter so we do not know the expected value type. Setters are ambiguous
				return -1;
			}
			return getIdxOfBestMatchByInputParameter(methods, key, mmGetMethod.getReturnType());
		}
	}

	private int getIdxOfBestMatchByReturnType(List<MMMethod> methods, MMMethodKind key) {
		if (methods.size() > 2) {
			throw new SpoonException("Resolving of more then 2 conflicting getters is not supported. There are: " + methods.toString());
		}
		// There is no input parameter. We are resolving getter field.
		// choose the getter whose return value is a collection
		// of second one
		CtTypeReference<?> returnType1 = methods.get(0).getActualCtMethod().getType();
		CtTypeReference<?> returnType2 = methods.get(1).getActualCtMethod().getType();
		Factory f = returnType1.getFactory();
		boolean is1Iterable = returnType1.isSubtypeOf(f.Type().ITERABLE);
		boolean is2Iterable = returnType2.isSubtypeOf(f.Type().ITERABLE);
		if (is1Iterable != is2Iterable) {
			// they are not some. Only one of them is iterable
			if (is1Iterable) {
				if (isIterableOf(returnType1, returnType2)) {
					// use 1st method, which is multivalue
					// representation of 2nd method
					return 0;
				}
			} else {
				if (isIterableOf(returnType2, returnType1)) {
					// use 2nd method, which is multivalue
					// representation of 1st method
					return 1;
				}
			}
		}
		// else report ambiguity
		return -1;
	}

	/**
	 * @return true if item type of `iterableType` is super type of `itemType`
	 */
	private boolean isIterableOf(CtTypeReference<?> iterableType, CtTypeReference<?> itemType) {
		CtTypeReference<?> iterableItemType = getTypeofItems(iterableType);
		if (iterableItemType != null) {
			return itemType.isSubtypeOf(iterableItemType);
		}
		return false;
	}

	private int getIdxOfBestMatchByInputParameter(List<MMMethod> methods, MMMethodKind key, CtTypeReference<?> expectedValueType)  {
		int idx = -1;
		MatchLevel maxMatchLevel = null;
		if (key.isMulti()) {
			expectedValueType = getTypeofItems(expectedValueType);
		}

		for (int i = 0; i < methods.size(); i++) {
			MMMethod mMethod = methods.get(i);
			MatchLevel matchLevel = getMatchLevel(expectedValueType, mMethod.getValueType());
			if (matchLevel != null) {
				//it is matching
				if (idx == -1) {
					idx = i;
					maxMatchLevel = matchLevel;
				} else {
					//both methods have matching value type. Use the better match
					if (maxMatchLevel.ordinal() < matchLevel.ordinal()) {
						idx = i;
						maxMatchLevel = matchLevel;
					} else if (maxMatchLevel == matchLevel) {
						//there is conflict
						return -1;
					} //else OK, we already have better match
				}
			}
		}
		return idx;
	}

	private static CtTypeReference<?> getTypeofItems(CtTypeReference<?> valueType) {
		ContainerKind valueContainerType = containerKindOf(valueType.getActualClass());
		if (valueContainerType == ContainerKind.SINGLE) {
			return null;
		}
		CtTypeReference<?> itemValueType;
		if (valueContainerType == ContainerKind.MAP) {
			if (String.class.getName().equals(valueType.getActualTypeArguments().get(0).getQualifiedName()) == false) {
				throw new SpoonException("Unexpected container of type: " + valueType.toString());
			}
			itemValueType = valueType.getActualTypeArguments().get(1);
		} else {
			//List or Set
			itemValueType = valueType.getActualTypeArguments().get(0);
		}
		if (itemValueType instanceof CtTypeParameterReference) {
			itemValueType = ((CtTypeParameterReference) itemValueType).getBoundingType();
			if (itemValueType == null) {
				itemValueType = valueType.getFactory().Type().OBJECT;
			}
		}
		return itemValueType;
	}

	private enum MatchLevel {
		SUBTYPE,
		ERASED_EQUALS,
		EQUALS
	}

	/**
	 * Checks whether expectedType and realType are matching.
	 *
	 * @param expectedType
	 * @param realType
	 * @return new expectedType or null if it is not matching
	 */
	private MatchLevel getMatchLevel(CtTypeReference<?> expectedType, CtTypeReference<?> realType) {
		if (expectedType.equals(realType)) {
			return MatchLevel.EQUALS;
		}
		if (expectedType.getTypeErasure().equals(realType.getTypeErasure())) {
			return MatchLevel.ERASED_EQUALS;
		}
		if (expectedType.isSubtypeOf(realType)) {
			/*
			 * CtFieldReference<T> CtFieldAccess#getVariable() CtFieldAccess
			 * inherits from CtVariableAccess which has
			 * #setVariable(CtVariableReference<T>) it is OK to use expected
			 * type CtFieldReference<T>, when setter has CtVariableReference<T>
			 */
			return MatchLevel.SUBTYPE;
		}
		return null;
	}

	/**
	 * @param valueType whose Map value type is needed
	 * @return Map value type If valueType is an Map. null if it is not
	 */
	private CtTypeReference<?> getMapValueType(CtTypeReference<?> valueType) {
		if (valueType != null) {
			Factory f = valueType.getFactory();
			if (valueType.isSubtypeOf(f.Type().MAP) && valueType.getActualTypeArguments().size() == 2) {
				return valueType.getActualTypeArguments().get(1);
			}
		}
		return null;
	}

	/**
	 * @return true if this {@link MetamodelProperty} is derived in owner concept, ig has the annotation @{@link DerivedProperty}.
	 */
	public boolean isDerived() {
		if (derived == null) {
			if (getOwner().getKind() == ConceptKind.LEAF && isUnsettable()) {
				derived = Boolean.TRUE;
				return derived;
			}
			// by default it's derived
			derived = Boolean.FALSE;

			//if DerivedProperty is found on any getter of this type, then this field is derived
			MMMethod getter = getMethod(MMMethodKind.GET);
			if (getter == null) {
				throw new SpoonException("No getter defined for " + this);
			}
			CtTypeReference<DerivedProperty> derivedProperty = getter.getActualCtMethod().getFactory().createCtTypeReference(DerivedProperty.class);

			for (CtMethod<?> ctMethod : getter.getDeclaredMethods()) {
				if (ctMethod.getAnnotation(derivedProperty) != null) {
					derived = Boolean.TRUE;
					return derived;
				}
			}

			//inherit derived property from super type
			//if DerivedProperty annotation is not found on any get method, then it is not derived

			//check all super fields. If any of them is derived then this field is derived too
			for (MetamodelProperty superField : superProperties) {
				if (superField.isDerived()) {
					derived = Boolean.TRUE;
					return derived;
				}
			}
		}
		return derived;
	}

	/**
	 * @return true if this {@link MetamodelProperty} is unsettable in owner concept
	 * ie. if the property has the annotation @{@link UnsettableProperty}
	 */
	public boolean isUnsettable() {
		if (unsettable == null) {
			// by default it's unsettable
			unsettable = Boolean.FALSE;

			//if UnsettablePropertyis found on any setter of this type, then this field is unsettable
			MMMethod setter = getMethod(MMMethodKind.SET);
			if (setter == null) {
				unsettable = Boolean.TRUE;
				return unsettable;
			}
			CtTypeReference<UnsettableProperty> unsettableProperty = setter.getActualCtMethod().getFactory().createCtTypeReference(UnsettableProperty.class);

			for (CtMethod<?> ctMethod : setter.getDeclaredMethods()) {
				if (ctMethod.getAnnotation(unsettableProperty) != null) {
					unsettable = Boolean.TRUE;
					return unsettable;
				}
			}

		}
		return unsettable;
	}

	private List<MMMethod> getRoleMethods() {
		return Collections.unmodifiableList(roleMethods);
	}

	@Override
	public String toString() {
		return ownerConcept.getName() + "#" + getName() + "<" + valueType + ">";
	}

	/**
	 * @return the super {@link MetamodelProperty} which has same valueType and which is upper in the metamodel hierarchy
	 * For example:
	 * The super property of {@link CtField}#NAME is {@link CtNamedElement}#NAME
	 * This method can be used to optimize generated code.
	 */
	public MetamodelProperty getSuperProperty() {
		List<MetamodelProperty> potentialRootSuperFields = new ArrayList<>();
		if (!roleMethods.isEmpty()) {
			potentialRootSuperFields.add(this);
		}
		superProperties.forEach(superField -> {
			addUniqueObject(potentialRootSuperFields, superField.getSuperProperty());
		});
		int idx = 0;
		if (potentialRootSuperFields.size() > 1) {
			boolean needsSetter = getMethod(MMMethodKind.SET) != null;
			CtTypeReference<?> expectedValueType = this.getTypeOfField().getTypeErasure();
			for (int i = 1; i < potentialRootSuperFields.size(); i++) {
				MetamodelProperty superField = potentialRootSuperFields.get(i);
				if (superField.getTypeOfField().getTypeErasure().equals(expectedValueType) == false) {
					break;
				}
				if (needsSetter && superField.getMethod(MMMethodKind.SET) == null) {
					//this field has setter but the superField has no setter. We cannot used it as super
					break;
				}
				idx = i;
			}
		}
		return potentialRootSuperFields.get(idx);
	}

	private	static ContainerKind containerKindOf(Class<?> valueClass) {
		if (List.class.isAssignableFrom(valueClass)) {
			return ContainerKind.LIST;
		}
		if (Map.class.isAssignableFrom(valueClass)) {
			return ContainerKind.MAP;
		}
		if (Collection.class.isAssignableFrom(valueClass)) {
			return ContainerKind.SET;
		}
		return ContainerKind.SINGLE;
	}

	/**
	 * @return {@link RoleHandler} which can access runtime data of this Property
	 */
	public RoleHandler getRoleHandler() {
		if (roleHandler == null) {
			//initialize it lazily, because CtGenerationTest#testGenerateRoleHandler needs metamodel to generate rolehandlers
			//and here it may happen that rolehandler doesn't exist yet
			roleHandler = RoleHandlerHelper.getRoleHandler((Class) ownerConcept.getMetamodelInterface().getActualClass(), role);
		}
		return roleHandler;
	}

	static boolean useRuntimeMethodInvocation = false;

	/**
	 * @param element an instance whose attribute value is read
	 * @return a value of attribute defined by this {@link MetamodelProperty} from the provided `element`
	 */
	public <T, U> U getValue(T element) {
		if (useRuntimeMethodInvocation) {
			MMMethod method = getMethod(MMMethodKind.GET);
			if (method != null) {
				Method rtMethod = RtHelper.getMethod(getOwner().getImplementationClass().getActualClass(), method.getName(), 0);
				if (rtMethod != null) {
					try {
						return (U) rtMethod.invoke(element);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new SpoonException("Invocation of getter on " + toString() + " failed", e);
					}
				}
				throw new SpoonException("Cannot invoke getter on " + toString());
			}
		}
		return getRoleHandler().getValue(element);
	}

	/**
	 * @param element an instance whose attribute value is set
	 * @param value to be set value of attribute defined by this {@link MetamodelProperty} on the provided `element`
	 */
	public <T, U> void setValue(T element, U value) {
		if (useRuntimeMethodInvocation) {
			MMMethod method = getMethod(MMMethodKind.SET);
			if (method != null) {
				Method rtMethod = RtHelper.getMethod(getOwner().getImplementationClass().getActualClass(), method.getName(), 1);
				if (rtMethod != null) {
					try {
						rtMethod.invoke(element, value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new SpoonException("Invocation of setter on " + toString() + " failed", e);
					}
					return;
				}
				throw new SpoonException("Cannot invoke setter on " + toString());
			}
		}
		getRoleHandler().setValue(element, value);
	}

}
