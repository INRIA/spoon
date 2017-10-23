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
package spoon.metamodel;

import java.util.ArrayList;
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

import static spoon.metamodel.SpoonMetaModel.addUniqueObject;

/**
 * Represents a type of Spoon model class
 */
public class MMType {
	/**
	 * Kind of this type
	 */
	MMTypeKind kind;
	/**
	 * Name of the type
	 */
	String name;
	/**
	 * List of fields ordered same like CtScanner scans them
	 */
	final Map<CtRole, MMField> role2field = new LinkedHashMap<>();

	/**
	 * List of super types of this type
	 */
	private final List<MMType> superTypes = new ArrayList<>();
	private final List<MMType> subTypes = new ArrayList<>();

	/**
	 * The {@link CtClass} linked to this {@link MMType}. Is null in case of class without interface
	 */
	private CtClass<?> modelClass;
	/**
	 * The {@link CtInterface} linked to this {@link MMType}. Is null in case of interface without class
	 */
	private CtInterface<?> modelInteface;

	private ClassTypingContext typeContext;

	/**
	 * own methods of MMType, which does not belong to any role
	 */
	final List<CtMethod<?>> otherMethods = new ArrayList<>();

	MMType() {
		super();
	}

	MMField getOrCreateMMField(CtRole role) {
		return SpoonMetaModel.getOrCreate(role2field, role, () -> new MMField(role.getCamelCaseName(), role, this));
	}

	public MMTypeKind getKind() {
		if (kind == null) {
			if (modelClass != null && modelClass.hasModifier(ModifierKind.ABSTRACT) == false) {
				kind = MMTypeKind.LEAF;
			} else {
				kind = MMTypeKind.ABSTRACT;
			}
		}
		return kind;
	}

	public String getName() {
		return name;
	}

	public Map<CtRole, MMField> getRole2field() {
		return role2field;
	}

	public List<MMType> getSuperTypes() {
		return superTypes;
	}

	void addSuperType(MMType superType) {
		if (superType == this) {
			throw new SpoonException("Cannot add supertype to itself");
		}
		if (addUniqueObject(superTypes, superType)) {
			superType.subTypes.add(this);
			superType.role2field.forEach((role, superMMField) -> {
				MMField mmField = getOrCreateMMField(role);
				mmField.addSuperField(superMMField);
			});
		}
	}

	public CtClass<?> getModelClass() {
		return modelClass;
	}

	void setModelClass(CtClass<?> modelClass) {
		this.modelClass = modelClass;
	}

	public CtInterface<?> getModelInteface() {
		return modelInteface;
	}

	void setModelInteface(CtInterface<?> modelInteface) {
		this.modelInteface = modelInteface;
	}

	@Override
	public String toString() {
		return getName();
	}

	public ClassTypingContext getTypeContext() {
		if (typeContext == null) {
			typeContext = new ClassTypingContext(modelClass != null ? modelClass : modelInteface);
		}
		return typeContext;
	}
}
