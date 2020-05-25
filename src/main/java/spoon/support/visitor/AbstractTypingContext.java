/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import java.util.ArrayList;
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
		if (!result.getActualTypeArguments().isEmpty()) {
			//we have to adapt actual type arguments recursive too
			if (isCopy == false) {
				CtElement parent = result.getParent();
				result = result.clone();
				result.setParent(parent);
				List<CtTypeReference<?>> actTypeArgs = new ArrayList<>(result.getActualTypeArguments());
				for (int i = 0; i < actTypeArgs.size(); i++) {
					CtTypeReference adaptedTypeArgs = adaptType(actTypeArgs.get(i));
					// for some type argument we might return null to avoid recursive calls
					if (adaptedTypeArgs != null) {
						actTypeArgs.set(i, adaptedTypeArgs.clone());
					}
				}
				result.setActualTypeArguments(actTypeArgs);
			}
		}
		return result;
	}

	private CtTypeReference<?> adaptTypeParameterReference(CtTypeParameterReference typeParamRef) {
		if ((typeParamRef instanceof CtWildcardReference)) {
			return adaptTypeParameterReferenceBoundingType((CtWildcardReference) typeParamRef, typeParamRef.getBoundingType());
		}
		return adaptTypeParameter(typeParamRef.getDeclaration());
	}

	private CtTypeReference<?> adaptTypeParameterReferenceBoundingType(CtWildcardReference typeParamRef, CtTypeReference<?> boundingType) {
		CtWildcardReference typeParamRefAdapted = typeParamRef.clone();
		typeParamRefAdapted.setParent(typeParamRef.getParent());
		typeParamRefAdapted.setBoundingType(boundingType.equals(boundingType.getFactory().Type().getDefaultBoundingType()) ? boundingType.getFactory().Type().getDefaultBoundingType() : adaptType(boundingType));
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
