/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.support.reflect.declaration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import spoon.Launcher;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotation}.
 *
 * @author Renaud Pawlak
 */
public class CtAnnotationImpl<A extends Annotation> extends CtElementImpl
		implements CtAnnotation<A> {
	class AnnotationInvocationHandler implements InvocationHandler {
		CtAnnotation<? extends Annotation> annotation;

		public AnnotationInvocationHandler(
				CtAnnotation<? extends Annotation> annotation) {
			super();
			this.annotation = annotation;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String fieldname = method.getName();
			if (fieldname.equals("toString")) {
				return CtAnnotationImpl.this.toString();
			} else if (fieldname.equals("annotationType")) {
				return annotation.getAnnotationType().getActualClass();
			}
			Object ret = getElementValue(fieldname);

			// This is done here because return types should not be CT types;
			// CtLiteral<String> vs String.
			if (ret instanceof CtLiteral<?>) {
				CtLiteral<?> l = (CtLiteral<?>) ret;
				return l.getValue();
			}

			return ret;
		}
	}

	private static final long serialVersionUID = 1L;

	CtTypeReference<A> annotationType;

	Map<String, Object> elementValues = new TreeMap<String, Object>() {

		private static final long serialVersionUID = 3501647177461995350L;

		@Override
		public Object put(String key, Object value) {
			if (value instanceof Class[]) {
				Class<?>[] valsNew = (Class<?>[]) value;
				ArrayList<CtTypeReference<?>> ret = new ArrayList<CtTypeReference<?>>(
						valsNew.length);

				for (int i = 0; i < valsNew.length; i++) {
					Class<?> class1 = valsNew[i];
					ret.add(i, getFactory().Type().createReference(class1));
				}
				return super.put(key, ret);
			}
			if (value instanceof Class) {
				return super.put(key,
						getFactory().Type().createReference((Class<?>) value));
			}
			return super.put(key, value);

		}

	};

	public CtAnnotationImpl() {
		super();
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtAnnotation(this);
	}

	protected void appendValues(String elementName, Object... values) {
		if (!elementValues.containsKey(elementName)) {
			elementValues.put(elementName, values);
		} else {
			Object o = elementValues.get(elementName);
			if (o.getClass().isArray()) {
				List<Object> tmp = new ArrayList<Object>();
				Object[] old = (Object[]) o;
				for (Object a : old) {
					tmp.add(a);
				}
				for (Object a : values) {
					tmp.add(a);
				}
				elementValues.put(elementName, tmp.toArray());
			} else {
				// o is not a array
				if (values.length > 1) {
					throw new RuntimeException(
							"Cannot add array to a non-array value");
				}
				elementValues.put(elementName, values[0]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public A getActualAnnotation() {
		return (A) Proxy.newProxyInstance(annotationType.getActualClass()
				.getClassLoader(), new Class[] { annotationType
				.getActualClass() }, new AnnotationInvocationHandler(this));
	}

	@SuppressWarnings("unchecked")
	private <E extends Enum<E>> Object convertValue(Object value) {
		if (value instanceof CtFieldReference) {
			Class<?> c = null;
			try {
				c = ((CtFieldReference<?>) value).getDeclaringType()
						.getActualClass();
			} catch (Exception e) {
				return ((CtLiteral<?>) ((CtFieldReference<?>) value)
						.getDeclaration().getDefaultExpression()
						.partiallyEvaluate()).getValue();
			}

			if (((CtFieldReference<?>) value).getSimpleName().equals("class")) {
				return c;
			}
			CtField<?> field = ((CtFieldReference<?>) value).getDeclaration();
			if (Enum.class.isAssignableFrom(c)) {
				// Value references a Enum field
				return Enum.valueOf((Class<E>) c,
						((CtFieldReference<?>) value).getSimpleName());
			}
			// Value is a static final
			if (field != null) {
				return convertValue(field.getDefaultExpression());
			} else {
				try {
					return ((Field) ((CtFieldReference<?>) value)
							.getActualField()).get(null);
				} catch (Exception e) {
					Launcher.logger.error(e.getMessage(), e);
				}
				return null;
			}
		} else if (value instanceof CtFieldAccess) {
			// Get variable
			return convertValue(((CtFieldAccess<?>) value).getVariable());
		}
		else if (value instanceof CtNewArray)
		{
			CtNewArray<?> arrayExpression = (CtNewArray<?>) value;

			Class<?> componentType = arrayExpression.getType().getActualClass().getComponentType();
			List<CtExpression<?>> elements = arrayExpression.getElements();

			Object array = Array.newInstance(componentType, elements.size());
			for (int i = 0; i < elements.size(); i++)
			{
				Array.set(array, i, this.convertValue(elements.get(i)));
			}

			return array;
		}
		else if (value instanceof CtAnnotation) {
			// Get proxy
			return ((CtAnnotation<?>) value).getActualAnnotation();
		} else if (value instanceof CtLiteral) {
			// Replace literal by his value
			return ((CtLiteral<?>) value).getValue();
		} else if (value instanceof CtCodeElement) {
			// Evaluate code elements
			PartialEvaluator eval = getFactory().Eval()
					.createPartialEvaluator();
			Object ret = eval.evaluate(null, (CtCodeElement) value);
			if (!(ret instanceof CtCodeElement)) {
				return convertValue(ret);
			}

			return ret;
		} else if (value instanceof CtTypeReference) {
			// Get RT class for References
			return ((CtTypeReference<?>) value).getActualClass();
		}
		return value;
	}

	private Class<?> getElementType(String name) {
		// Try by CT reflection
		CtSimpleType<?> t = getAnnotationType().getDeclaration();
		if (t != null) {
			CtField<?> f = t.getField(name);
			return f.getType().getActualClass();
		}
		// Try with RT reflection
		Class<?> c = getAnnotationType().getActualClass();
		for (Method m : c.getMethods()) {
			if (m.getName().equals(name)) {
				return m.getReturnType();
			}
		}
		return null;
	}

	public CtTypeReference<A> getAnnotationType() {
		return annotationType;
	}

	private Object getDefaultValue(String fieldName) {
		Object ret = null;
		CtAnnotationType<?> at = (CtAnnotationType<?>) getAnnotationType()
				.getDeclaration();
		if (at != null) {
			CtField<?> f = at.getField(fieldName);
			ret = f.getDefaultExpression();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T getElementValue(String key) {
		Object ret = this.elementValues.get(key);
		if (ret == null) {
			ret = getDefaultValue(key);
		}
		if (ret == null) {
			ret = getReflectValue(key);
		}

		Class<?> type = getElementType(key);

		ret = this.convertValue(ret);

		if (type.isPrimitive()) {
			if ((type == boolean.class) && (ret.getClass() != boolean.class)) {
				ret = Boolean.parseBoolean(ret.toString());
			} else if ((type == byte.class) && (ret.getClass() != byte.class)) {
				ret = Byte.parseByte(ret.toString());
			} else if ((type == char.class) && (ret.getClass() != char.class)) {
				ret = ret.toString().charAt(0);
			} else if ((type == double.class)
					&& (ret.getClass() != double.class)) {
				ret = Double.parseDouble(ret.toString());
			} else if ((type == float.class) && (ret.getClass() != float.class)) {
				ret = Float.parseFloat(ret.toString());
			} else if ((type == int.class) && (ret.getClass() != int.class)) {
				ret = Integer.parseInt(ret.toString());
			} else if ((type == long.class) && (ret.getClass() != long.class)) {
				ret = Long.parseLong(ret.toString());
			}
			else if (type == short.class && ret.getClass() != short.class)
			{
				ret = Short.parseShort(ret.toString());
			}
		}
		return (T) ret;
	}

	public Map<String, Object> getElementValues() {
		return elementValues;
	}

	private Object getReflectValue(String fieldname) {
		try {
			Class<?> c = getAnnotationType().getActualClass();
			Method m = c.getMethod(fieldname);
			return m.getDefaultValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void setAnnotationType(
			CtTypeReference<? extends Annotation> annotationType) {
		this.annotationType = (CtTypeReference<A>) annotationType;
	}

	public void setElementValues(Map<String, Object> values) {
		for (Entry<String, Object> e : values.entrySet()) {
			this.elementValues.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public CtElement getAnnotatedElement()
	{
		return this.getParent();
	}

	@Override
	public CtAnnotatedElementType getAnnotatedElementType()
	{
		CtElement annotatedElement = this.getAnnotatedElement();

		if (annotatedElement == null)
		{
			return null;
		}

		if (annotatedElement instanceof CtMethod)
		{
			return CtAnnotatedElementType.METHOD;
		}
		if (annotatedElement instanceof CtType)
		{
			return CtAnnotatedElementType.TYPE;
		}
		if (annotatedElement instanceof CtField)
		{
			return CtAnnotatedElementType.FIELD;
		}
		if (annotatedElement instanceof CtConstructor)
		{
			return CtAnnotatedElementType.CONSTRUCTOR;
		}
		if (annotatedElement instanceof CtParameter)
		{
			return CtAnnotatedElementType.PARAMETER;
		}
		if (annotatedElement instanceof CtLocalVariable)
		{
			return CtAnnotatedElementType.LOCAL_VARIABLE;
		}
		if (annotatedElement instanceof CtPackage)
		{
			return CtAnnotatedElementType.PACKAGE;
		}
		if (annotatedElement instanceof CtAnnotation || annotatedElement instanceof CtAnnotationType)
		{
			return CtAnnotatedElementType.ANNOTATION_TYPE;
		}
		return null;
	}
}
