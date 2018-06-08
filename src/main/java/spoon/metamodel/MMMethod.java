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

import static spoon.metamodel.Metamodel.addUniqueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.SpoonException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.MethodTypingContext;

/**
 * Represents a method used to get or set a {@link MetamodelProperty} of a {@link MetamodelConcept}.
 */
public class MMMethod {
	private final MetamodelProperty ownerField;
	private final CtMethod<?> method;
	private final List<CtMethod<?>> ownMethods = new ArrayList<>();
	private final List<MMMethod> superMethods = new ArrayList<>();
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
	CtMethod<?> getFirstOwnMethod(MetamodelConcept targetType) {
		for (CtMethod<?> ctMethod : ownMethods) {
			if (targetType.getTypeContext().isSubtypeOf(ctMethod.getDeclaringType().getReference())) {
				return ctMethod;
			}
		}
		for (MMMethod mmMethod : superMethods) {
			CtMethod<?> m = mmMethod.getFirstOwnMethod(targetType);
			if (m != null) {
				return m;
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
	 * Adds a `mmMethod` as super method of this {@link MMMethod}
	 * @param mmMethod
	 */
	void addSuperMethod(MMMethod mmMethod) {
		addUniqueObject(superMethods, mmMethod);
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
	 * @return {@link CtMethod}s, which are declared directly in the {@link MetamodelConcept}, that are related to the same {@link MetamodelProperty}.
	 * (It does not return methods which are inherited from super types.)
	 */
	public List<CtMethod<?>> getDeclaredMethods() {
		return Collections.unmodifiableList(ownMethods);
	}

	void addOwnMethod(CtMethod<?> method) {
		ownMethods.add(method);
	}

	/**
	 * @return List of {@link MMMethod}s, which comes from super types of type getOwnType()
	 */
	public List<MMMethod> getSuperMethods() {
		return Collections.unmodifiableList(superMethods);
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
