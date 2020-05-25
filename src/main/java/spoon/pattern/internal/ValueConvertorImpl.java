/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal;

import java.util.List;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.SignaturePrinter;

/**
 * Converts the individual parameter values to required type
 */
public class ValueConvertorImpl implements ValueConvertor {

	public ValueConvertorImpl() {
	}

	@Override
	public <T> T getValueAs(Factory factory, String parameterName, Object value, Class<T> valueClass) {
		if (valueClass.isInstance(value)) {
			return cloneIfNeeded(valueClass.cast(value));
		}
		if (CtExpression.class.isAssignableFrom(valueClass)) {
			if (value instanceof Class) {
				return (T) factory.Code().createClassAccess(factory.Type().createReference((Class) value));
			}
			if (value instanceof CtTypeReference) {
				//convert type reference into code element as class access
				CtTypeReference<?> tr = (CtTypeReference<?>) value;
				return (T) factory.Code().createClassAccess(tr);
			}
			if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character) {
				//convert String to code element as Literal
				return (T) factory.Code().createLiteral(value);
			}
			if (value.getClass().isArray()) {
				Class<?> itemClass = value.getClass().getComponentType();
				if (CtExpression.class.isAssignableFrom(itemClass)) {
					CtNewArray<Object> arr = factory.Core().createNewArray().setType(factory.Type().objectType());
					for (CtExpression expr : (CtExpression[]) value) {
						arr.addElement(expr);
					}
					return (T) arr;
				}
				@SuppressWarnings({ "unchecked", "rawtypes" })
				CtNewArray<?> arr = factory.Core().createNewArray().setType(factory.Type().createArrayReference(itemClass.getName()));
				for (Object v : (Object[]) value) {
					if (v == null || v instanceof String || v instanceof Number || v instanceof Boolean || v instanceof Character) {
						//convert String to code element as Literal
						arr.addElement(factory.Code().createLiteral(v));
					} else {
						throw new SpoonException("Parameter value item class: " + v.getClass().getName() + " cannot be converted to class is: " + valueClass.getName());
					}
				}
				return (T) arr;
			}
		}
		if (CtStatement.class.isAssignableFrom(valueClass)) {
			if (value == null) {
				//skip null statements
				return null;
			}
			if (value instanceof List) {
				List list = (List) value;
				if (list.isEmpty()) {
					return null;
				}
				if (list.size() == 1) {
					return (T) list.get(0);
				}
				CtBlock block = factory.createBlock();
				block.setImplicit(true);
				for (CtStatement statement : ((Iterable<CtStatement>) value)) {
					block.addStatement(statement);
				}
				return (T) block;
			}
		}
		if (valueClass.equals(String.class)) {
			if (value instanceof CtNamedElement) {
				return (T) ((CtNamedElement) value).getSimpleName();
			} else if (value instanceof CtReference) {
				return (T) ((CtReference) value).getSimpleName();
			} else if (value instanceof Class) {
				return (T) ((Class) value).getSimpleName();
			} else if (value instanceof CtInvocation) {
				return (T) getShortSignatureForJavadoc(((CtInvocation<?>) value).getExecutable());
			} else if (value instanceof CtExecutableReference) {
				return (T) getShortSignatureForJavadoc((CtExecutableReference<?>) value);
			} else if (value instanceof CtExecutable) {
				return (T) getShortSignatureForJavadoc(((CtExecutable<?>) value).getReference());
			} else if (value instanceof CtLiteral) {
				Object val = ((CtLiteral<Object>) value).getValue();
				return val == null ? null : (T) val.toString();
			} else if (value instanceof Enum) {
				return (T) ((Enum) value).name();
			} else if (value == null) {
				throw new SpoonException("Missing parameter value for parameter `" + parameterName + "`");
			}
			throw new SpoonException("Parameter value has unexpected class: " + value.getClass().getName() + ", whose conversion to String is not supported");
		}
		if (CtTypeReference.class.isAssignableFrom(valueClass)) {
			if (value == null) {
				throw new SpoonException("The null value is not valid substitution for CtTypeReference");
			}
			if (value instanceof Class) {
				return (T) factory.Type().createReference((Class<?>) value);
			} else if (value instanceof CtTypeReference) {
				return (T) ((CtTypeReference<?>) value).clone();
			} else if (value instanceof CtType) {
				return (T) ((CtType<?>) value).getReference();
			} else if (value instanceof String) {
				return (T) factory.Type().createReference((String) value);
			} else {
				throw new RuntimeException("unsupported reference substitution");
			}
		}

		throw new SpoonException("Parameter value class: " + value.getClass().getName() + " cannot be converted to class is: " + valueClass.getName());
	}

	/*
	 * return the typical Javadoc style link Foo#method(). The class name is not fully qualified.
	 */
	private static String getShortSignatureForJavadoc(CtExecutableReference<?> ref) {
		SignaturePrinter sp = new SignaturePrinter();
		sp.writeNameAndParameters(ref);
		return ref.getDeclaringType().getSimpleName() + CtExecutable.EXECUTABLE_SEPARATOR + sp.getSignature();
	}

	@SuppressWarnings("unchecked")
	protected <T> T cloneIfNeeded(T value) {
		if (value instanceof CtElement) {
			return (T) ((CtElement) value).clone();
		}
		return value;
	}
}
