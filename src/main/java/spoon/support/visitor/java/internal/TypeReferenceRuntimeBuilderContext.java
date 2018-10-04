/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeReferenceRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private CtTypeReference<?> typeReference;
	private Type type;
	private Map<String, CtTypeParameter> mapTypeParameters;

	public TypeReferenceRuntimeBuilderContext(Type type, CtTypeReference<?> typeReference) {
		super(typeReference);
		this.typeReference = typeReference;
		this.type = type;
		this.mapTypeParameters = new HashMap<>();
	}

	@Override
	public void addPackage(CtPackage ctPackage) {
		typeReference.setPackage(ctPackage.getReference());
	}

	@Override
	public void addTypeReference(CtRole role, CtTypeReference<?> ctTypeReference) {
		switch (role) {
		case DECLARING_TYPE:
			this.typeReference.setDeclaringType(ctTypeReference);
			return;
		case BOUNDING_TYPE:
		case SUPER_TYPE:
			if (typeReference instanceof CtWildcardReference) {
				((CtWildcardReference) typeReference).setBoundingType(ctTypeReference);
			} else {
				//Strange case?
				this.getClass();
			}
			return;
		case TYPE_ARGUMENT:
			typeReference.addActualTypeArgument(ctTypeReference);
			return;
		}
		super.addTypeReference(role, ctTypeReference);
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		typeReference.addAnnotation(ctAnnotation);
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		typeReference.addActualTypeArgument(parameterRef.getReference());
		this.mapTypeParameters.put(parameterRef.getSimpleName(), parameterRef);
	}

	@Override
	public CtTypeParameter getTypeParameter(GenericDeclaration genericDeclaration, String string) {
		return type == genericDeclaration ? this.mapTypeParameters.get(string) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final TypeReferenceRuntimeBuilderContext that = (TypeReferenceRuntimeBuilderContext) o;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeReference);
	}
}
