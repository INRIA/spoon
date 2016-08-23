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
package spoon.support.reflect.declaration;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.code.CtExpressionImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotation}.
 *
 * @author Renaud Pawlak
 */
public class CtAnnotationImpl<A extends Annotation> extends CtExpressionImpl<A> implements CtAnnotation<A> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<A> annotationType;

	private Map<String, CtExpression> elementValues = new TreeMap<>();

	public CtAnnotationImpl() {
		super();
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtAnnotation(this);
	}

	@Override
	public <T extends CtAnnotation<A>> T addValue(String elementName, Object value) {
		if (value instanceof CtExpression) {
			return addValueExpression(elementName, (CtExpression<?>) value);
		}
		return this.addValueExpression(elementName, convertValueToExpression(value));
	}

	private CtExpression convertValueToExpression(Object value) {
		CtExpression res;
		if (value.getClass().isArray()) {
			// Value should be converted to a CtNewArray.
			res = getFactory().Core().createNewArray();
			Object[] values = (Object[]) value;
			res.setType(getFactory().Type().createArrayReference(getFactory().Type().createReference(values[0].getClass())));
			for (Object o : values) {
				((CtNewArray) res).addElement(convertValueToExpression(o));
			}
		} else if (value instanceof Collection) {
			// Value should be converted to a CtNewArray.
			res = getFactory().Core().createNewArray();
			Collection values = (Collection) value;
			res.setType(getFactory().Type().createArrayReference(getFactory().Type().createReference(values.toArray()[0].getClass())));
			for (Object o : values) {
				((CtNewArray) res).addElement(convertValueToExpression(o));
			}
		} else if (value instanceof Class) {
			// Value should be a field access to a .class.
			res = getFactory().Code().createClassAccess(getFactory().Type().createReference((Class) value));
		} else if (value instanceof Field) {
			// Value should be a field access to a field.
			CtFieldReference<Object> variable = getFactory().Field().createReference((Field) value);
			variable.setStatic(true);
			CtTypeAccess target = getFactory().Code().createTypeAccess(getFactory().Type().createReference(((Field) value).getDeclaringClass()));
			CtFieldRead fieldRead = getFactory().Core().createFieldRead();
			fieldRead.setVariable(variable);
			fieldRead.setTarget(target);
			fieldRead.setType(target.getAccessedType());
			res = fieldRead;
		} else if (isPrimitive(value.getClass()) || value instanceof String) {
			// Value should be a literal.
			res = getFactory().Code().createLiteral(value);
		} else if (value.getClass().isEnum()) {
			final CtTypeReference declaringClass = getFactory().Type().createReference(((Enum) value).getDeclaringClass());
			final CtFieldReference variableRef = getFactory().Field().createReference(declaringClass, declaringClass, ((Enum) value).name());
			CtTypeAccess target = getFactory().Code().createTypeAccess(declaringClass);
			CtFieldRead fieldRead = getFactory().Core().createFieldRead();
			fieldRead.setVariable(variableRef);
			fieldRead.setTarget(target);
			fieldRead.setType(declaringClass);
			res = fieldRead;
		} else {
			throw new SpoonException("Please, submit a valid value.");
		}
		return res;
	}

	private boolean isPrimitive(Class c) {
		return c.isPrimitive() || c == Byte.class || c == Short.class || c == Integer.class || c == Long.class || c == Float.class || c == Double.class || c == Boolean.class || c == Character.class;
	}

	private <T extends CtAnnotation<A>> T addValueExpression(String elementName, CtExpression<?> expression) {
		if (elementValues.containsKey(elementName)) {
			// Update value of the existing one.
			final CtExpression ctExpression = (CtExpression) elementValues.get(elementName);
			if (ctExpression instanceof CtNewArray) {
				// Already an array, add the value inside it.
				if (expression instanceof CtNewArray) {
					List<CtExpression<?>> elements = ((CtNewArray) expression).getElements();
					for (CtExpression expInArray : elements) {
						((CtNewArray) ctExpression).addElement(expInArray);
					}
				} else {
					((CtNewArray) ctExpression).addElement(expression);
				}
			} else {
				// Switch the value to a CtNewArray.
				CtNewArray<Object> newArray = getFactory().Core().createNewArray();
				newArray.setType(ctExpression.getType());
				newArray.setParent(this);
				newArray.addElement(ctExpression);
				newArray.addElement(expression);
				elementValues.put(elementName, newArray);
			}
		} else {
			// Add the new value.
			elementValues.put(elementName, expression);
			expression.setParent(this);
		}
		return (T) this;
	}

	@Override
	public <T extends CtAnnotation<A>> T addValue(String elementName, CtLiteral<?> value) {
		return addValueExpression(elementName, value);
	}

	@Override
	public <T extends CtAnnotation<A>> T addValue(String elementName, CtNewArray<? extends CtExpression> value) {
		return addValueExpression(elementName, value);
	}

	@Override
	public <T extends CtAnnotation<A>> T addValue(String elementName, CtFieldAccess<?> value) {
		return addValueExpression(elementName, value);
	}

	@Override
	public <T extends CtAnnotation<A>> T addValue(String elementName, CtAnnotation<?> value) {
		return addValueExpression(elementName, value);
	}

	@SuppressWarnings("unchecked")
	private Object convertValue(Object value) {
		if (value instanceof CtFieldReference) {
			Class<?> c = null;
			try {
				c = ((CtFieldReference<?>) value).getDeclaringType().getActualClass();
			} catch (Exception e) {
				return ((CtLiteral<?>) ((CtFieldReference<?>) value).getDeclaration().getDefaultExpression()
						.partiallyEvaluate()).getValue();
			}

			if (((CtFieldReference<?>) value).getSimpleName().equals("class")) {
				return c;
			}
			CtField<?> field = ((CtFieldReference<?>) value).getDeclaration();
			if (Enum.class.isAssignableFrom(c)) {
				// Value references a Enum field
				return Enum.valueOf((Class<? extends Enum>) c, ((CtFieldReference<?>) value).getSimpleName());
			}
			// Value is a static final
			if (field != null) {
				return convertValue(field.getDefaultExpression());
			} else {
				try {
					return ((Field) ((CtFieldReference<?>) value).getActualField()).get(null);
				} catch (Exception e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				}
				return null;
			}
		} else if (value instanceof CtFieldAccess) {
			// Get variable
			return convertValue(((CtFieldAccess<?>) value).getVariable());
		} else if (value instanceof CtNewArray) {
			CtNewArray<?> arrayExpression = (CtNewArray<?>) value;

			Class<?> componentType = arrayExpression.getType().getActualClass().getComponentType();
			List<CtExpression<?>> elements = arrayExpression.getElements();

			Object array = Array.newInstance(componentType, elements.size());
			for (int i = 0; i < elements.size(); i++) {
				Array.set(array, i, this.convertValue(elements.get(i)));
			}

			return array;
		} else if (value instanceof CtAnnotation) {
			// Get proxy
			return ((CtAnnotation<?>) value).getActualAnnotation();
		} else if (value instanceof CtLiteral) {
			// Replace literal by his value
			return ((CtLiteral<?>) value).getValue();
		} else if (value instanceof CtCodeElement) {
			// Evaluate code elements
			PartialEvaluator eval = getFactory().Eval().createPartialEvaluator();
			Object ret = eval.evaluate(null, (CtCodeElement) value);

			return this.convertValue(ret);
		} else if (value instanceof CtTypeReference) {
			// Get RT class for References
			return ((CtTypeReference<?>) value).getActualClass();
		}
		return value;
	}

	private Class<?> getElementType(String name) {
		// Try by CT reflection
		CtType<?> t = getAnnotationType().getDeclaration();
		if (t != null) {
			CtMethod<?> method = t.getMethod(name);
			return method.getType().getActualClass();
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
		CtAnnotationType<?> at = (CtAnnotationType<?>) getAnnotationType().getDeclaration();
		if (at != null) {
			CtAnnotationMethod<?> f = (CtAnnotationMethod) at.getMethod(fieldName);
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
			} else if ((type == double.class) && (ret.getClass() != double.class)) {
				ret = Double.parseDouble(ret.toString());
			} else if ((type == float.class) && (ret.getClass() != float.class)) {
				ret = Float.parseFloat(ret.toString());
			} else if ((type == int.class) && (ret.getClass() != int.class)) {
				ret = Integer.parseInt(ret.toString());
			} else if ((type == long.class) && (ret.getClass() != long.class)) {
				ret = Long.parseLong(ret.toString());
			} else if (type == short.class && ret.getClass() != short.class) {
				ret = Short.parseShort(ret.toString());
			}
		}
		if (type.isArray() && ret != null && ret.getClass() != type) {
			final Object array = Array.newInstance(ret.getClass(), 1);
			((Object[]) array)[0] = ret;
			ret = array;
		}
		return (T) ret;
	}

	@Override
	public <T extends CtExpression> T getValue(String key) {
		return (T) this.elementValues.get(key);
	}

	public Map<String, Object> getElementValues() {
		TreeMap<String, Object> res = new TreeMap<>();
		for (Entry<String, CtExpression> elementValue : elementValues.entrySet()) {
			res.put(elementValue.getKey(), elementValue.getValue());
		}
		return res;
	}

	@Override
	public Map<String, CtExpression> getValues() {
		return Collections.unmodifiableMap(elementValues);
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

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtAnnotation<A>> T setAnnotationType(CtTypeReference<? extends Annotation> annotationType) {
		if (annotationType != null) {
			annotationType.setParent(this);
		}
		this.annotationType = (CtTypeReference<A>) annotationType;
		return (T) this;
	}

	@Override
	public <T extends CtAnnotation<A>> T setElementValues(Map<String, Object> values) {
		this.elementValues.clear();
		for (Entry<String, Object> e : values.entrySet()) {
			addValue(e.getKey(), e.getValue());
		}
		return (T) this;
	}

	@Override
	public <T extends CtAnnotation<A>> T setValues(Map<String, CtExpression> values) {
		this.elementValues.clear();
		for (Entry<String, CtExpression> e : values.entrySet()) {
			addValue(e.getKey(), e.getValue());
		}
		return (T) this;
	}

	@Override
	public CtElement getAnnotatedElement() {
		return this.getParent();
	}

	@Override
	public CtAnnotatedElementType getAnnotatedElementType() {
		CtElement annotatedElement = this.getAnnotatedElement();

		if (annotatedElement == null) {
			return null;
		}

		if (annotatedElement instanceof CtMethod) {
			return CtAnnotatedElementType.METHOD;
		}
		if (annotatedElement instanceof CtAnnotation || annotatedElement instanceof CtAnnotationType) {
			return CtAnnotatedElementType.ANNOTATION_TYPE;
		}
		if (annotatedElement instanceof CtType) {
			return CtAnnotatedElementType.TYPE;
		}
		if (annotatedElement instanceof CtField) {
			return CtAnnotatedElementType.FIELD;
		}
		if (annotatedElement instanceof CtConstructor) {
			return CtAnnotatedElementType.CONSTRUCTOR;
		}
		if (annotatedElement instanceof CtParameter) {
			return CtAnnotatedElementType.PARAMETER;
		}
		if (annotatedElement instanceof CtLocalVariable) {
			return CtAnnotatedElementType.LOCAL_VARIABLE;
		}
		if (annotatedElement instanceof CtPackage) {
			return CtAnnotatedElementType.PACKAGE;
		}
		if (annotatedElement instanceof CtTypeParameterReference) {
			return CtAnnotatedElementType.TYPE_PARAMETER;
		}
		if (annotatedElement instanceof CtTypeReference) {
			return CtAnnotatedElementType.TYPE_USE;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public A getActualAnnotation() {
		class AnnotationInvocationHandler implements InvocationHandler {
			CtAnnotation<? extends Annotation> annotation;

			AnnotationInvocationHandler(CtAnnotation<? extends Annotation> annotation) {
				super();
				this.annotation = annotation;
			}

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String fieldname = method.getName();
				if ("toString".equals(fieldname)) {
					return CtAnnotationImpl.this.toString();
				} else if ("annotationType".equals(fieldname)) {
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
		return (A) Proxy.newProxyInstance(annotationType.getActualClass().getClassLoader(), new Class[] { annotationType.getActualClass() }, new AnnotationInvocationHandler(this));
	}

	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtAnnotation<A> clone() {
		return (CtAnnotation<A>) super.clone();
	}
}
