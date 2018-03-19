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

import java.util.ArrayList;
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
 * Represents a concept of Spoon model AST node
 */
public class MetamodelConcept {
	/**
	 * Kind of this concept
	 */
	MMTypeKind kind;
	/**
	 * Name of the concept
	 */
	String name;
	/**
	 * Map of {@link CtRole} to {@link MetamodelProperty}s with values ordered same like CtScanner scans these properties when visiting this {@link MetamodelConcept}
	 */
	final Map<CtRole, MetamodelProperty> role2Property = new LinkedHashMap<>();

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

	MetamodelConcept() {
		super();
	}

	/**
	 * @return interface name of {@link MetamodelConcept}. For example CtClass, CtForEach, ...
	 * It is never followed by xxxImpl
	 */
	public String getName() {
		return name;
	}


	MetamodelProperty getOrCreateMMField(CtRole role) {
		return SpoonMetaModel.getOrCreate(role2Property, role, () -> new MetamodelProperty(role.getCamelCaseName(), role, this));
	}

	/**
	 * @return kind of this {@link MetamodelConcept}. Is it abstract concept or concept of leaf AST node?
	 */
	public MMTypeKind getKind() {
		if (kind == null) {
			if (modelClass == null && modelInterface == null) {
				return null;
			} else {
				// we first consider interface
				if (modelClass == null) {
					this.kind = MMTypeKind.ABSTRACT;
				} else {
					if (modelClass.hasModifier(ModifierKind.ABSTRACT)) {
						this.kind = MMTypeKind.ABSTRACT;
					} else {
						this.kind = MMTypeKind.LEAF;
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
	public CtClass<?> getModelClass() {
		return modelClass;
	}

	void setModelClass(CtClass<?> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * @return {@link CtInterface} which represents this {@link MetamodelConcept}
	 */
	public CtInterface<?> getModelInterface() {
		return modelInterface;
	}

	void setModelInterface(CtInterface<?> modelInterface) {
		this.modelInterface = modelInterface;
	}

	/**
	 * @return {@link ClassTypingContext}, which can be used to adapt super type methods to this {@link MetamodelConcept}
	 */
	public ClassTypingContext getTypeContext() {
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
