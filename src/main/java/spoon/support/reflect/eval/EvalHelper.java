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
package spoon.support.reflect.eval;

import spoon.SpoonException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;

import java.lang.reflect.Array;
import java.util.List;

public class EvalHelper {

	// this class contains only static methods
	private EvalHelper() {
	}

	/**
	 * Convert CtExpression (mostly) to their equivalent runtime objects
	 * eg CtLiteral(3) -> 3
	 */
	@SuppressWarnings("unchecked")
	public static Object convertElementToRuntimeObject(CtElement value) {
		if (value instanceof CtExpression) {
			CtExpression evaled = ((CtExpression) value).partiallyEvaluate();
			return getCorrespondingRuntimeObject(evaled);
		}
		throw new SpoonException("not possible to convert to runtime object " + value);
	}


	/** returns true if the value is known at compile tome */
	public static boolean isKnownAtCompileTime(CtExpression<?> exp) {
		if (exp instanceof CtLiteral) {
			return true;
		}

		if (exp instanceof CtTypeAccess) {
			return true;
		}

		return false;
	}

	/** returns the evaluated expression */
	public static <T> CtExpression<T> getEvaluatedExpression(CtExpression<T> exp) {
		return exp.partiallyEvaluate();
	}

	public static Object getCorrespondingRuntimeObject(CtExpression<?> value) {
		if (value instanceof CtNewArray) {
			return toArray((CtNewArray) value);
		} else if (value instanceof CtAnnotation) {
			// Get proxy
			return ((CtAnnotation<?>) value).getActualAnnotation();
		} else if (value instanceof CtLiteral) {
			// Replace literal by his value
			return ((CtLiteral<?>) value).getValue();
		} else if (value instanceof CtFieldRead) {
			CtFieldReference<?> fieldRef = ((CtFieldRead<?>) value).getVariable();
			Class<?> c;
			try {
				c = fieldRef.getDeclaringType().getActualClass();
			} catch (Exception e) {
				throw new SpoonException();
			}
			CtField<?> field = fieldRef.getDeclaration();
			if (Enum.class.isAssignableFrom(c)) {
				// Value references a Enum field
				return Enum.valueOf((Class<? extends Enum>) c, fieldRef.getSimpleName());
			}
		}

			throw new SpoonException("not possible to transform to runtime object " + value + " " + value.getClass().getName());
	}

	/** creating a real low level Java array from a CtNewArray */
	private static Object toArray(CtNewArray value) {
		CtNewArray<?> arrayExpression = (CtNewArray<?>) value;

		Class<?> componentType = arrayExpression.getType().getActualClass().getComponentType();
		List<CtExpression<?>> elements = arrayExpression.getElements();

		Object array = Array.newInstance(componentType, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Array.set(array, i, convertElementToRuntimeObject(elements.get(i)));
		}

		return array;
	}



}


