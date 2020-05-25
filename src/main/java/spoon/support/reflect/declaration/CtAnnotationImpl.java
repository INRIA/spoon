/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.eval.EvalHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotation}.
 *
 * @author Renaud Pawlak
 */
public class CtAnnotationImpl<A extends Annotation> extends CtExpressionImpl<A> implements CtAnnotation<A> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.ANNOTATION_TYPE)
	CtTypeReference<A> annotationType;

	@MetamodelPropertyField(role = CtRole.VALUE)
	private Map<String, CtExpression> elementValues = new TreeMap() {
		@Override
		public Set<Entry<String, CtExpression>> entrySet() {
			Set<Entry<String, CtExpression>> result = new TreeSet<>(new Comparator<Entry<String, CtExpression>>() {
				final CtLineElementComparator comp = new CtLineElementComparator();

				@Override
				public int compare(Entry<String, CtExpression> o1, Entry<String, CtExpression> o2) {
					return comp.compare(o1.getValue(), o2.getValue());
				}
			}
			);
			result.addAll(super.entrySet());
			return result;
		}
	};

	public CtAnnotationImpl() {
	}

	@Override
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

			res.setType(getFactory().Type().createArrayReference(getFactory().Type().createReference(value.getClass().getComponentType())));
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
			final CtExpression ctExpression = elementValues.get(elementName);
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
			expression.setParent(this);
			getFactory().getEnvironment().getModelChangeListener().onMapAdd(this, CtRole.VALUE, this.elementValues, elementName, expression);
			elementValues.put(elementName, expression);
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

	@Override
	public CtTypeReference<A> getAnnotationType() {
		return annotationType;
	}

	private CtExpression getDefaultExpression(String fieldName) {
		CtExpression ret = null;
		CtAnnotationType<?> at = (CtAnnotationType<?>) getAnnotationType().getDeclaration();
		if (at != null) {
			CtAnnotationMethod<?> f = (CtAnnotationMethod) at.getMethod(fieldName);
			ret = f.getDefaultExpression();
		}
		return ret;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtExpression> T getValue(String key) {
		return (T) getValueAsExpression(key);
	}

	@Override
	public int getValueAsInt(String key) {
		Object val = getValueAsObject(key);
		if (val == null) {
			throw new IllegalStateException(key + " not in the annotation");
		}
		return (int) val;
	}

	@Override
	public String getValueAsString(String key) {
		return (String) getValueAsObject(key);
	}

	@Override
	public Object getValueAsObject(String key) {
		CtExpression expr = getWrappedValue(key);

		// no such value, per the contract of the method
		if (expr == null) {
			return null;
		}

		Object ret = EvalHelper.convertElementToRuntimeObject(expr);
		Class<?> type = getElementType(key);
		return forceObjectToType(ret, type);
	}

	private Object forceObjectToType(Object ret, Class<?> type) {
		if (type.isPrimitive()) {
			if ((type == boolean.class) && (ret.getClass() != boolean.class)) {
				return Boolean.parseBoolean(ret.toString());
			} else if ((type == byte.class) && (ret.getClass() != byte.class)) {
				return Byte.parseByte(ret.toString());
			} else if ((type == char.class) && (ret.getClass() != char.class)) {
				return ret.toString().charAt(0);
			} else if ((type == double.class) && (ret.getClass() != double.class)) {
				return Double.parseDouble(ret.toString());
			} else if ((type == float.class) && (ret.getClass() != float.class)) {
				return Float.parseFloat(ret.toString());
			} else if ((type == int.class) && (ret.getClass() != int.class)) {
				return Integer.parseInt(ret.toString());
			} else if ((type == long.class) && (ret.getClass() != long.class)) {
				return Long.parseLong(ret.toString());
			} else if (type == short.class && ret.getClass() != short.class) {
				return Short.parseShort(ret.toString());
			}
		}
		return ret;
	}


	private CtExpression getValueAsExpression(String key) {

		// get specified field in annotation directly
		CtExpression ret = this.elementValues.get(key);
		if (ret != null) {
			return ret;
		}

		// get default value in annotation declaration in source code
		ret = getDefaultExpression(key);
		if (ret != null) {
			return ret;
		}

		// get default value in annotation declaration in classpath
		Object value = getReflectValue(key);
		if (value != null) {
			return convertValueToExpression(value);
		}

		return null;
	}

	@Override
	public <T extends CtExpression> T getWrappedValue(String key) {
		CtExpression ctExpression = this.getValue(key);

			CtTypeReference typeReference = this.getAnnotationType();
			CtType type = typeReference.getTypeDeclaration();
			if (type != null) {
				CtMethod method = type.getMethod(key);
				if (method != null) {
					CtTypeReference returnType = method.getType();
					if (returnType instanceof CtArrayTypeReference && !(ctExpression instanceof CtNewArray)) {
						CtNewArray newArray = getFactory().Core().createNewArray();
						CtArrayTypeReference typeReference2 = this.getFactory().createArrayTypeReference();
						typeReference2.setComponentType(ctExpression.getType().clone());
						newArray.setType(typeReference2);
						newArray.addElement(ctExpression.clone());
						return (T) newArray;
					}
				}
		}
		return (T) ctExpression;
	}

	public Map<String, Object> getElementValues() {
		Map<String, Object> res = new TreeMap<>();
		for (Entry<String, CtExpression> elementValue : elementValues.entrySet()) {
			res.put(elementValue.getKey(), elementValue.getValue());
		}
		return res;
	}

	@Override
	public Map<String, CtExpression> getValues() {
		return Collections.unmodifiableMap(elementValues);
	}

	@Override
	public Map<String, CtExpression> getAllValues() {
		Map<String, CtExpression> values = new TreeMap();
		// first, we put the default values
		CtAnnotationType<?> annotationType = (CtAnnotationType) getAnnotationType().getTypeDeclaration();
		for (CtAnnotationMethod m : annotationType.getAnnotationMethods()) {
			values.put(m.getSimpleName(), m.getDefaultExpression());
		}

		// we override the values with ones of this expression
		values.putAll(elementValues);
		return Collections.unmodifiableMap(values);
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, annotationType, this.annotationType);
		this.annotationType = (CtTypeReference<A>) annotationType;
		return (T) this;
	}

	@Override
	public <T extends CtAnnotation<A>> T setElementValues(Map<String, Object> values) {
		getFactory().getEnvironment().getModelChangeListener().onMapDeleteAll(this, CtRole.VALUE, this.elementValues, new HashMap<>(elementValues));
		this.elementValues.clear();
		for (Entry<String, Object> e : values.entrySet()) {
			addValue(e.getKey(), e.getValue());
		}
		return (T) this;
	}

	@Override
	public <T extends CtAnnotation<A>> T setValues(Map<String, CtExpression> values) {
		getFactory().getEnvironment().getModelChangeListener().onMapDeleteAll(this, CtRole.VALUE, this.elementValues, new HashMap<>(elementValues));
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

		return CtAnnotation.getAnnotatedElementTypeForCtElement(annotatedElement);
	}

	@Override
	@SuppressWarnings("unchecked")
	public A getActualAnnotation() {
		class AnnotationInvocationHandler implements InvocationHandler {
			CtAnnotation<? extends Annotation> annotation;

			AnnotationInvocationHandler(CtAnnotation<? extends Annotation> annotation) {
				this.annotation = annotation;
			}

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) {
				String fieldname = method.getName();
				if ("toString".equals(fieldname)) {
					return CtAnnotationImpl.this.toString();
				} else if ("annotationType".equals(fieldname)) {
					return annotation.getAnnotationType().getActualClass();
				}
				return getValueAsObject(fieldname);
			}
		}
		return (A) Proxy.newProxyInstance(annotationType.getActualClass().getClassLoader(), new Class[] { annotationType.getActualClass() }, new AnnotationInvocationHandler(this));
	}

	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtAnnotation<A> clone() {
		return (CtAnnotation<A>) super.clone();
	}

	@Override
	@DerivedProperty
	public List<CtTypeReference<?>> getTypeCasts() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtExpression<A>> C setTypeCasts(List<CtTypeReference<?>> casts) {
		return (C) this;
	}
}
