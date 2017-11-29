/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.test.metamodel;

import static spoon.test.metamodel.SpoonMetaModel.addUniqueObject;
import static spoon.test.metamodel.SpoonMetaModel.getOrCreate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import spoon.SpoonException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

/**
 * Represents a field of Spoon model type.
 * Each MMField belongs to one MMType
 */
public class MMField {
	/**
	 * Name of the field
	 */
	private final String name;
	/**
	 * {@link CtRole} of the field
	 */
	private final CtRole role;
	/**
	 * The list of {@link MMType}s which contains this field
	 */
	private final MMType ownerType;
	/**
	 * Type of value container [single, list, set, map]
	 */
	private MMContainerType valueContainerType;
	/**
	 * The type of value of this field - can be Set, List, Map or any non collection type
	 */
	private CtTypeReference<?> valueType;
	/**
	 * The item type of value of this field - can be non collection type
	 */
	private CtTypeReference<?> itemValueType;

	private Boolean derived;

	private Map<MMMethodKind, List<MMMethod>> methodsByKind = new HashMap<>();

	/**
	 * methods of this field defined directly on ownerType.
	 * There is PropertyGetter or PropertySetter annotation with `role` of this {@link MMField}
	 */
	private final List<MMMethod> roleMethods = new ArrayList<>();
	/**
	 * methods of this field grouped by signature defined directly on ownerType.
	 * There is PropertyGetter or PropertySetter annotation with `role` of this {@link MMField}
	 * note: There can be up to 2 methods in this list. 1) declaration from interface, 2) implementation from class
	 */
	private final Map<String, MMMethod> roleMethodsBySignature = new HashMap<>();
	/**
	 * List of fields with same `role`, from super type of `ownerType` {@link MMType}
	 */
	private final List<MMField> superFields = new ArrayList<>();

	private List<MMMethodKind> ambiguousMethodKinds = new ArrayList<>();


	MMField(String name, CtRole role, MMType ownerType) {
		super();
		this.name = name;
		this.role = role;
		this.ownerType = ownerType;
	}

	void addMethod(CtMethod<?> method) {
		MMMethod mmMethod = getOwnMethod(method, true);
		mmMethod.addOwnMethod(method);
	}

	/**
	 * @param method
	 * @param createIfNotExist
	 * @return existing {@link MMMethod}, which overrides `method` or creates and registers new one if `createIfNotExist`==true
	 */
	MMMethod getOwnMethod(CtMethod<?> method, boolean createIfNotExist) {
		for (MMMethod mmMethod : roleMethods) {
			if (mmMethod.overrides(method)) {
				return mmMethod;
			}
		}
		if (createIfNotExist) {
			MMMethod mmMethod = new MMMethod(this, method);
			roleMethods.add(mmMethod);
			getOrCreate(methodsByKind, mmMethod.getMethodKind(), () -> new ArrayList<>()).add(mmMethod);
			MMMethod conflict = roleMethodsBySignature.put(mmMethod.getSignature(), mmMethod);
			if (conflict != null) {
				throw new SpoonException("Conflict on " + getOwnerType().getName() + "." + name + " method signature: " + mmMethod.getSignature());
			}
			return mmMethod;
		}
		return null;
	}

	void addSuperField(MMField superMMField) {
		if (addUniqueObject(superFields, superMMField)) {
			for (MMMethod superMethod : superMMField.getRoleMethods()) {
				getOwnMethod(superMethod.getFirstOwnMethod(getOwnerType()), true).addSuperMethod(superMethod);
			}
		}
	}

	public String getName() {
		return name;
	}

	public CtRole getRole() {
		return role;
	}

	public MMType getOwnerType() {
		return ownerType;
	}

	public MMContainerType getValueContainerType() {
		return valueContainerType;
	}

	public CtTypeReference<?> getValueType() {
		if (valueType == null) {
			throw new SpoonException("Model is not initialized yet");
		}
		return valueType;
	}

	CtTypeReference<?> detectValueType() {
		MMMethod mmGetMethod = getMethod(MMMethodKind.GET);
		if (mmGetMethod == null) {
			throw new SpoonException("No getter exists for " + getOwnerType().getName() + "." + getName());
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
		if (MMContainerType.valueOf(getterValueType.getActualClass()) != MMContainerType.SINGLE) {
			getterValueType = getItemValueType(getterValueType);
			setterValueType = getItemValueType(setterValueType);
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
		throw new SpoonException("Incompatible getter and setter for " + getOwnerType().getName() + "." + getName());
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
		this.valueContainerType = MMContainerType.valueOf(valueType.getActualClass());
		if (valueContainerType != MMContainerType.SINGLE) {
			itemValueType = getItemValueType(valueType);
		} else {
			itemValueType = valueType;
		}
	}

	public CtTypeReference<?> getItemValueType() {
		if (itemValueType == null) {
			getValueType();
		}
		return itemValueType;
	}
	public void setItemValueType(CtTypeReference<?> itemValueType) {
		this.itemValueType = itemValueType;
	}

	public MMMethod getMethod(MMMethodKind kind) {
		List<MMMethod> ms = getMethods(kind);
		return ms.size() > 0 ? ms.get(0) : null;
	}

	public List<MMMethod> getMethods(MMMethodKind kind) {
		List<MMMethod> ms = methodsByKind.get(kind);
		return ms == null ? Collections.emptyList() : Collections.unmodifiableList(ms);
	}
	
	/**
	 * @param consumer is called for each CtMethod of this field which is not covered by this meta model
	 */
	public void forEachUnhandledMethod(Consumer<CtMethod<?>> consumer) {
		methodsByKind.forEach((kind, mmMethods) -> {
			if(kind == MMMethodKind.OTHER) {
				mmMethods.forEach(mmMethod -> mmMethod.getOwnMethods().forEach(consumer));
			} else {
				if (mmMethods.size() > 1) {
					mmMethods.subList(1, mmMethods.size()).forEach(mmMethod -> mmMethod.getOwnMethods().forEach(consumer));
				}
			}
		});
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
		if (mmMethod.getMethod().getParameters().size() == 0) {
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
		CtTypeReference<?> returnType1 = methods.get(0).getMethod().getType();
		CtTypeReference<?> returnType2 = methods.get(1).getMethod().getType();
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
		CtTypeReference<?> iterableItemType = getItemValueType(iterableType);
		if (iterableItemType != null) {
			return itemType.isSubtypeOf(iterableItemType);
		}
		return false;
	}

	private int getIdxOfBestMatchByInputParameter(List<MMMethod> methods, MMMethodKind key, CtTypeReference<?> expectedValueType)  {
		int idx = -1;
		MatchLevel maxMatchLevel = null;
		CtTypeReference<?> newValueType = null;
		if (key.isMulti()) {
			expectedValueType = getItemValueType(expectedValueType);
		}

		for (int i = 0; i < methods.size(); i++) {
			MMMethod mMethod = methods.get(i);
			MatchLevel matchLevel = getMatchLevel(expectedValueType, mMethod.getValueType());
			if (matchLevel != null) {
				//it is matching
				if (idx == -1) {
					idx = i;
					maxMatchLevel = matchLevel;
					newValueType = mMethod.getValueType();
				} else {
					//both methods have matching value type. Use the better match
					if (maxMatchLevel.ordinal() < matchLevel.ordinal()) {
						idx = i;
						maxMatchLevel = matchLevel;
						newValueType = mMethod.getValueType();
					} else if (maxMatchLevel == matchLevel) {
						//there is conflict
						return -1;
					} //else OK, we already have better match
				}
			}
		}
		return idx;
	}

	private static CtTypeReference<?> getItemValueType(CtTypeReference<?> valueType) {
		MMContainerType valueContainerType = MMContainerType.valueOf(valueType.getActualClass());
		if (valueContainerType == MMContainerType.SINGLE) {
			return null;
		}
		CtTypeReference<?> itemValueType;
		if (valueContainerType == MMContainerType.MAP) {
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

	public boolean isDerived() {
		if (derived == null) {
			//if DerivedProperty is found on any getter of this type, then this field is derived
			MMMethod getter = getMethod(MMMethodKind.GET);
			if (getter == null) {
				throw new SpoonException("No getter defined for " + this);
			}
			CtTypeReference<DerivedProperty> derivedProperty = getter.getMethod().getFactory().createCtTypeReference(DerivedProperty.class);

			boolean isConreteMethod = false;
			for (CtMethod<?> ctMethod : getter.getOwnMethods()) {
				if (ctMethod.getAnnotation(derivedProperty) != null) {
					derived = Boolean.TRUE;
					return true;
				}
				isConreteMethod = isConreteMethod || ctMethod.getBody() != null;
			}
			if (isConreteMethod) {
				//there exists a implementation of getter for this field in this type and there is no  DerivedProperty here, so it is not derived!
				derived = Boolean.FALSE;
				return false;
			}
			//inherit derived property from super type
			//if DerivedProperty annotation is not found on any get method, then it is not derived
			derived = Boolean.FALSE;
			//check all super fields. If any of them is derived then this field is derived too
			for (MMField superField : superFields) {
				if (superField.isDerived()) {
					derived = Boolean.TRUE;
					break;
				}
			}
		}
		return derived;
	}

	public List<MMMethod> getRoleMethods() {
		return Collections.unmodifiableList(roleMethods);
	}

	public Map<String, MMMethod> getRoleMethodsBySignature() {
		return Collections.unmodifiableMap(roleMethodsBySignature);
	}

	public List<MMField> getSuperFields() {
		return Collections.unmodifiableList(superFields);
	}

	@Override
	public String toString() {
		return ownerType.getName() + "#" + getName() + "<" + valueType + ">";
	}

	/**
	 * @return the super MMField which has same valueType and which is in root of the most implementations
	 */
	public MMField getRootSuperField() {
		List<MMField> potentialRootSuperFields = new ArrayList<>();
		if (roleMethods.size() > 0) {
			potentialRootSuperFields.add(this);
		}
		superFields.forEach(superField -> {
			addUniqueObject(potentialRootSuperFields, superField.getRootSuperField());
		});
		int idx = 0;
		if (potentialRootSuperFields.size() > 1) {
			CtTypeReference<?> expectedValueType = this.getValueType().getTypeErasure();
			for (int i = 1; i < potentialRootSuperFields.size(); i++) {
				MMField superField = potentialRootSuperFields.get(i);
				if (superField.getValueType().getTypeErasure().equals(expectedValueType) == false) {
					break;
				}
				idx = i;
			}
		}
		return potentialRootSuperFields.get(idx);
	}
}
