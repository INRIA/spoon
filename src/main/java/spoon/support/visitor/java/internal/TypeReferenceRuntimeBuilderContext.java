/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;

public class TypeReferenceRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private CtTypeReference<?> typeReference;

	public TypeReferenceRuntimeBuilderContext(CtTypeReference<?> typeReference) {
		super(typeReference);
		this.typeReference = typeReference;
	}

	@Override
	public void addPackage(CtPackage ctPackage) {
		typeReference.setPackage(ctPackage.getReference());
	}

	@Override
	public void addClassReference(CtTypeReference<?> typeReference) {
		this.typeReference.setDeclaringType(typeReference);
	}

	@Override
	public void addTypeName(CtTypeReference<?> ctTypeReference) {
		if (typeReference instanceof CtTypeParameterReference) {
			((CtTypeParameterReference) typeReference).addBound(ctTypeReference);
			return;
		}
		typeReference.addActualTypeArgument(ctTypeReference);
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		typeReference.addAnnotation(ctAnnotation);
	}
}
