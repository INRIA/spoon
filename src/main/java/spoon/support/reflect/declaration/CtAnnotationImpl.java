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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
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
			
			//This is done here because return types should not be CT types; CtLiteral<String> vs String.
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
				return super.put(key, getFactory().Type().createReference(
						(Class<?>) value));
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
	private Object convertValue(Object value) {
		if (value instanceof CtFieldReference) {
			Class c = ((CtFieldReference) value).getDeclaringType()
					.getActualClass();
			if (((CtFieldReference) value).getSimpleName().equals("class")) {
				return c;
			}
			CtField field = ((CtFieldReference) value).getDeclaration();
			if (Enum.class.isAssignableFrom(c)) {
				// Value references a Enum field
				return Enum.valueOf(c, ((CtFieldReference) value)
						.getSimpleName());
			}
			// Value is a static final
			if (field != null) {
				return convertValue(field.getDefaultExpression());
			} else {
				try {
					return ((Field) ((CtFieldReference) value).getActualField())
							.get(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		} else if (value instanceof CtFieldAccess) {
			// Get variable
			return convertValue(((CtFieldAccess) value).getVariable());
		} else if (value instanceof CtAnnotation) {
			// Get proxy
			return ((CtAnnotation) value).getActualAnnotation();
		} else if (value instanceof CtLiteral) {
			// Replace literal by his value
			return ((CtLiteral) value).getValue();
		} else if (value instanceof CtCodeElement) {
			// Evaluate code elements
			PartialEvaluator eval = getFactory().Eval()
					.createPartialEvaluator();
			Object ret = eval.evaluate(((CtCodeElement) value).getParent(),
					(CtCodeElement) value);
			if (!(ret instanceof CtCodeElement)) {
				return convertValue(ret);
			}
			
			return ret;
		} else if (value instanceof CtTypeReference) {
			// Get RT class for References
			return ((CtTypeReference) value).getActualClass();
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

	public Object getElementValue(String key) {
		Object ret = null;
		ret = elementValues.get(key);
		if (ret == null) {
			ret = getDefaultValue(key);
		}
		if (ret == null) {
			ret = getReflectValue(key);
		}

		Class<?> type = getElementType(key);

		if (type.isArray()) {
			if (!(ret instanceof Collection)) {
				List<Object> lst = new ArrayList<Object>();

				if (ret.getClass().isArray()) {
					Object[] temp = (Object[]) ret;
					lst.addAll(Arrays.asList(temp));
				} else {
					lst.add(ret);
				}
				ret = lst;

			}
			Collection<?> col = (Collection<?>) ret;
			Object[] array = (Object[]) Array.newInstance(type
					.getComponentType(), col.size());
			int i = 0;
			for (Object obj : col) {
				array[i++] = convertValue(obj);
			}
			ret = array;
		} else {
			ret = convertValue(ret);
		}

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
		}
		return ret;
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
}
