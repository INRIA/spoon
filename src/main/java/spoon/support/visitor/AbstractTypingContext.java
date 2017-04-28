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
package spoon.support.visitor;

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;

/**
 * Implements common adapting algorithm of {@link ClassTypingContext} and {@link MethodTypingContext}
 */
abstract class AbstractTypingContext implements GenericTypeAdapter {

	protected AbstractTypingContext() {
	}

	@Override
	public CtTypeReference<?> adaptType(CtTypeInformation type) {
		CtTypeReference<?> result;
		boolean isCopy = false;
		if (type instanceof CtTypeReference<?>) {
			if (type instanceof CtTypeParameterReference) {
				return adaptTypeParameterReference((CtTypeParameterReference) type);
			}
			result = (CtTypeReference<?>) type;
		} else {
			if (type instanceof CtTypeParameter) {
				return adaptTypeParameter((CtTypeParameter) type);
			}
			CtType<?> t = (CtType<?>) type;
			result = t.getFactory().Type().createReference(t, true);
			isCopy = true;
		}
		if (result.getActualTypeArguments().size() > 0) {
			//we have to adapt actual type arguments recursive too
			if (isCopy == false) {
				CtElement parent = result.getParent();
				result = result.clone();
				result.setParent(parent);
				List<CtTypeReference<?>> actTypeArgs = result.getActualTypeArguments();
				for (int i = 0; i < actTypeArgs.size(); i++) {
					actTypeArgs.set(i, adaptType(actTypeArgs.get(i)));
				}
			}
		}
		return result;
	}

	private CtTypeReference<?> adaptTypeParameterReference(CtTypeParameterReference typeParamRef) {
		if ((typeParamRef instanceof CtWildcardReference)) {
			return adaptTypeParameterReferenceBoundingType(typeParamRef, typeParamRef.getBoundingType());
		}
		CtTypeReference<?> typeRefAdapted = adaptTypeParameter(typeParamRef.getDeclaration());
		if (typeRefAdapted instanceof CtTypeParameterReference) {
			return adaptTypeParameterReferenceBoundingType((CtTypeParameterReference) typeRefAdapted, typeParamRef.getBoundingType());
		}
		return typeRefAdapted;
	}

	private CtTypeReference<?> adaptTypeParameterReferenceBoundingType(CtTypeParameterReference typeParamRef, CtTypeReference<?> boundingType) {
		CtTypeParameterReference typeParamRefAdapted = typeParamRef.clone();
		typeParamRefAdapted.setParent(typeParamRef.getParent());
		typeParamRefAdapted.setBoundingType(boundingType == null ? null : adaptType(boundingType));
		return typeParamRefAdapted;
	}

	/**
	 * adapts `typeParam` to the {@link CtTypeReference}
	 * of scope of this {@link GenericTypeAdapter}
	 * In can be {@link CtTypeParameterReference} again - depending actual type arguments of this {@link GenericTypeAdapter}.
	 *
	 * @param typeParam to be resolved {@link CtTypeParameter}
	 * @return {@link CtTypeReference} or {@link CtTypeParameterReference} adapted to scope of this {@link GenericTypeAdapter}
	 *  or null if `typeParam` cannot be adapted to target `scope`
	 */
	protected abstract CtTypeReference<?> adaptTypeParameter(CtTypeParameter typeParam);
}
