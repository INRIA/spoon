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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.visitor.ClassTypingContext;

/**
 * Represents a type of Spoon model AST node
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
	/**
	 * List of sub types of this type
	 */
	private final List<MMType> subTypes = new ArrayList<>();

	/**
	 * The {@link CtClass} linked to this {@link MMType}. Is null in case of class without interface
	 */
	private CtClass<?> modelClass;
	/**
	 * The {@link CtInterface} linked to this {@link MMType}. Is null in case of interface without class
	 */
	private CtInterface<?> modelInterface;

	/**
	 * {@link ClassTypingContext} of this type used to adapt methods from super type implementations to this {@link MMType}
	 */
	private ClassTypingContext typeContext;

	/**
	 * own methods of MMType, which does not belong to any role
	 */
	final List<CtMethod<?>> otherMethods = new ArrayList<>();

	MMType() {
		super();
	}

	/**
	 * @return interface name of {@link MMType}. For example CtClass, CtForEach, ...
	 * It is never followed by xxxImpl
	 */
	public String getName() {
		return name;
	}


	MMField getOrCreateMMField(CtRole role) {
		return SpoonMetaModel.getOrCreate(role2field, role, () -> new MMField(role.getCamelCaseName(), role, this));
	}

	/**
	 * @return kind of this {@link MMType}. Is it helper type or type of real AST node?
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
	 * @return map of {@link MMField}s by their {@link CtRole}
	 */
	public Map<CtRole, MMField> getRole2field() {
		return Collections.unmodifiableMap(role2field);
	}

	/**
	 * @return super types
	 */
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

	/**
	 * @return {@link CtClass} which represents this {@link MMType}
	 */
	public CtClass<?> getModelClass() {
		return modelClass;
	}

	void setModelClass(CtClass<?> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * @return {@link CtInterface} which represents this {@link MMType}
	 */
	public CtInterface<?> getModelInterface() {
		return modelInterface;
	}

	void setModelInterface(CtInterface<?> modelInterface) {
		this.modelInterface = modelInterface;
	}

	/**
	 * @return {@link ClassTypingContext}, which can be used to adapt super type methods to this {@link MMType}
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
