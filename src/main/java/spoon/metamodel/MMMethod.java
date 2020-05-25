/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.metamodel;

import spoon.SpoonException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.MethodTypingContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a method used to get or set a {@link MetamodelProperty} of a {@link MetamodelConcept}.
 */
public class MMMethod {
	private final MetamodelProperty ownerField;
	private final CtMethod<?> method;

	/** methods with the same role and same signature in the type hierarchy */
	private final List<CtMethod<?>> ownMethods = new ArrayList<>();

	private final String signature;
	private final MMMethodKind methodKind;

	/**
	 * Creates a {@link MMMethod} of a {@link MetamodelProperty}
	 * @param field a owner field
	 * @param method a method from ownerType or nearest super type
	 */
	MMMethod(MetamodelProperty field, CtMethod<?> method) {
		this.ownerField = field;
		//adapt method to scope of field.ownType
		MethodTypingContext mtc = new MethodTypingContext().setClassTypingContext(field.getOwner().getTypeContext()).setMethod(method);
		this.method = (CtMethod<?>) mtc.getAdaptationScope();
		signature = this.method.getSignature();
		methodKind = MMMethodKind.kindOf(this.method);
		this.addRelatedMethod(method);
	}

	/**
	 * @return a {@link CtMethod}, which represents this {@link MMMethod}
	 */
	public CtMethod<?> getActualCtMethod() {
		return method;
	}

	/**
	 * @return name of this {@link MMMethod}. It is equal to simple name of related {@link CtMethod}
	 */
	public String getName() {
		return method.getSimpleName();
	}

	/**
	 * @return signature of this method, without the declaring type
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @return kind of this method. Getter, setter, ...
	 */
	public MMMethodKind getKind() {
		return methodKind;
	}

	/**
	 * @return first own method in super type hierarchy of `targetType`
	 */
	CtMethod<?> getCompatibleMethod(MetamodelConcept targetType) {
		for (CtMethod<?> ctMethod : ownMethods) {
			if (targetType.getTypeContext().isSubtypeOf(ctMethod.getDeclaringType().getReference())) {
				return ctMethod;
			}
		}
		throw new SpoonException("No own method exists in type " + ownerField);
	}

	/**
	 * @param method
	 * @return true of this {@link MMMethod} overrides `method`. In different words, if it represents the same method
	 */
	public boolean overrides(CtMethod<?> method) {
		return ownerField.getOwner().getTypeContext().isOverriding(this.method, method);
	}

	/**
	 * @return the {@link MetamodelProperty} which is get or set by this {@link MMMethod}
	 */
	public MetamodelProperty getProperty() {
		return ownerField;
	}

	/**
	 * @return {@link MetamodelConcept} where this {@link MMMethod} belongs to
	 */
	public MetamodelConcept getOwner() {
		return getProperty().getOwner();
	}

	/**
	 * @return {@link CtMethod}s, which are declared in the {@link MetamodelConcept} or in the hierarchy, that have the same role and {@link MMMethodKind}.
	 */
	public List<CtMethod<?>> getDeclaredMethods() {
		return Collections.unmodifiableList(ownMethods);
	}

	void addRelatedMethod(CtMethod<?> method) {
		if (method.getDeclaringType().getSimpleName().endsWith("Impl")) {
			throw new SpoonException("the metametamodel should be entirely specified in the Spoon interfaces");
		}
		ownMethods.add(method);
	}

	/**
	 * @return the type returned by this method
	 */
	public CtTypeReference<?> getReturnType() {
		return method.getType();
	}

	/**
	 * @return a value type of this method
	 */
	public CtTypeReference<?> getValueType() {
		if (method.getParameters().isEmpty()) {
			return method.getType();
		}
		return method.getParameters().get(method.getParameters().size() - 1).getType();
	}

	@Override
	public String toString() {
		return getOwner().getName() + "#" + getSignature();
	}

}
