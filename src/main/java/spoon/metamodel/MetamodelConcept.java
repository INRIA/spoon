/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.metamodel;

import static spoon.metamodel.Metamodel.addUniqueObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.support.visitor.ClassTypingContext;

/**
 * Represents a concept of the Spoon metamodel (eg {@link CtClass}).
 */
public class MetamodelConcept {
	/**
	 * Kind of this concept
	 */
	private ConceptKind kind;
	/**
	 * Name of the concept
	 */
	private final String name;
	/**
	 * Map of {@link CtRole} to {@link MetamodelProperty}s with values ordered same like CtScanner scans these properties when visiting this {@link MetamodelConcept}
	 */
	private final Map<CtRole, MetamodelProperty> role2Property = new LinkedHashMap<>();

	/**
	 * List of super concepts of this concept
	 */
	private final List<MetamodelConcept> superConcepts = new ArrayList<>();
	/**
	 * List of sub concepts of this concept
	 */
	private final List<MetamodelConcept> subConcepts = new ArrayList<>();

	/**
	 * The {@link CtClass} linked to this {@link MetamodelConcept}. Is null in case of class without interface
	 */
	private CtClass<?> modelClass;
	/**
	 * The {@link CtInterface} linked to this {@link MetamodelConcept}. Is null in case of interface without class
	 */
	private CtInterface<?> modelInterface;

	/**
	 * {@link ClassTypingContext} of this concept used to adapt methods from super type implementations to this {@link MetamodelConcept}
	 */
	private ClassTypingContext typeContext;

	/**
	 * own methods of {@link MetamodelConcept}, which does not belong to any role
	 */
	final List<CtMethod<?>> otherMethods = new ArrayList<>();

	MetamodelConcept(String name) {
		this.name = name;
	}

	/**
	 * @return interface name of {@link MetamodelConcept}. For example CtClass, CtForEach, ...
	 * It is never followed by xxxImpl
	 */
	public String getName() {
		return name;
	}


	MetamodelProperty getOrCreateMMField(CtRole role) {
		return Metamodel.getOrCreate(role2Property, role, () -> new MetamodelProperty(role.getCamelCaseName(), role, this));
	}

	/**
	 * @return kind of this {@link MetamodelConcept}.
	 */
	public ConceptKind getKind() {
		if (kind == null) {
			if (modelClass == null && modelInterface == null) {
				return null;
			} else {
				// we first consider interface
				if (modelClass == null) {
					this.kind = ConceptKind.ABSTRACT;
				} else {
					if (modelClass.hasModifier(ModifierKind.ABSTRACT)) {
						this.kind = ConceptKind.ABSTRACT;
					} else {
						this.kind = ConceptKind.LEAF;
					}
				}
			}
		}
		return kind;
	}

	/**
	 * @return map of {@link MetamodelProperty}s by their {@link CtRole}
	 */
	public Map<CtRole, MetamodelProperty> getRoleToProperty() {
		return Collections.unmodifiableMap(role2Property);
	}

	/**
	 * @return Collection of all {@link MetamodelProperty} of current {@link MetamodelConcept}
	 * Note: actually is the order undefined
	 * TODO: return List in the same order like it is scanned by CtScanner
	 */
	public Collection<MetamodelProperty> getProperties() {
		return Collections.unmodifiableCollection(role2Property.values());
	}

	/**
	 * @param role a {@link CtRole}
	 * @return {@link MetamodelProperty} for `role` of this concept
	 */
	public MetamodelProperty getProperty(CtRole role) {
		return role2Property.get(role);
	}

	/**
	 * @return super types
	 */
	public List<MetamodelConcept> getSuperConcepts() {
		return superConcepts;
	}

	void addSuperConcept(MetamodelConcept superType) {
		if (superType == this) {
			throw new SpoonException("Cannot add supertype to itself");
		}
		if (addUniqueObject(superConcepts, superType)) {
			superType.subConcepts.add(this);
			superType.role2Property.forEach((role, superMMField) -> {
				MetamodelProperty mmField = getOrCreateMMField(role);
				mmField.addSuperField(superMMField);
			});
		}
	}

	/**
	 * @return {@link CtClass} which represents this {@link MetamodelConcept}
	 */
	public CtClass<?> getImplementationClass() {
		return modelClass;
	}

	void setModelClass(CtClass<?> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * @return {@link CtInterface} which represents this {@link MetamodelConcept}
	 */
	public CtInterface<?> getMetamodelInterface() {
		return modelInterface;
	}

	void setModelInterface(CtInterface<?> modelInterface) {
		this.modelInterface = modelInterface;
	}

	/**
	 * @return {@link ClassTypingContext}, which can be used to adapt super type methods to this {@link MetamodelConcept}
	 *
	 * (package protected, not in the public API)
	 */
	ClassTypingContext getTypeContext() {
		if (typeContext == null) {
			typeContext = new ClassTypingContext(modelClass != null ? modelClass : modelInterface);
		}
		return typeContext;
	}

	@Override
	public String toString() {
		return getName();
	}
}
